package com.example.dailytasks.core.utils

import android.content.Context
import com.example.dailytasks.core.data.ITaskRepository
import com.example.dailytasks.core.data.TaskRepository
import com.example.dailytasks.core.domain.TaskManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideTaskManager(context: Context): TaskManager {
        return TaskManager(context)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(context: Context): ITaskRepository {
        return TaskRepository(taskManager = provideTaskManager(context))
    }

}