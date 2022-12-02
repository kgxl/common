package com.kgxl.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kgxl.base.ext.isFinishing

/**
 * Created by zjy on 2022/12/2
 */
abstract class BaseBottomSheetDialog<VB : ViewBinding> : BottomSheetDialogFragment() {

    lateinit var mViewBinding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewBinding = initViewBinding(inflater, container)
        viewBindData()
        return mViewBinding.root
    }

    abstract fun viewBindData()

    @NonNull
    abstract fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    fun safetyShow(fragmentManager: FragmentManager, tag: String) {
        runCatching {
            fragmentManager.executePendingTransactions()
            if (this.isAdded) return
            if (context.isFinishing()) {
                return
            }
            show(fragmentManager, tag)
        }
    }
}