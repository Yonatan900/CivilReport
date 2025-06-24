package com.example.civilreport.data.models

import androidx.appcompat.app.AppCompatDelegate
import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.util.Locale

@Entity (tableName = "app_preferences")
data class AppPreferences(

    @PrimaryKey
    val id: Int = 0,
    val language: String = Locale.getDefault().language,

    @ColumnInfo(name = "night_mode")
    val nightMode: Int = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    )
