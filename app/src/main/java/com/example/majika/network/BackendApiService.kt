package com.example.majika.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.POST
import retrofit2.http.Path

private const val BASE_URL = "http://192.168.18.241:8000"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface BackendApiService {
    @POST("v1/payment/{code}")
    suspend fun pay(@Path(value="code", encoded = true) code: String): PaymentResponse
}

object BackendApi {
    val service: BackendApiService by lazy {
        retrofit.create(BackendApiService::class.java)
    }
}
