package com.kgxl.image

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation

/**
 * Created by zjy on 2023/2/1
 */
fun ImageView.loadCover(url: String, placeHolder: Int = 0, errorId: Int) {
    Glide.with(this).load(url).apply {
        if (placeHolder != 0) {
            this.placeholder(placeHolder).error(errorId).into(this@loadCover)
        } else {
            this.into(this@loadCover)
        }
    }
}

fun ImageView.loadCover(url: String, placeHolder: Drawable? = null, errorDrawable: Drawable) {
    Glide.with(this).load(url).apply {
        if (placeHolder != null) {
            this.placeholder(placeHolder).error(errorDrawable).into(this@loadCover)
        } else {
            this.into(this@loadCover)
        }
    }
}

fun ImageView.loadBlurCover(url: String, radius: Int, sampling: Int = 10) {
    Glide.with(this).load(url).apply(RequestOptions().transform(BlurTransformation(radius, sampling))).into(this)
}

fun ImageView.loadCircle(url: String) {
    Glide.with(this).load(url).apply(RequestOptions().circleCrop()).into(this)
}