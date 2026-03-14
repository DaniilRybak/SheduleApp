package com.example.sheduleapp.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

actual fun createScheduleDataStore(contextProvider: () -> Any): DataStore<Preferences> {
    val context = contextProvider() as Context
    val path = context.filesDir.resolve("schedule_prefs.preferences_pb").absolutePath
    return PreferenceDataStoreFactory.createWithPath {
        path.toPath()
    }
}
