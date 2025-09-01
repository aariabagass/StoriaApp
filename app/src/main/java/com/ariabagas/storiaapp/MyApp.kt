package com.ariabagas.storiaapp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

import com.ariabagas.storiaapp.di.LocalModule
import com.ariabagas.storiaapp.di.NetworkModule
import com.ariabagas.storiaapp.di.RepositoryModule
import com.ariabagas.storiaapp.di.UseCaseModule
import com.ariabagas.storiaapp.di.ViewModelModule

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@MyApp)
            modules(
                listOf(
                    LocalModule,
                    NetworkModule,
                    RepositoryModule, UseCaseModule, ViewModelModule
                )
            )
        }
    }
}