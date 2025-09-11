package com.ariabagas.storiaapp.di

import com.ariabagas.storiaapp.data.local.datastore.UserPreference
import com.ariabagas.storiaapp.data.local.room.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val LocalModule = module {
    single { UserPreference(androidContext()) }

    single { AppDatabase.getDatabase(androidContext()) }
    single { get<AppDatabase>().storyDao() }
    single { get<AppDatabase>().remoteKeyDao() }
}