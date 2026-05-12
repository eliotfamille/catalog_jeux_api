package com.nenykely.catalog_jeux.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nenykely.catalog_jeux.model.Livre
import com.nenykely.catalog_jeux.viewmodel.LivreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LivreListScreen(viewModel: LivreViewModel, onLivreClick: (Livre) -> Unit) {
    val livres by viewModel.livres.collectAsState()
    val loading by viewModel.loading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Catalogue de Livres") })
        }
    ) { padding ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(livres) { livre ->
                    LivreItem(livre = livre, onClick = { onLivreClick(livre) })
                }
            }
        }
    }
}

@Composable
fun LivreItem(livre: Livre, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        headlineContent = { Text(livre.titre) },
        supportingContent = { Text(livre.auteur) },
        leadingContent = {
            Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(40.dp))
        },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                Text("${livre.score}/100")
                LinearProgressIndicator(
                    progress = { livre.score / 100f },
                    modifier = Modifier.width(50.dp)
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LivreDetailScreen(livre: Livre, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(livre.titre) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Auteur: ${livre.auteur}", style = MaterialTheme.typography.bodyLarge)
            Text("Genre: ${livre.genre}", style = MaterialTheme.typography.bodyLarge)
            Text("Année de publication: ${livre.annee_publication}", style = MaterialTheme.typography.bodyLarge)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Score: ${livre.score}/100", style = MaterialTheme.typography.titleMedium)
            LinearProgressIndicator(
                progress = { livre.score / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp)
            )
        }
    }
}
