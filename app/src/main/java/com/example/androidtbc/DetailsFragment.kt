package com.example.androidtbc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.androidtbc.databinding.FragmentDetailsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = arguments?.getParcelable<Order>("order") ?: return
        setUpDetails(order)
    }

    private fun setUpDetails(order: Order) {
        binding.apply {
            tvOrderId.text = "Order #${order.orderId}"
            tvTrackingNumber.text = "Tracking number: ${order.trackingNumber}"

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(Date(order.dateInMillis))

            tvDate.text = formattedDate

            tvQuantity.text = "Quantity: ${order.quantity}"
            tvSubtotal.text = "Subtotal: $${order.subtotal}"
            tvStatus.text = "Status: ${order.orderStatus.name}"

            if (order.orderStatus == StatusType.PENDING) {
                displayButtons(order)
            } else {
                hideButtons()
            }

            btnBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun displayButtons(order: Order) {
        binding.apply {
            btnMarkDelivered.visibility = View.VISIBLE
            btnCancel.visibility = View.VISIBLE

            btnMarkDelivered.setOnClickListener {
                (activity as? MainActivity)?.updateOrderStatus(order.orderId, StatusType.DELIVERED)
                parentFragmentManager.popBackStack()
            }

            btnCancel.setOnClickListener {
                (activity as? MainActivity)?.updateOrderStatus(order.orderId, StatusType.CANCELED)
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun hideButtons() {
        binding.apply {
            btnMarkDelivered.visibility = View.GONE
            btnCancel.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}