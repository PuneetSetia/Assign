package com.assign.ui

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.assign.R
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity : AppCompatActivity() {
    fun showError(view : View, msg : String?, action : ()-> Unit){
        msg?.let { Snackbar.make(view, it, Snackbar.LENGTH_SHORT)
            .setAction(getString(R.string.retry)) { action() }.show() }
    }


    fun showMessage(view : View, msg : String?){
        msg?.let { Snackbar.make(view, it, Snackbar.LENGTH_SHORT).show() }
    }

}