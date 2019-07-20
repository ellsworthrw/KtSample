package com.diamondedge.ktsample

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.showKeyboard(focusDirection: Int) {
    if (requestFocus(focusDirection)) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.showSoftInput(this, 0)
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    if (imm?.isActive(this) == true) {
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()