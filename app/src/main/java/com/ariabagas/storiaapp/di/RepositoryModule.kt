package com.ariabagas.storiaapp.di

import com.ariabagas.storiaapp.core.repository.HomeRepository
import com.ariabagas.storiaapp.core.repository.WelcomeRepository
import com.ariabagas.storiaapp.data.repository.HomeRepositoryImpl
import com.ariabagas.storiaapp.data.repository.WelcomeRepositoryImpl
import org.koin.dsl.module

val RepositoryModule = module {
    single<HomeRepository> {
        HomeRepositoryImpl(
            api = get(),
            database = get()
        )
    }
    single<WelcomeRepository> { WelcomeRepositoryImpl(get(), get()) }
}
