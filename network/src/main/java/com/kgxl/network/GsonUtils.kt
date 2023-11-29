package com.kgxl.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * Created by kgxl on 2022/11/30
 */
object GsonUtils {

    val gson: Gson by lazy { build() }
    fun toJson(any: Any): String {
        return gson.toJson(any)
    }

    inline fun <reified T> toBean(json: String): T {
        return gson.fromJson(json, T::class.java)
    }

    private fun build(): Gson {
        return GsonBuilder().setLenient().create()
    }
}