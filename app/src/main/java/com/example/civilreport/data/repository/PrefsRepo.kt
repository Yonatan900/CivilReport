package com.example.civilreport.data.repository

import com.example.civilreport.data.local_db.PreferencesDao
import com.example.civilreport.data.models.AppPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefsRepo @Inject constructor(
    private val localDataSource: PreferencesDao
) {
    suspend fun initialNightMode() : Int? = localDataSource.nightModeOnce()

    suspend fun setNightMode(mode: Int) =
        localDataSource.save(AppPreferences(nightMode = mode))
}