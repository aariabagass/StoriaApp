package com.ariabagas.storiaapp.di

import com.ariabagas.storiaapp.ui.home.AddStoryViewModel
import com.ariabagas.storiaapp.ui.home.DetailViewModel
import com.ariabagas.storiaapp.ui.home.HomeViewModel
import com.ariabagas.storiaapp.ui.welcome.LoginViewModel
import com.ariabagas.storiaapp.ui.welcome.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ViewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { AddStoryViewModel(get()) }
    viewModel { DetailViewModel(get()) }
}