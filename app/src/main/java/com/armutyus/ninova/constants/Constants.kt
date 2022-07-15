package com.armutyus.ninova.constants

import com.armutyus.ninova.BuildConfig
import com.armutyus.ninova.roomdb.entities.LocalBook
import com.armutyus.ninova.roomdb.entities.LocalShelf

object Constants {

    const val VERSION_NAME = BuildConfig.VERSION_NAME

    //Messages
    const val ERROR_MESSAGE = "Unexpected error!"

    //Intents
    const val SPLASH_INTENT = "splashIntent"
    const val LOGIN_INTENT = "loginIntent"
    const val MAIN_INTENT = "mainIntent"
    const val REGISTER_INTENT = "registerIntent"
    const val ABOUT_INTENT = "aboutIntent"
    const val BOOK_DETAILS_INTENT = "bookDetailsIntent"

    //Preferences
    const val REGISTER = "register"
    const val CHANGE_EMAIL = "change_email"
    const val CHANGE_PASSWORD = "change_password"
    const val FORGOT_PASSWORD = "forgot_password"
    const val SETTINGS_ACTION_KEY = "action"
    const val LIGHT_THEME = "light"
    const val DARK_THEME = "dark"
    const val SYSTEM_THEME = "system"
    const val FROM_DETAILS_ACTIVITY = "bookDetailsActivity"
    const val DETAILS_INT_EXTRA = "fromDetails"
    const val DETAILS_STRING_EXTRA = "detailsActivity"
    const val FROM_DETAILS_TO_NOTES_EXTRA = 99

    //References
    const val USERS_REF = "users"
    const val BOOKS_REF = "books"

    //Fields
    const val NAME = "name"
    const val EMAIL = "email"
    const val PHOTO_URL = "photoUrl"
    const val CREATED_AT = "createdAt"
    const val USER_TYPE = "userType"

    //Items
    var currentShelf: LocalShelf? = null
    var currentBook: LocalBook? = null
    //var currentRemoteBook: RemoteBook? = null
}