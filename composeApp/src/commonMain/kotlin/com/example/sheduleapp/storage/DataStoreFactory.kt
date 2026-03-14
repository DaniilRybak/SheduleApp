package com.example.sheduleapp.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect fun createScheduleDataStore(contextProvider: () -> Any): DataStore<Preferences>
