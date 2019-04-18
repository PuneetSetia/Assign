package com.assign.dagger

import android.content.Context
import androidx.room.Room
import com.assign.Constants
import com.assign.db.AppDB
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

@Module
class DbModule{

    @Provides
    @Singleton
    @Inject
    fun getDB(context : Context) : AppDB{
        return  Room.databaseBuilder(context,AppDB::class.java,
            Constants.DB_NAME).build()
    }


}