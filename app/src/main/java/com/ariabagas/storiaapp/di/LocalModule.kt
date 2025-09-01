package com.ariabagas.storiaapp.di

import com.ariabagas.storiaapp.data.local.datastore.UserPreference
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val LocalModule = module {
    single { UserPreference(androidContext()) }
}