package com.example.scheduleapp.di

import com.example.scheduleapp.data.local.PrefsRepository
import com.example.scheduleapp.data.local.PrefsRepositoryImpl
import com.example.scheduleapp.data.local.SettingsRepositoryImpl
import com.example.scheduleapp.data.remote.ScheduleApi
import com.example.scheduleapp.data.repository.ScheduleRepository
import com.example.scheduleapp.data.repository.ScheduleRepositoryImpl
import com.example.scheduleapp.data.repository.ScheduleEntryRepositoryImpl
import com.example.scheduleapp.data.repository.WeekScheduleRepositoryImpl
import com.example.scheduleapp.domain.repository.ScheduleEntryRepository
import com.example.scheduleapp.domain.repository.SettingsRepository
import com.example.scheduleapp.domain.repository.WeekScheduleRepository
import com.example.scheduleapp.domain.usecase.GetScheduleEntryUseCase
import com.example.scheduleapp.domain.usecase.GetSettingsUseCase
import com.example.scheduleapp.domain.usecase.GetWeekScheduleUseCase
import com.example.scheduleapp.domain.usecase.SearchScheduleEntriesUseCase
import com.example.scheduleapp.domain.usecase.UpdateSettingsUseCase
import com.example.scheduleapp.presentation.ScheduleViewModel
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
    }
    singleOf(::ScheduleApi)
    single<ScheduleRepository> { ScheduleRepositoryImpl(get()) }
}

val dataModule = module {
    single<SettingsRepository> { SettingsRepositoryImpl() }
    single<PrefsRepository> { PrefsRepositoryImpl() }
    single<ScheduleEntryRepository> { ScheduleEntryRepositoryImpl() }
    single<WeekScheduleRepository> { WeekScheduleRepositoryImpl(get()) }
}

val useCaseModule = module {
    single { GetSettingsUseCase(get()) }
    single { UpdateSettingsUseCase(get()) }
    single { GetScheduleEntryUseCase(get()) }
    single { GetWeekScheduleUseCase(get()) }
    single { SearchScheduleEntriesUseCase() }
}

val viewModelModule = module {
    singleOf(::ScheduleViewModel)
}

val commonModule = module {
    includes(networkModule, dataModule, useCaseModule, viewModelModule)
}
