package com.alcorp.githubuser.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.alcorp.githubuser.R
import com.alcorp.githubuser.fragment.PreferenceFragment

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        setToolbar()

        supportFragmentManager.beginTransaction().add(R.id.setting_layout, PreferenceFragment()).commit()
    }

    private fun setToolbar() {
        supportActionBar?.title = resources.getString(R.string.txt_setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}