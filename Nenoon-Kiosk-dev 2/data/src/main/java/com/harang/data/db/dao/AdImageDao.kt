package com.harang.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.harang.data.db.entity.AdImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdImageDao {
    @Query("SELECT * FROM ad_image ORDER BY `order` ASC")
    fun getAllAdImages(): Flow<List<AdImageEntity>>

    @Query("SELECT * FROM ad_image WHERE locationId = :locationId ORDER BY `order` ASC")
    fun getAdImagesByLocation(locationId: Int): Flow<List<AdImageEntity>>

    @Query("SELECT * FROM ad_image WHERE id = :id")
    suspend fun getAdImageById(id: Int): AdImageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdImage(adImage: AdImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdImages(adImages: List<AdImageEntity>)

    @Update
    suspend fun updateAdImage(adImage: AdImageEntity)

    @Delete
    suspend fun deleteAdImage(adImage: AdImageEntity)

    @Query("DELETE FROM ad_image WHERE locationId = :locationId")
    suspend fun deleteAdImagesByLocation(locationId: Int)

    @Query("DELETE FROM ad_image")
    suspend fun deleteAllAdImages()
}