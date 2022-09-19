package com.example.findmydog.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.findmydog.ui.fragment.DogsFragment
import com.example.findmydog.ui.fragment.HomeFragment


class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private val fragments = arrayOf(
        HomeFragment.newInstance(),
        DogsFragment.newInstance()
    )

    private val titles = arrayOf("Home", "Dogs")

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }

    override fun getCount(): Int {
        return fragments.size;
    }

}