package com.eldercare.companion.data.network

import com.eldercare.companion.data.SessionManager
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private var currentBaseUrl: String? = null
    private var cachedService: ApiService? = null

    fun getService(sessionManager: SessionManager): ApiService {
        val targetBaseUrl = sessionManager.baseUrl

        if (cachedService != null && targetBaseUrl == currentBaseUrl) {
            return cachedService!!
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(targetBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val service = retrofit.create(ApiService::class.java)
        currentBaseUrl = targetBaseUrl
        cachedService = service
        return service
    }

    fun invalidateCache() {
        cachedService = null
        currentBaseUrl = null
    }
}
