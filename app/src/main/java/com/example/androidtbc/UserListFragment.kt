package com.example.androidtbc

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtbc.databinding.ActivityMainBinding.inflate
import kotlinx.coroutines.launch

class UserListFragment : BaseFragment<FragmentUserListBinding>(FragmentUserListBinding::inflate) {

    private lateinit var userAdapter: UserAdapter
    private lateinit var userViewModel: UserViewModel

    override fun start() {
        val repository = UserRepository(RetrofitClient.apiService, MyApp.dataBase.userDao())
        userViewModel = ViewModelProvider(this, UserViewModelFactory(repository))
            .get(UserViewModel::class.java)

        userAdapter = UserAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = userAdapter

        lifecycleScope.launch {
            userViewModel.users.collect { users ->
                userAdapter.submitList(users)
            }
        }

        binding.refreshButton.setOnClickListener {
            userViewModel.refreshUsers()
        }
    }
}
