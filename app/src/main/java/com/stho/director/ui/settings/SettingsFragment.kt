package com.stho.director.ui.settings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.stho.director.Algorithms
import com.stho.director.R
import com.stho.director.databinding.FragmentSettingsBinding
import java.lang.Exception

class SettingsFragment : Fragment() {

    private lateinit var viewModel: SettingsViewModel
    private var bindingReference: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding get() = bindingReference!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bindingReference = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.buttonSave.setOnClickListener { onSave() }
        binding.orion.setOnClickListener { onDefault("5h 35m 50", "-1° 32' 33") }
        binding.urs.setOnClickListener { onDefault("12h 15m 26", "+57° 1' 57") }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.switchShowStar.isChecked = viewModel.repository.settings.showStar
        binding.editRightAscension.setText(Algorithms.hourToString(viewModel.repository.settings.star.rightAscension))
        binding.editDeclination.setText(Algorithms.angleToString(viewModel.repository.settings.star.declination))
        binding.switchUpdateLocation.isChecked = viewModel.repository.settings.requestLocationUpdates
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }


    private fun onSave() {
        try {
            val rightAscension = Algorithms.parseHourString(binding.editRightAscension.text.toString())
            val declination = Algorithms.parseAngleString(binding.editDeclination.text.toString())

            viewModel.repository.settings.showStar = binding.switchShowStar.isChecked
            viewModel.repository.settings.star.rightAscension = rightAscension
            viewModel.repository.settings.star.declination = declination
            viewModel.repository.settings.requestLocationUpdates = binding.switchUpdateLocation.isChecked

            findNavController().popBackStack()

        } catch (ex: Exception)
        {
            showSnackbar("Error: ${ex.message}")
        }
    }

    private fun showSnackbar(message: String) {
        val container: View = requireActivity().findViewById<View>(R.id.drawer_layout)
        Snackbar.make(container, message, 4000)
            .setBackgroundTint(requireActivity().getColor(R.color.snackbarBackground))
            .setTextColor(requireActivity().getColor(R.color.snackbarTextColor))
            .show()
    }

    private fun onDefault(rightAscension: String, declination: String) {
        binding.editRightAscension.setText(rightAscension)
        binding.editDeclination.setText(declination)
        binding.switchShowStar.isChecked = true
    }

}