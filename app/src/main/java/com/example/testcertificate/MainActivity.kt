package com.example.testcertificate

import android.view.View
import android.os.Bundle
import android.widget.Toast
import android.widget.Button
import android.widget.TextView
import android.content.Context
import android.security.KeyChain
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn_sign).setOnClickListener { sign() }
    }

    override fun onResume() {
        super.onResume()
        configurePreGrantIfNecessary(canEnable = BuildConfig.CAN_ENABLE_PRE_GRANT)
    }

    private fun configurePreGrantIfNecessary(canEnable: Boolean) {
        enableViewPreGrantIfNecessary(canEnable)
        hydratePreGrantIfNecessary()
    }

    private fun enableViewPreGrantIfNecessary(canEnable: Boolean) {
        if (canEnable) {
            findViewById<TextView>(R.id.tv_label).visibility = View.VISIBLE
            findViewById<TextView>(R.id.tv_content).visibility = View.VISIBLE
            findViewById<TextView>(R.id.btn_sign).visibility = View.GONE
        } else {
            findViewById<TextView>(R.id.tv_label).visibility = View.GONE
            findViewById<TextView>(R.id.tv_content).visibility = View.GONE
            findViewById<TextView>(R.id.btn_sign).visibility = View.VISIBLE
        }
    }

    private fun hydratePreGrantIfNecessary() {
        val context = this
        SignAndVerifyTask(this) { msgId, args ->
            context.findViewById<TextView>(R.id.tv_content).text = getString(msgId, *args)
        }.execute(BuildConfig.CERT_ALIAS)
    }

    private fun sign() {
        KeyChain.choosePrivateKeyAlias(
            this,
            { alias ->
                if (alias == null) {
                    // No value was chosen.
                    runOnUiThread { toast("Unable to sign the app") }
                } else {
                    SignAndVerifyTask(this) { msgId, args ->
                        toast(msgId, *args)
                    }.execute(alias)
                }
            }, null, null, null, BuildConfig.CERT_ALIAS
        )
    }

    private fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun Context.toast(msgId: Int, vararg args: Any) {
        toast(getString(msgId, *args))
    }
}
