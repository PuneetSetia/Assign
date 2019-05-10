package com.assign.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.assign.R
import com.assign.beans.Delivery
import com.assign.beans.Result
import com.assign.network.DeliveryRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeliveryViewModel(private val context: Context, private val deliveryRepo: DeliveryRepo) : ViewModel() {

     val deliveryData = MutableLiveData<Result>()

    // This will fetch the data from DeliveryRepo
    fun getDeliveries(startIndex: Int, count: Int){
        CoroutineScope(Dispatchers.IO).launch {
            deliveryData.postValue(Result.LOADING)
            val result = withContext(Dispatchers.Default) {
                if (startIndex >= 0 && count >= 0)
                    deliveryRepo.getDelivery(startIndex, count)
                else
                    Result.ERROR(
                        context.getString(R.string.invalid_input))
                }
            deliveryData.postValue(result)
        }
    }

    fun getDelivery(id: Int)= getDeliveries(id, 1)
}