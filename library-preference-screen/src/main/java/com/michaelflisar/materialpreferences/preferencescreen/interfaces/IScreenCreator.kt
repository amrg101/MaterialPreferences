package com.michaelflisar.materialpreferences.preferencescreen.interfaces

import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.michaelflisar.materialpreferences.preferencescreen.PreferenceScreen
import com.michaelflisar.materialpreferences.preferencescreen.PreferenceScreenState

interface IScreenCreator : Parcelable {
    fun createScreen(
        activity: AppCompatActivity,
        savedInstanceState: Bundle?,
        state: PreferenceScreenState?,
        updateTitle: (title: String) -> Unit
    ): PreferenceScreen
}