package com.example.androidtbc.presentation.moviedetail.tablayoutfragments.cast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.androidtbc.data.remote.dto.CastMemberDto
import com.example.androidtbc.databinding.FragmentCastBottomSheetBinding
import com.example.androidtbc.utils.loadTmdbImage
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CastBottomSheetFragment(private val cast: CastMemberDto) : BottomSheetDialogFragment() {

    private var _binding: FragmentCastBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCastBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            tvCastName.text = cast.name
            tvCharacter.text = "Character: ${cast.character}"

            // Handle gender
            cast.gender?.let { genderValue ->
                val genderText = when (genderValue) {
                    1 -> "Female"
                    2 -> "Male"
                    else -> "Not specified"
                }
                tvGender.text = "Gender: $genderText"
                tvGender.visibility = View.VISIBLE
            } ?: run {
                tvGender.visibility = View.GONE
            }

            // Handle popularity
            cast.popularity?.let { popularityValue ->
                tvPopularity.text = "Popularity: ${popularityValue}"
                tvPopularity.visibility = View.VISIBLE
            } ?: run {
                tvPopularity.visibility = View.GONE
            }

            // Load image if profilePath is not null
            ivCastImage.loadTmdbImage(cast.profilePath)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}