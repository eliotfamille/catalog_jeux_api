package com.nenykely.catalog_jeux.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nenykely.catalog_jeux.api.RetrofitInstance
import com.nenykely.catalog_jeux.model.Livre
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LivreViewModel : ViewModel() {
    private val _livres = MutableStateFlow<List<Livre>>(emptyList())
    val livres: StateFlow<List<Livre>> = _livres

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _selectedLivre = MutableStateFlow<Livre?>(null)
    val selectedLivre: StateFlow<Livre?> = _selectedLivre

    init {
        fetchLivres()
    }

    fun fetchLivres() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _livres.value = RetrofitInstance.api.getLivres()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    fun selectLivre(livre: Livre) {
        _selectedLivre.value = livre
    }
}
