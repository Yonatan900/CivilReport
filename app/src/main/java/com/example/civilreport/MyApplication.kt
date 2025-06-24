package com.example.civilreport
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.civilreport.data.repository.PrefsRepo
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PDFBoxResourceLoader.init(this)

        val repo = EntryPointAccessors.fromApplication(
            this, PrefsEntryPoint::class.java
        ).prefRepo()

        val mode = runBlocking { repo.initialNightMode() }
            ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

        AppCompatDelegate.setDefaultNightMode(mode)
    }

    // Hilt EntryPoint to access PrefsRepo.
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PrefsEntryPoint {
        fun prefRepo(): PrefsRepo
    }

    }
