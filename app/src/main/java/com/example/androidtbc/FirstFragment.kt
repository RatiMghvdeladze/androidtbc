package com.example.androidtbc


import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.androidtbc.databinding.FragmentFirstBinding
import com.google.android.material.snackbar.Snackbar
import kotlin.math.abs


class FirstFragment : BaseFragment<FragmentFirstBinding>(FragmentFirstBinding::inflate) {
    private lateinit var cardAdapter: CardAdapter
    private val viewModel: MainViewModel by viewModels()

    override fun start() {
        initVP()
        loadData()
        setupListeners()
    }

    private fun setupListeners() {
        cardAdapter.longClickListener { card ->
            val position = cardAdapter.currentList.indexOf(card)
            if (position != -1) {
                showDeleteDialog(position)
            }
        }
        binding.btnAddNew.setOnClickListener{
            findNavController().navigate(R.id.action_firstFragment_to_addNewCardFragment)
        }
    }


    private fun showDeleteDialog(position: Int) {
        val bottomSheet = DeleteCardBottomSheet {
            viewModel.deleteCard(position)
            Snackbar.make(binding.root, "Card deleted successfully!", Snackbar.LENGTH_SHORT).show()
            loadData()
        }
        bottomSheet.show(childFragmentManager, "DeleteCardBottomSheet")
    }

    private fun initVP() {
        cardAdapter = CardAdapter()

        binding.vp2.apply {
            adapter = cardAdapter
            offscreenPageLimit = 1

            setPadding(30, 0, 180, 0)
            clipToPadding = false
            clipChildren = false

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (position == (cardAdapter.itemCount - 1)) {
                        setPadding(30, 0, 30, 0)
                    } else {
                        setPadding(30, 0, 180, 0)
                    }
                }
            })

            setPageTransformer { page, position ->
                page.apply {
                    when {
                        position > -1f && position < 1f -> {
                            val scaleFactor = 0.85f + (1f - abs(position)) * 0.15f
                            scaleY = scaleFactor
                            scaleX = scaleFactor
                            alpha = 1f
                        }
                        else -> {
                            scaleY = 0.85f
                            scaleX = 0.85f
                        }
                    }
                }
            }
        }

        (binding.vp2.parent as? ViewGroup)?.clipChildren = false
    }
    private fun loadData() {
        val cards = viewModel.getCards()
        cardAdapter.submitList(cards)
    }
}