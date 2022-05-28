package com.armutyus.ninova.constants

data class Response<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {

        fun <T> success(data: T?): Response<T> {
            return Response(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): Response<T> {
            return Response(Status.ERROR, data, msg)
        }

        fun <T> loading(data: T?): Response<T> {
            return Response(Status.LOADING, data, null)
        }

    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}