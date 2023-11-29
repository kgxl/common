package com.kgxl.base

import com.kgxl.base.bean.Closeth
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Created by kgxl on 2022/11/29
 */
interface TestApi {

    @GET
    suspend fun getBaidu(@Url url: String = "http://baike.baidu.com/api/openapi/BaikeLemmaCardApi?scope=103&format=json&appid=379020&bk_key=%E9%93%B6%E9%AD%82&bk_length=600"): Closeth
}