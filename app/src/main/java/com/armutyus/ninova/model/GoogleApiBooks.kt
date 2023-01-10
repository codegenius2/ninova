package com.armutyus.ninova.model

data class GoogleApiBooks(
    val items: List<DataModel.GoogleBookItem>?,
    val kind: String,
    val totalItems: Int
)