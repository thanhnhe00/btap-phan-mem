package com.example.coursemanagement.data.remote

import com.example.coursemanagement.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiProvider {
    val isConfigured: Boolean = !BuildConfig.MOCKAPI_BASE_URL.contains("YOUR_PROJECT_ID") &&
        BuildConfig.MOCKAPI_BASE_URL.startsWith("https://") &&
        BuildConfig.MOCKAPI_BASE_URL.endsWith("/")

    val service: MockApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.MOCKAPI_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MockApiService::class.java)
    }
}
