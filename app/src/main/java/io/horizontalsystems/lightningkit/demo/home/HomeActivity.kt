package io.horizontalsystems.lightningkit.demo.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import io.horizontalsystems.lightningkit.ILndNode
import io.horizontalsystems.lightningkit.demo.R
import io.horizontalsystems.lightningkit.demo.core.App
import io.horizontalsystems.lightningkit.demo.unlock.UnlockWalletActivity
import io.reactivex.android.schedulers.AndroidSchedulers

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val homeFragmentsAdapter = HomeFragmentsAdapter(supportFragmentManager)
        val viewPager = findViewById<ViewPager>(R.id.pager)
        viewPager.adapter = homeFragmentsAdapter

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)


        val subscribe = App.lightningKit.statusObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it == ILndNode.Status.LOCKED) {
                    unlockWallet()
                }

                Log.e("AAA", "Status updated to: ${it.name}")
            }, {
                Log.e("AAA", "Error on status updated", it)
            })
    }

    private fun unlockWallet() {
        val intent = Intent(this, UnlockWalletActivity::class.java)
        startActivity(intent)
    }
}

