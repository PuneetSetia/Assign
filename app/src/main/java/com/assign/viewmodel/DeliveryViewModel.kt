package com.assign.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.assign.R
import com.assign.beans.Result
import com.assign.network.DeliveryRepo

class DeliveryViewModel(private val context: Context, private val deliveryRepo: DeliveryRepo) : ViewModel() {

    private var mutableLiveData = MutableLiveData<Result>()

    // This will fetch the data from DeliveryRepo
    fun getDeliveries(startIndex: Int, count: Int):
            MutableLiveData<Result> {
        return if (startIndex >= 0 && count >= 0)
            deliveryRepo.getDelivery(startIndex, count)
        else {
            mutableLiveData.value = Result.ERROR(
                context.getString(R.string.invalid_input)
            )
            mutableLiveData
        }
    }

    fun getDelivery(id: Int): MutableLiveData<Result> {
        return getDeliveries(id, 1)
    }

}