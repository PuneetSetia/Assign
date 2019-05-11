package com.assign.network

import com.assign.beans.Delivery
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface{

    @GET("/deliveries")
     fun getDeliveriesAsync(@Query("offset") startIndex : Int,
                            @Query("limit") count : Int) : Deferred<List<Delivery>>


}