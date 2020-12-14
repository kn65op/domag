package io.github.kn65op.domag.uitests.common

import android.content.Intent
import androidx.test.runner.intercepting.SingleActivityFactory
import io.github.kn65op.domag.MainActivity
import io.github.kn65op.domag.data.database.database.DatabaseFactoryImpl
import io.github.kn65op.domag.data.database.database.MemoryDatabaseFactory

var factory = MemoryDatabaseFactory()

var activityFactory: SingleActivityFactory<MainActivity> =
    object : SingleActivityFactory<MainActivity>(
        MainActivity::class.java
    ) {

        override fun create(intent: Intent): MainActivity {
            DatabaseFactoryImpl(factory)
            return MainActivity()
        }
    }
