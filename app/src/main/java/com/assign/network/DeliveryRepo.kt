package com.assign.network

import android.content.Context
import com.assign.R
import com.assign.beans.Delivery
import com.assign.beans.Result
import com.assign.db.AppDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class DeliveryRepo @Inject constructor(
    private val context: Context,
    private val appDb: AppDB,
    private val networkApi: ApiInterface
) {

    suspend fun getDelivery(start: Int, count: Int): Result {
        val data = getDataFromDb(start, count)
        return if (data.size == count)
            Result.SUCCESS(data)
        else
            getDeliveryFromNetwork(start, count)
    }

    private suspend fun getDataFromDb(start: Int, count: Int): List<Delivery> {
        return withContext(Dispatchers.IO) {
            appDb.deliveryDao().getDeliveries(start, start + count)
        }
    }

    private suspend fun getDeliveryFromNetwork(
        start: Int,
        count: Int
    ): Result {
        return try {
            val dataList = networkApi.getDeliveriesAsync(start, count).await()
            if (dataList.isNotEmpty()) {
                insertData(dataList)
                Result.SUCCESS(dataList)
            } else {
                Result.ERROR(context.getString(R.string.no_data_msg))
            }

        } catch (ex: Exception) {
            handleError(ex)
        }
    }

    fun handleError(t: Throwable): Result {
        return when (t) {
            is IOException -> Result.ERROR(context.getString(R.string.no_internet_msg))
            else -> Result.ERROR(t.localizedMessage)
        }
    }

    private fun insertData(listData: List<Delivery>) {
        CoroutineScope(Dispatchers.IO).launch {
            appDb.deliveryDao().insertAll(listData)
        }
    }
}