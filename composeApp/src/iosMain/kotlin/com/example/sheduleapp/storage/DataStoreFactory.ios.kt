package com.example.sheduleapp.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun createScheduleDataStore(contextProvider: () -> Any): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath {
        val documentDir = NSFileManager.defaultManager.URLForDirectory(
            NSDocumentDirectory,
            NSUserDomainMask,
            null,
            true,
            null
        ) ?: error("Cannot get document directory")

        @OptIn(BetaInteropApi::class)
        "${documentDir.path}/schedule_prefs.preferences_pb".toPath()
    }