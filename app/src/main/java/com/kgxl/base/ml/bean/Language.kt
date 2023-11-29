package com.kgxl.base.ml.bean

/**
 * Created by kgxl on 2023/1/31
 */
data class Language(val code: String, val displayName: String) {
    override fun toString(): String {
        return "${code}-${displayName}"
    }
}
