package com.assign.dagger

import com.assign.ui.DeliveryListActivity
import com.assign.ui.DeliveryDetailsActivity
import dagger.Component
import javax.inject.Singleton

@Component(modules = [DeliveryNetworkModule::class, DbModule::class, ContextModule::class])
@Singleton
interface Component{
    fun init(deliveryListActivity: DeliveryListActivity)
    fun init(mapsActivity: DeliveryDetailsActivity)

}