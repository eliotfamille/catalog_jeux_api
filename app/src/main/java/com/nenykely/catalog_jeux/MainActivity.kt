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
import com.nenykely.catalog_jeux.ui.JeuAddScreen
import com.nenykely.catalog_jeux.ui.JeuDetailScreen
import com.nenykely.catalog_jeux.ui.JeuListScreen
import com.nenykely.catalog_jeux.ui.theme.Catalog_jeuxTheme
import com.nenykely.catalog_jeux.viewmodel.JeuViewModel

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
    val viewModel: JeuViewModel = viewModel()

    NavHost(navController = navController, startDestination = "liste") {
        composable("liste") {
            JeuListScreen(
                viewModel = viewModel,
                onJeuClick = { jeu ->
                    viewModel.selectJeu(jeu)
                    navController.navigate("detail")
                },
                onAddClick = {
                    navController.navigate("ajouter")
                }
            )
        }
        composable("ajouter") {
            JeuAddScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("detail") {
            val selectedJeu by viewModel.selectedJeu.collectAsState()
            selectedJeu?.let { jeu ->
                JeuDetailScreen(
                    jeu = jeu,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
