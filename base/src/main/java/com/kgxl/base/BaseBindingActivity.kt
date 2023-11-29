package com.kgxl.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * Created by kgxl on 2022/11/10
 */
abstract class BaseBindingActivity<V : ViewBinding> : AppCompatActivity() {

    protected val mViewBinding: V by lazy {
        initViewBinding()
    }

    abstract fun initViewBinding(): V

    abstract fun initView()

    abstract fun initData()

    abstract fun initObserver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)
        initView()
        initObserver()
        initData()
    }
}