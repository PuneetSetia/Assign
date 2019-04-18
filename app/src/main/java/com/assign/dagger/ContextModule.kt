package com.assign.dagger

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ContextModule(appContext: Context){
    private val mApplication =appContext

    @Provides
    fun provideContext(): Context {
        return mApplication
    }
}
