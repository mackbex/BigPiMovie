package com.bigpi.movie.ext

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bigpi.movie.R
import com.bigpi.movie.common.PagingLoadStateAdapter
import com.bigpi.movie.util.RecyclerViewDecoration

@BindingAdapter("paddingFirstItemHeight", "dividerHeight", "dividerPadding", "dividerColor", requireAll = false)
fun RecyclerView.setDivider(paddingFirstItemHeight: Float?, dividerHeight: Float?, dividerPadding: Float?, @ColorInt dividerColor: Int?) {
    val decoration = RecyclerViewDecoration(
        paddingFirstItemHeight = paddingFirstItemHeight ?: 0f,
        height = dividerHeight ?: 0f,
        padding = dividerPadding ?: 0f,
        color = dividerColor ?: Color.TRANSPARENT
    )

    addItemDecoration(decoration)
}

@BindingAdapter("android:src")
fun ImageView.glide(url:String?) {
    Glide.with(this.context)
        .load(url ?: kotlin.run { ColorDrawable(ContextCompat.getColor(this.context, R.color.grey_04)) })
        .fitCenter()
        .error(ColorDrawable(ContextCompat.getColor(this.context, R.color.grey_04)))
        .into(this)
}

@BindingAdapter("onEditorActionListener")
fun EditText.onEditorActionListener(listener: TextView.OnEditorActionListener) {
    this.setOnEditorActionListener(listener)
}

inline fun <reified T: ViewDataBinding> getDataBinding(parent: ViewGroup, id: Int): T =
    DataBindingUtil.inflate<T>(
        LayoutInflater.from(parent.context), id, parent, false
    )

fun <T : Any, VH : RecyclerView.ViewHolder> PagingDataAdapter<T, VH>.loadFooter(): ConcatAdapter {

    return this.withLoadStateFooter(
        footer = PagingLoadStateAdapter(this)
    )
}

@BindingAdapter("setAdapter")
fun <T,VH : RecyclerView.ViewHolder> RecyclerView.setAdapter(
    adapter: ListAdapter<T, VH>?,
) {
    this.adapter = adapter
}