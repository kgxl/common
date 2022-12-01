package com.kgxl.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by zjy on 2022/11/29
 */
object RetrofitHelper {
    private const val defaultTimeout = 30L

    @Volatile
    var resetOkhttp = false //重新设置okhttp
    inline fun <reified T> create(baseUrl: String): T {
        resetOkhttp = false
        val retrofit =
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkhttpClient())
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.gson))
                .build()
        return retrofit.create(T::class.java) as T
    }

    private val defaultClient: OkHttpClient by lazy {
        OkHttpClient().newBuilder().callTimeout(defaultTimeout, TimeUnit.SECONDS).connectTimeout(defaultTimeout, TimeUnit.SECONDS).readTimeout(defaultTimeout, TimeUnit.SECONDS).writeTimeout(defaultTimeout, TimeUnit.SECONDS).retryOnConnectionFailure(true).addInterceptor(HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }).build()
    }

    private var userSetClient: OkHttpClient? = null

    fun resetOkhttpClient() {
        resetOkhttp = true
    }

    fun setOkhttpClient(userSetClient: OkHttpClient) {
        this.userSetClient = userSetClient
    }

    fun getOkhttpClient(): OkHttpClient {
        return if (userSetClient == null) defaultClient else userSetClient!!
    }

}