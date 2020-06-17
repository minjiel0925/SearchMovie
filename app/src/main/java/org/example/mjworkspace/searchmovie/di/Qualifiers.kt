package org.example.mjworkspace.searchmovie.di

import javax.inject.Qualifier


@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class TmdbApi

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class CoroutineScopeIO