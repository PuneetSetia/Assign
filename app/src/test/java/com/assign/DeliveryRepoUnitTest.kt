package com.assign

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.assign.beans.Result
import com.assign.db.AppDB
import com.assign.network.ApiInterface
import com.assign.network.DeliveryRepo
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException

@RunWith(MockitoJUnitRunner.Silent::class)
class DeliveryRepoUnitTest {

    @Rule @JvmField val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var deliveryRepo: DeliveryRepo
    private var appDB = mock<AppDB>()
    private var context = mock<Context>()
    private var apiInterface = mock<ApiInterface>()

    @Before
    fun init() {
        deliveryRepo= DeliveryRepo(context,appDB,apiInterface)
    }

    @Test
    fun handleError_NoInternetError(){
        val errorMsg = "Please check your internet connection and try again."
        whenever(context.getString(R.string.no_internet_msg)).
            thenReturn(errorMsg)
       val value = deliveryRepo.handleError(IOException())

        assert(value is Result.ERROR)
        assert((value as? Result.ERROR)?.exception == errorMsg)
    }

    @Test
    fun handleError_OtherErrors(){
        val errMsg="Error Message"
        val value = deliveryRepo.handleError(RuntimeException(errMsg))

        assert(value is Result.ERROR)
        assert((value as? Result.ERROR)?.exception == errMsg)
    }

}
