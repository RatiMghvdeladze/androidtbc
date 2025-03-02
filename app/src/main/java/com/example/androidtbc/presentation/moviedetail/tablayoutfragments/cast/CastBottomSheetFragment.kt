package com.example.androidtbc.presentation.moviedetail.tablayoutfragments.cast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentCastBottomSheetBinding
import com.example.androidtbc.presentation.model.CastMember
import com.example.androidtbc.utils.loadTmdbImage
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CastBottomSheetFragment(private val cast: CastMember) : BottomSheetDialogFragment() {

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
            tvCharacter.text = getString(R.string.character, cast.character)

            cast.gender?.let { genderValue ->
                val genderText = when (genderValue) {
                    1 -> getString(R.string.female)
                    2 -> getString(R.string.male)
                    else -> getString(R.string.not_specified)
                }
                tvGender.text = getString(R.string.gender, genderText)
                tvGender.visibility = View.VISIBLE
            } ?: run {
                tvGender.visibility = View.GONE
            }

            cast.popularity?.let {
                tvPopularity.text = getString(R.string.popularity, it.toString())
                tvPopularity.visibility = View.VISIBLE
            } ?: run {
                tvPopularity.visibility = View.GONE
            }

            ivCastImage.loadTmdbImage(cast.profilePath)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}