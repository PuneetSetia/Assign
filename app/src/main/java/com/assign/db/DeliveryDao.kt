package com.assign.db

import androidx.room.*
import com.assign.beans.Delivery

@Dao
interface DeliveryDao {

    @Query("select * from delivery")
    fun getAll(): List<Delivery>

    @Query("select * from delivery where id >=:startID AND id <:endID")
    fun getDeliveries(startID: Int, endID: Int): List<Delivery>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Delivery>)


}