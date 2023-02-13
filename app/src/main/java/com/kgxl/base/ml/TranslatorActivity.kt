package com.kgxl.base.ml

import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import com.kgxl.base.BaseBindingActivity
import com.kgxl.base.ml.bean.Language
import com.kgxl.base.ml.vm.TranslatorVM
import com.kgxl.base.test.databinding.ActivityTranslatorBinding
import java.util.*

class TranslatorActivity : BaseBindingActivity<ActivityTranslatorBinding>() {
    private val vm by lazy { TranslatorVM() }
    private val rotationAnim by lazy {
        RotateAnimation(
            0f,
            180f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ).apply {
            duration = 450
            repeatCount = 1
            repeatMode = RotateAnimation.RESTART
            interpolator = LinearInterpolator()
        }
    }

    override fun initViewBinding(): ActivityTranslatorBinding {
        return ActivityTranslatorBinding.inflate(layoutInflater)
    }

    override fun initView() {
        mViewBinding.spannerTarget.apply {
            val arrayAdapter =
                ArrayAdapter(this@TranslatorActivity, android.R.layout.simple_selectable_list_item, vm.getAllLanguage())
            this.adapter = arrayAdapter
            this.setSelection(arrayAdapter.getPosition(Language("en", "英语")))
            onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    vm.targetLanguage.value = arrayAdapter.getItem(position)?.code
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
        }

        mViewBinding.spannerSource.apply {
            val arrayAdapter =
                ArrayAdapter(this@TranslatorActivity, android.R.layout.simple_selectable_list_item, vm.getAllLanguage())
            this.adapter = arrayAdapter
            this.setSelection(arrayAdapter.getPosition(Language(Locale.getDefault().language, Locale(Locale.getDefault().language).displayName)))
            onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    vm.sourceLanguage.value = arrayAdapter.getItem(position)?.code
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
        }
        mViewBinding.btnSwap.setOnClickListener {
            mViewBinding.btnSwap.animation = rotationAnim
            rotationAnim.start()
            val lastSourcePosition = mViewBinding.spannerSource.selectedItemPosition
            val lastTargetPosition = mViewBinding.spannerTarget.selectedItemPosition
            mViewBinding.spannerTarget.setSelection(lastSourcePosition)
            mViewBinding.spannerSource.setSelection(lastTargetPosition)
        }
        mViewBinding.btnTranslate.setOnClickListener {
            val text = mViewBinding.etText.text.toString()
            mViewBinding.tvResult.text = "翻译中..."
            vm.translate(text)
        }
    }

    override fun initData() {
    }

    override fun initObserver() {
        vm.result.observe(this) {
            mViewBinding.tvResult.text = it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.translator?.close()
    }

}