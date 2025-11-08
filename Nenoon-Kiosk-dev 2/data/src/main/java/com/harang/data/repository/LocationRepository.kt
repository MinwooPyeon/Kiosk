package com.harang.data.repository

import com.harang.data.db.dao.LocationDao
import com.harang.data.db.entity.LocationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val locationDao: LocationDao
) {
    fun getAllLocations(): Flow<List<LocationEntity>> {
        return locationDao.getAllLocations()
    }

    suspend fun getLocationById(id: Int): LocationEntity? {
        return locationDao.getLocationById(id)
    }

    suspend fun insertLocation(location: LocationEntity) {
        locationDao.insertLocation(location)
    }

    suspend fun updateLocation(location: LocationEntity) {
        locationDao.updateLocation(location)
    }

    suspend fun deleteLocation(location: LocationEntity) {
        locationDao.deleteLocation(location)
    }
}