package com.assign

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.assign.beans.Delivery
import com.assign.beans.Location
import com.assign.db.AppDB
import com.assign.db.DeliveryDao
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeliveryDaoAndroidTest{
    private lateinit var deliveryDao: DeliveryDao
    private lateinit var db: AppDB

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDB::class.java).build()
        deliveryDao = db.deliveryDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun writeDeliveryAndVerifyCount() {
        val sizeBefore = deliveryDao.getAll().size
        assert(sizeBefore==0)

        val delivery = Delivery(1,"description","url",
           Location(12.1,13.2,"address"))
        val delivery1 = Delivery(2,"description1","url1",
            Location(1.1,11.2,"address1"))

        val listDelivery = arrayListOf(delivery,delivery1)
        deliveryDao.insertAll(listDelivery)
        val sizeAfter = deliveryDao.getAll().size
        assert(  sizeAfter == 2)
    }

    @Test
    fun writeDeliveryAndVerifyValue() {
        val delivery = Delivery(11,"description","url",
            Location(12.1,13.2,"address"))

        val listDelivery = arrayListOf(delivery)
        deliveryDao.insertAll(listDelivery)
        val byName = deliveryDao.getDeliveries(11,
            12)
        val deliveryFetched= byName[0]

        Assert.assertThat(deliveryFetched,
            CoreMatchers.equalTo(delivery))
    }
}