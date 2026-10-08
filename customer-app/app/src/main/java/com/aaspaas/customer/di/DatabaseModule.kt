package com.aaspaas.customer.di

import android.content.Context
import androidx.room.Room
import com.aaspaas.customer.core.database.AasPaasWalaDatabase
import com.aaspaas.customer.core.database.dao.ProductDao
import com.aaspaas.customer.core.database.dao.StoreDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AasPaasWalaDatabase =
        Room.databaseBuilder(
            context,
            AasPaasWalaDatabase::class.java,
            AasPaasWalaDatabase.DATABASE_NAME
        ).build()

    @Provides
    fun provideProductDao(database: AasPaasWalaDatabase): ProductDao = database.productDao()

    @Provides
    fun provideStoreDao(database: AasPaasWalaDatabase): StoreDao = database.storeDao()
}
