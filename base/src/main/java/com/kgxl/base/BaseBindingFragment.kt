package com.kgxl.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * Created by zjy on 2022/11/10
 */
abstract class BaseBindingFragment<V : ViewBinding> : Fragment() {

    protected lateinit var mViewBinding: V

    abstract fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?): V

    abstract fun initView()

    abstract fun initData()

    abstract fun initObserver()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewBinding = initViewBinding(inflater, container)
        initView()
        return mViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        initData()
    }
}