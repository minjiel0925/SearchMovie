package org.example.mjworkspace.searchmovie.di

import android.app.Application
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import org.example.mjworkspace.searchmovie.api.END_POINT
import org.example.mjworkspace.searchmovie.api.TmdbService
import org.example.mjworkspace.searchmovie.data.db.SearchDb
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class, CoreDataModule::class])
class AppModule {

    // service
    @Singleton
    @Provides
    fun provideTmdbService(@TmdbApi okHttpClient: OkHttpClient, moshiConverterFactory: MoshiConverterFactory) =
        provideService(okHttpClient, moshiConverterFactory, TmdbService::class.java)


    @TmdbApi
    @Provides
    fun providePrivateOkHttpClient(
        upstreamClient: OkHttpClient
    ): OkHttpClient {
        return upstreamClient.newBuilder()
            .build()
    }

    // database
    @Singleton
    @Provides
    fun provideDb(app: Application) = SearchDb.getInstance(app)

    // dao
    @Singleton
    @Provides
    fun provideNowDao(searchdb: SearchDb) = searchdb.searchResultDao()

    // coroutines
    @CoroutineScopeIO
    @Provides
    fun provideCoroutineScopeIO() = CoroutineScope(Dispatchers.IO)


    private fun createRetrofit(
        okhttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(END_POINT)
            .client(okhttpClient)
            .addConverterFactory(moshiConverterFactory)
            .build()
    }

    private fun <T> provideService(
        okhttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory,
        clazz: Class<T>
    ): T {
        return createRetrofit(okhttpClient, moshiConverterFactory).create(clazz)
    }
}