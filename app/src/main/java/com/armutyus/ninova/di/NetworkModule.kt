package com.armutyus.ninova.di

import android.content.Context
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.GOOGLE_BOOKS_BASE_URL
import com.armutyus.ninova.constants.Constants.OPEN_LIBRARY_BASE_URL
import com.armutyus.ninova.constants.Util
import com.armutyus.ninova.repository.FirebaseRepositoryImpl
import com.armutyus.ninova.repository.FirebaseRepositoryInterface
import com.armutyus.ninova.repository.GoogleBooksRepositoryImpl
import com.armutyus.ninova.repository.GoogleBooksRepositoryInterface
import com.armutyus.ninova.repository.LocalBooksRepositoryImpl
import com.armutyus.ninova.repository.LocalBooksRepositoryInterface
import com.armutyus.ninova.repository.OpenLibRepositoryImpl
import com.armutyus.ninova.repository.OpenLibRepositoryInterface
import com.armutyus.ninova.repository.ShelfRepositoryImpl
import com.armutyus.ninova.repository.ShelfRepositoryInterface
import com.armutyus.ninova.roomdb.NinovaDao
import com.armutyus.ninova.service.GoogleBooksApiService
import com.armutyus.ninova.service.OpenLibraryApiService
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
            .baseUrl(GOOGLE_BOOKS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleBooksApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideOpenLibraryRetrofit(): OpenLibraryApiService {
        return Retrofit.Builder()
            .baseUrl(OPEN_LIBRARY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenLibraryApiService::class.java)
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
        GoogleBooksRepositoryImpl(googleBooksApiService) as GoogleBooksRepositoryInterface

    @Singleton
    @Provides
    fun provideLocalBooksRepository(ninovaDao: NinovaDao) =
        LocalBooksRepositoryImpl(ninovaDao) as LocalBooksRepositoryInterface

    @Singleton
    @Provides
    fun provideOpenLibRepository(openLibraryApiService: OpenLibraryApiService) =
        OpenLibRepositoryImpl(openLibraryApiService) as OpenLibRepositoryInterface

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