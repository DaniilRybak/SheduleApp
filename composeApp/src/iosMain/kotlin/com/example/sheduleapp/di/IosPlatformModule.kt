package com.example.sheduleapp.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.sheduleapp.storage.createScheduleDataStore
import org.koin.dsl.module

val iosPlatformModule = module {
    single<DataStore<Preferences>> {
        createScheduleDataStore { }
    }
}
