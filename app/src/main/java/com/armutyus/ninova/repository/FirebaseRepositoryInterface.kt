package com.armutyus.ninova.repository

import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.google.firebase.auth.AuthCredential

interface FirebaseRepositoryInterface {

    suspend fun signInWithEmailPassword(email: String, password: String): Response<Boolean>

    suspend fun signInAnonymous(): Response<Boolean>

    suspend fun anonymousToPermanent(credential: AuthCredential): Response<Boolean>

    suspend fun signUpWithEmailPassword(email: String, password: String): Response<Boolean>

    suspend fun createUserInFirestore(): Response<Boolean>

    suspend fun deleteUserBookFromFirestore(bookId: String): Response<Boolean>

    suspend fun deleteUserCrossRefFromFirestore(crossRefId: String): Response<Boolean>

    suspend fun deleteUserShelfFromFirestore(shelfId: String): Response<Boolean>

    suspend fun downloadUserBooksFromFirestore(): Response<List<DataModel.LocalBook>>

    suspend fun downloadUserShelvesFromFirestore(): Response<List<LocalShelf>>

    suspend fun downloadUserCrossRefFromFirestore(): Response<List<BookShelfCrossRef>>

    suspend fun uploadUserBooksToFirestore(localBook: DataModel.LocalBook): Response<Boolean>

    suspend fun uploadUserShelvesToFirestore(shelf: LocalShelf): Response<Boolean>

    suspend fun uploadUserCrossRefToFirestore(bookShelfCrossRef: BookShelfCrossRef): Response<Boolean>

    suspend fun signOut(): Response<Boolean>

    suspend fun reAuthUser(credential: AuthCredential): Response<Boolean>

    suspend fun changeUserEmail(email: String): Response<Boolean>

    suspend fun changeUserPassword(password: String): Response<Boolean>

    suspend fun sendResetPassword(email: String): Response<Boolean>

}