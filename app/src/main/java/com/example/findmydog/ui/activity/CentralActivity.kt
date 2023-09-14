package com.example.findmydog.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.findmydog.R
import com.example.findmydog.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth


class CentralActivity : AppCompatActivity() {


    private lateinit var scanCode: TextView
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_central)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.action_bar_layout)
        /*supportActionBar?.elevation = 0f*/
        setUpTabs()
        scanCode=findViewById(R.id.scanCode)

        scanCode.setOnClickListener {
            val intent= Intent(this, QrScanActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setUpTabs() {
        val viewPager: ViewPager = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_dog_house)
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.ic_dog)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_signOut ->{
                FirebaseAuth.getInstance().signOut()
                val intent=Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }


}