package io.horizontalsystems.lightningkit.demo.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import io.horizontalsystems.lightningkit.demo.R

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val homeFragmentsAdapter = HomeFragmentsAdapter(supportFragmentManager)
        val viewPager = findViewById<ViewPager>(R.id.pager)
        viewPager.adapter = homeFragmentsAdapter

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)
    }
}

