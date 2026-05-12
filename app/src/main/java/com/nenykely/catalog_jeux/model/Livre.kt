package com.nenykely.catalog_jeux.model

data class Livre(
    val id: Int = 0,
    val titre: String = "",
    val auteur: String = "",
    val genre: String = "",
    val annee_publication: Int = 0,
    val score: Int = 0
)
