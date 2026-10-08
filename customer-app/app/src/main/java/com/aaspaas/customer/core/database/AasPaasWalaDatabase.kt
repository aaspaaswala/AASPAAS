package com.aaspaas.customer.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aaspaas.customer.core.database.dao.ProductDao
import com.aaspaas.customer.core.database.dao.ReservationDao
import com.aaspaas.customer.core.database.dao.SearchHistoryDao
import com.aaspaas.customer.core.database.dao.StoreDao
import com.aaspaas.customer.core.database.entity.*

@Database(
    entities = [
        ProductEntity::class,
        StoreEntity::class,
        ReservationEntity::class,
        SearchHistoryEntity::class,
        SavedLocationEntity::class,
        CategoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class, VariantListConverter::class)
abstract class AasPaasWalaDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun reservationDao(): ReservationDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun storeDao(): StoreDao

    companion object {
        const val DATABASE_NAME = "aaspaas_wala.db"
    }
}
