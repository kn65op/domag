package com.example.domag2.uitests.common

import android.content.Intent
import androidx.test.runner.intercepting.SingleActivityFactory
import com.example.domag2.MainActivity
import com.example.domag2.database.database.DatabaseFactoryImpl
import com.example.domag2.database.database.MemoryDatabaseFactory

var factory = MemoryDatabaseFactory()

var activityFactory: SingleActivityFactory<MainActivity> =
    object : SingleActivityFactory<MainActivity>(
        MainActivity::class.java
    ) {

        override fun create(intent: Intent): MainActivity {
            return MainActivity(DatabaseFactoryImpl(factory))
        }
    }
