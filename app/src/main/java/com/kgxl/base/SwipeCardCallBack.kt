package com.kgxl.base

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.kgxl.base.bean.SwipeCardBean


/**
 * Created by zjy on 2022/12/2
 */
class SwipeCardCallBack : ItemTouchHelper.SimpleCallback {
    private var mDatas: MutableList<SwipeCardBean>? = null
    private var adapter: UniversalAdapter? = null
    private var mRv: RecyclerView? = null

    constructor(mDatas: MutableList<SwipeCardBean>?, adapter: UniversalAdapter?, mRv: RecyclerView?) : super(0,
        ItemTouchHelper.LEFT or ItemTouchHelper.UP or
                ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN
    ) {
        this.mDatas = mDatas
        this.adapter = adapter
        this.mRv = mRv
    }

    constructor(dragDirs: Int, swipeDirs: Int) : super(dragDirs, swipeDirs) {}
    constructor() : super(0,
        (ItemTouchHelper.LEFT or ItemTouchHelper.UP or
                ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN)
    ) {
        /*
        * 即我们对哪些方向操作关心。如果我们关心用户向上拖动，可以将
         填充swipeDirs参数为LEFT | RIGHT 。0表示从不关心。
        * */
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        //当已经滑动删除了的时候会被回掉--删除数据，循环的效果
        println("remove ${viewHolder.layoutPosition}")
        val remove: SwipeCardBean? = mDatas?.removeAt(viewHolder.layoutPosition)
        if (remove != null) {
            mDatas?.add(0, remove)
        }
        adapter?.notifyDataSetChanged()
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, (viewHolder)!!, dX, dY, actionState, isCurrentlyActive)
        //监听话滑动的距离--控制动画的执行程度
        //灵界点
        val maxDistance = (recyclerView.width * 0.5f).toDouble()
        val distance = Math.sqrt((dX * dX).toDouble())
        //动画执行的百分比
        var fraction = distance / maxDistance
        if (fraction > 1) {
            fraction = 1.0
        }
        val itemcount = recyclerView.childCount
        for (i in 0 until itemcount) {
            //执行
            val view: View = recyclerView.getChildAt(i)
            //几个view层叠的效果，错开的效果--便宜动画+缩放动画
            val level = itemcount - i - 1
            if (level > 0) {
                if (level < CardConfig.MAX_SHOW_COUNT - 1) {
                    view.setTranslationY((1 - CardConfig.TRANS_V_GAP * level + fraction * CardConfig.TRANS_V_GAP).toFloat())
                    view.setScaleX((1 - CardConfig.SCALE_GAP * level + fraction * CardConfig.SCALE_GAP).toFloat())
                    view.setTranslationY((1 - CardConfig.SCALE_GAP * level + fraction * CardConfig.SCALE_GAP).toFloat())
                }
            }
        }
    }
}