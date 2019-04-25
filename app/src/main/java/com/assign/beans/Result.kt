package com.assign.beans


sealed class Result{
    object LOADING : Result()
    data class ERROR(val exception: String) : Result()
    data class SUCCESS(val data : List<Delivery>) : Result()
}
