package com.perqin.heynext

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class AppSettingsFragment : PreferenceFragmentCompat() {
    private lateinit var runningActionPreference: Preference
    private lateinit var ridingActionPreference: Preference
    private lateinit var v2ExerciseActionPreference: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_app, null)
        runningActionPreference = findPreference(getString(R.string.spk_runningAction))!!
        runningActionPreference.summaryProvider = DisableSupportedSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance(), requireContext(), R.string.text_v1Required)
        ridingActionPreference = findPreference(getString(R.string.spk_ridingAction))!!
        ridingActionPreference.summaryProvider = DisableSupportedSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance(), requireContext(), R.string.text_v1Required)
        v2ExerciseActionPreference = findPreference(getString(R.string.spk_v2ExerciseAction))!!
        v2ExerciseActionPreference.summaryProvider = DisableSupportedSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance(), requireContext(), R.string.text_v2Required)
    }

    override fun onResume() {
        super.onResume()
        // Check HeyPlus app version
        val version = try {
            requireContext().packageManager.getPackageInfo(PN_HEY_PLUS, 0).versionName.split('.').firstOrNull()?.toIntOrNull()?:0
        } catch (e: PackageManager.NameNotFoundException) {
            0
        }
        runningActionPreference.isEnabled = version == 1
        ridingActionPreference.isEnabled = version == 1
        v2ExerciseActionPreference.isEnabled = version == 2
    }

    fun onPermissionChanged(granted: Boolean) {
        preferenceScreen.isEnabled = granted
    }
}

private class DisableSupportedSummaryProvider<T : Preference>(
    private val delegate: Preference.SummaryProvider<T>,
    private val context: Context,
    @StringRes private val summaryOnDisabledRes: Int
): Preference.SummaryProvider<T> by delegate {
    override fun provideSummary(preference: T): CharSequence {
        return if (preference.isEnabled) {
            delegate.provideSummary(preference)
        } else {
            context.getString(summaryOnDisabledRes)
        }
    }
}
