package com.example.androidtbc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.androidtbc.databinding.FragmentSetupBinding


class SetupFragment : Fragment() {
    private var _binding: FragmentSetupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
    }


    private fun setUp(){
        binding.btnStartGame.setOnClickListener {
            val selectedSize = when (binding.sizeRadioGroup.checkedRadioButtonId) {
                R.id.rb3x3 -> 3
                R.id.rb4x4 -> 4
                R.id.rb5x5 -> 5
                else -> 3
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, GameFragment(selectedSize))
                .addToBackStack(null)
                .commit()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
