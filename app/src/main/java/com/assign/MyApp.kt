package com.assign

import android.app.Application

class MyApp : Application(){
    override fun onCreate() {
        super.onCreate()
        context=this
       component = DaggerComponent.builder().
           contextModule(ContextModule(context)).build() as DaggerComponent

    }

    companion object {
        private lateinit var context : MyApp
        private lateinit var component : DaggerComponent

        fun getDagger() : DaggerComponent{
            return component
        }

    }
}