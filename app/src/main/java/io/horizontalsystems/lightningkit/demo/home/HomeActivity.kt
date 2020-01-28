package io.horizontalsystems.lightningkit.demo.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import io.horizontalsystems.lightningkit.demo.MainActivity
import io.horizontalsystems.lightningkit.demo.R

class HomeActivity : AppCompatActivity(), ErrorDialog.Listener, UnlockWalletDialog.Listener {

    private val presenter by lazy { ViewModelProvider(this, HomeModule.Factory()).get(HomePresenter::class.java) }
    private var errorDialog: ErrorDialog? = null
    private var unlockWalletDialog: UnlockWalletDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val unlockingGroup = findViewById<Group>(R.id.unlockingGroup)

        val homeFragmentsAdapter = HomeFragmentsAdapter(supportFragmentManager)
        val viewPager = findViewById<ViewPager>(R.id.pager)
        viewPager.adapter = homeFragmentsAdapter

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)

        presenter.onLoad()

        presenter.goToMainLiveEvent.observe(this, Observer {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        })

        presenter.error.observe(this, Observer { errorMessage ->
            if (errorMessage != null) {
                errorDialog = ErrorDialog()
                errorDialog?.setMessage(errorMessage)
                errorDialog?.show(supportFragmentManager, "ErrorDialog")
            } else {
                errorDialog?.dismiss()
                errorDialog = null
            }
        })

        presenter.toggleUnlockWalletDialog.observe(this, Observer { show ->
            if (show) {
                unlockWalletDialog = UnlockWalletDialog()
                unlockWalletDialog?.show(supportFragmentManager, "UnlockWalletDialog")
            } else {
                unlockWalletDialog?.dismiss()
                unlockWalletDialog = null
            }
        })

        presenter.unlockError.observe(this, Observer {
            unlockWalletDialog?.setPasswordError(it.message)
        })

        presenter.unlockingProgress.observe(this, Observer {
            unlockingGroup.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when {
            item.itemId == R.id.logout -> {
                presenter.logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onUnlockClick(dialog: DialogFragment, password: String) {
        presenter.unlock(password)
    }

    override fun onLogoutClick(dialog: DialogFragment) {
        presenter.logout()
    }
}

