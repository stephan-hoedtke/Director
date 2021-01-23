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
        viewModel.starVectorLD.observe(viewLifecycleOwner, { vector -> onObserveStarVector(vector) })

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
        binding.textViewAzimuth.text = Angle.toString(orientation.pointerAzimuth, Angle.AngleType.AZIMUTH)
        binding.textViewPitch.text = Angle.toString(orientation.pointerAltitude, Angle.AngleType.ALTITUDE)
        binding.textViewRoll.text = Angle.toString(orientation.roll, Angle.AngleType.ROLL)
        binding.textViewCenter.text = Angle.toString(orientation.centerAzimuth, orientation.centerAltitude, Angle.AngleType.ORIENTATION)
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

    private fun onObserveStarVector(vector: Vector) {
        binding.textViewStarX.text = Formatter.df2.format(vector.x)
        binding.textViewStarY.text = Formatter.df2.format(vector.y)
        binding.textViewStarZ.text = Formatter.df2.format(vector.z)
    }
}
