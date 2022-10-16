package com.bigpi.movie.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.fragment.app.Fragment


fun View.hideSoftInput() {
    val manager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    manager.hideSoftInputFromWindow(windowToken, 0)
}

fun Fragment.setOnBackPressHandler(f: () -> Unit) {
    this.requireActivity().onBackPressedDispatcher.addCallback(this) {
        f.invoke()
    }
}