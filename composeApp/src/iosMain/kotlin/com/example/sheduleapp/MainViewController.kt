package com.example.sheduleapp

import androidx.compose.ui.window.ComposeUIViewController
import com.example.scheduleapp.di.commonModule
import com.example.sheduleapp.di.iosPlatformModule
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    try {
        startKoin {
            modules(commonModule, iosPlatformModule)
        }
    } catch (_: Exception) {}
    return ComposeUIViewController {
        App()
    }
}
