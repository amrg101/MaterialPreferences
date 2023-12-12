package com.michaelflisar.materialpreferences.demo.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.michaelflisar.materialpreferences.demo.DemoSettings
import com.michaelflisar.materialpreferences.demo.settings.DemoSettingsModel
import com.michaelflisar.materialpreferences.preferencescreen.*
import com.michaelflisar.materialpreferences.preferencescreen.databinding.PreferenceActivitySettingsBinding
import com.michaelflisar.materialpreferences.preferencescreen.enums.NoIconVisibility
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CustomSettingsActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, CustomSettingsActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    lateinit var binding: PreferenceActivitySettingsBinding
    lateinit var preferenceScreen: PreferenceScreen
    private var savedState: PreferenceScreenState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(if (DemoSettingsModel.darkTheme.value) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)

        binding = PreferenceActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ---------------
        // set up settings
        // ---------------

        preferenceScreen = initSettings(savedInstanceState)
        lifecycleScope.launch{
            repeat(10000) {
                delay(5000)
                savedState = preferenceScreen.lastState
                preferenceScreen = initSettings(savedInstanceState)
                Log.d("","refreshing settings..")
            }
        }
    }

    private fun initSettings(savedInstanceState: Bundle?): PreferenceScreen {

        // global settings to avoid
        // INFO:
        // some global settings can be overwritten per preference (e.g. bottomSheet yes/no)
        // other global settings can only be changed globally

        // following is optional!
        PreferenceScreenConfig.apply {
            bottomSheet = false                             // default: false
            maxLinesTitle = 1                               // default: 1
            maxLinesSummary = 3                             // default: 3
            noIconVisibility = NoIconVisibility.Invisible   // default: Invisible
            alignIconsWithBackArrow = false                 // default: false
        }

        // -----------------
        // 1) create screen(s)
        // -----------------

        val screen = screen {

            // set up screen
            state = savedState
            savedInstanceSate = savedInstanceState
            onScreenChanged = { subScreenStack, stateRestored ->
                val breadcrumbs =
                    subScreenStack.joinToString(" > ") { it.title.get(this@CustomSettingsActivity) }
                supportActionBar?.subtitle = breadcrumbs
            }

            // -----------------
            // create settings screen
            // -----------------

            DemoSettings.createSettingsScreen(this, this@CustomSettingsActivity)

        }
        screen.bind(binding.rvSettings, this)
        return screen
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        preferenceScreen.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (preferenceScreen.onBackPressed()) {
            return
        }
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        if (!preferenceScreen.onBackPressed()) {
            finish()
        }
        return true
    }
}