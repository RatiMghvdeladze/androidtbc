package com.example.androidtbc.fragments

import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtbc.data.MyApp
import com.example.androidtbc.data.UserAdapter
import com.example.androidtbc.data.UserRepository
import com.example.androidtbc.data.UserViewModel
import com.example.androidtbc.data.UserViewModelFactory
import com.example.androidtbc.databinding.FragmentUserListBinding
import com.example.androidtbc.retrofitApi.RetrofitClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class UserFragment : BaseFragment<FragmentUserListBinding>(FragmentUserListBinding::inflate) {
    private lateinit var userAdapter: UserAdapter
    private lateinit var userViewModel: UserViewModel

    override fun start() {
        initViewModelFactory()
        setUpRv()
        observers()
        setupRefreshButton()
    }

    private fun initViewModelFactory(){
        val repository = UserRepository(
            MyApp.getDatabase().userDao(),
            RetrofitClient.apiService,
            requireContext()
        )
        userViewModel = ViewModelProvider(this, UserViewModelFactory(repository))
            .get(UserViewModel::class.java)

    }

    private fun setUpRv() {
        userAdapter = UserAdapter()
        with(binding.rvUsers){
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }
    }

    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.users.collect { users ->
                    userAdapter.submitList(users)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.isLoading.collect { isLoading ->
                    binding.progressBar.isVisible = isLoading
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.error.collect { error ->
                    error?.let {
                        Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.isOnline.collect { isOnline ->
                    binding.tvNetworkStatus.text = if (isOnline) {
                        "You are online"
                    } else {
                        "You are offline"
                    }
                }
            }
        }
    }

    private fun setupRefreshButton() {
        binding.btnRefresh.setOnClickListener {
            userViewModel.refreshUsers()
        }
    }
}