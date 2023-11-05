package com.armutyus.ninova.model.googlebooksmodel

data class GoogleBookItemInfo(
    val authors: List<String>?,
    val categories: List<String>?,
    val description: String?,
    val imageLinks: ImageLinks?,
    val pageCount: Int?,
    val publishedDate: String?,
    val publisher: String?,
    val subtitle: String?,
    val title: String?
)