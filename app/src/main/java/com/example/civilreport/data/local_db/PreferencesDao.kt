package com.example.civilreport.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Dao
import com.example.civilreport.data.models.AppPreferences

@Dao
interface PreferencesDao {

    @Query("SELECT night_mode FROM app_preferences WHERE id = 0")
    fun nightModeLive(): LiveData<Int?>

    @Query("SELECT night_mode FROM app_preferences WHERE id = 0")
    suspend fun nightModeOnce(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(prefs: AppPreferences)

}