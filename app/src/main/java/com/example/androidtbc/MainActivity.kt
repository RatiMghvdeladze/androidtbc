package com.example.androidtbc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtbc.adapters.ViewPagerAdapter
import com.example.androidtbc.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val fragmentList = listOf(ActiveFragment(), CompletedFragment())
    private val tabTitles: List<String> = listOf("Active", "Completed")


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
        initVP()
    }

    private fun initVP() {
        val adapter = ViewPagerAdapter(this, fragmentList)
        binding.vp2.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.vp2) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }


    companion object {
        val orders: List<Order> = listOf(
            Order(
                color = "Black",
                title = "Wooden Chair",
                image = R.drawable.school_chair,
                quantity = 3,
                price = 140.00,
                status = OrderType.COMPLETED
            ),
            Order(
                color = "Brown",
                title = "Modern Wingback",
                image = R.drawable.black_chair,
                quantity = 2,
                price = 280.00,
                status = OrderType.COMPLETED
            ),
            Order(
                color = "Black",
                title = "Wooden Chair",
                image = R.drawable.school_chair,
                quantity = 3,
                price = 140.00,
                status = OrderType.ACTIVE
            ),
            Order(
                color = "Brown",
                title = "Modern Wingback",
                image = R.drawable.black_chair,
                quantity = 2,
                price = 280.00,
                status = OrderType.ACTIVE
            ),
            Order(
                color = "Black",
                title = "Mirrored Reflector",
                image = R.drawable.mirrored_reflector,
                quantity = 1,
                price = 90.00,
                status = OrderType.ACTIVE
            ),
            Order(
                color = "Black",
                title = "Mirrored Reflector",
                image = R.drawable.mirrored_reflector,
                quantity = 1,
                price = 90.00,
                status = OrderType.COMPLETED
            ),
            Order(
                color = "Blue Grey",
                title = "Laswon Chair",
                image = R.drawable.lawson_chair,
                quantity = 1,
                price = 120.00,
                status = OrderType.COMPLETED
            ),
            Order(
                color = "Blue Grey",
                title = "Laswon Chair",
                image = R.drawable.lawson_chair,
                quantity = 1,
                price = 120.00,
                status = OrderType.ACTIVE
            ),
            Order(
                color = "Black",
                title = "Wooden Chair",
                image = R.drawable.school_chair,
                quantity = 3,
                price = 140.00,
                status = OrderType.COMPLETED
            ),
            Order(
                color = "Brown",
                title = "Modern Wingback",
                image = R.drawable.black_chair,
                quantity = 2,
                price = 280.00,
                status = OrderType.COMPLETED
            ),
            Order(
                color = "Black",
                title = "Wooden Chair",
                image = R.drawable.school_chair,
                quantity = 3,
                price = 140.00,
                status = OrderType.ACTIVE
            ),
            Order(
                color = "Brown",
                title = "Modern Wingback",
                image = R.drawable.black_chair,
                quantity = 2,
                price = 280.00,
                status = OrderType.ACTIVE
            ),
            Order(
                color = "Black",
                title = "Mirrored Reflector",
                image = R.drawable.mirrored_reflector,
                quantity = 1,
                price = 90.00,
                status = OrderType.ACTIVE
            ),
            Order(
                color = "Black",
                title = "Mirrored Reflector",
                image = R.drawable.mirrored_reflector,
                quantity = 1,
                price = 90.00,
                status = OrderType.COMPLETED
            ),
            Order(
                color = "Blue Grey",
                title = "Laswon Chair",
                image = R.drawable.lawson_chair,
                quantity = 1,
                price = 120.00,
                status = OrderType.COMPLETED
            ),
            Order(
                color = "Blue Grey",
                title = "Laswon Chair",
                image = R.drawable.lawson_chair,
                quantity = 1,
                price = 120.00,
                status = OrderType.ACTIVE
            ),

            )
    }
}
