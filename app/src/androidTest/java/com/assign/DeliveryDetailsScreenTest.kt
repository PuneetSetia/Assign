package com.assign

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.assign.ui.DeliveryDetailsActivity
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DeliveryDetailsScreenTest {

    @Rule
    @JvmField
    val deliveryDetailsActivityRule =
        ActivityTestRule<DeliveryDetailsActivity>(DeliveryDetailsActivity::class.java, true, false)
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun backButtonFinishActivity() {
        val intent = Intent()
        intent.putExtra(Constants.ARG_ID, 1)
        deliveryDetailsActivityRule.launchActivity(intent)
        onView(withContentDescription(context.getString(R.string.navigate_up))).perform(click())
        assertTrue(deliveryDetailsActivityRule.activity.isFinishing)

    }

}