package com.nenykely.catalog_jeux.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nenykely.catalog_jeux.api.RetrofitInstance
import com.nenykely.catalog_jeux.model.Jeu
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class JeuViewModel : ViewModel() {
    private val _allJeux = MutableStateFlow<List<Jeu>>(emptyList())
    
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _selectedJeu = MutableStateFlow<Jeu?>(null)
    val selectedJeu: StateFlow<Jeu?> = _selectedJeu

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _selectedCategory = MutableStateFlow("Tous")
    val selectedCategory: StateFlow<String> = _selectedCategory

    val platformesList = listOf("PC", "PS5", "PS4", "Xbox Series", "Switch", "Android", "iOS", "Multiplateforme", "Autre")
    val yearsList = (2025 downTo 1990).map { it.toString() }
    val categories: StateFlow<List<String>> = MutableStateFlow(listOf("Tous") + platformesList)

    // Fusion de la liste brute + recherche + filtre par catégorie
    val jeux: StateFlow<List<Jeu>> = combine(_allJeux, _searchText, _selectedCategory) { list, query, category ->
        list.filter { jeu ->
            val matchQuery = jeu.titre.contains(query, ignoreCase = true) || 
                            jeu.developpeur.contains(query, ignoreCase = true)
            val matchCategory = category == "Tous" || jeu.plateforme == category
            matchQuery && matchCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        fetchJeux()
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline

    fun fetchJeux() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = RetrofitInstance.api.getJeux()
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    _allJeux.value = list
                    _isOffline.value = false // Connecté
                } else {
                    _isOffline.value = true
                }
            } catch (e: Exception) {
                _isOffline.value = true // Erreur réseau (serveur éteint par ex)
            } finally {
                _loading.value = false
            }
        }
    }

    fun selectJeu(jeu: Jeu) {
        _selectedJeu.value = jeu
    }

    fun addJeu(
        context: Context,
        titre: String,
        developpeur: String,
        plateforme: String,
        annee: Int,
        score: Int,
        imageUri: Uri?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val titreRB = titre.toRequestBody("text/plain".toMediaTypeOrNull())
                val plateformeRB = plateforme.toRequestBody("text/plain".toMediaTypeOrNull())
                val developpeurRB = developpeur.toRequestBody("text/plain".toMediaTypeOrNull())
                val anneeRB = annee.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val scoreRB = score.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                var imagePart: MultipartBody.Part? = null
                imageUri?.let { uri ->
                    val file = uriToFile(context, uri)
                    if (file != null) {
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                    }
                }

                val response = RetrofitInstance.api.addJeu(
                    titreRB, plateformeRB, developpeurRB, anneeRB, scoreRB, imagePart
                )
                
                if (response.isSuccessful) {
                    fetchJeux()
                    onSuccess()
                } else {
                    Log.e("JeuViewModel", "Erreur ajout (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("JeuViewModel", "Crash ajout: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        val tempFile = File(context.cacheDir, "upload_image_${System.currentTimeMillis()}.jpg")
        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            tempFile
        } catch (e: Exception) {
            null
        }
    }
}
