package com.stho.director

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
class MainActivity : AppCompatActivity(), IOrientationFilter {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private val algorithms: Algorithms by lazy { Algorithms() }
    private val orientationFilter = OrientationAccelerationFilter()
    private val elementsFilter = ElementsFilter()
    private val orientationSensorListener: OrientationSensorListener by lazy { OrientationSensorListener(this, this) }
    private val repository: Repository = Repository.getInstance()
    private val settings: Settings = repository.settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()
        setupAppBar()
        setupLocationListener()

        repository.locationLD.observe(this, { location -> onObserveLocation(location) })
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun setupAppBar() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_info,
                R.id.nav_settings
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun setupLocationListener() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_LOW_POWER)
            .setInterval(10 * 1000)        // 10 seconds, in milliseconds
            .setFastestInterval(1 * 1000) // 1 second, in milliseconds

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.also {
                    onUpdateLocation(it.lastLocation)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        orientationSensorListener.onResume()

        executeHandlerToUpdateStar()
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

    override fun onOrientationChanged(R: FloatArray) {
        orientationFilter.onOrientationChanged(R)
        elementsFilter.onOrientationChanged(R)
    }

    private fun executeHandlerToUpdateStar() {
        val runnableCode: Runnable = object : Runnable {
            override fun run() {
                CoroutineScope(Dispatchers.Default).launch {
                    if (settings.showStar) {
                        settings.star.also { star ->
                            star.azimuthAltitude = algorithms.getAzimuthAltitudeFor(star.rightAscension, star.declination)
                            elementsFilter.setStar(star.earth)
                        }
                    }
                }
                handler.postDelayed(this, 10000)
            }
        }
        handler.postDelayed(runnableCode, 200)
    }

    private fun executeHandlerToUpdateOrientation() {
        val runnableCode: Runnable = object : Runnable {
            override fun run() {
                CoroutineScope(Dispatchers.Default).launch {
                    repository.updateOrientation(orientationFilter.currentOrientation)
                    repository.updateNorthVector(elementsFilter.currentNorthVector)
                    repository.updateGravityVector(elementsFilter.currentGravity)

                    if (settings.showStar) {
                        repository.updateStarVector(elementsFilter.currentStar)
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
        if (!repository.updateLocation(location)) {
            stopLocationUpdates()
            showSnackbar("Location done.")
        }
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