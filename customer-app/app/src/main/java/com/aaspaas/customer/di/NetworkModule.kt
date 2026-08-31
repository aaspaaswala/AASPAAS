package com.aaspaas.customer.di

import com.aaspaas.customer.BuildConfig
import com.aaspaas.customer.core.network.AuthInterceptor
import com.aaspaas.customer.core.network.DataStoreTokenProvider
import com.aaspaas.customer.core.network.TokenProvider
import com.aaspaas.customer.data.remote.AuthApiService
import com.aaspaas.customer.data.remote.ProductApiService
import com.aaspaas.customer.data.remote.ReservationApiService
import com.aaspaas.customer.data.remote.StoreApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                        else HttpLoggingInterceptor.Level.NONE
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApiService = retrofit.create(AuthApiService::class.java)

    @Provides @Singleton
    fun provideProductApi(retrofit: Retrofit): ProductApiService = retrofit.create(ProductApiService::class.java)

    @Provides @Singleton
    fun provideStoreApi(retrofit: Retrofit): StoreApiService = retrofit.create(StoreApiService::class.java)

    @Provides @Singleton
    fun provideReservationApi(retrofit: Retrofit): ReservationApiService = retrofit.create(ReservationApiService::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkBindingModule {
    @Binds @Singleton
    abstract fun bindTokenProvider(impl: DataStoreTokenProvider): TokenProvider
}
