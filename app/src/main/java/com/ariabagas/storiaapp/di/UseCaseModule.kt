package com.ariabagas.storiaapp.di

import com.ariabagas.storiaapp.core.usecase.HomeInteractor
import com.ariabagas.storiaapp.core.usecase.HomeUseCase
import com.ariabagas.storiaapp.core.usecase.WelcomeInteractor
import com.ariabagas.storiaapp.core.usecase.WelcomeUseCase
import org.koin.dsl.module

val UseCaseModule = module {
    factory<HomeUseCase> { HomeInteractor(get()) }
    factory<WelcomeUseCase> { WelcomeInteractor(get()) }
}