package com.armutyus.ninova.di

import android.content.Context
import androidx.room.Room
import com.armutyus.ninova.R
import com.armutyus.ninova.repository.AuthRepository
import com.armutyus.ninova.repository.AuthRepositoryInterface
import com.armutyus.ninova.repository.FirebaseAuthenticator
import com.armutyus.ninova.repository.FirebaseAuthenticatorInterface
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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

    @Singleton
    @Provides
    fun provideAuthenticator() : FirebaseAuthenticatorInterface{
        return  FirebaseAuthenticator()
    }

    //this just takes the same idea as the authenticator. If we create another repository class
    //we can simply just swap here
    @Singleton
    @Provides
    fun provideRepository(
        authenticator : FirebaseAuthenticatorInterface
    ) : AuthRepositoryInterface {
        return AuthRepository(authenticator)
    }

    @Singleton
    @Provides
    fun injectGlide(@ApplicationContext context: Context) = Glide
        .with(context).setDefaultRequestOptions(
            RequestOptions().placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_error)
        )


}