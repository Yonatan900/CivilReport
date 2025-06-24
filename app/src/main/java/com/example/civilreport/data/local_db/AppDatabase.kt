package com.example.civilreport.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.civilreport.data.models.AppPreferences

@Database(entities = [AppPreferences::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract fun preferencesDao(): PreferencesDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "preferences_database"
                ).build().also {
                    instance = it
                }
            }
    }

}