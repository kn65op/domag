package io.github.kn65op.domag.dbtests.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.kn65op.domag.data.database.database.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class MemoryDatabaseModule {

    @Singleton
    @Provides
    fun bindApplicationDatabase(@ApplicationContext applicationContext: Context): AppDatabase =
        Room.inMemoryDatabaseBuilder(
            applicationContext,
            AppDatabase::class.java
        ).build()

}