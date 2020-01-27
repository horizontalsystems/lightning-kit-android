package io.horizontalsystems.lightningkit.demo.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import io.horizontalsystems.lightningkit.demo.MainActivity
import io.horizontalsystems.lightningkit.demo.R
import io.horizontalsystems.lightningkit.demo.unlock.UnlockWalletActivity

class HomeActivity : AppCompatActivity(), ErrorDialog.Listener {

    private val presenter by lazy { ViewModelProvider(this, HomeModule.Factory()).get(HomePresenter::class.java) }
    private var errorDialog: ErrorDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val homeFragmentsAdapter = HomeFragmentsAdapter(supportFragmentManager)
        val viewPager = findViewById<ViewPager>(R.id.pager)
        viewPager.adapter = homeFragmentsAdapter

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)

        presenter.onLoad()

        presenter.goToUnlockWalletLiveEvent.observe(this, Observer {
            val intent = Intent(this, UnlockWalletActivity::class.java)
            startActivity(intent)
        })

        presenter.goToMainLiveEvent.observe(this, Observer {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        })


        presenter.error.observe(this, Observer {
            if (it == null) {
                errorDialog?.dismiss()
                errorDialog = null
            } else {
                errorDialog = ErrorDialog()
                errorDialog?.setMessage(it)
                errorDialog?.show(supportFragmentManager, "dialog")
            }
        })
    }

    override fun onLogoutClick(dialog: DialogFragment) {
        presenter.logout()
    }
}

