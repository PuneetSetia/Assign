package com.assign.dagger

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ContextModule(private val appContext: Context){

    @Provides
    fun provideContext(): Context {
        return appContext
    }
}
