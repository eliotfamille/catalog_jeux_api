package com.nenykely.catalog_jeux

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nenykely.catalog_jeux.ui.LivreDetailScreen
import com.nenykely.catalog_jeux.ui.LivreListScreen
import com.nenykely.catalog_jeux.ui.theme.Catalog_jeuxTheme
import com.nenykely.catalog_jeux.viewmodel.LivreViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Catalog_jeuxTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: LivreViewModel = viewModel()

    NavHost(navController = navController, startDestination = "liste") {
        composable("liste") {
            LivreListScreen(
                viewModel = viewModel,
                onLivreClick = { livre ->
                    viewModel.selectLivre(livre)
                    navController.navigate("detail")
                }
            )
        }
        composable("detail") {
            val selectedLivre by viewModel.selectedLivre.collectAsState()
            selectedLivre?.let { livre ->
                LivreDetailScreen(
                    livre = livre,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
