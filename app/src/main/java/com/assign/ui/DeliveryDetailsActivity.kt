package com.assign.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.assign.*
import com.assign.R
import com.assign.beans.Delivery
import com.assign.beans.Result
import com.assign.dagger.Component
import com.assign.network.ViewModelFactory
import com.assign.viewmodel.DeliveryViewModel
import com.bumptech.glide.Glide
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import javax.inject.Inject

class DeliveryDetailsActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
   private lateinit var deliveryOb : Delivery
    private lateinit var imgView: ImageView
    private lateinit var txtView: TextView

    @Inject
    lateinit var factory : ViewModelFactory
    private lateinit var deliveryViewModel: DeliveryViewModel
    private var id: Int = 0
    private val zoomFactor = 4
    private lateinit var dagger: Component
    private lateinit var mapFragment : SupportMapFragment

    private val liveDataObserver = Observer<Result<List<Delivery>>> {
        val data = it.data
        val status = it.status
        if(status== Result.Status.SUCCESS)
            if(data!=null && data.isNotEmpty())
            {
                deliveryOb = data[0]
                txtView.text=deliveryOb.description
                Glide.with(baseContext).
                    load(deliveryOb.imageUrl).into(imgView)
                mapFragment.getMapAsync(this)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        Utils.hideKeyboard(baseContext)
        dagger = MyApp.getDagger()
        dagger.init(this)
        deliveryViewModel = ViewModelProviders.of(this,factory).
            get(DeliveryViewModel::class.java)

        id = intent.extras.getInt(Constants.ARG_ID)

       mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        imgView = layoutDelivery.findViewById(R.id.imgView)
        txtView=layoutDelivery.findViewById(R.id.txtDesc)
        deliveryViewModel.getDelivery(id).observe(this, liveDataObserver)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val sydney = LatLng(
            deliveryOb.location.lat
            , deliveryOb.location.lng)
        mMap.addMarker(MarkerOptions().position(sydney).title(deliveryOb.location.address))
        val zoom = mMap.maxZoomLevel- mMap.maxZoomLevel.div(zoomFactor)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,zoom))
    }
}
