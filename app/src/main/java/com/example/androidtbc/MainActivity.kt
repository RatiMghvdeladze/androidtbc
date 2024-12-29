package com.example.androidtbc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.androidtbc.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var orders = mutableListOf(
        Order("1524", "IK287368838", 110, 2, parseDate("13/05/2021"), StatusType.PENDING),
        Order("1525", "IK287368897", 100, 2,parseDate("12/05/2021"), StatusType.PENDING),
        Order("1526", "IK287368820",400 , 5, parseDate("10/05/2021"), StatusType.PENDING),

        Order("1527", "IK287368838", 110, 2, parseDate("13/05/2021"), StatusType.PENDING),
        Order("1528", "IK287368897", 100, 2,parseDate("12/05/2021"), StatusType.PENDING),
        Order("1529", "IK287368820",400 , 5, parseDate("10/05/2021"), StatusType.PENDING),

        Order("1530", "IK287368838", 110, 2, parseDate("13/05/2021"), StatusType.PENDING),
        Order("1531", "IK287368897", 100, 2,parseDate("12/05/2021"), StatusType.PENDING),
        Order("1532", "IK287368820",400 , 5, parseDate("10/05/2021"), StatusType.PENDING)
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpNavigation()
        switchFragment(PendingFragment())
        binding.btnPending.isSelected = true
    }

    private fun setUpNavigation() {
        binding.apply {
            btnPending.setOnClickListener{
                switchFragment(PendingFragment())
                updateSelection(btnPending.id)
            }
            btnDelivered.setOnClickListener {
                switchFragment(DeliveredFragment())
                updateSelection(btnDelivered.id)
            }
            btnCancelled.setOnClickListener{
                switchFragment(CanceledFragment())
                updateSelection(btnCancelled.id)
            }
        }
    }


    private fun updateSelection(selectedId: Int){
        binding.apply {
            btnPending.setSelected(btnPending.id == selectedId)
            btnDelivered.setSelected(btnDelivered.id == selectedId)
            btnCancelled.setSelected(btnCancelled.id == selectedId)
        }

    }

    private fun switchFragment(fragment: Fragment) {
        val bundle = Bundle().apply {
            putParcelableArrayList("orders", ArrayList(orders))
        }
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }


    fun updateOrderStatus(orderId: String, newStatus: StatusType) {
        val order = orders.find { it.orderId == orderId }
        order?.let {
            val updatedOrder = it.copy(orderStatus = newStatus)
            val index = orders.indexOf(it)
            if (index != -1) {
                orders[index] = updatedOrder
            }
        }
    }

    private fun parseDate(dateString: String): Long {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.parse(dateString)?.time ?: 0L
    }



}
