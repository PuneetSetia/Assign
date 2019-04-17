package com.assign.network

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.assign.R
import com.assign.beans.Delivery
import com.assign.beans.Result
import com.assign.db.AppDB
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

open class DeliveryRepo @Inject constructor(context : Context, appDb: AppDB, networkApi: ApiInterface) {

    private var mContext = context
    private var mAppDb  = appDb
    private var mNetworkApi = networkApi

    private var liveResult= MutableLiveData<Result<List<Delivery>>>()

     fun getDelivery(start: Int, count : Int) : MutableLiveData<Result<List<Delivery>>> {
                GlobalScope.launch {
                    liveResult.postValue(Result.loading())
                    val data = getDataFromDb(start,count)
                    if(data.size == count)
                        liveResult.postValue(Result.success(data))
                    else
                         getDeliveryFromNetwork(start,count,liveResult)
                }

        return  liveResult
    }

    private fun getDataFromDb(start: Int, count: Int): List<Delivery> {
           return mAppDb.deliveryDao().getDeliveries(start, start + count)
    }

     fun getDeliveryFromNetwork(
        start: Int,
        count: Int, liveResult: MutableLiveData<Result<List<Delivery>>>) {

        mNetworkApi.getDeliveries(start,count).
            enqueue(object  : Callback<List<Delivery>>{

                override fun onFailure(call: Call<List<Delivery>>, t: Throwable) {
                    return liveResult.postValue(handleError(t))
                }

                override fun onResponse(call: Call<List<Delivery>>, response: Response<List<Delivery>>) {
                    val result = response.body()
                    if (result != null) {
                        when {
                            result.isNotEmpty() -> {
                                insertData(result)
                                liveResult.postValue(Result.success(result))
                            }
                            else -> liveResult.postValue(Result.error(mContext.getString(R.string.no_data_msg)))
                        }
                    }
                }
            })
    }

    fun handleError(t: Throwable): Result<List<Delivery>> {
        return if(t is IOException)
            Result.error(mContext.getString(R.string.no_internet_msg))
        else
            Result.error(t.localizedMessage)

    }

    fun insertData(listData : List<Delivery>?){
        GlobalScope.launch {
                  if (listData != null) {
                     mAppDb.deliveryDao().
                          insertAll(listData)
                  }
              }
    }
}