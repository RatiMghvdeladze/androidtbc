package com.example.androidtbc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtbc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var clothingAdapter: ClothingAdapter

    private val allItems = listOf(
        Item(R.drawable.item1, "Belt suit blazer", 120, CategoryType.Party),
        Item(R.drawable.item2, "Belt suit blazer", 120, CategoryType.Camping),
        Item(R.drawable.item3, "Belt suit blazer", 120, CategoryType.Category1),
        Item(R.drawable.item4, "Belt suit blazer", 120, CategoryType.Category2),

        Item(R.drawable.item4, "Belt suit blazer", 120, CategoryType.Category2),
        Item(R.drawable.item3, "Belt suit blazer", 120, CategoryType.Category1),
        Item(R.drawable.item2, "Belt suit blazer", 120, CategoryType.Camping),
        Item(R.drawable.item1, "Belt suit blazer", 120, CategoryType.Party)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRvCategories()
        setUpRvItems()
    }

    private fun setUpRvCategories() {
        categoryAdapter = CategoryAdapter(CategoryType.entries)
        categoryAdapter.onClick {
            filterItems(it)
        }
        binding.rvCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategory.adapter = categoryAdapter
    }

    private fun setUpRvItems() {
        clothingAdapter = ClothingAdapter()
        binding.rvItems.layoutManager = GridLayoutManager(this, 2)
        binding.rvItems.adapter = clothingAdapter
        filterItems(CategoryType.All)
    }

    private fun filterItems(category: CategoryType) {
        val filteredItems = if (category == CategoryType.All) allItems else allItems.filter { it.categoryType == category }
        clothingAdapter.updateList(filteredItems)
    }
}
