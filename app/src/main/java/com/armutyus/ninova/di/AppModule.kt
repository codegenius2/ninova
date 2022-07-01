package com.armutyus.ninova.di

import android.app.Application
import android.content.Context
import android.content.Intent
import com.armutyus.ninova.constants.Constants.LOGIN_INTENT
import com.armutyus.ninova.constants.Constants.MAIN_INTENT
import com.armutyus.ninova.constants.Constants.SPLASH_INTENT
import com.armutyus.ninova.ui.login.LoginActivity
import com.armutyus.ninova.ui.main.MainActivity
import com.armutyus.ninova.ui.splash.SplashActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Named(SPLASH_INTENT)
    fun provideSplashIntent(context: Context): Intent {
        return Intent(context, SplashActivity::class.java)
    }

    @Provides
    @Named(LOGIN_INTENT)
    fun provideAuthIntent(context: Context): Intent {
        return Intent(context, LoginActivity::class.java)
    }

    @Provides
    @Named(MAIN_INTENT)
    fun provideMainIntent(context: Context): Intent {
        return Intent(context, MainActivity::class.java)
    }

}