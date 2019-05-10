package com.assign

import android.app.Application
import com.assign.dagger.ContextModule
import com.assign.dagger.DaggerComponent

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        component = DaggerComponent.builder().contextModule(ContextModule(this)).build() as DaggerComponent

    }

    companion object {
        private lateinit var component: DaggerComponent

        fun getDagger(): DaggerComponent {
            return component
        }

    }
}