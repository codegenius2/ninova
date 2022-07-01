package com.armutyus.ninova.repository

import com.armutyus.ninova.model.Book
import com.armutyus.ninova.roomdb.NinovaDao
import com.armutyus.ninova.roomdb.entities.BookWithShelves
import com.armutyus.ninova.roomdb.entities.LocalBook
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BooksRepository @Inject constructor(
    private val ninovaDao: NinovaDao
) : BooksRepositoryInterface {

    //change lists to flow when databases and services are ready

    override fun getBookList(): List<Book> {
        val book1 = Book("Semerkand", "Amin Maalouf", "300", "01.02.1989")
        val book2 = Book("Fedailerin Kalesi: Alamut", "Vladimir Bartol", "290", "03.12.1975")
        val book3 = Book("Cesur Yeni Dünya", "Aldous Huxley", "519", "12.09.1945")
        val book4 = Book("Beyaz Geceler", "Fyodor Mihayloviç Dostoyevski", "417", "30.05.1780")
        val book5 = Book("Nietzsche Ağladığında", "Irvin Yalom", "236", "06.03.2000")

        return mutableListOf(book1, book2, book3, book4, book5)
    }

    override fun searchBookFromLocal(searchString: String): List<Book> {
        val book1 = Book("Körlük", "Jose Saramago", "451", "01.02.1998")
        val book2 = Book("Satranç", "Stefan Zweig", "243", "02.01.1946")
        val book3 = Book("Altıncı Koğuş", "Anton Çehov", "187", "10.07.1966")
        val book4 = Book("Gurur ve Önyargı", "Jane Austen", "432", "30.05.1780")
        val book5 = Book("Martin Eden", "Jack London", "218", "06.03.1993")

        val localBooks = listOf(book1, book2, book3, book4, book5)

        val filteredBooks: List<Book> = localBooks.filter { books ->
            books.bookTitle.lowercase().contains(searchString) || books.bookAuthor.lowercase()
                .contains(searchString)
        }

        return filteredBooks

    }

    override fun searchBookFromApi(searchString: String): List<Book> {

        val book1 = Book("Hayvan Çiftliği", "George Orwell", "319", "07.02.1952")
        val book2 = Book("Dönüşüm", "Franz Kafka", "290", "03.12.1961")
        val book3 = Book("İçimizdeki Şeytan", "Sabahattin Ali", "396", "22.11.1935")
        val book4 =
            Book("Hayvanlardan Tanrılara Sapiens", "Yuval Noah Harari", "288", "19.05.2010")
        val book5 = Book("Bir İdam Mahkumunun Son Günü", "Victor Hugo", "956", "04.08.1856")

        val apiBooks = listOf(book1, book2, book3, book4, book5)

        val filteredBooks: List<Book> = apiBooks.filter { books ->
            books.bookTitle.lowercase().contains(searchString) || books.bookAuthor.lowercase()
                .contains(searchString)
        }

        return filteredBooks
    }

    override suspend fun insert(localBook: LocalBook) {
        ninovaDao.insertBook(localBook)
    }

    override suspend fun update(localBook: LocalBook) {
        ninovaDao.updateBook(localBook)
    }

    override suspend fun delete(localBook: LocalBook) {
        ninovaDao.deleteBook(localBook)
    }

    override fun getLocalBooks(): Flow<List<LocalBook>> {
        return ninovaDao.getLocalBooks()
    }

    override fun searchLocalBooks(searchString: String): Flow<List<LocalBook>> {
        return ninovaDao.searchLocalBooks(searchString)
    }

    override suspend fun getBookWithShelves(bookId: Int): Flow<List<BookWithShelves>> {
        return ninovaDao.getShelvesOfBook(bookId)
    }

}