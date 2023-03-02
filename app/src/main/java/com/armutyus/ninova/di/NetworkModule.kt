package com.armutyus.ninova.di

import android.content.Context
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.BASE_URL
import com.armutyus.ninova.constants.Util
import com.armutyus.ninova.repository.*
import com.armutyus.ninova.roomdb.NinovaDao
import com.armutyus.ninova.service.GoogleBooksApiService
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): GoogleBooksApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleBooksApiService::class.java)
    }

    @Provides
    fun provideFirebaseAuthInstance() = FirebaseAuth.getInstance()

    @Provides
    fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    fun provideFirebaseStorage() = FirebaseStorage.getInstance()

    @Singleton
    @Provides
    fun provideAuthRepository(
        auth: FirebaseAuth,
        db: FirebaseFirestore,
        storage: FirebaseStorage
    ) = FirebaseRepositoryImpl(auth, db, storage) as FirebaseRepositoryInterface

    @Singleton
    @Provides
    fun provideApiBooksRepository(googleBooksApiService: GoogleBooksApiService) =
        ApiBooksRepositoryImpl(googleBooksApiService) as ApiBooksRepositoryInterface

    @Singleton
    @Provides
    fun provideLocalBooksRepository(ninovaDao: NinovaDao) =
        LocalBooksRepositoryImpl(ninovaDao) as LocalBooksRepositoryInterface

    @Singleton
    @Provides
    fun provideShelfRepository(ninovaDao: NinovaDao) =
        ShelfRepositoryImpl(ninovaDao) as ShelfRepositoryInterface

    @Singleton
    @Provides
    fun injectGlide(@ApplicationContext context: Context) = Glide
        .with(context)
        .setDefaultRequestOptions(
            RequestOptions()
                .placeholder(Util.progressDrawable(context))
                .error(R.drawable.ic_placeholder_book_icon)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        )

}