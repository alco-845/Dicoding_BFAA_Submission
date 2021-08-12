package com.alcorp.githubuser.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.preference.SwitchPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.alcorp.githubuser.R
import com.alcorp.githubuser.helper.AlarmReceiver

class PreferenceFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener,
    Preference.OnPreferenceClickListener {
    private lateinit var REMIND: String

    private lateinit var languagePreference: PreferenceScreen
    private lateinit var remindPreference: SwitchPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference)
        init()
        setSummaries()
    }

    private fun init() {
        REMIND = resources.getString(R.string.key_reminder)

        languagePreference = findPreference<PreferenceScreen>(resources.getString(R.string.key_language)) as PreferenceScreen
        remindPreference = findPreference<SwitchPreference>(resources.getString(R.string.key_reminder)) as SwitchPreference

        languagePreference.onPreferenceClickListener = this
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == REMIND){
            val alarmReceiver = AlarmReceiver()
            if (remindPreference.isChecked){
                alarmReceiver.setNotification(context, "09:00")
            } else {
                alarmReceiver.cancelNotification(context)
            }
            remindPreference.isChecked = sharedPreferences.getBoolean(REMIND, true)
        }
    }

    private fun setSummaries() {
        val shared = preferenceManager.sharedPreferences
        remindPreference.isChecked = shared.getBoolean(REMIND, true)
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        if (preference == languagePreference){
            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(intent)
        }
        return true
    }

}