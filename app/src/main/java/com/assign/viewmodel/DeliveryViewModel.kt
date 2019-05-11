package com.assign.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.assign.R
import com.assign.beans.Result
import com.assign.network.DeliveryRepo
import kotlinx.coroutines.*

class DeliveryViewModel(private val context: Context, private val deliveryRepo: DeliveryRepo) : ViewModel() {

     val deliveryData = MutableLiveData<Result>()
     private val viewModelJob = SupervisorJob()
     private val scope = CoroutineScope(Dispatchers.IO
             + viewModelJob)

    // This will fetch the data from DeliveryRepo
    fun getDeliveries(startIndex: Int, count: Int){
        scope.launch {
            deliveryData.postValue(Result.LOADING)
            val result = withContext(Dispatchers.IO) {
                if (startIndex >= 0 && count >= 0)
                    deliveryRepo.getDelivery(startIndex, count)
                else
                    Result.ERROR(
                        context.getString(R.string.invalid_input))
                }
            deliveryData.postValue(result)
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun getDelivery(id: Int)= getDeliveries(id, 1)
}