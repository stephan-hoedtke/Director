package com.stho.director.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.stho.director.Angle
import com.stho.director.Formatter
import com.stho.director.Orientation
import com.stho.director.R
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
        binding.textViewGravityX.text = Formatter.df0.format(orientation.gravity.x)
        binding.textViewGravityY.text = Formatter.df0.format(orientation.gravity.y)
        binding.textViewGravityZ.text = Formatter.df0.format(orientation.gravity.z)
    }
}