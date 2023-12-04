package com.example.anifun.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit客户端
 * @author yu
 * @date 2023/07/03
 */
object RetrofitClient {

    private val instance: Retrofit by lazy {

        val logInterceptor = HttpLoggingInterceptor()

        val okhttpClient = OkHttpClient.Builder().addInterceptor(logInterceptor)
            .connectTimeout(5, TimeUnit.SECONDS)//设置超时时间
            .retryOnConnectionFailure(true).build()
        //显示日志
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        Retrofit.Builder()
            .client(okhttpClient)
            .baseUrl("https://api.bilibili.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * 获取实例
     * @param [clazz] clazz
     * @return [T]
     */
    fun <T> createApi(clazz: Class<T>): T {
        return instance.create(clazz) as T
    }
}

