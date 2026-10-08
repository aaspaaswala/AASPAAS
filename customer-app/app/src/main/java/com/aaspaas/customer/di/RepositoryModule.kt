package com.aaspaas.customer.di

import com.aaspaas.customer.data.repository.AuthRepositoryImpl
import com.aaspaas.customer.data.repository.ProductRepositoryImpl
import com.aaspaas.customer.data.repository.ReservationRepositoryImpl
import com.aaspaas.customer.data.repository.StoreRepositoryImpl
import com.aaspaas.customer.data.repository.WishlistRepositoryImpl
import com.aaspaas.customer.domain.repository.AuthRepository
import com.aaspaas.customer.domain.repository.ProductRepository
import com.aaspaas.customer.domain.repository.ReservationRepository
import com.aaspaas.customer.domain.repository.StoreRepository
import com.aaspaas.customer.domain.repository.WishlistRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds @Singleton
    abstract fun bindStoreRepository(impl: StoreRepositoryImpl): StoreRepository

    @Binds @Singleton
    abstract fun bindReservationRepository(impl: ReservationRepositoryImpl): ReservationRepository

    @Binds @Singleton
    abstract fun bindWishlistRepository(impl: WishlistRepositoryImpl): WishlistRepository
}
