package com.assign.network

import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import androidx.lifecycle.ViewModelProvider
import com.assign.BuildConfig

@Module
class DeliveryNetworkModule{
    private val baseUrl = BuildConfig.BASE_URL
    private val timeOutSecs = 60L

    @Singleton
    @Provides
    fun getRetroFitApi(okHttpClient: OkHttpClient, gSonFactory : GsonConverterFactory) : ApiInterface {
        val instance = Retrofit.Builder()
            .addConverterFactory(gSonFactory)
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .build()
        return instance.create(ApiInterface::class.java)
    }

    @Singleton
    @Provides
    fun getHttpFactory(): OkHttpClient {
        return OkHttpClient().newBuilder().
            readTimeout(timeOutSecs, TimeUnit.SECONDS)
            .connectTimeout(timeOutSecs, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun getJsonFactory() : GsonConverterFactory {
        return  GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    fun getViewModelFactory(context : Context , myRepo : DeliveryRepo): ViewModelProvider.Factory {
        return ViewModelFactory(myRepo, context)
    }
}