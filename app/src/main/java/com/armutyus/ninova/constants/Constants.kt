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
    const val FLATICON_URL = "https://www.flaticon.com/"
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
    val discoverScreenCategories = mapOf(
        "Drama" to R.drawable.drama_01,
        "Fantasy" to R.drawable.fantasy_02,
        "Historical Fiction" to R.drawable.historical_fiction_03,
        "Horror" to R.drawable.horror_04,
        "Humor" to R.drawable.humor_05,
        "Literature" to R.drawable.literature_06,
        "Magic" to R.drawable.magic_07,
        "Mystery and Detective Stories" to R.drawable.mystery_08,
        "Plays" to R.drawable.plays_09,
        "Poetry" to R.drawable.poetry_10,
        "Romance" to R.drawable.romance_11,
        "Science Fiction" to R.drawable.sci_fi_12,
        "Short Stories" to R.drawable.short_13,
        "Thriller" to R.drawable.thriller_14,
        "Young Adult Fiction" to R.drawable.young_fiction_15,
        "Science" to R.drawable.science_16,
        "Business" to R.drawable.business_17,
        "Cooking" to R.drawable.cooking_18,
        "Cookbooks" to R.drawable.cookbook_19,
        "Mental Health" to R.drawable.mental_health_20,
        "Exercise" to R.drawable.exercise_21,
        "Nutrition" to R.drawable.nutrition_22,
        "Self-help" to R.drawable.self_help_23,
        "History" to R.drawable.history_24,
        "Anthropology" to R.drawable.anthropology_25,
        "Religion" to R.drawable.religion_26,
        "Political Science" to R.drawable.political_science_27,
        "Psychology" to R.drawable.psychology_28,
        "Biography" to R.drawable.biography_29,
        "Architecture" to R.drawable.architecture_30,
        "Art & Art Instruction" to R.drawable.art_31,
        "Art History" to R.drawable.art_history_32,
        "Dance" to R.drawable.dance_33,
        "Design" to R.drawable.design_34,
        "Fashion" to R.drawable.fashion_35,
        "Film" to R.drawable.film_36,
        "Graphic Design" to R.drawable.graphic_design_37,
        "Music" to R.drawable.music_38,
        "Music Theory" to R.drawable.music_theory_39,
        "Painting" to R.drawable.painting_40,
        "Photography" to R.drawable.photography_41,
        "Bedtime" to R.drawable.bedtime_42,
        "Children" to R.drawable.children_43
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