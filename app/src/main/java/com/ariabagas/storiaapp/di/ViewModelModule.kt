package com.ariabagas.storiaapp.di

import com.ariabagas.storiaapp.ui.home.AddStoryViewModel
import com.ariabagas.storiaapp.ui.home.DetailViewModel
import com.ariabagas.storiaapp.ui.home.HomeViewModel
import com.ariabagas.storiaapp.ui.home.MapViewModel
import com.ariabagas.storiaapp.ui.welcome.LoginViewModel
import com.ariabagas.storiaapp.ui.welcome.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val ViewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::AddStoryViewModel)
    viewModelOf(::DetailViewModel)
    viewModelOf(::MapViewModel)
}