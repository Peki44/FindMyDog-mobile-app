package com.example.findmydog.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.findmydog.ui.activity.ProfileActivity
import com.example.findmydog.ui.activity.QrScanActivity
import com.example.findmydog.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val bind = FragmentHomeBinding.inflate(layoutInflater)
        bind.nextActivity.setOnClickListener {
            val intent = Intent(this@HomeFragment.requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
        /*bind.scanCode.setOnClickListener {
            val intentScan = Intent(this@HomeFragment.requireContext(), QrScanActivity::class.java)
            startActivity(intentScan)
        }*/
        return bind.root
    }

}