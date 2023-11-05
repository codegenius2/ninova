package com.armutyus.ninova.constants

import com.armutyus.ninova.BuildConfig
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Util.Companion.toLocalizedString

object Constants {

    const val VERSION_NAME = BuildConfig.VERSION_NAME

    //Firebase
    const val DELETED_FIRESTORE = "Deleted from firestore"
    const val DELETING_FIRESTORE = "Deleting from firestore"
    const val UPLOADING_FIRESTORE = "Uploading to firestore"
    const val UPLOADED_FIRESTORE = "Uploaded to firestore"

    //Messages
    val ERROR_MESSAGE = R.string.unexpected_error.toLocalizedString()

    //Intents
    const val ABOUT_INTENT = "aboutIntent"
    const val BOOK_DETAILS_INTENT = "bookDetailsIntent"
    const val LOGIN_INTENT = "loginIntent"
    const val MAIN_INTENT = "mainIntent"
    const val REGISTER_INTENT = "registerIntent"

    //Preferences
    const val CHANGE_EMAIL = "change_email"
    const val CHANGE_PASSWORD = "change_password"
    const val NINOVA_DARK_THEME = "ninova_dark"
    const val NINOVA_LIGHT_THEME = "ninova_light"
    const val BERGAMA_LIGHT_THEME = "bergama_light"
    const val BERGAMA_DARK_THEME = "bergama_dark"
    const val ALEXANDRIA_LIGHT_THEME = "alexandria_light"
    const val ALEXANDRIA_DARK_THEME = "alexandria_dark"
    const val MAIN_SHARED_PREF = "main_shared_preferences"
    const val FORGOT_PASSWORD = "forgot_password"
    const val REGISTER = "register"
    const val SETTINGS_ACTION_KEY = "action"

    //References
    const val GOOGLE_BOOKS_BASE_URL = "https://www.googleapis.com/books/v1/"
    const val OPEN_LIBRARY_BASE_URL = "https://openlibrary.org/"
    const val BOOKS_REF = "Books"
    const val BOOKSHELF_CROSS_REF = "BookShelfCrossRef"
    const val FIREBASE_URL = "https://firebase.google.com/"
    const val GLIDE_URL = "https://github.com/bumptech/glide"
    const val GOOGLE_BOOKS_API_URL = "https://developers.google.com/books"
    const val LINK_BUILDER_URL = "https://github.com/klinker24/Android-TextView-LinkBuilder"
    const val LOTTIE_FILES_URL = "https://lottiefiles.com/"
    const val NINOVA_LOGO_URL =
        "https://play-lh.googleusercontent.com/ZeOfg2rgd6wCdWDlBjepSHK4dLenmvSd0wQz0mNIZmRXie95GIhNLWZhKm3iU81xww=w480-h960-rw"
    const val PRIVACY_POLICY_URL = "https://sites.google.com/view/ninova-bookshelf-app"
    const val RETROFIT_URL = "https://square.github.io/retrofit/"
    const val SHELVES_REF = "Shelves"
    const val USERS_REF = "Users"

    //UserFields
    const val CREATED_AT = "createdAt"
    const val EMAIL = "email"
    const val NAME = "name"
    const val PHOTO_URL = "photoUrl"

    //BookViewType
    const val BOOK_TYPE_FOR_DETAILS = "bookTypeForDetails"
    const val GOOGLE_BOOK_TYPE = 0
    const val LOCAL_BOOK_TYPE = 1
    const val OPEN_LIB_BOOK_TYPE = 2

    //DiscoverScreenCategories
    val discoverScreenCategories = listOf(
        "Drama",
        "Fantasy",
        "Historical Fiction",
        "Horror",
        "Humor",
        "Literature",
        "Magic",
        "Mystery and Detective Stories",
        "Plays",
        "Poetry",
        "Romance",
        "Science Fiction",
        "Short Stories",
        "Thriller",
        "Young Adult Fiction",
        "Science",
        "Business",
        "Cooking",
        "Cookbooks",
        "Mental Health",
        "Exercise",
        "Nutrition",
        "Self-help",
        "History",
        "Anthropology",
        "Religion",
        "Political Science",
        "Psychology",
        "Biography",
        "Architecture",
        "Art & Art Instruction",
        "Art History",
        "Dance",
        "Design",
        "Fashion",
        "Film",
        "Graphic Design",
        "Music",
        "Music Theory",
        "Painting",
        "Photography",
        "Bedtime",
        "Children"
    )

    //RandomWordsList
    val randomWordList = listOf(
        "William",
        "Shakespeare",
        "Christie",
        "Agatha",
        "Cartland",
        "Barbara",
        "Danielle",
        "Steel",
        "Harold",
        "Robbins",
        "Georges",
        "Simenon",
        "Enid",
        "Blyton",
        "Sidney",
        "Sheldon",
        "Eiichiro",
        "Rowling",
        "Gilbert",
        "Patten",
        "Seuss",
        "Akira",
        "Tolstoy",
        "Dostoyevski",
        "Corin",
        "Tellado",
        "Pushkin",
        "Fyodor",
        "Aleksandr",
        "Dean",
        "Koontz",
        "Chuck",
        "Jackie",
        "Collins",
        "Hegel",
        "Darwin",
        "Yalom",
        "Harari",
        "Gogol",
        "Stephen",
        "King",
        "Paulo",
        "Coelho",
        "Edgar",
        "Allan",
        "Wallace",
        "Jiro",
        "Akagawa",
        "Tolkien",
        "Robert",
        "Ludlum",
        "Dan",
        "Brown",
        "Yuval",
        "Palahniuk",
        "Kant",
        "Heidegger",
        "James",
        "Patterson",
        "Rene",
        "Goscinny",
        "Frederik",
        "Osamu",
        "Roald",
        "Dahl",
        "Irving",
        "Wallace",
        "Karl",
        "Carter",
        "Masashi",
        "Kishimoto",
        "Fleming",
        "Robin",
        "Cook",
        "Charles",
        "Dickens",
        "Antoine",
        "Saint-Exupery",
        "Lewis",
        "Haggard",
        "Salinger",
        "Nabokov",
        "Gabriel",
        "Garcia",
        "Marquez",
        "Ursula",
        "Atwood",
        "George",
        "Orwell",
        "Lucy",
        "Maud",
        "Montgomery",
        "Machiavelli",
        "Umberto",
        "Eco",
        "Richard",
        "Adams",
        "Higgins",
        "Harper",
        "Lee",
        "Carl",
        "Sagan",
        "Anne",
        "Frank",
        "Scott",
        "Fitzgerald",
        "Stieg",
        "Larsson",
        "Margaret",
        "Mitchell",
        "Hawking",
        "Suzanne",
        "Collins",
        "Mark",
        "Twain",
        "Jane",
        "Austen",
        "Albert",
        "Camus",
        "Dante",
        "Alighieri",
        "Carlo",
        "Collodi"
    )

}