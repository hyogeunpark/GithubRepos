package com.hyogeun.githubrepos.common

import android.databinding.BindingAdapter
import android.support.v7.widget.AppCompatImageView
import com.bumptech.glide.Glide

object Bindings {

    /**
     * NetworkImageView 관련
     */
    @BindingAdapter("image_url")
    @JvmStatic fun setImageUrl(networkImageView: AppCompatImageView, url: String) {
        Glide.with(networkImageView.context).load(url).into(networkImageView)
    }
}