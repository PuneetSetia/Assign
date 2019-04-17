package com.assign.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.assign.beans.Delivery


@Database(entities = [Delivery::class], version = 1)
abstract class AppDB : RoomDatabase() {
    abstract fun deliveryDao(): DeliveryDao
}
