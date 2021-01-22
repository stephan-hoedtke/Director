package com.stho.director.ui.info

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.stho.director.*
import com.stho.director.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {

    private lateinit var viewModel: InfoViewModel
    private var bindingReference: FragmentInfoBinding? = null
    private val binding: FragmentInfoBinding get() = bindingReference!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(InfoViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentInfoBinding.inflate(inflater, container, false)

        viewModel.orientationLD.observe(viewLifecycleOwner, { orientation -> onObserveOrientation(orientation) })
        viewModel.locationLD.observe(viewLifecycleOwner, { location -> onObserveLocation(location) })
        viewModel.northVectorLD.observe(viewLifecycleOwner, { vector -> onObserveNorthVector(vector) })
        viewModel.gravityVectorLD.observe(viewLifecycleOwner, { vector -> onObserveGravityVector(vector) })
        viewModel.starLD.observe(viewLifecycleOwner, { star -> onObserveStar(star) })
        viewModel.centerLD.observe(viewLifecycleOwner, { center -> onObserverCenter(center) })
        viewModel.pointerLD.observe(viewLifecycleOwner, { pointer -> onObserverPointer(pointer) })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }

    private fun onObserveOrientation(orientation: Orientation) {
        binding.textViewAzimuth.text = Angle.toString(orientation.azimuth, Angle.AngleType.AZIMUTH)
        binding.textViewDirection.text = Angle.toString(orientation.direction, Angle.AngleType.ALTITUDE)
        binding.textViewPitch.text = Angle.toString(orientation.pitch, Angle.AngleType.ALTITUDE)
        binding.textViewRoll.text = Angle.toString(orientation.roll, Angle.AngleType.ROLL)
    }

    private fun onObserveLocation(location: LongitudeLatitude) {
        binding.textViewLocation.text = location.toString()
    }

    private fun onObserveNorthVector(vector: Vector) {
        binding.textViewNorthX.text = Formatter.df2.format(vector.x)
        binding.textViewNorthY.text = Formatter.df2.format(vector.y)
        binding.textViewNorthZ.text = Formatter.df2.format(vector.z)
    }

    private fun onObserveGravityVector(vector: Vector) {
        binding.textViewGravityX.text = Formatter.df2.format(vector.x)
        binding.textViewGravityY.text = Formatter.df2.format(vector.y)
        binding.textViewGravityZ.text = Formatter.df2.format(vector.z)
    }

    private fun onObserveStar(star: Star) {
        binding.textViewStarX.text = Formatter.df2.format(star.phone.x)
        binding.textViewStarY.text = Formatter.df2.format(star.phone.y)
        binding.textViewStarZ.text = Formatter.df2.format(star.phone.z)
    }

    private fun onObserverCenter(center: AzimuthAltitude) {
        binding.textViewCenter.text = Angle.toString(center.azimuth, center.altitude, Angle.AngleType.ORIENTATION)
    }

    private fun onObserverPointer(pointer: AzimuthAltitude) {
        binding.textViewPointer.text = Angle.toString(pointer.azimuth, pointer.altitude, Angle.AngleType.ORIENTATION)
    }
}
