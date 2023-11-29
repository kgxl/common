package com.kgxl.base.view

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * Created by kgxl on 2022/11/28
 */
class CustomItemDecoration private constructor(val build: Builder) : ItemDecoration() {

    class Builder {
        var needTop = true
        var needBottom = true
        var space = 0
        var orientation = OrientationHelper.VERTICAL

        fun setNeedTop(needTop: Boolean): Builder {
            this.needTop = needTop
            return this
        }

        fun setNeedBottom(needBottom: Boolean): Builder {
            this.needBottom = needBottom
            return this
        }

        fun setSpace(space: Int): Builder {
            this.space = space
            return this
        }

        fun setOrientation(orientation: Int): Builder {
            this.orientation = orientation
            return this
        }

        fun build(): CustomItemDecoration {
            return CustomItemDecoration(this)
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.layoutManager is GridLayoutManager) {
            //grid
            val pos = parent.getChildAdapterPosition(view)
            val spanCount = (parent.layoutManager as GridLayoutManager).spanCount
            if (build.needTop && pos < spanCount) {
                outRect.top = build.space
            }
            if (pos % spanCount == 0) {
                outRect.left = build.space
                outRect.right =
                    build.space / 2
            } else {
                outRect.left =
                    build.space / 2
                outRect.right =
                    build.space
            }
            outRect.bottom = build.space
        } else if (parent.layoutManager is StaggeredGridLayoutManager) {
            //瀑布流先不管了
        } else {
            if (build.orientation == OrientationHelper.VERTICAL) {
                val pos = parent.getChildAdapterPosition(view)
                if (build.needTop && pos == 0) {
                    outRect.top = build.space
                }
                outRect.bottom = build.space // 竖直方向
            } else {
                outRect.right = build.space // 水平方向
            }
        }

    }
}