package com.example.androidtbc.presentation.home

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.example.androidtbc.databinding.FragmentHomeBinding
import com.example.androidtbc.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private var isSearchExpanded = false

    override fun start() {
        setupSearchBar()
        setupSearchBackground()
    }

    private fun setupSearchBackground() {
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = resources.displayMetrics.density * 24
            setColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        }
        binding.searchBackground.background = drawable
    }

    private fun setupSearchBar() {
        binding.btnSearch.setOnClickListener {
            if (!isSearchExpanded) {
                expandSearchBar()
            }
        }

        binding.root.setOnClickListener {
            if (isSearchExpanded && it != binding.searchExpandContainer) {
                collapseSearchBar()
            }
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                collapseSearchBar()
                true
            } else {
                false
            }
        }
    }

    private fun expandSearchBar() {
        val searchContainer = binding.searchExpandContainer
        val searchEditText = binding.searchEditText
        val searchBackground = binding.searchBackground

        searchBackground.visibility = View.VISIBLE

        val targetWidth = (resources.displayMetrics.widthPixels * 0.7).toInt()

        val widthAnimator = ValueAnimator.ofInt(
            searchContainer.width,
            targetWidth
        )

        widthAnimator.addUpdateListener { animator ->
            val params = searchContainer.layoutParams
            params.width = animator.animatedValue as Int
            searchContainer.layoutParams = params
        }

        searchEditText.visibility = View.VISIBLE
        searchEditText.alpha = 0f
        searchEditText.animate()
            .alpha(1f)
            .setDuration(300)
            .start()

        widthAnimator.duration = 300
        widthAnimator.start()

        isSearchExpanded = true

        searchEditText.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun collapseSearchBar() {
        val searchContainer = binding.searchExpandContainer
        val searchEditText = binding.searchEditText
        val searchBackground = binding.searchBackground

        val widthAnimator = ValueAnimator.ofInt(
            searchContainer.width,
            48
        )

        widthAnimator.addUpdateListener { animator ->
            val params = searchContainer.layoutParams
            params.width = animator.animatedValue as Int
            searchContainer.layoutParams = params
        }

        widthAnimator.duration = 300
        widthAnimator.start()

        searchEditText.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                searchEditText.visibility = View.GONE
                searchEditText.setText("")
                searchBackground.visibility = View.GONE
            }
            .start()

        isSearchExpanded = false

        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }
}