package com.example.androidtbc.presentation.imageaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtbc.databinding.FragmentImageActionBottomSheetBinding
import com.example.androidtbc.presentation.imageaction.adapter.ImageActionAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ImageActionBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentImageActionBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val itemAdapter by lazy {
        ImageActionAdapter(
            onClick = {
                selectedItem(it)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageActionBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRv()
    }


    private fun setUpRv() {
        binding.rvImageActions.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = itemAdapter
        }
    }

    private fun selectedItem(item: String) {
        val data = Bundle().apply {
            putString("selected_item_key", item)
        }
        setFragmentResult("request_key", data)

        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}