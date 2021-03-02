package io.github.kn65op.domag.application.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.data.repository.DatabaseRepository
import io.github.kn65op.domag.data.repository.Repository
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class DatabaseRepositoryModule {

    @Singleton
    @Provides
    fun bindRepository(@ApplicationContext applicationContext: Context, db : AppDatabase): Repository =
        DatabaseRepository(db)

}