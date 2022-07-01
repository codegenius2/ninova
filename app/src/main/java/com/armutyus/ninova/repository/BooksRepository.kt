package com.armutyus.ninova.repository

import com.armutyus.ninova.model.Books
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import javax.inject.Inject

class BooksRepository @Inject constructor(

) : BooksRepositoryInterface {

    //change lists to flow when databases and services are ready

    override fun getBooksList(): List<Books> {

        val book1 = Books("Semerkand", "Amin Maalouf", "300", "01.02.1989")
        val book2 = Books("Fedailerin Kalesi: Alamut", "Vladimir Bartol", "290", "03.12.1975")
        val book3 = Books("Cesur Yeni Dünya", "Aldous Huxley", "519", "12.09.1945")
        val book4 = Books("Beyaz Geceler", "Fyodor Mihayloviç Dostoyevski", "417", "30.05.1780")
        val book5 = Books("Nietzsche Ağladığında", "Irvin Yalom", "236", "06.03.2000")

        return mutableListOf(book1, book2, book3, book4, book5)
    }

    override fun searchBooksFromLocal(searchString: String): List<Books>  {

        val book1 = Books("Körlük", "Jose Saramago", "451", "01.02.1998")
        val book2 = Books("Satranç", "Stefan Zweig", "243", "02.01.1946")
        val book3 = Books("Altıncı Koğuş", "Anton Çehov", "187", "10.07.1966")
        val book4 = Books("Gurur ve Önyargı", "Jane Austen", "432", "30.05.1780")
        val book5 = Books("Martin Eden", "Jack London", "218", "06.03.1993")

        val localBooks = listOf(book1, book2, book3, book4, book5)

        val filteredBooks: List<Books> = localBooks.filter { books ->
            books.bookTitle.lowercase().contains(searchString) || books.bookAuthor.lowercase().contains(searchString) }

        return filteredBooks

    }

    override fun searchBooksFromApi(searchString: String): List<Books> {

        val book1 = Books("Hayvan Çiftliği", "George Orwell", "319", "07.02.1952")
        val book2 = Books("Dönüşüm", "Franz Kafka", "290", "03.12.1961")
        val book3 = Books("İçimizdeki Şeytan", "Sabahattin Ali", "396", "22.11.1935")
        val book4 =
            Books("Hayvanlardan Tanrılara Sapiens", "Yuval Noah Harari", "288", "19.05.2010")
        val book5 = Books("Bir İdam Mahkumunun Son Günü", "Victor Hugo", "956", "04.08.1856")

        val apiBooks = listOf(book1, book2, book3, book4, book5)

        val filteredBooks: List<Books> = apiBooks.filter { books ->
            books.bookTitle.lowercase().contains(searchString) || books.bookAuthor.lowercase().contains(searchString) }

        return filteredBooks
    }
}