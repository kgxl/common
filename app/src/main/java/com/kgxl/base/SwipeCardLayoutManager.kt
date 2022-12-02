package com.kgxl.base

import android.content.Context
import android.util.TypedValue
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler


/**
 * Created by zjy on 2022/12/2
 */
class SwipeCardLayoutManager internal constructor(context: Context) : RecyclerView.LayoutManager() {
    var context: Context? = null
    var TRANS_Y_GAP: Int

    init {
        TRANS_Y_GAP = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f,
            context.resources.displayMetrics).toInt()
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State?) {
        //1.如何实现层叠效果--cardView.layout(l,t,r,b)
        //2.如何让8个条目中的4个展示在RecylerView里面
        //1在布局layout之前，将所有的子View先全部detach掉，然后放到Scrap集合里面缓存。
        detachAndScrapAttachedViews(recycler)
        //2)只将最上面4个view添加到RecylerView容器里面
        val itemCount = itemCount //8个
        val bottomPosition: Int
        bottomPosition = if (itemCount < 4) {
            0
        } else {
            itemCount - 4
        }
        for (i in bottomPosition until itemCount) {
            val view = recycler.getViewForPosition(i)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            val widthSpace = width - getDecoratedMeasuredWidth(view)
            val heightSpace = width - getDecoratedMeasuredHeight(view)
            //摆放cardView
            layoutDecorated(view,
                TRANS_Y_GAP,
                TRANS_Y_GAP,
                widthSpace / 2 + getDecoratedMeasuredWidth(view),
                heightSpace / 2 + getDecoratedMeasuredHeight(view))
            //层叠效果--Scale+TranslationY
            //层级的位置关系1/2/3/4
            val level = itemCount - i - 1
            if (level > 0) {
                if (level < CardConfig.MAX_SHOW_COUNT) {
                    view.translationY = (TRANS_Y_GAP * level).toFloat()
                    view.scaleX = 1 - CardConfig.SCALE_GAP * level
                    view.scaleY = 1 - CardConfig.SCALE_GAP * level
                }
            } else {
                view.translationY = (TRANS_Y_GAP * (level - 1)).toFloat()
                view.scaleX = 1 - CardConfig.SCALE_GAP * (level - 1)
                view.scaleY = 1 - CardConfig.SCALE_GAP * (level - 1)
            }
        }
    }
}