package com.assign.beans


class Result<T> private constructor(val status: Status, val data: T?,
                                    val exception: String?) {
    enum class Status {
        SUCCESS, ERROR, LOADING
    }
    companion object {
        fun <T> success(data: T?): Result<T> {
            return Result(Status.SUCCESS, data, null)
        }
        fun <T> error(exception: String?): Result<T> {
            return Result(Status.ERROR, null, exception)
        }
        fun <T> loading(): Result<T> {
            return Result(Status.LOADING, null, null)
        }
    }

}