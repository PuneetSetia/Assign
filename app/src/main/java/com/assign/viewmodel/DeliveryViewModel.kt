package com.assign.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.assign.R
import com.assign.beans.Delivery
import com.assign.network.DeliveryRepo
import com.assign.beans.Result

class DeliveryViewModel(context: Context, deliveryRepo: DeliveryRepo) : ViewModel() {

    private  var mDeliveryRepo = deliveryRepo
    private var mutableLiveData = MutableLiveData<Result<List<Delivery>>>()
    private val mContext = context

    // This will fetch the data from DeliveryRepo
    fun getDeliveries(startIndex : Int, count : Int):
            MutableLiveData<Result<List<Delivery>>>{
        return if(startIndex >=0 && count >= 0)
            mDeliveryRepo.getDelivery(startIndex,count)
        else
        {
            mutableLiveData.value= Result.error(mContext.
                getString(R.string.invalid_input))
            mutableLiveData
        }
    }

    fun getDelivery(id : Int): MutableLiveData<Result<List<Delivery>>> {
        return getDeliveries(id,1)
    }

}