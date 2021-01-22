package com.stho.director.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.stho.director.*
import com.stho.director.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private var bindingReference: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = bindingReference!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentHomeBinding.inflate(inflater, container, false)

        viewModel.orientationLD.observe(viewLifecycleOwner, { orientation -> onObserveOrientation(orientation) })
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
        binding.textViewPitch.text = Angle.toString(orientation.pitch, Angle.AngleType.PITCH)
        binding.textViewRoll.text = Angle.toString(orientation.roll, Angle.AngleType.ROLL)
    }

    private fun onObserveNorthVector(vector: Vector) {
        val w: Double = (800.0 / 1800.0) * binding.grid.width
        val h: Double = (800.0 / 1800.0) * binding.grid.height

        val dx = (vector.x * w).toFloat()
        val dy = (vector.y * h).toFloat()
        val alpha = (0.5 + vector.z / 2).toFloat()

        binding.orangePoint.translationX = dx
        binding.orangePoint.translationY = -dy
        binding.orangePoint.alpha = alpha
        binding.redPoint.translationX = dx
        binding.redPoint.translationY = -dy
        binding.redPoint.alpha = 1 - alpha
        binding.grid.alpha = if (vector.z > 0) 0.5f else 1.0f
    }

    private fun onObserveGravityVector(vector: Vector) {
        val w: Double = (800.0 / 1800.0) * binding.grid.width
        val h: Double = (800.0 / 1800.0) * binding.grid.height

        val dx = (vector.x * w).toFloat()
        val dy = (vector.y * h).toFloat()

        binding.yellowPoint.translationX = dx
        binding.yellowPoint.translationY = -dy
    }

    private fun onObserveStar(star: Star) {
        val w: Double = (800.0 / 1800.0) * binding.grid.width
        val h: Double = (800.0 / 1800.0) * binding.grid.height

        val dx = (star.phone.x * w).toFloat()
        val dy = (star.phone.y * h).toFloat()

        binding.star.translationX = dx
        binding.star.translationY = -dy
    }

    private fun onObserverCenter(center: AzimuthAltitude) {
        binding.textViewCenter.text = Angle.toString(center.azimuth, center.altitude, Angle.AngleType.ORIENTATION)
    }

    private fun onObserverPointer(pointer: AzimuthAltitude) {
        binding.textViewPointer.text = Angle.toString(pointer.azimuth, pointer.altitude, Angle.AngleType.ORIENTATION)
    }

}