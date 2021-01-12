package io.github.kn65op.domag.data

import io.github.kn65op.domag.data.database.database.AppDatabase
import javax.inject.Inject

class Repository @Inject constructor(private val db : AppDatabase){
}