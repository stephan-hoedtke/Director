package com.stho.director.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.stho.director.Angle
import com.stho.director.Degree
import com.stho.director.Orientation
import com.stho.director.R
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
        val w: Double = (800.0 / 1800.0) * binding.grid.width
        val h: Double = (800.0 / 1800.0) * binding.grid.height

        val l: Double = Degree.cos(orientation.pitch)
        val z: Double = Degree.sin(orientation.pitch)
        val x: Double = Degree.sin(orientation.azimuth)
        val y: Double = Degree.cos(orientation.azimuth)

        val dx = (l * x * w).toFloat()
        val dy = (l * y * h).toFloat()
        val alpha = (0.5 + z / 2).toFloat()

        binding.orangePoint.translationX = -dx
        binding.orangePoint.translationY = -dy
        binding.orangePoint.alpha = alpha
        binding.redPoint.translationX = -dx
        binding.redPoint.translationY = -dy
        binding.redPoint.alpha = 1 - alpha
        binding.grid.alpha = if (z > 0) 0.5f else 1.0f

        binding.textViewAzimuth.text = Angle.toString(orientation.azimuth, Angle.AngleType.AZIMUTH)
        binding.textViewPitch.text = Angle.toString(orientation.pitch, Angle.AngleType.PITCH)
    }
}