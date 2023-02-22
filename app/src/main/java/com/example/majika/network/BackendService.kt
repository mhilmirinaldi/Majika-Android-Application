package com.example.majika.network

import com.example.majika.ui.menu.ItemResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.GET

private const val BASE_URL = "http://192.168.18.241:8000"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface KeranjangAPI {
    @POST("v1/payment/{code}")
    suspend fun pay(@Path(value="code", encoded = true) code: String): PaymentResponse
}

interface ItemAPI{
    @GET("v1/menu")
    suspend fun getItems(): ItemResponse
}

object BackendApiKeranjang {
    val service: KeranjangAPI by lazy {
        retrofit.create(KeranjangAPI::class.java)
    }
}

object BackendApiItem{
    val itemApi: ItemAPI by lazy {
        retrofit.create(ItemAPI::class.java)
    }
}
