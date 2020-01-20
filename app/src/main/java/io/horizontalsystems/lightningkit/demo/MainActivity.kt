package io.horizontalsystems.lightningkit.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isWalletSetUp()) {

        } else {
            val intent = Intent(this, GuestActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun isWalletSetUp(): Boolean {
        return false
    }
}
