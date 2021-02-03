package io.github.kn65op.domag.data.repository

import io.github.kn65op.domag.data.database.database.AppDatabase
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val db : AppDatabase)