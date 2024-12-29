package com.example.androidtbc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtbc.databinding.FragmentDeliveredBinding

class DeliveredFragment : Fragment() {
    private var _binding: FragmentDeliveredBinding? = null
    private val binding get() = _binding!!

    private val orderAdapter = OrderAdapter {
        navigateToDetails(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveredBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadOrders()
    }

    private fun setupRecyclerView() {
        binding.rvDelivered.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDelivered.adapter = orderAdapter
    }

    private fun loadOrders() {
        val orders = arguments?.getParcelableArrayList<Order>("orders") ?: return
        orderAdapter.submitList(orders.filter {
            it.orderStatus == StatusType.DELIVERED }
        )
    }

    private fun navigateToDetails(order: Order) {
        val detailsFragment = DetailsFragment().apply {
            arguments = Bundle().apply {
                putParcelable("order", order)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, detailsFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
