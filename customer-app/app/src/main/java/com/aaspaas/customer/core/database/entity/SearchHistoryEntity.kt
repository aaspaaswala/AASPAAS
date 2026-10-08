package com.aaspaas.customer.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val id: String,
    val query: String,
    val latitude: Double?,
    val longitude: Double?,
    val timestamp: Long
)

@Entity(tableName = "saved_locations")
data class SavedLocationEntity(
    @PrimaryKey val id: String,
    val label: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)
