package com.nenykely.catalog_jeux.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items as lazyItems
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.nenykely.catalog_jeux.model.Jeu
import com.nenykely.catalog_jeux.viewmodel.JeuViewModel
import com.nenykely.catalog_jeux.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JeuListScreen(
    viewModel: JeuViewModel,
    onJeuClick: (Jeu) -> Unit,
    onAddClick: () -> Unit
) {
    val jeux by viewModel.jeux.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    
    var isSearchActive by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        // IMAGE DE BACKGROUND GAMING
        AsyncImage(
            model = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?q=80&w=2070",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.5f // Très visible mais l'arrière-plan noir le garde sombre
        )

        Scaffold(
            containerColor = Color.Transparent, // Pour voir le background
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkBackground.copy(alpha = 0.8f),
                        titleContentColor = NeonCyan
                    ),
                    title = {
                        if (isSearchActive) {
                            TextField(
                                value = searchText,
                                onValueChange = { viewModel.onSearchTextChange(it) },
                                placeholder = { Text("RECHERCHE...", color = NeonCyan.copy(alpha = 0.5f)) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = NeonCyan,
                                    unfocusedTextColor = NeonCyan,
                                    cursorColor = NeonCyan
                                )
                            )
                        } else {
                            Text(
                                "GAMER CATALOG", 
                                fontWeight = FontWeight.Black,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { 
                            isSearchActive = !isSearchActive
                            if (!isSearchActive) viewModel.onSearchTextChange("")
                        }) {
                            Icon(
                                imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Rechercher",
                                tint = NeonCyan
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = NeonMagenta,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Ajouter")
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                
                // BANDEAU ALERTE OFFLINE (MODERNE - FLOTTANT)
                AnimatedVisibility(
                    visible = isOffline,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .border(1.dp, NeonMagenta, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.95f)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.WifiOff, contentDescription = null, tint = NeonMagenta)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("CONNEXION INTERROMPUE", color = NeonMagenta, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                    Text("Affichage des données locales", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            Button(
                                onClick = { viewModel.fetchJeux() },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta.copy(alpha = 0.2f)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, NeonMagenta)
                            ) {
                                Text("RÉESSAYER", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }

                // Filter Chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    lazyItems(categories) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onCategorySelected(category) },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan,
                                selectedLabelColor = Color.Black,
                                labelColor = NeonCyan,
                                containerColor = DarkSurface.copy(alpha = 0.7f)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = NeonCyan,
                                selectedBorderColor = NeonCyan
                            )
                        )
                    }
                }

                if (loading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = NeonMagenta)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        gridItems(jeux) { jeu ->
                            JeuItem(jeu = jeu, onClick = { onJeuClick(jeu) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JeuItem(jeu: Jeu, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, Brush.linearGradient(listOf(NeonCyan, NeonMagenta)), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface.copy(alpha = 0.8f)
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(DarkSurfaceVariant)
            ) {
                if (!jeu.image.isNullOrBlank()) {
                    AsyncImage(
                        model = jeu.fullImageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Gamepad,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp).align(Alignment.Center),
                        tint = NeonCyan.copy(alpha = 0.3f)
                    )
                }
                
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                    color = NeonMagenta,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "${jeu.score}",
                        modifier = Modifier.padding(horizontal = 4.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = jeu.titre.uppercase(),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(12.dp),
                maxLines = 2,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JeuDetailScreen(
    jeu: Jeu,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        AsyncImage(
            model = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?q=80&w=2070",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.4f
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = NeonCyan,
                        navigationIconContentColor = NeonCyan
                    ),
                    title = { Text(jeu.titre.uppercase(), fontWeight = FontWeight.Black) },
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
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                    if (!jeu.image.isNullOrBlank()) {
                        AsyncImage(
                            model = jeu.fullImageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                                .border(2.dp, NeonMagenta, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = jeu.titre,
                        style = MaterialTheme.typography.headlineMedium,
                        color = NeonCyan,
                        fontWeight = FontWeight.ExtraBold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = NeonMagenta.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, NeonMagenta),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = jeu.plateforme,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = NeonMagenta,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Surface(
                            color = NeonGreen.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, NeonGreen),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = jeu.annee_sortie.toString(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = NeonGreen,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    Text(
                        text = "DÉVELOPPEUR : ${jeu.developpeur.uppercase()}",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "SKILL SCORE: ${jeu.score}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonCyan
                    )
                    LinearProgressIndicator(
                        progress = { jeu.score / 100f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = NeonCyan,
                        trackColor = DarkSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JeuAddScreen(viewModel: JeuViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var titre by remember { mutableStateOf("") }
    var developpeur by remember { mutableStateOf("") }
    var plateforme by remember { mutableStateOf("") }
    var anneeSortie by remember { mutableStateOf("") }
    var score by remember { mutableStateOf(50f) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val platformes = viewModel.platformesList
    val years = viewModel.yearsList
    var expandedPlatform by remember { mutableStateOf(false) }
    var expandedYear by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        AsyncImage(
            model = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?q=80&w=2070",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.4f
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = NeonCyan,
                        navigationIconContentColor = NeonCyan
                    ),
                    title = { Text("NOUVEAU JEU", fontWeight = FontWeight.Black) },
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
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = titre, 
                    onValueChange = { titre = it }, 
                    label = { Text("TITRE") }, 
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = NeonCyan.copy(alpha = 0.5f),
                        focusedLabelColor = NeonCyan,
                        unfocusedLabelColor = NeonCyan.copy(alpha = 0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                OutlinedTextField(
                    value = developpeur, 
                    onValueChange = { developpeur = it }, 
                    label = { Text("DÉVELOPPEUR") }, 
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = NeonCyan.copy(alpha = 0.5f),
                        focusedLabelColor = NeonCyan,
                        unfocusedLabelColor = NeonCyan.copy(alpha = 0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                
                ExposedDropdownMenuBox(
                    expanded = expandedPlatform,
                    onExpandedChange = { expandedPlatform = !expandedPlatform }
                ) {
                    OutlinedTextField(
                        value = plateforme,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("PLATEFORME") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPlatform) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = NeonCyan.copy(alpha = 0.5f),
                            focusedLabelColor = NeonCyan,
                            unfocusedLabelColor = NeonCyan.copy(alpha = 0.5f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPlatform,
                        onDismissRequest = { expandedPlatform = false },
                        modifier = Modifier.background(DarkSurface)
                    ) {
                        platformes.forEach { platform ->
                            DropdownMenuItem(
                                text = { Text(platform, color = Color.White) },
                                onClick = {
                                    plateforme = platform
                                    expandedPlatform = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = expandedYear,
                    onExpandedChange = { expandedYear = !expandedYear }
                ) {
                    OutlinedTextField(
                        value = anneeSortie,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("ANNÉE") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedYear) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = NeonCyan.copy(alpha = 0.5f),
                            focusedLabelColor = NeonCyan,
                            unfocusedLabelColor = NeonCyan.copy(alpha = 0.5f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedYear,
                        onDismissRequest = { expandedYear = false },
                        modifier = Modifier.background(DarkSurface)
                    ) {
                        years.forEach { year ->
                            DropdownMenuItem(
                                text = { Text(year, color = Color.White) },
                                onClick = {
                                    anneeSortie = year
                                    expandedYear = false
                                }
                            )
                        }
                    }
                }
                
                Column {
                    Text(text = "SCORE: ${score.toInt()}%", style = MaterialTheme.typography.bodyLarge, color = NeonCyan)
                    Slider(
                        value = score,
                        onValueChange = { score = it },
                        valueRange = 0f..100f,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = NeonMagenta,
                            activeTrackColor = NeonCyan,
                            inactiveTrackColor = DarkSurfaceVariant
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(48.dp), tint = NeonCyan)
                            Text("CHOISIR UNE IMAGE", color = NeonCyan, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }

                val isFormValid = titre.isNotBlank() && 
                                 developpeur.isNotBlank() && 
                                 plateforme.isNotBlank() && 
                                 anneeSortie.isNotBlank() && 
                                 selectedImageUri != null

                Button(
                    onClick = {
                        viewModel.addJeu(
                            context = context,
                            titre = titre,
                            developpeur = developpeur,
                            plateforme = plateforme,
                            annee = anneeSortie.toIntOrNull() ?: 0,
                            score = score.toInt(),
                            imageUri = selectedImageUri
                        ) {
                            onBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 8.dp),
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonMagenta,
                        disabledContainerColor = NeonMagenta.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("ENREGISTRER LE JEU", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun InfoBadge(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
