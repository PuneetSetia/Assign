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
import androidx.test.espresso.idling.CountingIdlingResource
import com.assign.Constants
import com.assign.MyApp
import com.assign.R
import com.assign.Utils
import com.assign.beans.Delivery
import com.assign.beans.Result
import com.assign.viewmodel.DeliveryViewModel
import com.assign.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

private const val QUERY = "key_query"
class DeliveryListActivity : BaseActivity(), LifecycleOwner, DeliveryAdapter.ItemSelectListener,
    DeliveryAdapter.ItemFilterListener {


    val counterLoading = CountingIdlingResource("Load_Data")
    private var queryString: String? = null
    lateinit var deliveryAdapter: DeliveryAdapter
    private val layoutManager = LinearLayoutManager(baseContext, RecyclerView.VERTICAL, false)
    private var isLoadingMore = false
    private var listItems = arrayListOf<Delivery>()
    private var startIndex = 0
    private lateinit var parent: View
    private lateinit var deliveryViewModel: DeliveryViewModel
    private lateinit var searchView: android.widget.SearchView
    private var showSearch = false

    @Inject
    lateinit var factory: ViewModelFactory


    private val liveDataObserver = Observer<Result> { result ->
        when (result) {
            is Result.SUCCESS -> {
                handleSuccess(result.data)
                counterLoading.decrementCounter()
            }
            is Result.LOADING -> {
                linearError.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                isLoadingMore = true

            }
            is Result.ERROR -> {
                handleError(result.exception)
                counterLoading.decrementCounter()
            }
        }
    }

    private val paginationScrollListener = object : PaginationScrollListener(layoutManager) {
        override fun totalPageCount(): Int {
            return Int.MAX_VALUE
        }

        override fun loadMoreItems() {

            isLoadingMore = true
            showMessage(
                parent,
                getString(R.string.loading_more)
            )

            loadData()

        }

        override fun isLastPage(): Boolean {
            return false
        }

        override fun isLoading(): Boolean {
            return isLoadingMore
        }
    }

    private val actionExpandListener = object : MenuItem.OnActionExpandListener {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MyApp.getDagger().init(this)

        parent = findViewById(R.id.parent)

        deliveryAdapter = DeliveryAdapter(baseContext, listItems, this, this)
        deliveryViewModel = ViewModelProviders.of(this, factory).get(DeliveryViewModel::class.java)

        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = deliveryAdapter
        recyclerView.addOnScrollListener(paginationScrollListener)

        queryString = savedInstanceState?.getString(QUERY)
        loadData()
    }


    override fun onItemSelected(delivery: Delivery) {
        val intent = Intent(this, DeliveryDetailsActivity::class.java)
        intent.putExtra(Constants.ARG_ID, delivery.id)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.search).actionView as
                android.widget.SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(false)

        val menuItem = menu.findItem(R.id.search)
        menuItem.isVisible = showSearch

        menuItem.setOnActionExpandListener(actionExpandListener)

        searchView.setOnQueryTextFocusChangeListener { _: View, hasFocus: Boolean ->
            if (!hasFocus) {
                searchView.setQuery("", true)
            }
        }

        searchView.setOnQueryTextListener(object :
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

        if (!queryString.isNullOrEmpty()) {
            searchView.isIconified = false
            searchView.setQuery(queryString, true)
            menuItem.expandActionView()
        }

        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val queryText = searchView.query.toString()
        outState.putString(QUERY, queryText)
    }


    override fun onItemFilter(size: Int) {
        if (size == 0) {
            linearError.visibility = View.VISIBLE
            txtError.visibility = View.VISIBLE
            btnRetry.visibility = View.GONE

            txtError.text = getString(R.string.no_item)
        } else {
            txtError.visibility = View.GONE
            btnRetry.visibility = View.VISIBLE
            linearError.visibility = View.GONE
        }
    }

    private fun loadData() {
        counterLoading.increment()
        deliveryViewModel.getDeliveries(startIndex, Constants.PAGE_SIZE).observe(this, liveDataObserver)
    }

    private fun isLoadingFirstPage(): Boolean {
        return startIndex == 0
    }

    private fun handleError(exception: String) {
        isLoadingMore = false
        progressBar.visibility = View.GONE

        if (isLoadingFirstPage()) {
            linearError.visibility = View.VISIBLE
            txtError.text = exception
            btnRetry.setOnClickListener {
                loadData()
            }
        } else
            showError(parent, exception) { loadData() }
    }

    private fun handleSuccess(data: List<Delivery>) {
        isLoadingMore = false
        progressBar.visibility = View.GONE
        linearError.visibility = View.GONE
        // Show Search only when Items are present
        if (isLoadingFirstPage()) {
            showSearch = true
            invalidateOptionsMenu()
        }
        if (data.isNotEmpty()) {
            if (!listItems.containsAll(data)) {
                listItems.addAll(data)
                startIndex = listItems.size
                deliveryAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun CountingIdlingResource.decrementCounter() {
        if (!this.isIdleNow) this.decrement()
    }
}
