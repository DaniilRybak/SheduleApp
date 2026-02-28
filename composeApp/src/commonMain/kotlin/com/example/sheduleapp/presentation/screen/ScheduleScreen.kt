package com.example.scheduleapp.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scheduleapp.di.commonModule
import com.example.scheduleapp.presentation.ScheduleViewModel
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = koinInject()) {
    val scheduleState by viewModel.scheduleState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { viewModel.fetchSchedule() }) {
            Text("Загрузить расписание")
        }

        Spacer(modifier = Modifier.height(16.dp))

        scheduleState?.let {
            val eventCount = it.embedded?.events?.size ?: 0
            Text("Получено событий: $eventCount")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScheduleScreenPreview() {
    KoinApplication(application = { modules(commonModule) }) {
        MaterialTheme {
            ScheduleScreen()
        }
    }
}
