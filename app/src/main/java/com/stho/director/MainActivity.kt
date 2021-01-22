package com.stho.director

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.View
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("SameParameterValue")
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private val algorithms: Algorithms by lazy { Algorithms() }
    private val orientationFilter: OrientationFilter by lazy { OrientationAccelerationFilter() }
    private val orientationSensorListener: OrientationSensorListener by lazy { OrientationSensorListener(this, orientationFilter) }
    private val repository: Repository = Repository.getInstance()
    private val settings: Settings = repository.settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()
        // setupFAB()
        setupAppBar()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_LOW_POWER)
            .setInterval(10 * 1000)        // 10 seconds, in milliseconds
            .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.also {
                    onUpdateLocation(it.lastLocation)
                }
            }
        }

        repository.locationLD.observe(this, { location -> onObserveLocation(location) })
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

//    private fun setupFAB() {
//        val fab: FloatingActionButton = findViewById(R.id.fab)
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
//    }

    private fun setupAppBar() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_info
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        orientationSensorListener.onResume()
        executeHandlerToUpdateOrientation()

        if (settings.requestLocationUpdates)
            acquireCurrentLocation()
    }

    override fun onPause() {
        super.onPause()
        orientationSensorListener.onPause()
        stopHandler()
        stopLocationUpdates()
    }

    private fun onSettingsUpdated() {
        if (settings.showStar) {
            repository.updateStar(Star(settings.starRightAscension, settings.starDeclination))
        }
    }

    private fun executeHandlerToUpdateOrientation() {
        val runnableCode: Runnable = object : Runnable {
            override fun run() {
                CoroutineScope(Dispatchers.Default).launch {
                    repository.updateOrientation(orientationFilter.currentOrientation)
                    // (1, 0, 0) is pointing east
                    // (0, 1, 0) is pointing north
                    // (0, 0, 1) is pointing upwards into the sky
                    repository.updateNorthVector(orientationFilter.rotateFromEarthToPhone(Vector(0.0, 1.0, 0.0)))
                    repository.updateGravityVector(orientationFilter.rotateFromEarthToPhone(Vector(0.0, 0.0, -1.0)))
                    repository.updateCenter(orientationFilter.rotateFromPhoneToEarth(Vector(0.0, 0.0, -1.0)))
                    repository.updatePointer(orientationFilter.rotateFromPhoneToEarth(Vector(0.0, 1.0, 0.0)))

                     if (settings.showStar) {
                        val star = repository.star
                        star.azimuthAltitude = algorithms.getAzimuthAltitudeFor(star.rightAscension, star.declination)
                        star.phone = orientationFilter.rotateFromEarthToPhone(star.earth)
                        repository.updateStar(star)
                    }

                }
                handler.postDelayed(this, 200)
            }
        }
        handler.postDelayed(runnableCode, 200)
    }

    private fun stopHandler() {
        handler.removeCallbacksAndMessages(null)
    }

    private fun acquireCurrentLocation() {
        when (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)) {
            PERMISSION_GRANTED -> {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location == null) {
                        showSnackbar("Request location updates...")
                        startLocationUpdates()
                    } else {
                        onUpdateLocation(location)
                    }
                }
            }
            else -> {
                if (shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)) {
                    showSnackbar("Course Location is needed to calculate the stars current position")
                } else {
                    requestPermissions(
                        arrayOf(ACCESS_COARSE_LOCATION),
                        ACCESS_COARSE_LOCATION_PERMISSION_REQUESTCODE
                    )
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun onUpdateLocation(location: Location) {
        repository.updateLocation(location)
        stopLocationUpdates()
    }

    private fun onObserveLocation(location: LongitudeLatitude) {
        algorithms.setLocation(location)
        showSnackbar("Location: $location")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ACCESS_COARSE_LOCATION_PERMISSION_REQUESTCODE -> {
                if (isGranted(permissions, grantResults, ACCESS_COARSE_LOCATION)) {
                    showSnackbar("Request location updates...")
                    startLocationUpdates()
                } else {
                    stopLocationUpdates()
                    settings.requestLocationUpdates = false
                    showSnackbar("Location updates disabled.")
                }
            }
        }
    }

    private fun showSnackbar(message: String) {
        val container: View = findViewById<View>(R.id.drawer_layout)
        Snackbar.make(container, message, 4000)
            .setBackgroundTint(getColor(R.color.snackbarBackground))
            .setTextColor(getColor(R.color.snackbarTextColor))
            .show()
    }


    companion object {

        private const val ACCESS_COARSE_LOCATION_PERMISSION_REQUESTCODE = 129473

        private fun isGranted(permissions: Array<out String>, grantResults: IntArray, requestedPermission: String) =
            permissions.isNotEmpty() && permissions[0] == requestedPermission && grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED
    }
}