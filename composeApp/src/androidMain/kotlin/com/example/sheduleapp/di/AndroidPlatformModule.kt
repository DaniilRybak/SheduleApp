package com.example.sheduleapp.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.sheduleapp.storage.createScheduleDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidPlatformModule = module {
    single<DataStore<Preferences>> {
        createScheduleDataStore { androidContext() }
    }
}