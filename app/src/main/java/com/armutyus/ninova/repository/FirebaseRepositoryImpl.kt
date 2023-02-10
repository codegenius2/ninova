package com.armutyus.ninova.repository

import com.armutyus.ninova.constants.Constants.BOOKSHELF_CROSS_REF
import com.armutyus.ninova.constants.Constants.BOOKS_REF
import com.armutyus.ninova.constants.Constants.CREATED_AT
import com.armutyus.ninova.constants.Constants.EMAIL
import com.armutyus.ninova.constants.Constants.ERROR_MESSAGE
import com.armutyus.ninova.constants.Constants.NAME
import com.armutyus.ninova.constants.Constants.PHOTO_URL
import com.armutyus.ninova.constants.Constants.SHELVES_REF
import com.armutyus.ninova.constants.Constants.USERS_REF
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class FirebaseRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) : FirebaseRepositoryInterface {

    override suspend fun signInWithEmailPassword(
        email: String,
        password: String
    ): Response<Boolean> = withContext(coroutineContext) {
        try {
            Response.Loading
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.let {
                return@let Response.Success(true)
            }
        } catch (e: Exception) {
            Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
        }
    }

    override suspend fun anonymousToPermanent(credential: AuthCredential): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                Response.Loading
                val authResult = auth.currentUser!!.linkWithCredential(credential).await()
                authResult.let {
                    return@let Response.Success(true)
                }
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun signInAnonymous(): Response<Boolean> = withContext(coroutineContext) {
        try {
            Response.Loading
            val authResult = auth.signInAnonymously().await()
            authResult.let {
                return@let Response.Success(true)
            }
        } catch (e: Exception) {
            Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
        }
    }

    override suspend fun createUserInFirestore(): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                Response.Loading
                val createUser = auth.currentUser?.apply {
                    db.collection(USERS_REF).document(uid).set(
                        mapOf(
                            NAME to displayName,
                            EMAIL to email,
                            PHOTO_URL to photoUrl?.toString(),
                            CREATED_AT to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
                createUser.let {
                    return@let Response.Success(true)
                }
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun deleteUserBookFromFirestore(bookId: String): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                Response.Loading
                val uid = auth.currentUser?.uid!!
                db.collection(USERS_REF).document(uid).collection(BOOKS_REF).document(bookId)
                    .delete()
                Response.Success(true)
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun deleteUserCrossRefFromFirestore(crossRefId: String): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                Response.Loading
                val uid = auth.currentUser?.uid!!
                db.collection(USERS_REF).document(uid).collection(BOOKSHELF_CROSS_REF)
                    .document(crossRefId).delete()
                Response.Success(true)
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun deleteUserShelfFromFirestore(shelfId: String): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                Response.Loading
                val uid = auth.currentUser?.uid!!
                db.collection(USERS_REF).document(uid).collection(SHELVES_REF).document(shelfId)
                    .delete()
                Response.Success(true)
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun deleteUserPermanently(): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                Response.Loading
                val uid = auth.currentUser?.uid!!
                auth.currentUser!!.delete()
                db.collection(USERS_REF).document(uid).delete()
                Response.Success(true)
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun downloadUserBooksFromFirestore(): Response<List<DataModel.LocalBook>> =
        withContext(coroutineContext) {
            try {
                Response.Loading
                val uid = auth.currentUser?.uid!!
                val querySnapshot: QuerySnapshot =
                    db.collection(USERS_REF).document(uid).collection(BOOKS_REF)
                        .get().await()
                val bookList = querySnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(DataModel.LocalBook::class.java)?.apply {
                        this.bookId = documentSnapshot.id
                    }
                }
                Response.Success(bookList)
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun downloadUserShelvesFromFirestore(): Response<List<LocalShelf>> =
        withContext(coroutineContext) {
            try {
                Response.Loading
                val uid = auth.currentUser?.uid!!
                val querySnapshot: QuerySnapshot =
                    db.collection(USERS_REF).document(uid).collection(SHELVES_REF)
                        .get().await()
                val shelfList = querySnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(LocalShelf::class.java)?.apply {
                        this.shelfId = documentSnapshot.id
                    }
                }
                Response.Success(shelfList)
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun downloadUserCrossRefFromFirestore(): Response<List<BookShelfCrossRef>> =
        withContext(coroutineContext) {
            try {
                Response.Loading
                val uid = auth.currentUser?.uid!!
                val querySnapshot: QuerySnapshot =
                    db.collection(USERS_REF).document(uid).collection(BOOKSHELF_CROSS_REF)
                        .get().await()
                val crossRefList = querySnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(BookShelfCrossRef::class.java)
                }
                Response.Success(crossRefList)
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun uploadUserBooksToFirestore(localBook: DataModel.LocalBook): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                Response.Loading
                val uploadBooks = auth.currentUser?.apply {
                    db.collection(USERS_REF).document(uid).collection(BOOKS_REF)
                        .document(localBook.bookId).set(
                            mapOf(
                                "bookAuthors" to localBook.bookAuthors,
                                "bookCategories" to localBook.bookCategories,
                                "bookCoverSmallThumbnail" to localBook.bookCoverSmallThumbnail,
                                "bookCoverThumbnail" to localBook.bookCoverThumbnail,
                                "bookDescription" to localBook.bookDescription,
                                "bookNotes" to localBook.bookNotes,
                                "bookPages" to localBook.bookPages,
                                "bookPublishedDate" to localBook.bookPublishedDate,
                                "bookPublisher" to localBook.bookPublisher,
                                "bookSubtitle" to localBook.bookSubtitle,
                                "bookTitle" to localBook.bookTitle
                            )
                        ).await()
                }
                uploadBooks.let {
                    return@let Response.Success(true)
                }
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun uploadUserShelvesToFirestore(shelf: LocalShelf): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                Response.Loading
                val uploadShelves = auth.currentUser?.apply {
                    db.collection(USERS_REF).document(uid).collection(SHELVES_REF)
                        .document(shelf.shelfId).set(
                            mapOf(
                                "shelfTitle" to shelf.shelfTitle,
                                "createdAt" to shelf.createdAt,
                                "shelfCover" to shelf.shelfCover
                            )
                        ).await()
                }
                uploadShelves.let {
                    return@let Response.Success(true)
                }
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun uploadUserCrossRefToFirestore(bookShelfCrossRef: BookShelfCrossRef): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                Response.Loading
                val uploadCrossRef = auth.currentUser?.apply {
                    db.collection(USERS_REF).document(uid).collection(BOOKSHELF_CROSS_REF)
                        .document(bookShelfCrossRef.bookId + bookShelfCrossRef.shelfId).set(
                            mapOf(
                                "bookId" to bookShelfCrossRef.bookId,
                                "shelfId" to bookShelfCrossRef.shelfId
                            )
                        ).await()
                }
                uploadCrossRef.let {
                    return@let Response.Success(true)
                }
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun signUpWithEmailPassword(
        email: String,
        password: String
    ): Response<Boolean> = withContext(coroutineContext) {
        try {
            Response.Loading
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.let {
                return@let Response.Success(true)
            }
        } catch (e: Exception) {
            Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
        }
    }

    override suspend fun signOut(): Response<Boolean> = withContext(coroutineContext) {
        try {
            Response.Loading
            auth.signOut().let {
                return@let Response.Success(true)
            }
        } catch (e: Exception) {
            Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
        }
    }

    override suspend fun reAuthUser(credential: AuthCredential): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                Response.Loading
                val reAuthResult = auth.currentUser!!.reauthenticate(credential).await()
                reAuthResult.let {
                    return@let Response.Success(true)
                }
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun changeUserEmail(email: String): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                Response.Loading
                val reAuthResult = auth.currentUser!!.updateEmail(email).await()
                reAuthResult.let {
                    auth.currentUser!!.apply {
                        db.collection(USERS_REF).document(uid).update("email", email)
                    }
                    return@let Response.Success(true)
                }
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun changeUserPassword(password: String): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                Response.Loading
                val reAuthResult = auth.currentUser!!.updatePassword(password).await()
                reAuthResult.let {
                    return@let Response.Success(true)
                }
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun sendResetPassword(email: String): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                Response.Loading
                val reAuthResult = auth.sendPasswordResetEmail(email).await()
                reAuthResult.let {
                    return@let Response.Success(true)
                }
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

}