package com.assign.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.assign.R
import com.assign.network.DeliveryRepo
import javax.inject.Inject

class ViewModelFactory @Inject
constructor(private val repository: DeliveryRepo, private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeliveryViewModel::class.java)) {
            return DeliveryViewModel(context, repository) as T
        }
        throw IllegalArgumentException(context.getString(R.string.unknown_class))
    }
}
