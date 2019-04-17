package com.assign

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

class Utils{
    companion object {
         fun hideKeyboard(context: Context){
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
             if(imm.isAcceptingText)
                 imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
         }

         fun openKeyboard(context: Context){
            val imm = context.
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }
}