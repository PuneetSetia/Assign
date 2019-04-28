package com.assign


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.assign.ui.DeliveryAdapter
import com.assign.ui.DeliveryDetailsActivity
import com.assign.ui.DeliveryListActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DeliveryListScreenTest {

    @Rule
    @JvmField
    val deliveryActivityRule = IntentsTestRule<DeliveryListActivity>(DeliveryListActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(deliveryActivityRule.activity.counterLoading)
    }

    @After
    fun release() {
        IdlingRegistry.getInstance().unregister(deliveryActivityRule.activity.counterLoading)
    }

    @Test
    fun itemClickLaunchDetailsActivity() {
        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<DeliveryAdapter.ViewHolder>(0, ViewActions.click())
        )
        intended(hasComponent(DeliveryDetailsActivity::class.java.name))
    }

    @Test
    fun checkLoadedItemsCount() {
        val count = deliveryActivityRule.activity.deliveryAdapter.itemCount
        assert(count == Constants.PAGE_SIZE)
    }

}