package com.example.androidtbc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtbc.adapters.OrderListAdapter
import com.example.androidtbc.databinding.FragmentCompletedBinding

class CompletedFragment : Fragment() {
    private var _binding: FragmentCompletedBinding? = null
    private val binding get() = _binding!!

    private val orderListAdapter = OrderListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompletedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        setUpRV()

    }

    private fun loadData() {
        val completedOrders = MainActivity.orders.filter{it.status == OrderType.COMPLETED}
        orderListAdapter.submitList(completedOrders)
    }

    private fun setUpRV() {
        binding.rvCompleted.apply{
            adapter = orderListAdapter
            layoutManager = LinearLayoutManager(requireContext())

        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}