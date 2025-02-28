package com.example.androidtbc.presentation.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.androidtbc.R
import com.example.androidtbc.databinding.BottomSheetLocationBinding
import com.example.androidtbc.presentation.location.model.LocationUi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LocationBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetLocationBinding? = null
    private val binding get() = _binding!!

    private var location: LocationUi? = null

    companion object {
        fun newInstance(location: LocationUi): LocationBottomSheetFragment {
            val fragment = LocationBottomSheetFragment()
            fragment.location = location
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLocationDetails()
    }

    private fun setupLocationDetails() {
        location?.let {
            with(binding){
            tvLocationTitle.text = it.title
            tvLocationAddress.text = it.address
            tvLocationCoordinates.text =
                getString(R.string.location_coordinates, it.latitude, it.longitude)
        }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}