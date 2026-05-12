package com.nenykely.catalog_jeux.api

import com.nenykely.catalog_jeux.model.Livre
import retrofit2.http.GET

interface ApiService {
    @GET("livres")
    suspend fun getLivres(): List<Livre>
}
