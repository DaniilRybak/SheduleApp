package com.example.sheduleapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform