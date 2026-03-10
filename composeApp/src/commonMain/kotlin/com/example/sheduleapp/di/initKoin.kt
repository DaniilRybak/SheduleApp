package com.example.sheduleapp.di

import com.example.scheduleapp.di.commonModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(commonModule)
    }
}
