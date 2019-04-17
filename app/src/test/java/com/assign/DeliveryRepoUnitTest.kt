package com.assign

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.assign.beans.Delivery
import com.assign.beans.Location
import com.assign.beans.Result
import com.assign.db.AppDB
import com.assign.db.DeliveryDao
import com.assign.network.ApiInterface
import com.assign.network.DeliveryRepo
import com.jraska.livedata.test

import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.RuntimeException

@RunWith(MockitoJUnitRunner.Silent::class)
class DeliveryRepoUnitTest {

    @Rule @JvmField val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var deliveryRepo: DeliveryRepo
    private var appDB = mock<AppDB>()
    private var context = mock<Context>()
    private var apiInterface = mock<ApiInterface>()
    private val deliveryDao = mock<DeliveryDao>()

    private val startIndex=0
    private val count = Constants.PAGE_SIZE

    @Before
    fun init() {
        deliveryRepo= DeliveryRepo(context,appDB,apiInterface)
    }

    @Test
    fun getDelivery_DataPresent_IfDataFetchedFromDB(){
        whenever(appDB.deliveryDao())
                .thenReturn(deliveryDao)

        whenever(deliveryDao.getDeliveries(startIndex,startIndex + count)).
            thenReturn(TestUtils.getData(startIndex, count))

        runBlocking {
            deliveryRepo.
                getDelivery(startIndex,count)
        }
        verify(apiInterface, times(0)).
            getDeliveries(startIndex,count)

    }

    @Test
    fun getDelivery_PartialPresent_IfDataFetchedFromServer(){
            whenever(appDB.deliveryDao())
                .thenReturn(deliveryDao)
            val tempData = TestUtils.getData(startIndex,count)

        whenever(deliveryDao.
            getDeliveries(startIndex,startIndex + count + 10)).thenReturn(tempData)
        runBlocking {
            deliveryRepo.
                getDelivery(startIndex,count+10)
        }

        verify(apiInterface, times(1)).
            getDeliveries(startIndex,count+10)

    }


    @Test
    fun handleError_NoInternetError(){
        val errorMsg = "Please check your internet connection and try again."
        whenever(context.getString(R.string.no_internet_msg)).
            thenReturn(errorMsg)
       val value = deliveryRepo.handleError(IOException())

        assert(value.exception!=null)
        assert(value.data==null)
        assert(value.status==Result.Status.ERROR)
        assert(value.exception.equals(errorMsg))
    }

    @Test
    fun handleError_OtherErrors(){
        val errMsg="Error Message"
        val value = deliveryRepo.handleError(RuntimeException(errMsg))

        assert(value.exception!=null)
        assert(value.data==null)
        assert(value.status==Result.Status.ERROR)
        assert(value.exception.equals(errMsg))
    }


    private val mockedCall = mock<Call<List<Delivery>>>()

    @Test
    fun getDelivery_Success(){
       whenever(appDB.deliveryDao()).thenReturn(deliveryDao)
        whenever(apiInterface.getDeliveries(11,1)).thenReturn(mockedCall)

        val delivery = Delivery(11,"description","url",
            Location(12.1,13.2,"address")
        )
        val listDelivery = arrayListOf(delivery)

        doAnswer {invocationOnMock ->
            val callback = invocationOnMock.getArgument<Callback<List<Delivery>>>(0)
            callback.onResponse(mockedCall, Response.success(listDelivery))
        }.`when`(mockedCall).enqueue(any())


        deliveryRepo.getDelivery(11,1).test()
            .awaitValue()
            .awaitNextValue().map { it.data }
            .assertValue(listDelivery)
    }

    @Test
    fun getDeliveryFromNetwork_Success(){
        val mutableLiveData = MutableLiveData<Result<List<Delivery>>>()
        whenever(appDB.deliveryDao()).thenReturn(deliveryDao)
        whenever(apiInterface.getDeliveries(11,1)).thenReturn(mockedCall)

        val delivery = Delivery(11,"description","url",
            Location(12.1,13.2,"address")
        )
        val listDelivery = arrayListOf(delivery)

        doAnswer {invocationOnMock ->
            val callback = invocationOnMock.getArgument<Callback<List<Delivery>>>(0)
            callback.onResponse(mockedCall, Response.success(listDelivery))
        }.`when`(mockedCall).enqueue(any())

        deliveryRepo.getDeliveryFromNetwork(11,1,mutableLiveData)
            mutableLiveData.test()
            .awaitValue().map { it.data }
            .assertValue(listDelivery)
    }

    @Test
    fun getDeliveryFromNetwork_Error(){
        val mutableLiveData = MutableLiveData<Result<List<Delivery>>>()
        whenever(apiInterface.getDeliveries(-1,0)).
            thenReturn(mockedCall)

        doAnswer {invocationOnMock ->
            val callback = invocationOnMock.getArgument<Callback<List<Delivery>>>(0)
            callback.onFailure(mockedCall,IOException("Bad Request"))
        }.`when`(mockedCall).enqueue(any())

        deliveryRepo.getDeliveryFromNetwork(-1,0,mutableLiveData)
        mutableLiveData.test()
            .awaitValue().map { it.status }
            .assertValue(Result.Status.ERROR)
    }
}
