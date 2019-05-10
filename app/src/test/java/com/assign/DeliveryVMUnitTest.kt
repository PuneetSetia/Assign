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
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

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
    private val mockedDeferred = CompletableDeferred<List<Delivery>>()

    @Before
    fun init() {
        deliveryRepo= DeliveryRepo(context,appDB,apiInterface)
        deliveryViewModel = DeliveryViewModel(context,deliveryRepo)
    }

    @Test
    fun getDelivery_Invalid_Input(){
        whenever(context.getString(R.string.invalid_input)).thenReturn("Invalid Input")
        runBlocking {
            deliveryViewModel.getDelivery(-1)
        }
         deliveryViewModel.deliveryData
            .test().
             awaitValue()
            .map { (it as? Result.ERROR)?.exception }
            .assertValue("Invalid Input")


    }

    @Test
    fun getDeliveries_Invalid_Input(){
        whenever(context.getString(R.string.invalid_input)).thenReturn("Invalid Input")

        runBlocking {
            deliveryViewModel.getDeliveries(-1,0)
        }

        deliveryViewModel.deliveryData.test().
            awaitValue()
            .map { (it as? Result.ERROR)?.exception }
            .assertValue("Invalid Input")
    }

    @Test
    fun getDeliveries_Valid_Input(){
        val testData = TestUtils.getData(startIndex, count)

        val mutableLiveData = MutableLiveData<Result>()
        mutableLiveData.value = Result.SUCCESS(testData)

        whenever(appDB.deliveryDao()).thenReturn(deliveryDao)
        whenever(appDB.deliveryDao().getDeliveries(startIndex,Constants.PAGE_SIZE)).
            thenReturn((mutableLiveData.value as? Result.SUCCESS)?.data)
       whenever(apiInterface.getDeliveries(startIndex,Constants.PAGE_SIZE)).thenReturn(mockedDeferred)
        mockedDeferred.complete(testData)
        runBlocking {
            deliveryViewModel.getDeliveries(startIndex,Constants.PAGE_SIZE)

        }

            deliveryViewModel.deliveryData.test().
            awaitValue().
            awaitNextValue().map { (it as? Result.SUCCESS)?.data
        }.assertValue(testData)

    }


    @Test
    fun getDeliveries_Success(){
        val testData = TestUtils.getData(1, 1)

        val mutableLiveData = MutableLiveData<Result>()
        mutableLiveData.value = Result.SUCCESS(testData)

        whenever(appDB.deliveryDao()).thenReturn(deliveryDao)
        whenever(appDB.deliveryDao().getDeliveries(1,1)).
            thenReturn((mutableLiveData.value as? Result.SUCCESS)?.data)
        whenever(apiInterface.getDeliveries(1,1)).
            thenReturn(mockedDeferred)

        mockedDeferred.complete(testData)
        deliveryViewModel.getDeliveries(1,1)
            deliveryViewModel.deliveryData.test().
            awaitValue().
            awaitNextValue().map { (it as? Result.SUCCESS)?.data
        }.assertValue(testData)
    }

    @Test
    fun getDeliveries_PartialPresent_IsDataFetchedFromServer(){
        whenever(appDB.deliveryDao())
            .thenReturn(deliveryDao)
        val tempData = TestUtils.getData(startIndex,count)

        whenever(deliveryDao.
            getDeliveries(startIndex,startIndex + count + 10)).thenReturn(tempData)
        runBlocking {
            deliveryViewModel.
                getDeliveries(startIndex,count+10)
        }

        verify(apiInterface, times(1)).
            getDeliveries(startIndex,count+10)
    }

    @Test
    fun getDeliveries_DataPresent_IsDataFetchedFromDB(){
        whenever(appDB.deliveryDao())
            .thenReturn(deliveryDao)

        whenever(deliveryDao.getDeliveries(startIndex,startIndex + count)).
            thenReturn(TestUtils.getData(startIndex, count))

        runBlocking {
            deliveryViewModel.
                getDeliveries(startIndex,count)
        }
        verify(apiInterface, times(0)).
            getDeliveries(startIndex,count)
    }

    @Test
    fun getDeliveries_Error(){
        whenever(appDB.deliveryDao()).thenReturn(deliveryDao)
        whenever(appDB.deliveryDao().getDeliveries(startIndex,Constants.PAGE_SIZE)).
            thenReturn(null)
        whenever(apiInterface.getDeliveries(startIndex,Constants.PAGE_SIZE)).thenReturn(mockedDeferred)
        mockedDeferred.completeExceptionally(java.lang.RuntimeException("Bad Request"))


        deliveryViewModel.getDeliveries(startIndex,Constants.PAGE_SIZE)
            deliveryViewModel.deliveryData.test()
            .awaitValue().awaitNextValue().map { (it as? Result.ERROR) }
            .assertValue(Result.ERROR("Bad Request"))
    }
}