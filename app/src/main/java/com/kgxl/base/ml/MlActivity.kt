package com.kgxl.base.ml

import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.*
import com.kgxl.base.BaseBindingActivity
import com.kgxl.base.test.databinding.ActivityMlBinding
import com.kgxl.ml.segment.SegmentationSelfieHelper

class MlActivity : BaseBindingActivity<ActivityMlBinding>() {
    private var bitmap = MutableLiveData<Bitmap>()
    override fun initViewBinding(): ActivityMlBinding {
        return ActivityMlBinding.inflate(layoutInflater)
    }

    override fun initView() {
        mViewBinding.btnSelect.setOnClickListener {
//            open()
            val germanModel = TranslateRemoteModel.Builder(TranslateLanguage.ENGLISH).build()
            RemoteModelManager.getInstance().deleteDownloadedModel(germanModel)
        }

    }

    override fun initData() {

    }

    override fun initObserver() {
        bitmap.observe(this) {
            mViewBinding.ivSelfie.setImageBitmap(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            data?.data?.let {
                SegmentationSelfieHelper.startImage(this, it, bitmap)
                Glide.with(this).load(it).into(mViewBinding.ivSelect)
            }

        }
    }

    fun open() {
        val gallery = Intent(Intent.ACTION_PICK)
        gallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(gallery, 100)
    }

}