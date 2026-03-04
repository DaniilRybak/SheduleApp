package com.example.sheduleapp

import androidx.compose.ui.window.ComposeUIViewController
import com.example.scheduleapp.di.commonModule
import com.example.scheduleapp.presentation.screen.ScheduleScreen
import com.example.sheduleapp.ui.theme.ScheduleAppTheme
import org.koin.compose.KoinApplication
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController =
    ComposeUIViewController {
        KoinApplication(
            application = { modules(commonModule) }
        ) {
            ScheduleAppTheme {
                ScheduleScreen()
            }
        }
    }