package com.nenykely.catalog_jeux.api

import com.nenykely.catalog_jeux.model.Jeu
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("jeux")
    suspend fun getJeux(): Response<List<Jeu>>

    @Multipart
    @POST("jeux")
    suspend fun addJeu(
        @Part("titre") titre: RequestBody,
        @Part("plateforme") plateforme: RequestBody,
        @Part("developpeur") developpeur: RequestBody,
        @Part("annee_sortie") annee_sortie: RequestBody,
        @Part("score") score: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<Jeu>

    @PUT("jeux/{id}")
    suspend fun updateJeu(@Path("id") id: Int, @Body jeu: Jeu): Response<Jeu>
}
