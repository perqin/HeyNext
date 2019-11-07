package com.perqin.heynext

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settingsFrameLayout, AppSettingsFragment())
            .commit()

        grantButton.setOnClickListener {
            startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    private fun checkPermission() {
        val cn = ComponentName(this, BandNotificationListenerService::class.java)
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        val granted = flat != null && flat.contains(cn.flattenToString())
        permissionBannerConstraintLayout.visibility = if (granted) View.GONE else View.VISIBLE
        (supportFragmentManager.findFragmentById(R.id.settingsFrameLayout) as? AppSettingsFragment)?.onPermissionChanged(granted)
        if (granted) {
            startService(Intent(this, BandNotificationListenerService::class.java))
        }
    }

    companion object {
        private val ACTION_NOTIFICATION_LISTENER_SETTINGS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
        } else {
            "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
        }
    }
}
