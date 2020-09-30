package org.jakewood.z2fa_receiver

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun requestSMSPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECEIVE_SMS),
                101
            )
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val button = findPreference<Preference>("grantSMS");
            if (button != null) {
                println("Found the button!")
                button.setOnPreferenceClickListener { _: Preference ->
                    requestSMS()
                    true
                }
            }
            else {
                println("DIDNT FIND TEH BUTTON!")
            }
        }

        fun requestSMS() {
            val act = activity
            if (act is SettingsActivity) {
                act.requestSMSPermissions()
            }
        }
    }
}