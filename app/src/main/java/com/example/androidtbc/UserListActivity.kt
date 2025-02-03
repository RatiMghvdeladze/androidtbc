package com.example.androidtbc

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch

class UserListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserListBinding
    private val viewModel: UserListViewModel by viewModels()
    private val userAdapter = UserAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(this@UserListActivity)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.users.collect { users ->
                userAdapter.submitList(users)
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.isVisible = isLoading
            }
        }

        lifecycleScope.launch {
            viewModel.isOnline.collect { isOnline ->
                binding.statusText.text = if (isOnline) "You are online" else "You are offline"
            }
        }
    }
}