package com.ku.kuvn.custom_views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class HomeBannerImageView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredWidth / 3)
    }
}