package com.armutyus.ninova.di

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.room.Room
import com.armutyus.ninova.constants.Constants.ABOUT_INTENT
import com.armutyus.ninova.constants.Constants.BOOK_DETAILS_INTENT
import com.armutyus.ninova.constants.Constants.LOGIN_INTENT
import com.armutyus.ninova.constants.Constants.MAIN_INTENT
import com.armutyus.ninova.constants.Constants.REGISTER_INTENT
import com.armutyus.ninova.roomdb.NinovaLocalDB
import com.armutyus.ninova.ui.about.AboutActivity
import com.armutyus.ninova.ui.books.BookDetailsActivity
import com.armutyus.ninova.ui.login.LoginActivity
import com.armutyus.ninova.ui.login.RegisterActivity
import com.armutyus.ninova.ui.main.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
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

    @Provides
    @Named(REGISTER_INTENT)
    fun provideRegisterIntent(context: Context): Intent {
        return Intent(context, RegisterActivity::class.java)
    }

    @Provides
    @Named(ABOUT_INTENT)
    fun provideAboutIntent(context: Context): Intent {
        return Intent(context, AboutActivity::class.java)
    }

    @Provides
    @Named(BOOK_DETAILS_INTENT)
    fun provideBookDetailsIntent(context: Context): Intent {
        return Intent(context, BookDetailsActivity::class.java)
    }

    @Provides
    @Singleton
    fun injectLocalBooksDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, NinovaLocalDB::class.java, "NinovaLocalDB"
    ).build()

    @Provides
    @Singleton
    fun injectNinovaDao(database: NinovaLocalDB) = database.ninovaDao()

}