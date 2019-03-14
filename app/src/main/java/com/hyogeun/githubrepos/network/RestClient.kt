package com.hyogeun.githubrepos.network

import android.net.Uri
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hyogeun.githubrepos.Application
import com.hyogeun.githubrepos.BuildConfig
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * Created by hyogeun.park on 2017. 12. 20..
 */
object RestClient {
    private val BASE_URL by lazy { getBaseUrl() }

    val service: APIService by lazy { getClient() }
    val GSON by lazy { createGson() }

    private fun getClient(): APIService {
        val networkCacheInterceptor = Interceptor { chain ->
            val cacheControl = CacheControl.Builder()
                .maxAge(1, TimeUnit.SECONDS)
                .build()

            val request = chain.request().newBuilder()
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Cache-Control", cacheControl.toString())
                .build()

            chain.proceed(request)
        }

        val cacheSize = 1 * 1024 * 1024 // 10 MB
        val httpCacheDirectory = File(Application.getContext().cacheDir, "http-cache")
        val cache = Cache(httpCacheDirectory, cacheSize.toLong())

        val okHttpClient = OkHttpClient.Builder()
            .cache(cache)
            .addNetworkInterceptor(networkCacheInterceptor)
            .build()

        val client = Retrofit.Builder()
            .validateEagerly(true)
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return client.create(APIService::class.java)
    }


    private fun getBaseUrl(): String {
        val url = URL(BuildConfig.APISERVER)
        val builder = Uri.Builder()
            .scheme(url.protocol)
            .authority(url.authority)
            .path(url.path)
        return "$builder/"
    }

    private fun createGson(): Gson = GsonBuilder().create()
}