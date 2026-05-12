package com.nenykely.catalog_jeux.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    // Utilisation de 10.0.2.2 pour l'émulateur (équivalent à localhost sur le PC)
    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Accept", "application/json")
                .build()
            
            val response = chain.proceed(request)
            
            // Log pour le débogage si nécessaire (visible dans Logcat)
            if (!response.isSuccessful) {
                android.util.Log.e("RetrofitInstance", "Erreur serveur: ${response.code}")
            }
            
            response
        }
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
