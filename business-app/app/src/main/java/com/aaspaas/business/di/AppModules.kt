package com.aaspaas.business.di

import com.aaspaas.business.BuildConfig
import com.aaspaas.business.core.network.*
import com.aaspaas.business.data.remote.*
import com.aaspaas.business.data.repository.*
import com.aaspaas.business.domain.repository.*
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

    @Provides @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton fun provideAuthApi(r: Retrofit): BusinessAuthApiService = r.create(BusinessAuthApiService::class.java)
    @Provides @Singleton fun provideStoreApi(r: Retrofit): BusinessStoreApiService = r.create(BusinessStoreApiService::class.java)
    @Provides @Singleton fun provideProductApi(r: Retrofit): BusinessProductApiService = r.create(BusinessProductApiService::class.java)
    @Provides @Singleton fun provideReservationApi(r: Retrofit): BusinessReservationApiService = r.create(BusinessReservationApiService::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingModule {

    @Binds @Singleton abstract fun bindTokenProvider(impl: DataStoreTokenProvider): TokenProvider
    @Binds @Singleton abstract fun bindAuthRepo(impl: BusinessAuthRepositoryImpl): BusinessAuthRepository
    @Binds @Singleton abstract fun bindStoreRepo(impl: StoreRepositoryImpl): StoreRepository
    @Binds @Singleton abstract fun bindProductRepo(impl: ProductManagementRepositoryImpl): ProductManagementRepository
    @Binds @Singleton abstract fun bindReservationRepo(impl: BusinessReservationRepositoryImpl): BusinessReservationRepository
}
