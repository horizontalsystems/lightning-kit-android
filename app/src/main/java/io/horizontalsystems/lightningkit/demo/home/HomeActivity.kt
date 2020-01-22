package io.horizontalsystems.lightningkit.demo.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import io.horizontalsystems.lightningkit.demo.R
import io.horizontalsystems.lightningkit.demo.send.SendActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val homeFragmentsAdapter = HomeFragmentsAdapter(supportFragmentManager)
        val viewPager = findViewById<ViewPager>(R.id.pager)
        viewPager.adapter = homeFragmentsAdapter

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)
        val sendButton = findViewById<Button>(R.id.button)
        sendButton.setOnClickListener(View.OnClickListener() {
            val intent = Intent(this, SendActivity::class.java)
            startActivity(intent)
            finish()
        })

        val presenter = ViewModelProvider(this, HomeModule.Factory()).get(HomePresenter::class.java)
        presenter.onLoad()

        presenter.channels.observe(this, Observer {
            channelsAdapter.updateChannels(it)
        })
    }
}

