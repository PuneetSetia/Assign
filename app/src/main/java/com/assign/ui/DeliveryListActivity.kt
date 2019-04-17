package com.assign.ui

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assign.Constants
import com.assign.MyApp
import com.assign.R
import com.assign.network.ViewModelFactory
import com.assign.viewmodel.DeliveryViewModel
import com.assign.beans.Result
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import com.assign.Utils
import com.assign.beans.Delivery

class DeliveryListActivity : BaseActivity(), LifecycleOwner, DeliveryAdapter.ItemSelectListener,
    DeliveryAdapter.ItemFilterListener {
    override fun onItemFilter(size: Int) {
        if(size == 0)
        {
            linearError.visibility=View.VISIBLE
            txtError.visibility=View.VISIBLE
            btnRetry.visibility=View.GONE

            txtError.text =getString(R.string.no_item)
        }else{
            txtError.visibility=View.GONE
            btnRetry.visibility=View.VISIBLE
            linearError.visibility=View.GONE
        }
    }



    lateinit var deliveryAdapter: DeliveryAdapter
    private val layoutManager = LinearLayoutManager(baseContext,RecyclerView.VERTICAL,false)
    private var isLoadingMore = false
    private var listItems= arrayListOf<Delivery>()
    private var startIndex=0
    private lateinit var parent : View
    private lateinit var  deliveryViewModel  : DeliveryViewModel
    private lateinit var searchView : android.widget.SearchView

    @Inject
    lateinit var factory : ViewModelFactory


    private val liveDataObserver = Observer<Result<List<Delivery>>> { result ->
        when {
            result.status == Result.Status.SUCCESS -> handleSuccess(result)
            result.status ==  Result.Status.LOADING -> {
                linearError.visibility=View.GONE
                progressBar.visibility=View.VISIBLE
                isLoadingMore=true

            }
            else -> handleError(result)
        }
    }

    private val paginationScrollListener = object : PaginationScrollListener(layoutManager) {
        override fun totalPageCount(): Int {
            return Int.MAX_VALUE
        }

        override fun loadMoreItems() {
            isLoadingMore=true
            showMessage(parent,
                getString(R.string.loading_more))

            progressBar.visibility= View.VISIBLE
            deliveryViewModel.getDeliveries(startIndex, Constants.PAGE_SIZE)
            progressBar.visibility=View.GONE
        }

        override fun isLastPage(): Boolean {
            return false
        }

        override fun isLoading(): Boolean {
            return isLoadingMore
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)

       val component =  MyApp.getDagger()
       component.init(this)

       parent = findViewById(R.id.parent)

        deliveryAdapter = DeliveryAdapter(baseContext, listItems, this, this)
        deliveryViewModel = ViewModelProviders.of(this,factory).
            get(DeliveryViewModel::class.java)

        recyclerView.layoutManager=layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter=deliveryAdapter
        recyclerView.addOnScrollListener(paginationScrollListener)

        loadData()
    }

    private fun loadData(){
        deliveryViewModel.getDeliveries(startIndex, Constants.PAGE_SIZE)
            .observe(this, liveDataObserver)
    }

    private inline fun isLoadingFirstPage(): Boolean {
        return startIndex == 0
    }

    private fun handleError(result: Result<List<Delivery>>) {
        isLoadingMore=false
        progressBar.visibility=View.GONE

        if(isLoadingFirstPage())
        {
            linearError.visibility=View.VISIBLE
            txtError.text=result.exception
            btnRetry.setOnClickListener {
                loadData()
            }
        }
        else
            showError(parent,result.exception){loadData()}
    }

    private fun handleSuccess(result: Result<List<Delivery>>) {
        isLoadingMore=false
        progressBar.visibility=View.GONE

        // Show Search only when Items are present
        if(isLoadingFirstPage())
        {
            showSearch = true
            invalidateOptionsMenu()
        }
        if(result.data != null && result.data.isNotEmpty()){
            listItems.addAll(result.data)
            startIndex = listItems.size+1
            deliveryAdapter.notifyDataSetChanged()
        }
    }


    override fun onItemSelected(delivery: Delivery) {
        val intent = Intent(this, DeliveryDetailsActivity::class.java)
        intent.putExtra(Constants.ARG_ID,delivery.id)
        startActivity(intent)
    }

    private val actionExpandListener =  object : MenuItem.OnActionExpandListener{
        override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
            searchView.requestFocus()
            Utils.openKeyboard(baseContext)
            return true
        }

        override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
            searchView.clearFocus()
            return true
        }

    }
    private var showSearch = false

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
         searchView=menu.findItem(R.id.search).actionView as
                android.widget.SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.setIconifiedByDefault(false)

        val menuItem = menu.findItem(R.id.search)
        menuItem.isVisible = showSearch

        menuItem.setOnActionExpandListener(actionExpandListener)

        searchView.
            setOnQueryTextFocusChangeListener{ _: View, b: Boolean ->
            if(!b){
                searchView.setQuery("",true)
            }
        }

        searchView.setOnQueryTextListener(object  :
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
               deliveryAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                deliveryAdapter.filter.filter(newText)
                return false
            }

        })
        return true
    }
}
