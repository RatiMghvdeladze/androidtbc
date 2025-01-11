package com.example.androidtbc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtbc.adapters.OrderListAdapter
import com.example.androidtbc.databinding.FragmentActiveBinding

class ActiveFragment : Fragment() {
    private var _binding: FragmentActiveBinding? = null
    private val binding get() = _binding!!

    private val orderListAdapter = OrderListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRV()
        loadData()
    }

    private fun loadData() {
        val activeOrders = MainActivity.orders.filter { it.status == OrderType.ACTIVE }
        orderListAdapter.submitList(activeOrders)
    }

    private fun setUpRV() {
        binding.rvActive.apply {
            adapter = orderListAdapter
            layoutManager = LinearLayoutManager(requireContext())

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}