package com.mistplay.carddeckgame

import android.app.Application
import com.mistplay.carddeckgame.di.allModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CardDeckApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CardDeckApplication)
            modules(allModules())
        }
    }
}
