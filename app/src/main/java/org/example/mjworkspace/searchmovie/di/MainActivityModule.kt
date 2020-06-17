package org.example.mjworkspace.searchmovie.di

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.example.mjworkspace.searchmovie.MainActivity

@Suppress("unused")
@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity
}