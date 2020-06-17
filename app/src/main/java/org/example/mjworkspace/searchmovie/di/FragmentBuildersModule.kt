package org.example.mjworkspace.searchmovie.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.example.mjworkspace.searchmovie.ui.SearchFragment

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment
}