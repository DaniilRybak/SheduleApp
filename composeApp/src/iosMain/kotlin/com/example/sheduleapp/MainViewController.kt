package com.example.sheduleapp

import androidx.compose.ui.window.ComposeUIViewController
import com.example.scheduleapp.di.commonModule
import com.example.scheduleapp.presentation.screen.ScheduleScreen
import org.koin.compose.KoinApplication
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController =
    ComposeUIViewController {
        KoinApplication(
            application = { modules(commonModule) }
        ) {
            ScheduleScreen()
        }
    }