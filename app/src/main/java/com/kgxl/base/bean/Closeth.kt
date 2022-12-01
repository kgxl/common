package com.kgxl.base.bean
import androidx.annotation.Keep


/**
 * Created by zjy on 2022/11/30
 */
@Keep
data class Closeth(
    val `abstract`: String,
    val card: List<Card>,
    val catalog: List<String>,
    val copyrights: String,
    val customImg: String,
    val desc: String,
    val hasOther: Int,
    val id: Int,
    val image: String,
    val imageHeight: Int,
    val imageWidth: Int,
    val isSummaryPic: String,
    val key: String,
    val logo: String,
    val moduleIds: List<Long>,
    val newLemmaId: Int,
    val redirect: List<Any>,
    val src: String,
    val subLemmaId: Int,
    val title: String,
    val totalUrl: String,
    val url: String,
    val wapCatalog: List<String>,
    val wapUrl: String
) {
    @Keep
    data class Card(
        val format: List<String>,
        val key: String,
        val name: String,
        val value: List<String>
    )
}