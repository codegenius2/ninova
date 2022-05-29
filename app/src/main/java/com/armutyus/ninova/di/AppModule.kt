package com.armutyus.ninova.di

import android.app.Application
import android.content.Context
import android.content.Intent
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.LOGIN_INTENT
import com.armutyus.ninova.constants.Constants.MAIN_INTENT
import com.armutyus.ninova.constants.Constants.SPLASH_INTENT
import com.armutyus.ninova.constants.Constants.USERS_REF
import com.armutyus.ninova.repository.AuthRepository
import com.armutyus.ninova.repository.AuthRepositoryInterface
import com.armutyus.ninova.ui.login.LoginActivity
import com.armutyus.ninova.ui.main.MainActivity
import com.armutyus.ninova.ui.splash.SplashActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    /*@Singleton
    @Provides
    fun provideRetrofit(): googleBooksAPI {

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(googleBooksAPI::class.java)
    }*/

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

    @Provides
    fun provideFirebaseAuthInstance() = FirebaseAuth.getInstance()

    @Provides
    fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Named(USERS_REF)
    fun provideUsersRef(db: FirebaseFirestore) = db.collection(USERS_REF)

    @Singleton
    @Provides
    fun provideAuthRepository(
        auth : FirebaseAuth
    ) = AuthRepository(auth) as AuthRepositoryInterface

    @Singleton
    @Provides
    fun injectGlide(@ApplicationContext context: Context) = Glide
        .with(context).setDefaultRequestOptions(
            RequestOptions().placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_error)
        )


}