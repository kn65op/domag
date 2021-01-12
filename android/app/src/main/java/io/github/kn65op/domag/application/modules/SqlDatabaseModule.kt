package io.github.kn65op.domag.application.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.data.database.database.migrations.MIGRATION_2_3
import io.github.kn65op.domag.data.database.database.migrations.MIGRATION_3_4
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class SqlDatabaseModule {

    @Singleton
    @Provides
    fun bindApplicationDatabase(@ApplicationContext applicationContext: Context): AppDatabase =
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).addMigrations(MIGRATION_2_3, MIGRATION_3_4).build()

}