package com.example.civilreport.data.remote_db

import com.example.civilreport.data.models.LocationIqInfo
import com.example.civilreport.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRemoteDataSource @Inject constructor(
    private val locationService: LocationService
) : BaseDataSource() {

    suspend fun autoComplete(query: String): Resource<List<LocationIqInfo>> =
        getResult {
            locationService.autoComplete(query)
        }
    }
