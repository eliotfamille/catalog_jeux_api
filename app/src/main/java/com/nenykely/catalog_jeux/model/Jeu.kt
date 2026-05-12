package com.nenykely.catalog_jeux.model

import com.google.gson.annotations.SerializedName

data class Jeu(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("titre") val titre: String = "Sans titre",
    @SerializedName("plateforme") val plateforme: String = "Inconnue",
    @SerializedName("developpeur") val developpeur: String = "Inconnu",
    @SerializedName("annee_sortie") val annee_sortie: Int = 0,
    @SerializedName("score") val score: Int = 0,
    @SerializedName("image") val image: String? = null
) {
    val fullImageUrl: String
        get() = if (image.isNullOrBlank()) "" else "http://10.0.2.2:8000/images/$image"
}
