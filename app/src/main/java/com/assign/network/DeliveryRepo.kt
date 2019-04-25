package com.assign.network

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.assign.R
import com.assign.beans.Delivery
import com.assign.beans.Result
import com.assign.db.AppDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class DeliveryRepo @Inject constructor(
    private val context: Context,
    private val appDb: AppDB,
    private val networkApi: ApiInterface
) {
    private var liveResult = MutableLiveData<Result>()

     fun getDelivery(start: Int, count: Int): MutableLiveData<Result> {
        CoroutineScope(Dispatchers.Default).launch {
            liveResult.postValue(Result.LOADING)
            val data = getDataFromDb(start, count)
            if (data.size == count)
                liveResult.postValue(Result.SUCCESS(data))
            else
                getDeliveryFromNetwork(start, count, liveResult)
        }

        return liveResult
    }

    private fun getDataFromDb(start: Int, count: Int): List<Delivery> {
        return appDb.deliveryDao().getDeliveries(start, start + count)
    }

    private fun getDeliveryFromNetwork(
        start: Int,
        count: Int, liveResult: MutableLiveData<Result>
    ) {

        networkApi.getDeliveries(start, count).enqueue(object : Callback<List<Delivery>> {
            override fun onFailure(call: Call<List<Delivery>>, t: Throwable) {
                return liveResult.postValue(handleError(t))
            }

            override fun onResponse(call: Call<List<Delivery>>, response: Response<List<Delivery>>) {
                val result = response.body()
                if (result != null) {
                    when {
                        result.isNotEmpty() -> {
                            insertData(result)
                            liveResult.postValue(Result.SUCCESS(result))
                        }
                        else -> liveResult.postValue(Result.ERROR(context.getString(R.string.no_data_msg)))
                    }
                }else {
                    liveResult.postValue(Result.ERROR(context.getString(R.string.something_wrong)))
                }
            }
        })
    }

    fun handleError(t: Throwable): Result {
        return if (t is IOException)
            Result.ERROR(context.getString(R.string.no_internet_msg))
        else
            Result.ERROR(t.localizedMessage)

    }

   private fun insertData(listData: List<Delivery>) {
        CoroutineScope(Dispatchers.Default).launch {
                appDb.deliveryDao().insertAll(listData)
        }
    }
}