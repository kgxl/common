package com.kgxl.ml.translate

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

/**
 * Created by kgxl on 2023/1/30
 */
object TranslateHelper {
    const val TAG = "TranslateHelper"
    fun translate(text: String, targetTag: String, sourceTag: String = ""): Task<String> {
        val identifierTag: String = if (sourceTag.isEmpty()) {
            val languageIdentifier = LanguageIdentification.getClient()
            val result = languageIdentifier.identifyLanguage(text)
            if (result.isSuccessful) {
                val s = TranslateLanguage.fromLanguageTag(result.result) ?: ""
                println("isSuccessful $s")
                s
            } else {
                return Tasks.forCanceled()
            }
        } else {
            sourceTag
        }
        if (identifierTag.isEmpty()) {
            return Tasks.forCanceled()
        }
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.CHINESE)
            .build()
        val translation = Translation.getClient(options)
        return translation.downloadModelIfNeeded().continueWithTask {
            if (it.isSuccessful) {
                translation.translate(text)
            } else {
                Tasks.forException<String>(
                    it.exception ?: Exception("error")
                )
            }
        }
    }

    fun getLanguageTag(sourceTxt: String): String {
        val languageIdentifier = LanguageIdentification.getClient()
        val result = languageIdentifier.identifyLanguage(sourceTxt)
        return if (result.isSuccessful) {
            val s = TranslateLanguage.fromLanguageTag(result.result) ?: ""
            println("isSuccessful $s")
            s
        } else {
            ""
        }
    }

    fun getAllLanguage(): List<String>{
       return TranslateLanguage.getAllLanguages()
    }
}