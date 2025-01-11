package com.example.androidtbc.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fa: FragmentActivity, private val lst: List<Fragment>) :
    FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = lst.size

    override fun createFragment(position: Int): Fragment = lst[position]

}