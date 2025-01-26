package com.example.androidtbc.fragments

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.User
import com.example.androidtbc.UsersAdapter
import com.example.androidtbc.databinding.FragmentHomeBinding
import com.example.androidtbc.viewModels.HomeViewModel
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val homeViewModel: HomeViewModel by viewModels()
    private val usersAdapter = UsersAdapter()

    private val args: HomeFragmentArgs by navArgs()

    override fun start() {
        setUpRv()
        observer()
        setupListener()
    }

    private fun setupListener(){
        with(binding){
            btnGoToProfile.setOnClickListener{
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment(args.email))
            }
        }
    }

    private fun setUpRv() {
        with(binding.rvUsers) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = usersAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                    ) {
                        homeViewModel.fetchUsers()
                    }
                }
            })
        }
    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.users.collect { userList ->
                    updateAdapter(userList)
                }
            }
        }
    }


    private fun updateAdapter(userList: List<User>) {
        usersAdapter.submitList(userList)
    }
}
