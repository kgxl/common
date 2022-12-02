package com.kgxl.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kgxl.base.bean.SwipeCardBean


/**
 * Created by zjy on 2022/12/2
 */
class UniversalAdapter(mData: ArrayList<SwipeCardBean>?, context: Context) : RecyclerView.Adapter<UniversalAdapter.UniversalViewHolder?>() {
    var mData: ArrayList<SwipeCardBean>?
    var context: Context

    init {
        this.mData = mData
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UniversalViewHolder {
        val view: View = LayoutInflater.from(context).inflate(com.kgxl.base.test.R.layout.recylerview_item, null)
        return UniversalViewHolder(view)
    }

    override fun onBindViewHolder(holder: UniversalViewHolder, position: Int) {
        holder.recy_item_im.setBackgroundResource(mData!![position].resoutimage)
        holder.recy_item_tv.text = mData!![position].title
    }

    override fun getItemCount(): Int {
        return if (mData == null) 0 else mData!!.size
    }

    inner class UniversalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recy_item_tv: TextView
        var recy_item_im: ImageView

        init {
            recy_item_im = itemView.findViewById(com.kgxl.base.test.R.id.recy_item_im)
            recy_item_tv = itemView.findViewById(com.kgxl.base.test.R.id.recy_item_tv)
        }
    }
}
