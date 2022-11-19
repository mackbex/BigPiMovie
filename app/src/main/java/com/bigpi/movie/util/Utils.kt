package com.bigpi.movie.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


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

inline fun <T> Flow<T>.launchAndCollectIn(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend CoroutineScope.(T) -> Unit
) = owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(minActiveState) {
        collect {
            action(it)
        }
    }
}