package com.assign.network

import com.assign.beans.Delivery
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface{

    @GET("/deliveries")
    fun getDeliveries(@Query("offset") startIndex : Int,
                      @Query("limit") count : Int) : Call<List<Delivery>>


}