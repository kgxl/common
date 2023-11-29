package com.kgxl.base.ml.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.kgxl.base.ml.bean.Language
import com.kgxl.ml.translate.TranslateHelper
import java.util.*

/**
 * Created by kgxl on 2023/1/31
 */
class TranslatorVM : ViewModel() {
    var translator: Translator? = null
    val result = MutableLiveData<String>()
    val targetLanguage = MutableLiveData<String>()
    val sourceLanguage = MutableLiveData<String>()
    fun getAllLanguage(): List<Language> {
        return TranslateHelper.getAllLanguage().map { Language(it, Locale(it).displayName) }
    }

    fun translate(txt: String) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage.value.toString())
            .setTargetLanguage(targetLanguage.value.toString())
            .build()
        translator = Translation.getClient(options)
        val conditions = DownloadConditions.Builder().requireWifi().build()
        translator?.downloadModelIfNeeded(conditions)?.addOnSuccessListener {
            translator?.translate(txt)?.addOnSuccessListener {
                println("result addOnSuccessListener ${it}")
                result.value = it
            }?.addOnFailureListener {
                println("result addOnFailureListener ${it.toString()}")
            }?.addOnCompleteListener {
                println("result addOnCompleteListener ${it.isCanceled} ${it.exception.toString()}")
                if (it.isSuccessful) {
                    println("result ${it.result}")
                }
            }?.addOnCanceledListener {
                println("result addOnCanceledListener")
            }
        }?.addOnFailureListener {
            println("downloadModel addOnFailureListener ${it.toString()}")
        }?.addOnCompleteListener {
            println("downloadModel addOnCompleteListener ${it.isCanceled} ${it.exception.toString()}")
        }?.addOnCanceledListener {
            println("downloadModel addOnCanceledListener")
        }
    }
}