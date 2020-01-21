package io.horizontalsystems.lightningkit.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.horizontalsystems.lightningkit.demo.core.App
import io.horizontalsystems.lightningkit.demo.home.HomeActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (App.instance.isWalletSetup()) {
            App.instance.initLightningKit()

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {

            val intent = Intent(this, GuestActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
