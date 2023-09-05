package com.example.kelineyt.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kelineyt.R
import com.example.kelineyt.adapters.HomeViewpagerAdapter
import com.example.kelineyt.databinding.FragmentHomeBinding
import com.example.kelineyt.fragments.categories.AccessoryFragment
import com.example.kelineyt.fragments.categories.ChairFragment
import com.example.kelineyt.fragments.categories.CupboardFragment
import com.example.kelineyt.fragments.categories.FurnitureFragment
import com.example.kelineyt.fragments.categories.MainCategoryFragment
import com.example.kelineyt.fragments.categories.TableFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoriesFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
            ChairFragment(),
            CupboardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment()
        )

        val viewPager2Adapter = HomeViewpagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.viewpagerHome.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout, binding.viewpagerHome) { tab, position ->
            tab.text = when(position) {
                0 -> "Main"
                1 -> "Chair"
                2 -> "Cupboard"
                3 -> "Table"
                4 -> "Accessory"
                5 -> "Furniture"
                else -> ""
            }
        }.attach()
    }
}