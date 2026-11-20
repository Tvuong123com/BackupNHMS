package com.eldercare.companion.data.network

import com.eldercare.companion.data.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()

        val token = sessionManager.authToken
        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        requestBuilder.header("Accept", "application/json")
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
