package com.assign

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.assign.beans.Delivery
import com.assign.beans.Result
import com.assign.db.AppDB
import com.assign.db.DeliveryDao
import com.assign.network.ApiInterface
import com.assign.network.DeliveryRepo
import com.assign.viewmodel.DeliveryViewModel
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(MockitoJUnitRunner.Silent::class)
class DeliveryVMUnitTest {

    private val startIndex=1
    private val count = Constants.PAGE_SIZE
    private lateinit var deliveryViewModel : DeliveryViewModel

    @Rule
    @JvmField val instantExecutorRule = InstantTaskExecutorRule()
    private var deliveryRepo = mock<DeliveryRepo>()

    private var appDB = mock<AppDB>()
    private var context = mock<Context>()
    private var apiInterface = mock<ApiInterface>()
    private val deliveryDao = mock<DeliveryDao>()

    @Before
    fun init() {
        deliveryRepo= DeliveryRepo(context,appDB,apiInterface)
        deliveryViewModel = DeliveryViewModel(context,deliveryRepo)
    }

    @Test
    fun getDelivery_Invalid_Input(){
        whenever(context.getString(R.string.invalid_input)).thenReturn("Invalid Input")
        deliveryViewModel.getDelivery(-1).test().
            awaitValue()
            .map { it.exception }
            .assertValue("Invalid Input")
    }

    @Test
    fun getDeliveries_Invalid_Input(){
        whenever(context.getString(R.string.invalid_input)).thenReturn("Invalid Input")
        deliveryViewModel.getDeliveries(-1,0).test().
            awaitValue()
            .map { it.exception }
            .assertValue("Invalid Input")
    }

    @Test
    fun getDeliveries_Valid_Input(){
        val testData = TestUtils.getData(startIndex, count)

        val mockedCall = mock<Call<List<Delivery>>>()
        val mutableLiveData = MutableLiveData<Result<List<Delivery>>>()
        mutableLiveData.value = Result.success(testData)

        whenever(appDB.deliveryDao()).thenReturn(deliveryDao)
        whenever(appDB.deliveryDao().getDeliveries(startIndex,Constants.PAGE_SIZE)).thenReturn(mutableLiveData.value!!.data)
       whenever(apiInterface.getDeliveries(startIndex,Constants.PAGE_SIZE)).thenReturn(mockedCall)

        doAnswer {invocationOnMock ->
            val callback = invocationOnMock.getArgument<Callback<List<Delivery>>>(0)
            callback.onResponse(mockedCall, Response.success(testData))
        }.`when`(mockedCall).enqueue(any())

        deliveryViewModel.getDeliveries(startIndex,Constants.PAGE_SIZE).test().
            awaitValue().
            awaitNextValue().map { it.data
        }.assertValue(testData)
    }


    @Test
    fun getDelivery_Valid_Input(){
        val testData = TestUtils.getData(1, 1)

        val mockedCall = mock<Call<List<Delivery>>>()
        val mutableLiveData = MutableLiveData<Result<List<Delivery>>>()
        mutableLiveData.value = Result.success(testData)

        whenever(appDB.deliveryDao()).thenReturn(deliveryDao)
        whenever(appDB.deliveryDao().getDeliveries(1,1)).
            thenReturn(mutableLiveData.value!!.data)
        whenever(apiInterface.getDeliveries(1,1)).
            thenReturn(mockedCall)

        doAnswer {invocationOnMock ->
            val callback = invocationOnMock.getArgument<Callback<List<Delivery>>>(0)
            callback.onResponse(mockedCall, Response.success(testData))
        }.`when`(mockedCall).enqueue(any())

        deliveryViewModel.getDeliveries(1,1).test().
            awaitValue().
            awaitNextValue().map { it.data
        }.assertValue(testData)
    }

}