package com.perqin.heynext

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class AppSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_app, null)
    }

    fun onPermissionChanged(granted: Boolean) {
        preferenceScreen.isEnabled = granted
    }
}