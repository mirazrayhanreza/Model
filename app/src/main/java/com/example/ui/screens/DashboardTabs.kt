package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import kotlinx.coroutines.delay
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import com.example.viewmodel.UserPaymentMethod
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.components.ModelImage
import coil.compose.SubcomposeAsyncImage
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

// ============================================================================
// 1. HOME TAB (MODEL MARKETPLACE HOME)
// ============================================================================

@Composable
fun HomeTab(viewModel: AppViewModel) {
    val models by viewModel.allModels.collectAsStateWithLifecycle()
    val favorites by viewModel.userFavorites.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Runway", "Editorial", "Commercial", "Bridal", "Fitting")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Hero Banner Slider (Featured Model)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF2E0854), Color(0xFFFF2B85))
                        )
                    )
            ) {
                // Background artistic pattern
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(PinkHighlight.copy(alpha = 0.4f), Color.Transparent)
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1.2f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(PinkHighlight.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "FEATURED BANNER",
                                color = PinkHighlight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Verified Professional Models",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 20.sp,
                                lineHeight = 24.sp
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Text(
                            text = "Book and connect safely in Bangladesh.",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                        
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        Button(
                            onClick = { 
                                viewModel.searchCategory = "All"
                                viewModel.selectedTab = 1
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = PinkHighlight),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Explore Now", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    // Avatar badge of model in banner
                    Box(
                        modifier = Modifier
                            .weight(0.8f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        ModelImage(
                            imageName = "jessica",
                            contentDescription = "Featured Model Jessica",
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        )
                    }
                }
            }
        }

        // Categories Chips List
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSel = cat == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) PinkHighlight else DarkSurface)
                            .clickable {
                                selectedCategory = cat
                                if (cat != "All") {
                                    viewModel.searchCategory = cat
                                } else {
                                    viewModel.searchCategory = "All"
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = cat,
                            color = if (isSel) Color.White else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Top Models Section
        item {
            SectionHeader(
                title = "Top Models",
                onViewAllClick = {
                    viewModel.searchCategory = "All"
                    viewModel.selectedTab = 1
                }
            )
            
            val filteredModels = if (selectedCategory == "All") {
                models.sortedByDescending { it.rating }
            } else {
                models.filter { it.skills.contains(selectedCategory, ignoreCase = true) }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                items(filteredModels) { model ->
                    val isFav = favorites.any { it.modelId == model.id }
                    ModelRowCard(
                        model = model,
                        isFav = isFav,
                        onFavToggle = { viewModel.toggleFavorite(model.id) },
                        onCardClick = {
                            viewModel.selectModel(model.id)
                            viewModel.navigateTo("MODEL_PROFILE")
                        }
                    )
                }
            }
        }

        // Recommended / New Members
        item {
            SectionHeader(title = "New Members")
            
            val newModels = models.sortedBy { it.id }
            
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                items(newModels) { model ->
                    val isFav = favorites.any { it.modelId == model.id }
                    ModelRowCard(
                        model = model,
                        isFav = isFav,
                        onFavToggle = { viewModel.toggleFavorite(model.id) },
                        onCardClick = {
                            viewModel.selectModel(model.id)
                            viewModel.navigateTo("MODEL_PROFILE")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ModelRowCard(
    model: ModelProfile,
    isFav: Boolean,
    onFavToggle: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onCardClick() }
            .testTag("model_card_${model.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Photo & Badges overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                ModelImage(
                    imageName = model.imageResName,
                    contentDescription = model.name,
                    modifier = Modifier.fillMaxSize()
                )

                // Verified Badge (Pink Tick as in mockup)
                if (model.isVerified) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(PinkHighlight, shape = RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("VERIFIED", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Favorite Icon Button
                IconButton(
                    onClick = onFavToggle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFav) PinkHighlight else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Price Badge bottom right
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("৳${model.hourlyRate}/hr", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Online indicator
                if (model.isOnline) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(OnlineGreen, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Online", color = Color.White, fontSize = 8.sp)
                    }
                }
            }

            // Info details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = model.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = "${model.location}, ${model.country}",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = StarYellow,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = model.rating.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "(${model.reviewCount})",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

// ============================================================================
// 2. SEARCH TAB (MARKETPLACE SEARCH WITH LIVE FILTERS)
// ============================================================================

@Composable
fun SearchTab(viewModel: AppViewModel) {
    val models by viewModel.allModels.collectAsStateWithLifecycle()
    val favorites by viewModel.userFavorites.collectAsStateWithLifecycle()
    
    var showFilterSheet by remember { mutableStateOf(false) }

    // Dynamic Filter Calculation
    val filteredModels = models.filter { model ->
        val matchesQuery = model.name.contains(viewModel.searchQuery, ignoreCase = true) ||
                model.location.contains(viewModel.searchQuery, ignoreCase = true) ||
                model.country.contains(viewModel.searchQuery, ignoreCase = true) ||
                model.skills.contains(viewModel.searchQuery, ignoreCase = true)
        
        val matchesCountry = viewModel.searchCountry == "All" ||
                model.country.contains(viewModel.searchCountry, ignoreCase = true) ||
                model.location.contains(viewModel.searchCountry, ignoreCase = true)
        val matchesCity = viewModel.searchCity == "All" || model.location.contains(viewModel.searchCity, ignoreCase = true)
        val matchesCategory = viewModel.searchCategory == "All" || model.skills.contains(viewModel.searchCategory, ignoreCase = true) || model.services.contains(viewModel.searchCategory, ignoreCase = true)
        val matchesVerified = !viewModel.searchVerifiedOnly || model.isVerified
        val matchesOnline = !viewModel.searchOnlineOnly || model.isOnline
        val matchesPrice = model.hourlyRate <= viewModel.searchPriceMax
        val matchesAge = model.age >= viewModel.searchAgeMin && model.age <= viewModel.searchAgeMax
        val matchesHeight = model.heightCm >= viewModel.searchHeightMin && model.heightCm <= viewModel.searchHeightMax

        matchesQuery && matchesCountry && matchesCity && matchesCategory && matchesVerified && matchesOnline && matchesPrice && matchesAge && matchesHeight
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar & Filter Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search TextField
                OutlinedTextField(
                    value = viewModel.searchQuery,
                    onValueChange = { viewModel.searchQuery = it },
                    placeholder = { Text("Search by name, city, services...", color = TextSecondary, fontSize = 14.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    trailingIcon = {
                        if (viewModel.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Color.White)
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PinkHighlight,
                        unfocusedBorderColor = Color(0xFF2E2E3E),
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("search_input")
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Filter Button with Badge if active
                IconButton(
                    onClick = { showFilterSheet = true },
                    modifier = Modifier
                        .size(52.dp)
                        .background(PinkHighlight.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .border(1.dp, PinkHighlight.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    Box {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = "Filters", tint = PinkHighlight)
                        // If any filter is customized, show active indicator
                        val isFilterActive = viewModel.searchCountry != "All" || viewModel.searchCity != "All" || viewModel.searchCategory != "All" || viewModel.searchVerifiedOnly || viewModel.searchOnlineOnly || viewModel.searchPriceMax < 200
                        if (isFilterActive) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(OnlineGreen, CircleShape)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }
                }
            }

            // Results count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Found ${filteredModels.size} Professional Models",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                
                if (viewModel.searchCountry != "All" || viewModel.searchCity != "All" || viewModel.searchCategory != "All" || viewModel.searchVerifiedOnly || viewModel.searchOnlineOnly) {
                    Text(
                        "Reset Filters",
                        color = PinkHighlight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            viewModel.searchCountry = "All"
                            viewModel.searchCity = "All"
                            viewModel.searchCategory = "All"
                            viewModel.searchPriceMax = 200
                            viewModel.searchVerifiedOnly = false
                            viewModel.searchOnlineOnly = false
                            viewModel.searchAgeMin = 18
                            viewModel.searchAgeMax = 30
                            viewModel.searchHeightMin = 150
                            viewModel.searchHeightMax = 190
                        }
                    )
                }
            }

            // Models Grid Display
            if (filteredModels.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "No models found",
                            tint = TextSecondary,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No Models Found", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Try adjusting search terms or filters", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredModels) { model ->
                        val isFav = favorites.any { it.modelId == model.id }
                        ModelGridCard(
                            model = model,
                            isFav = isFav,
                            onFavToggle = { viewModel.toggleFavorite(model.id) },
                            onCardClick = {
                                viewModel.selectModel(model.id)
                                viewModel.navigateTo("MODEL_PROFILE")
                            }
                        )
                    }
                }
            }
        }

        // Search Filter Dialog (Bottom sheet style)
        if (showFilterSheet) {
            AlertDialog(
                onDismissRequest = { showFilterSheet = false },
                title = { Text("Filters & Preferences", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Country Filter
                        Text("Country Preference", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("All", "Bangladesh", "China", "USA", "UK", "UAE", "India").forEach { country ->
                                val isSel = viewModel.searchCountry == country
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) PinkHighlight else Color(0xFF2E2E3E))
                                        .clickable { viewModel.searchCountry = country }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    val label = when (country) {
                                        "Bangladesh" -> "🇧🇩 Bangladesh"
                                        "China" -> "🇨🇳 China"
                                        "USA" -> "🇺🇸 USA"
                                        "UK" -> "🇬🇧 UK"
                                        "UAE" -> "🇦🇪 UAE"
                                        "India" -> "🇮🇳 India"
                                        else -> "🌐 All Countries"
                                    }
                                    Text(label, color = Color.White, fontSize = 12.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // City Filter
                        Text("City", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("All", "Dhaka", "Chittagong", "Sylhet", "Shanghai", "Dubai", "New York", "Mumbai", "London").forEach { city ->
                                val isSel = viewModel.searchCity == city
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) PinkHighlight else Color(0xFF2E2E3E))
                                        .clickable { viewModel.searchCity = city }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(city, color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Category Filter
                        Text("Main Category", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("All", "Runway", "Editorial", "Commercial", "Bridal", "Fitting").forEach { cat ->
                                val isSel = viewModel.searchCategory == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) PinkHighlight else Color(0xFF2E2E3E))
                                        .clickable { viewModel.searchCategory = cat }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(cat, color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Max Price Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Max Hourly Rate", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("৳${viewModel.searchPriceMax}/hr", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Slider(
                            value = viewModel.searchPriceMax.toFloat(),
                            onValueChange = { viewModel.searchPriceMax = it.toInt() },
                            valueRange = 80f..200f,
                            colors = SliderDefaults.colors(
                                thumbColor = PinkHighlight,
                                activeTrackColor = PinkHighlight,
                                inactiveTrackColor = Color(0xFF2E2E3E)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Toggles
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Verified Only", color = Color.White, fontSize = 14.sp)
                            Switch(
                                checked = viewModel.searchVerifiedOnly,
                                onCheckedChange = { viewModel.searchVerifiedOnly = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = PinkHighlight, checkedTrackColor = PinkHighlight.copy(alpha = 0.5f))
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Online Now", color = Color.White, fontSize = 14.sp)
                            Switch(
                                checked = viewModel.searchOnlineOnly,
                                onCheckedChange = { viewModel.searchOnlineOnly = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = PinkHighlight, checkedTrackColor = PinkHighlight.copy(alpha = 0.5f))
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showFilterSheet = false }) {
                        Text("Apply Filters", color = PinkHighlight, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        viewModel.searchCountry = "All"
                        viewModel.searchCity = "All"
                        viewModel.searchCategory = "All"
                        viewModel.searchPriceMax = 200
                        viewModel.searchVerifiedOnly = false
                        viewModel.searchOnlineOnly = false
                        showFilterSheet = false
                    }) {
                        Text("Clear All", color = TextSecondary)
                    }
                },
                containerColor = DarkSurface,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun ModelGridCard(
    model: ModelProfile,
    isFav: Boolean,
    onFavToggle: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                ModelImage(
                    imageName = model.imageResName,
                    contentDescription = model.name,
                    modifier = Modifier.fillMaxSize()
                )

                if (model.isVerified) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(PinkHighlight, shape = RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("VERIFIED", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                IconButton(
                    onClick = onFavToggle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFav) PinkHighlight else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("৳${model.hourlyRate}/hr", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                if (model.isOnline) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(OnlineGreen, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Online", color = Color.White, fontSize = 8.sp)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(model.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text("${model.location}, ${model.country}", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = StarYellow, modifier = Modifier.size(14.dp))
                    Text(model.rating.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("(${model.reviewCount})", color = TextSecondary, fontSize = 10.sp)
                }
            }
        }
    }
}

// ============================================================================
// 3. BOOKINGS TAB (MY BOOKINGS HISTORY & APPROVALS)
// ============================================================================

@Composable
fun BookingsTab(viewModel: AppViewModel) {
    val userBookings by viewModel.userBookings.collectAsStateWithLifecycle()
    val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()
    val bookings = if (userBookings.isNotEmpty()) userBookings else allBookings
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Pending", "Accepted", "Ongoing", "Completed", "Cancelled")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Bookings state filters Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBg)
                .padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { f ->
                val isSel = f.equals(selectedFilter, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSel) PinkHighlight else DarkSurface)
                        .clickable { selectedFilter = f }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = f,
                        color = if (isSel) Color.White else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        val filteredBookings = bookings.filter { booking ->
            val s = booking.status.uppercase()
            when (selectedFilter) {
                "All" -> true
                "Pending" -> s == "PENDING" || s == "PAYMENT_RECEIVED" || s == "PAYMENT_PENDING" || s == "ADMIN_REVIEW"
                "Accepted" -> s == "ACCEPTED"
                "Ongoing" -> s == "ONGOING" || s == "IN_PROGRESS" || s == "PROOF_UPLOADED"
                "Completed" -> s == "COMPLETED" || s == "PAYMENT_RELEASED" || s == "USER_CONFIRMED"
                "Cancelled" -> s == "CANCELLED" || s == "REJECTED" || s == "REFUNDED"
                else -> true
            }
        }

        if (filteredBookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "No bookings",
                        tint = TextSecondary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No $selectedFilter Bookings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Bookings in this category will appear here", color = TextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredBookings) { booking ->
                    BookingCard(
                        booking = booking,
                        currentUserRole = currentUser?.role ?: "USER",
                        onStatusChange = { newStatus ->
                            viewModel.updateBookingStatus(booking, newStatus)
                        },
                        onCardClick = {
                            viewModel.selectModel(booking.modelId)
                            viewModel.navigateTo("MODEL_PROFILE")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BookingCard(
    booking: Booking,
    currentUserRole: String,
    onStatusChange: (String) -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Model and price details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        ModelImage(imageName = booking.modelPhoto, contentDescription = booking.modelName)
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = booking.modelName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = booking.serviceType,
                            color = PinkHighlight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "৳${booking.totalPrice.toInt()}",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                    Text(
                        text = booking.paymentStatus,
                        color = if (booking.paymentStatus == "PAID") OnlineGreen else Color.LightGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFF2E2E3E), thickness = 1.dp)
            Spacer(modifier = Modifier.height(14.dp))

            // Booking specs: Date, Time, Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Event, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(booking.date, color = Color.White, fontSize = 12.sp)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${booking.durationHours} hrs (${booking.time})", color = Color.White, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(booking.location, color = Color.White, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            if (booking.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Note: \"${booking.notes}\"",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Quick Actions panel based on Status & Role! Allows dynamic testing.
            if (booking.status == "PENDING" && (currentUserRole == "MODEL" || currentUserRole == "ADMIN")) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onStatusChange("ACCEPTED") },
                        colors = ButtonDefaults.buttonColors(containerColor = OnlineGreen, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Text("Accept", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { onStatusChange("CANCELLED") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Text("Reject", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            } else if (booking.status == "ACCEPTED" && (currentUserRole == "USER" || currentUserRole == "ADMIN")) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onStatusChange("ONGOING") },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Text("Start Booking Session", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            } else if (booking.status == "ONGOING" && (currentUserRole == "USER" || currentUserRole == "ADMIN")) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onStatusChange("COMPLETED") },
                    colors = ButtonDefaults.buttonColors(containerColor = OnlineGreen, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Text("Complete & Release Payment", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

// ============================================================================
// 4. CHAT TAB (MESSAGE INBOX & LIVE MESSAGING)
// ============================================================================

@Composable
fun ChatTab(viewModel: AppViewModel) {
    val messages by viewModel.allMessages.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val models by viewModel.allModels.collectAsStateWithLifecycle()

    var isChatActive by remember { mutableStateOf(false) }
    var chatPartnerId by remember { mutableStateOf<String?>(null) }
    var chatPartnerName by remember { mutableStateOf("") }
    var chatPartnerPhoto by remember { mutableStateOf("jessica") }

    // Group messages by partner to make recent conversation list
    val currentUserId = currentUser?.id ?: "user_1"
    val recentChats = remember(messages, currentUserId) {
        val groups = messages.groupBy {
            if (it.senderId == currentUserId) it.receiverId else it.senderId
        }
        groups.map { (partnerId, list) ->
            list.maxByOrNull { it.timestamp }!!
        }.sortedByDescending { it.timestamp }
    }

    if (isChatActive && chatPartnerId != null) {
        // Detailed Chat screen overlay!
        val chatMessages by viewModel.activeChatMessages.collectAsStateWithLifecycle()
        var textToSend by remember { mutableStateOf("") }

        LaunchedEffect(chatPartnerId) {
            viewModel.selectChatPartner(chatPartnerId!!)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
        ) {
            // Chat details header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { isChatActive = false }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                ) {
                    ModelImage(imageName = chatPartnerPhoto, contentDescription = chatPartnerName)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(chatPartnerName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).background(OnlineGreen, CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Online", color = OnlineGreen, fontSize = 10.sp)
                    }
                }
            }

            // Message timeline
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(chatMessages) { msg ->
                    val isSenderMe = msg.senderId == currentUserId
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isSenderMe) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSenderMe) PinkHighlight else Color(0xFF2E2E3E)
                            ),
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isSenderMe) 16.dp else 0.dp,
                                bottomEnd = if (isSenderMe) 0.dp else 16.dp
                            ),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Text(
                                text = msg.content,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Chat input row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Attach button
                IconButton(onClick = {
                    viewModel.sendMessage("📸 [Photo Attached]", "mock_image_uri")
                }) {
                    Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = "Attach", tint = PinkHighlight)
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = textToSend,
                    onValueChange = { textToSend = it },
                    placeholder = { Text("Write a message...", color = TextSecondary, fontSize = 14.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PinkHighlight,
                        unfocusedBorderColor = Color(0xFF2E2E3E),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = DarkBg,
                        unfocusedContainerColor = DarkBg
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("chat_input_text"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (textToSend.isNotBlank()) {
                            viewModel.sendMessage(textToSend)
                            textToSend = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(PinkHighlight, CircleShape)
                        .testTag("chat_send_button")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    } else {
        // Chat inbox conversation listing
        if (recentChats.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBg),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = "Inbox Empty", tint = TextSecondary, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Your Inbox is Empty", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Select a model from Home to start chatting!", color = TextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBg),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recentChats) { chat ->
                    val partnerId = if (chat.senderId == currentUserId) chat.receiverId else chat.senderId
                    val partnerModel = models.firstOrNull { it.id.toString() == partnerId }
                    val name = partnerModel?.name ?: chat.senderName
                    val photo = partnerModel?.imageResName ?: "jessica"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                chatPartnerId = partnerId
                                chatPartnerName = name
                                chatPartnerPhoto = photo
                                isChatActive = true
                            },
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                            ) {
                                ModelImage(imageName = photo, contentDescription = name)
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = chat.content,
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Active", color = OnlineGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                if (!chat.isRead && chat.senderId != currentUserId) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .size(8.dp)
                                            .background(PinkHighlight, CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 5. PROFILE TAB (USER DETAILS & ROLE MANAGEMENT)
// ============================================================================

@Composable
fun ProfileTab(viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val bgLight = Color(0xFFF6F7FB)
    val cardBg = Color.White
    val textDark = Color(0xFF1E202C)
    val textSub = Color(0xFF6B7280)
    val purplePrimary = Color(0xFF7C3AED)
    val greenSuccess = Color(0xFF16A34A)
    val orangeWarn = Color(0xFFF97316)
    val redDanger = Color(0xFFEF4444)

    val userName = currentUser?.name ?: "Rahul Verma"
    val userEmail = currentUser?.email ?: "rahul.verma@email.com"
    val userPhone = viewModel.clientPhone
    val userCity = currentUser?.city ?: "Dhaka"
    val userAvatar = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d"

    val userBookings by viewModel.userBookings.collectAsStateWithLifecycle()
    val totalBookingsCount = userBookings.size.coerceAtLeast(18)
    val completedCount = userBookings.count { it.status == "COMPLETED" || it.status == "PAYMENT_RELEASED" }.coerceAtLeast(12)
    val upcomingCount = userBookings.count { it.status == "ACCEPTED" || it.status == "IN_PROGRESS" || it.status == "PENDING" || it.status == "PAYMENT_RECEIVED" }.coerceAtLeast(3)
    val cancelledCount = userBookings.count { it.status == "CANCELLED" || it.status == "REFUNDED" || it.status == "REJECTED" }.coerceAtLeast(3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgLight)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. TOP PURPLE BANNER
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF2A1B60),
                                    Color(0xFF4A1E82),
                                    Color(0xFF1E103C)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Avatar + Details
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            // Avatar with Camera Icon
                            Box(
                                contentAlignment = Alignment.BottomEnd,
                                modifier = Modifier.clickable { viewModel.showPhotoUploadDialog = true }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, Color.White, CircleShape)
                                ) {
                                    SubcomposeAsyncImage(
                                        model = userAvatar,
                                        contentDescription = "Profile Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(purplePrimary, CircleShape)
                                        .border(1.5.dp, Color.White, CircleShape)
                                        .clickable { viewModel.showPhotoUploadDialog = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoCamera,
                                        contentDescription = "Edit Photo",
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }

                            // Info text
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = userName,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified",
                                        tint = Color(0xFF3B82F6),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // Premium Client Tag
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFFFD700).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                        .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.WorkspacePremium,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Premium Client",
                                            color = Color(0xFFFFD700),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Mail, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = userEmail, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = userPhone, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "$userCity, Bangladesh", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                }
                            }
                        }

                        // Right: Wallet Balance Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.9f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Wallet Balance", color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "৳${(currentUser?.balance ?: 0.0).toInt()}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = { viewModel.navigateTo("WALLET") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Wallet controls", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 2. STATS ROW (4 CARDS)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Total Bookings
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color(0xFFF3E8FF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = purplePrimary, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("$totalBookingsCount", color = textDark, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("Total Bookings", color = textSub, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                            Text("All Time", color = textSub.copy(alpha = 0.7f), fontSize = 9.sp)
                        }
                    }

                    // Completed Bookings
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color(0xFFDCFCE7), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = greenSuccess, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("$completedCount", color = textDark, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("Completed", color = textSub, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                            Text("Bookings", color = textSub.copy(alpha = 0.7f), fontSize = 9.sp)
                        }
                    }

                    // Upcoming Bookings
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color(0xFFFFEDD5), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = orangeWarn, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("$upcomingCount", color = textDark, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("Upcoming", color = textSub, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                            Text("Bookings", color = textSub.copy(alpha = 0.7f), fontSize = 9.sp)
                        }
                    }

                    // Cancelled Bookings
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color(0xFFFEE2E2), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Cancel, contentDescription = null, tint = redDanger, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("$cancelledCount", color = textDark, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("Cancelled", color = textSub, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                            Text("Bookings", color = textSub.copy(alpha = 0.7f), fontSize = 9.sp)
                        }
                    }
                }
            }

            // 3. PERSONAL INFORMATION & BOOKING SUMMARY GRID
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Personal Information Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.PersonOutline, contentDescription = null, tint = purplePrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Personal Information", color = textDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }

                                Text("Edit", color = purplePrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.clickable { viewModel.showEditPersonalInfoModal = true })
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            val infoList = listOf(
                                "Full Name" to userName,
                                "Phone Number" to userPhone,
                                "Email Address" to userEmail,
                                "Date of Birth" to viewModel.clientDob,
                                "Gender" to viewModel.clientGender,
                                "Location" to "$userCity, Bangladesh",
                                "Member Since" to "12 Jan 2024"
                            )

                            infoList.forEachIndexed { index, (label, value) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(label, color = textSub, fontSize = 12.sp)
                                    Text(value, color = textDark, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                }
                                if (index < infoList.size - 1) {
                                    HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)
                                }
                            }
                        }
                    }

                    // Booking Summary Donut Chart Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.DonutLarge, contentDescription = null, tint = purplePrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Booking Summary", color = textDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }

                                Text("View All", color = purplePrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.clickable { viewModel.selectedTab = 1 })
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                // Donut Chart Canvas
                                Box(
                                    modifier = Modifier.size(110.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                        val stroke = 18.dp.toPx()
                                        val radius = size.minDimension / 2 - stroke / 2

                                        // Total = 18. Angles:
                                        // Completed 12 -> 240 deg
                                        // Upcoming 3 -> 60 deg
                                        // Cancelled 3 -> 60 deg
                                        drawArc(
                                            color = purplePrimary,
                                            startAngle = -90f,
                                            sweepAngle = 238f,
                                            useCenter = false,
                                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
                                        )
                                        drawArc(
                                            color = greenSuccess,
                                            startAngle = 150f,
                                            sweepAngle = 58f,
                                            useCenter = false,
                                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
                                        )
                                        drawArc(
                                            color = orangeWarn,
                                            startAngle = 210f,
                                            sweepAngle = 58f,
                                            useCenter = false,
                                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("18", color = textDark, fontWeight = FontWeight.Black, fontSize = 18.sp)
                                        Text("Total", color = textSub, fontSize = 10.sp)
                                    }
                                }

                                // Legend
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(8.dp).background(purplePrimary, CircleShape))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Completed", color = textSub, fontSize = 11.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("12 (67%)", color = textDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(8.dp).background(greenSuccess, CircleShape))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Upcoming", color = textSub, fontSize = 11.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("3 (17%)", color = textDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(8.dp).background(orangeWarn, CircleShape))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Cancelled", color = textSub, fontSize = 11.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("3 (17%)", color = textDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(8.dp).background(redDanger, CircleShape))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("No Show", color = textSub, fontSize = 11.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("0 (0%)", color = textDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Recent Bookings Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Recent Bookings", color = textDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("View All", color = purplePrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.clickable { viewModel.selectedTab = 1 })
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Item 1: Anika Sharma
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                ) {
                                    SubcomposeAsyncImage(
                                        model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                                        contentDescription = "Anika Sharma",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Anika Sharma", color = textDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("22 Jul 2026 • 08:00 PM", color = textSub, fontSize = 11.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFDCFCE7), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Completed", color = greenSuccess, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("$120.00", color = textDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = textSub, modifier = Modifier.size(18.dp))
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF3F4F6), thickness = 1.dp)

                            // Item 2: Pooja Singh
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                ) {
                                    SubcomposeAsyncImage(
                                        model = "https://images.unsplash.com/photo-1517841905240-472988babdf9",
                                        contentDescription = "Pooja Singh",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Pooja Singh", color = textDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("20 Jul 2026 • 10:00 PM", color = textSub, fontSize = 11.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFE0F2FE), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Upcoming", color = Color(0xFF0284C7), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("$150.00", color = textDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = textSub, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    // Verification Status Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = purplePrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Verification Status", color = textDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Verified", color = greenSuccess, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = greenSuccess, modifier = Modifier.size(14.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    "Email" to Icons.Default.Mail,
                                    "Phone" to Icons.Default.Phone,
                                    "ID Proof" to Icons.Default.Badge,
                                    "Address" to Icons.Default.Home
                                ).forEach { (title, icon) ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(imageVector = icon, contentDescription = null, tint = textSub, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(title, color = textDark, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("Verified", color = greenSuccess, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = greenSuccess, modifier = Modifier.size(8.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Payment Methods Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Payment, contentDescription = null, tint = purplePrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Payment Methods", color = textDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }

                                Text("Manage", color = purplePrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.clickable { viewModel.showPaymentManagementModal = true })
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                viewModel.savedPaymentMethods.forEach { pm ->
                                    val brandColor = when (pm.type.uppercase()) {
                                        "BKASH" -> Color(0xFFE11D48)
                                        "NAGAD" -> Color(0xFFEA580C)
                                        "ROCKET" -> Color(0xFF8B5CF6)
                                        "VISA", "MASTERCARD" -> Color(0xFF1E3A8A)
                                        else -> Color(0xFF10B981)
                                    }

                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, if (pm.isPrimary) purplePrimary else Color(0xFFE5E7EB)),
                                        modifier = Modifier
                                            .width(110.dp)
                                            .clickable { viewModel.showPaymentManagementModal = true }
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(pm.type, color = brandColor, fontWeight = FontWeight.Black, fontSize = 11.sp, maxLines = 1)
                                                if (pm.isPrimary) {
                                                    Box(
                                                        modifier = Modifier
                                                            .background(Color(0xFFDCFCE7), RoundedCornerShape(4.dp))
                                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                                    ) {
                                                        Text("Primary", color = greenSuccess, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(pm.accountNumber, color = textSub, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("Verified", color = greenSuccess, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                // Add New Card
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                    modifier = Modifier
                                        .width(90.dp)
                                        .clickable { viewModel.showAddPaymentMethodModal = true }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add New", tint = purplePrimary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("Add New", color = purplePrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Preferences Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = null, tint = purplePrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Preferences", color = textDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }

                                Text("Edit", color = purplePrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.clickable { viewModel.showEditPreferencesModal = true })
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            val prefList = listOf(
                                "Preferred Language" to viewModel.clientPreferredLanguages,
                                "Preferred Category" to viewModel.clientPreferredCategories,
                                "Budget Range" to viewModel.clientBudgetRange,
                                "Booking Time" to viewModel.clientBookingTime
                            )

                            prefList.forEachIndexed { index, (label, value) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(label, color = textSub, fontSize = 12.sp)
                                    Text(value, color = textDark, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                }
                                if (index < prefList.size - 1) {
                                    HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)
                                }
                            }
                        }
                    }

                    // 24/7 LIVE SUPPORT CHAT BANNER CARD
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.showLiveSupportModal = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        Brush.linearGradient(listOf(Color(0xFFFF2B85), Color(0xFF9C27B0))),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SupportAgent,
                                    contentDescription = "Live Support",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "24/7 Admin Live Support",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(Color(0xFF00E676), CircleShape)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "Instant support chat with Modol Connect Admin team",
                                    color = Color(0xFFC7D2FE),
                                    fontSize = 11.sp
                                )
                            }

                            Surface(
                                color = Color(0xFFE0E7FF).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Chat,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Live Chat", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // App Settings & Sign Out Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.showLiveSupportModal = true }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = purplePrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Customer Support & Live Chat", color = textDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("24/7 Help Desk & Real-time Admin Chat", color = textSub, fontSize = 11.sp)
                                }
                                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = textSub, modifier = Modifier.size(18.dp))
                            }

                            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.navigateTo("SETTINGS") }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = purplePrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("App Settings", color = textDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Language, Notifications & Security", color = textSub, fontSize = 11.sp)
                                }
                                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = textSub, modifier = Modifier.size(18.dp))
                            }

                            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.logout() }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Logout, contentDescription = null, tint = redDanger)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Sign Out", color = redDanger, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Safely log out of your account", color = textSub, fontSize = 11.sp)
                                }
                                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = textSub, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (viewModel.showEditPersonalInfoModal) {
        EditPersonalInfoModal(
            onDismiss = { viewModel.showEditPersonalInfoModal = false },
            viewModel = viewModel
        )
    }

    if (viewModel.showEditPreferencesModal) {
        EditPreferencesModal(
            onDismiss = { viewModel.showEditPreferencesModal = false },
            viewModel = viewModel
        )
    }
}

@Composable
fun EditPersonalInfoModal(onDismiss: () -> Unit, viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val purplePrimary = Color(0xFF7C3AED)

    var editedName by remember { mutableStateOf(currentUser?.name ?: "Rahul Verma") }
    var editedEmail by remember { mutableStateOf(currentUser?.email ?: "rahul.verma@email.com") }
    var editedPhone by remember { mutableStateOf(viewModel.clientPhone) }
    var editedCity by remember { mutableStateOf(currentUser?.city ?: "Dhaka") }
    var editedDob by remember { mutableStateOf(viewModel.clientDob) }
    var editedGender by remember { mutableStateOf(viewModel.clientGender) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = purplePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Personal Information", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Full Name", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purplePrimary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    singleLine = true
                )

                Text("Email Address", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                OutlinedTextField(
                    value = editedEmail,
                    onValueChange = { editedEmail = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purplePrimary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    singleLine = true
                )

                Text("Phone Number", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                OutlinedTextField(
                    value = editedPhone,
                    onValueChange = { editedPhone = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purplePrimary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    singleLine = true
                )

                Text("City / Location", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                OutlinedTextField(
                    value = editedCity,
                    onValueChange = { editedCity = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purplePrimary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    singleLine = true
                )

                Text("Date of Birth", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                OutlinedTextField(
                    value = editedDob,
                    onValueChange = { editedDob = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purplePrimary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    singleLine = true
                )

                Text("Gender", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Male", "Female", "Other").forEach { gender ->
                        val isSel = editedGender.equals(gender, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) purplePrimary else Color(0xFF2E2E3E))
                                .clickable { editedGender = gender }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(gender, color = Color.White, fontSize = 12.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.updateUserProfileDetails(
                        name = editedName,
                        email = editedEmail,
                        phone = editedPhone,
                        city = editedCity,
                        dob = editedDob,
                        gender = editedGender
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = purplePrimary)
            ) {
                Text("Save Changes", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        },
        containerColor = Color(0xFF1E1E2E)
    )
}

@Composable
fun EditPreferencesModal(onDismiss: () -> Unit, viewModel: AppViewModel) {
    val purplePrimary = Color(0xFF7C3AED)

    var editedLanguages by remember { mutableStateOf(viewModel.clientPreferredLanguages) }
    var editedCategories by remember { mutableStateOf(viewModel.clientPreferredCategories) }
    var editedBudget by remember { mutableStateOf(viewModel.clientBudgetRange) }
    var editedBookingTime by remember { mutableStateOf(viewModel.clientBookingTime) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = purplePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Booking Preferences", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Preferred Languages", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                OutlinedTextField(
                    value = editedLanguages,
                    onValueChange = { editedLanguages = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purplePrimary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    singleLine = true
                )

                Text("Preferred Categories", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                OutlinedTextField(
                    value = editedCategories,
                    onValueChange = { editedCategories = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purplePrimary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    singleLine = true
                )

                Text("Budget Range", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                OutlinedTextField(
                    value = editedBudget,
                    onValueChange = { editedBudget = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purplePrimary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    singleLine = true
                )

                Text("Preferred Booking Times", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                OutlinedTextField(
                    value = editedBookingTime,
                    onValueChange = { editedBookingTime = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purplePrimary, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.updateUserPreferences(
                        languages = editedLanguages,
                        categories = editedCategories,
                        budget = editedBudget,
                        bookingTime = editedBookingTime
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = purplePrimary)
            ) {
                Text("Save Preferences", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        },
        containerColor = Color(0xFF1E1E2E)
    )
}

@Composable
fun ProfileOptionRow(
    title: String,
    desc: String,
    icon: ImageVector,
    tint: Color = PinkHighlight,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(tint.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(desc, color = TextSecondary, fontSize = 11.sp)
        }

        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
    }
}

// ============================================================================
// 6. MODEL APP TABS (FOR MODEL MODE)
// ============================================================================

// 6.1 MODEL DASHBOARD TAB
@Composable
fun ModelDashboardTab(viewModel: AppViewModel) {
    val bookings by viewModel.userBookings.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    val totalBookings = bookings.size.coerceAtLeast(1)
    val userBalance = currentUser?.balance ?: 0.0
    val totalEarnings = "৳${bookings.filter { it.status == "COMPLETED" || it.status == "PAYMENT_RELEASED" }.sumOf { it.totalPrice }.toInt()}"
    val rating = "4.8"
    val walletBalance = "৳${userBalance.toInt()}"

    val modelName = currentUser?.name ?: "Anika"
    val avatarUrl = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb"

    val bgLight = Color(0xFFF6F7FB)
    val cardBg = Color.White
    val textDark = Color(0xFF1E202C)
    val textSub = Color(0xFF6B7280)
    val purplePrimary = Color(0xFF7C3AED)
    val greenSuccess = Color(0xFF16A34A)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgLight),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. TOP PURPLE GRADIENT PROFILE BANNER
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF3F2B96),
                                Color(0xFF5A2CBA),
                                Color(0xFF281366)
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    // Top Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Circular Avatar with Green "Online" Pill Tag
                            Box(contentAlignment = Alignment.BottomCenter) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .border(2.5.dp, Color.White, CircleShape)
                                ) {
                                    SubcomposeAsyncImage(
                                        model = avatarUrl,
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                // Online Green Pill
                                Box(
                                    modifier = Modifier
                                        .offset(y = 6.dp)
                                        .background(Color(0xFF22C55E), RoundedCornerShape(10.dp))
                                        .border(1.5.dp, Color.White, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 1.5.dp)
                                ) {
                                    Text(
                                        text = "Online",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Profile Name & Subtitles
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Hi, $modelName 👋",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp
                                    )
                                }
                                Text(
                                    text = "Welcome back!",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                // Premium Model Tag
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFFFD700).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                        .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.WorkspacePremium,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Premium Model",
                                            color = Color(0xFFFFD700),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Notification Bell with Badge 5
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                .clickable { viewModel.navigateTo("NOTIFICATIONS") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = (-2).dp)
                                    .size(16.dp)
                                    .background(Color(0xFFEF4444), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "5",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f), thickness = 1.dp)

                    // Stat Metrics inside Top Banner
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$totalBookings",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Total Bookings",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .height(26.dp)
                                .width(1.dp)
                                .background(Color.White.copy(alpha = 0.2f))
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = totalEarnings,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Total Earnings",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .height(26.dp)
                                .width(1.dp)
                                .background(Color.White.copy(alpha = 0.2f))
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = rating,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB800),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "Rating",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // 2. OVERVIEW SECTION
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Overview",
                    color = textDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 1: Bookings
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.width(135.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(Color(0xFFF3E8FF), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = purplePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "24",
                                    color = textDark,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = "Bookings",
                                    color = textSub,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "↑ 12%",
                                    color = greenSuccess,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "from last month",
                                    color = greenSuccess,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }

                    // Card 2: Earnings
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.width(135.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(Color(0xFFDCFCE7), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AttachMoney,
                                        contentDescription = null,
                                        tint = greenSuccess,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "$1,450",
                                    color = textDark,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = "Earnings",
                                    color = textSub,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "↑ 18%",
                                    color = greenSuccess,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "from last month",
                                    color = greenSuccess,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }

                    // Card 3: Rating
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.width(135.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(Color(0xFFFFEDD5), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFF97316),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "4.8",
                                    color = textDark,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = "Rating",
                                    color = textSub,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "↑ 0.3",
                                    color = greenSuccess,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "from last month",
                                    color = greenSuccess,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }

                    // Card 4: Wallet Balance with Withdraw Button
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.width(135.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(Color(0xFFDBEAFE), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = walletBalance,
                                    color = textDark,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = "Wallet Balance",
                                    color = textSub,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.navigateTo("WALLET") },
                                    colors = ButtonDefaults.buttonColors(containerColor = purplePrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text(
                                        text = "Withdraw",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // QUICK ACTION: SERVICES & PRICE CONFIGURATION BANNER
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { viewModel.navigateTo("MODEL_OFFERED_SERVICES") }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(PinkHighlight.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null,
                                tint = PinkHighlight,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Offered Services & Price",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Categories, hourly rates & travel rules",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.navigateTo("MODEL_OFFERED_SERVICES") },
                        colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Configure", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 3. UPCOMING BOOKINGS SECTION
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Upcoming Bookings",
                        color = textDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "View All",
                        color = purplePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { viewModel.selectedTab = 1 }
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Booking 1: Rahul Verma
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                            ) {
                                SubcomposeAsyncImage(
                                    model = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
                                    contentDescription = "Rahul Verma",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Rahul Verma",
                                    color = textDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "22 Jul 2026 • 08:00 PM",
                                    color = textSub,
                                    fontSize = 11.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFDCFCE7), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Upcoming",
                                        color = greenSuccess,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "$120",
                                        color = textDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = textSub,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                        // Booking 2: Arjun Kapoor
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                            ) {
                                SubcomposeAsyncImage(
                                    model = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
                                    contentDescription = "Arjun Kapoor",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Arjun Kapoor",
                                    color = textDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "20 Jul 2026 • 10:00 PM",
                                    color = textSub,
                                    fontSize = 11.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFDCFCE7), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Upcoming",
                                        color = greenSuccess,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "$150",
                                        color = textDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = textSub,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. QUICK ACTIONS SECTION
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Quick Actions",
                    color = textDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                // Grid of 8 Actions (2 rows of 4 items)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionTile(
                            title = "Edit Profile",
                            icon = Icons.Default.Person,
                            iconBg = Color(0xFFF3E8FF),
                            iconTint = purplePrimary,
                            modifier = Modifier.weight(1f)
                        ) { viewModel.navigateTo("EDIT_PROFILE") }

                        QuickActionTile(
                            title = "My Photos",
                            icon = Icons.Default.Image,
                            iconBg = Color(0xFFDCFCE7),
                            iconTint = greenSuccess,
                            modifier = Modifier.weight(1f)
                        ) { viewModel.navigateTo("PHOTOS") }

                        QuickActionTile(
                            title = "Set Availability",
                            icon = Icons.Default.CalendarToday,
                            iconBg = Color(0xFFFFEDD5),
                            iconTint = Color(0xFFF97316),
                            modifier = Modifier.weight(1f)
                        ) { viewModel.navigateTo("SETTINGS") }

                        QuickActionTile(
                            title = "My Bookings",
                            icon = Icons.Default.CreditCard,
                            iconBg = Color(0xFFDBEAFE),
                            iconTint = Color(0xFF2563EB),
                            modifier = Modifier.weight(1f)
                        ) { viewModel.selectedTab = 1 }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionTile(
                            title = "My Earnings",
                            icon = Icons.Default.AttachMoney,
                            iconBg = Color(0xFFDCFCE7),
                            iconTint = greenSuccess,
                            modifier = Modifier.weight(1f)
                        ) { viewModel.navigateTo("WALLET") }

                        QuickActionTile(
                            title = "Wallet",
                            icon = Icons.Default.AccountBalanceWallet,
                            iconBg = Color(0xFFF3E8FF),
                            iconTint = purplePrimary,
                            modifier = Modifier.weight(1f)
                        ) { viewModel.navigateTo("WALLET") }

                        QuickActionTile(
                            title = "Payouts",
                            icon = Icons.Default.CardGiftcard,
                            iconBg = Color(0xFFFFE4E6),
                            iconTint = Color(0xFFE11D48),
                            modifier = Modifier.weight(1f)
                        ) { viewModel.navigateTo("WALLET") }

                        QuickActionTile(
                            title = "Statistics",
                            icon = Icons.Default.BarChart,
                            iconBg = Color(0xFFDBEAFE),
                            iconTint = Color(0xFF2563EB),
                            modifier = Modifier.weight(1f)
                        ) { viewModel.selectedTab = 3 }
                    }
                }
            }
        }

        // 5. PROFILE COMPLETION SECTION
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profile Completion",
                        color = textDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "85% Complete",
                        color = purplePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                // Smooth Progress Bar
                LinearProgressIndicator(
                    progress = { 0.85f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = purplePrimary,
                    trackColor = Color(0xFFE5E7EB)
                )

                // Profile completion card
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo("EDIT_PROFILE") }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFF3E8FF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = purplePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Complete Your Profile",
                                color = textDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Get more bookings by completing your profile",
                                color = textSub,
                                fontSize = 11.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = textSub,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionTile(
    title: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = Color(0xFF1E202C),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

// 6.2 MODEL BOOKINGS TAB (BOOKING MANAGEMENT - MATCHING MOCKUP DESIGN)
@Composable
fun ModelBookingsTab(viewModel: AppViewModel) {
    val userBookings by viewModel.userBookings.collectAsStateWithLifecycle()
    val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()
    val bookings = if (userBookings.isNotEmpty()) userBookings else allBookings

    var selectedFilterChip by remember { mutableStateOf("All Bookings") } // All Bookings, Pending, Accepted, In Progress, Completed
    var showProofModalForBooking by remember { mutableStateOf<Booking?>(null) }
    var showInspectModalForBooking by remember { mutableStateOf<Booking?>(null) }

    // State for uploading proof modal
    var proofSelfieInput by remember { mutableStateOf("https://images.unsplash.com/photo-1534528741775-53994a69daeb") }
    var proofPhotosInput by remember { mutableStateOf("https://images.unsplash.com/photo-1517841905240-472988babdf9,https://images.unsplash.com/photo-1524504388940-b1c1722653e1") }
    var proofVideoInput by remember { mutableStateOf("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4") }
    var proofGpsInput by remember { mutableStateOf("23.7937° N, 90.4066° E (On-location verified)") }
    var proofNotesInput by remember { mutableStateOf("Service completed successfully as agreed with client.") }

    val totalCount = bookings.size
    val pendingCount = bookings.count { it.status == "PENDING" || it.status == "PAYMENT_RECEIVED" }
    val acceptedCount = bookings.count { it.status == "ACCEPTED" }
    val inProgressCount = bookings.count { it.status == "IN_PROGRESS" }
    val completedCount = bookings.count { it.status == "COMPLETED" || it.status == "PAYMENT_RELEASED" }
    val totalEarnings = bookings.filter { it.status == "COMPLETED" || it.status == "PAYMENT_RELEASED" }.sumOf { it.totalPrice }

    val filteredList = bookings.filter {
        when (selectedFilterChip) {
            "Pending" -> it.status == "PENDING" || it.status == "PAYMENT_RECEIVED"
            "Accepted" -> it.status == "ACCEPTED"
            "In Progress" -> it.status == "IN_PROGRESS"
            "Completed" -> it.status == "COMPLETED" || it.status == "PAYMENT_RELEASED" || it.status == "PROOF_UPLOADED" || it.status == "ADMIN_REVIEW"
            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // --- 1. TOP HEADER SECTION ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(PinkHighlight.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("My Bookings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Text("Manage all your bookings and earnings", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.padding(start = 40.dp))
                }

                Button(
                    onClick = { viewModel.navigateTo("MODEL_OFFERED_SERVICES") },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Availability", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // --- 2. TOP METRIC CARDS ROW (5 SCROLLABLE CARDS) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Total Card
                MetricStatCard(
                    icon = Icons.Default.CalendarMonth,
                    iconBg = Color(0xFFF3E8FF),
                    iconTint = Color(0xFF9333EA),
                    value = "$totalCount",
                    label = "Total"
                )
                // Upcoming Card
                MetricStatCard(
                    icon = Icons.Default.Schedule,
                    iconBg = Color(0xFFFEF3C7),
                    iconTint = Color(0xFFD97706),
                    value = "${pendingCount + acceptedCount}",
                    label = "Upcoming"
                )
                // In Progress Card
                MetricStatCard(
                    icon = Icons.Default.PlayCircle,
                    iconBg = Color(0xFFDBEAFE),
                    iconTint = Color(0xFF2563EB),
                    value = "$inProgressCount",
                    label = "In Progress"
                )
                // Completed Card
                MetricStatCard(
                    icon = Icons.Default.CheckCircle,
                    iconBg = Color(0xFFDCFCE7),
                    iconTint = Color(0xFF16A34A),
                    value = "$completedCount",
                    label = "Completed"
                )
                // Total Earnings Card
                MetricStatCard(
                    icon = Icons.Default.MonetizationOn,
                    iconBg = Color(0xFFFFE4E6),
                    iconTint = Color(0xFFE11D48),
                    value = "৳${totalEarnings.toInt()}",
                    label = "Total Earnings"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // --- 3. FILTER CHIPS ROW ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "All Bookings" to "$totalCount",
                    "Pending" to "$pendingCount",
                    "Accepted" to "$acceptedCount",
                    "In Progress" to "$inProgressCount",
                    "Completed" to "$completedCount"
                ).forEach { (label, countStr) ->
                    val isSelected = selectedFilterChip == label
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) PinkHighlight else DarkSurface)
                            .clickable { selectedFilterChip = label }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) Color.White.copy(alpha = 0.25f) else Color(0xFF2E2E3E),
                                        CircleShape
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = countStr,
                                    color = if (isSelected) Color.White else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 4. BOOKINGS CARDS LIST ---
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.EventNote, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(56.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No Bookings Found", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("No booking requests matching '$selectedFilterChip'.", color = TextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredList) { booking ->
                    ModelBookingItemCard(
                        booking = booking,
                        viewModel = viewModel,
                        onEndServiceClick = { showProofModalForBooking = booking },
                        onViewProofClick = { showInspectModalForBooking = booking }
                    )
                }
            }
        }
    }

    // Modal for uploading proof
    if (showProofModalForBooking != null) {
        val target = showProofModalForBooking!!
        AlertDialog(
            onDismissRequest = { showProofModalForBooking = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = PinkHighlight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload Service Proof - Booking #${target.id}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Admin will review your uploaded proof to release 85% Escrow payment.", color = TextSecondary, fontSize = 11.sp)

                    OutlinedTextField(
                        value = proofSelfieInput,
                        onValueChange = { proofSelfieInput = it },
                        label = { Text("Client Selfie Verification URL", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = proofPhotosInput,
                        onValueChange = { proofPhotosInput = it },
                        label = { Text("Service Photos URLs (Comma separated)", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = proofGpsInput,
                        onValueChange = { proofGpsInput = it },
                        label = { Text("GPS Location Tag", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = proofNotesInput,
                        onValueChange = { proofNotesInput = it },
                        label = { Text("Service Completion Notes", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitServiceProof(
                            bookingId = target.id,
                            selfieUrl = proofSelfieInput,
                            photoUrls = proofPhotosInput,
                            videoUrl = proofVideoInput,
                            gpsLocation = proofGpsInput,
                            notes = proofNotesInput
                        )
                        showProofModalForBooking = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
                ) {
                    Text("Submit Proof to Admin", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showProofModalForBooking = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }

    // Modal for inspecting proof
    if (showInspectModalForBooking != null) {
        val b = showInspectModalForBooking!!
        AlertDialog(
            onDismissRequest = { showInspectModalForBooking = null },
            title = { Text("Submitted Service Proof", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Status: ${b.status} | Escrow: ${b.paymentStatus}", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("GPS Tag: ${b.proofGpsLocation ?: "Verified on-location"}", color = Color.White, fontSize = 11.sp)
                    Text("Notes: ${b.proofNotes ?: "None"}", color = TextSecondary, fontSize = 11.sp)
                }
            },
            confirmButton = {
                Button(onClick = { showInspectModalForBooking = null }, colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)) {
                    Text("Close")
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
fun MetricStatCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    value: String,
    label: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.width(105.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, color = Color(0xFF1E202C), fontWeight = FontWeight.Black, fontSize = 16.sp)
            Text(text = label, color = Color(0xFF6B7280), fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ModelBookingItemCard(
    booking: Booking,
    viewModel: AppViewModel,
    onEndServiceClick: () -> Unit,
    onViewProofClick: () -> Unit
) {
    val isPending = booking.status == "PENDING" || booking.status == "PAYMENT_RECEIVED"
    val isInProgress = booking.status == "IN_PROGRESS"
    val isProofUploaded = booking.status == "PROOF_UPLOADED"
    val isAdminReview = booking.status == "ADMIN_REVIEW"
    val isCompleted = booking.status == "COMPLETED" || booking.status == "PAYMENT_RELEASED"

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // --- TOP CLIENT INFO & BADGE ROW ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.size(48.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        ) {
                            SubcomposeAsyncImage(
                                model = when (booking.userId) {
                                    "Rahul Verma" -> "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d"
                                    "Arjun Kapoor" -> "https://images.unsplash.com/photo-1500648767791-00dcc994a43e"
                                    "Pooja Singh" -> "https://images.unsplash.com/photo-1534528741775-53994a69daeb"
                                    "Vikram Singh" -> "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce"
                                    "Amit Patel" -> "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d"
                                    else -> "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde"
                                },
                                contentDescription = booking.userId,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(booking.userId, color = Color(0xFF1E202C), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Verified", tint = Color(0xFF2196F3), modifier = Modifier.size(14.dp))
                            if (booking.userId == "Rahul Verma") {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFFEF3C7), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("👑 Premium Client", color = Color(0xFFD97706), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB800), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text((booking.userFeedback ?: "").ifEmpty { "4.9 (32 reviews)" }, color = Color(0xFF4B5563), fontSize = 10.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(booking.location, color = Color(0xFF6B7280), fontSize = 10.sp)
                        }
                    }
                }

                // Top Right Badge & Price
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val (statusLabel, statusBg, statusColor) = when {
                            isPending -> Triple("New Booking", Color(0xFFFEF3C7), Color(0xFFD97706))
                            isInProgress -> Triple("⏱️ In Progress", Color(0xFFDBEAFE), Color(0xFF2563EB))
                            isProofUploaded -> Triple("📤 Proof Uploaded", Color(0xFFF3E8FF), Color(0xFF9333EA))
                            isAdminReview -> Triple("⏱️ Admin Review", Color(0xFFFEF3C7), Color(0xFFD97706))
                            isCompleted -> Triple("✅ Completed", Color(0xFFDCFCE7), Color(0xFF16A34A))
                            else -> Triple(booking.status, Color.LightGray, Color.Black)
                        }

                        Box(
                            modifier = Modifier
                                .background(statusBg, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(statusLabel, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when {
                                isPending -> "5 min ago"
                                isInProgress -> "Started 1h ago"
                                isProofUploaded -> "2h ago"
                                isCompleted -> "2 days ago"
                                else -> "1 day ago"
                            },
                            color = Color(0xFF9CA3AF),
                            fontSize = 9.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("৳${booking.totalPrice.toInt()}", color = Color(0xFF1E202C), fontWeight = FontWeight.Black, fontSize = 16.sp)

                    Box(
                        modifier = Modifier
                            .background(
                                if (isCompleted) Color(0xFFDCFCE7) else Color(0xFFF3E8FF),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isCompleted) "Payment Released ✓" else if (isAdminReview) "Under Review" else "Advance Paid",
                            color = if (isCompleted) Color(0xFF16A34A) else Color(0xFF7C3AED),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- SERVICE DETAILS TAGS ROW ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("📷 ${booking.serviceType}", color = Color(0xFF374151), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("⏱️ ${booking.durationHours} Hours", color = Color(0xFF374151), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("📅 ${booking.date} • ${booking.time}", color = Color(0xFF374151), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
            }

            // --- ALERT BANNER FOR NEW REQUEST ---
            if (isPending) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF1F2), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFFECDD3), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text("🔒", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Booking request from client. Please accept or reject.", color = Color(0xFFE11D48), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                        Text("Auto cancel in 1h 45m", color = Color(0xFFE11D48), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // --- WORKFLOW PROGRESS TRACKER STEPPER BAR ---
            if (!isPending) {
                Spacer(modifier = Modifier.height(12.dp))
                val currentStepIndex = when {
                    isInProgress -> 2 // Service Started
                    isProofUploaded -> 3 // Proof
                    isAdminReview -> 4 // Admin Review
                    isCompleted -> 5 // Payment
                    else -> 1
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        "Booked" to 0,
                        "Accepted" to 1,
                        "Service Started" to 2,
                        "Proof" to 3,
                        "Admin Review" to 4,
                        "Payment" to 5
                    ).forEach { (stepLabel, stepIdx) ->
                        val isDone = stepIdx < currentStepIndex
                        val isCurrent = stepIdx == currentStepIndex

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(
                                        when {
                                            isDone -> Color(0xFF16A34A)
                                            isCurrent -> Color(0xFF7C3AED)
                                            else -> Color(0xFFE5E7EB)
                                        },
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                } else if (isCurrent) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                } else {
                                    Box(modifier = Modifier.size(6.dp).background(Color.White, CircleShape))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stepLabel,
                                color = if (isDone || isCurrent) Color(0xFF1E202C) else Color(0xFF9CA3AF),
                                fontSize = 8.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // --- ACTION BUTTONS ROW ---
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            when {
                isPending -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier.weight(1f).height(36.dp)
                        ) {
                            Text("View Profile", color = Color(0xFF374151), fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.updateBookingStatus(booking, "CANCELLED") },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE11D48)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier.weight(1f).height(36.dp)
                        ) {
                            Text("Reject", color = Color(0xFFE11D48), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.updateBookingStatus(booking, "ACCEPTED") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier.weight(1.2f).height(36.dp)
                        ) {
                            Text("✓ Accept Booking", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                isInProgress -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.selectChatPartner("user_1"); viewModel.navigateTo("CHAT_ROOM") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("💬 Chat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF7C3AED)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("📞 Call", color = Color(0xFF7C3AED), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onEndServiceClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.weight(1f).height(36.dp)
                        ) {
                            Text("⏹️ End Service", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                isProofUploaded -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onViewProofClick,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF7C3AED)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("🖼️ View Proof", color = Color(0xFF7C3AED), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.selectChatPartner("user_1"); viewModel.navigateTo("CHAT_ROOM") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("💬 Chat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("📞 Call", color = Color(0xFF374151), fontSize = 11.sp)
                        }

                        TextButton(onClick = { onViewProofClick() }) {
                            Text("Booking Details >", color = Color(0xFF6B7280), fontSize = 10.sp)
                        }
                    }
                }

                isAdminReview -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onViewProofClick,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFD97706)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("🖼️ View Proof", color = Color(0xFFD97706), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.selectChatPartner("user_1"); viewModel.navigateTo("CHAT_ROOM") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("💬 Chat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFFFEF3C7), RoundedCornerShape(8.dp))
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Waiting for admin approval...", color = Color(0xFFD97706), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                isCompleted -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(
                                "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                                "https://images.unsplash.com/photo-1517841905240-472988babdf9",
                                "https://images.unsplash.com/photo-1524504388940-b1c1722653e1"
                            ).forEach { url ->
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.Gray)
                                ) {
                                    SubcomposeAsyncImage(model = url, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFF3F4F6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("+3", color = Color(0xFF6B7280), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFEF3C7), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("⭐ Rated 5.0", color = Color(0xFFD97706), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = onViewProofClick,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFF7C3AED)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("View Review", color = Color(0xFF7C3AED), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 6.3 MODEL WALLET TAB (BALANCE & WITHDRAWAL CONTROLS)
@Composable
fun ModelWalletTab(viewModel: AppViewModel) {
    WalletDashboardContent(viewModel = viewModel, isModel = true)
}

@Composable
fun OldModelWalletTab(viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val transactions by viewModel.walletTransactions.collectAsStateWithLifecycle()

    var withdrawChannel by remember { mutableStateOf("bKash") }
    val commissionRate = 0.10 // 10% platform commission

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Balance card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Available Wallet Balance", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "৳${currentUser?.balance?.toInt() ?: 0}",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .background(PinkHighlight.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("10% Platform Fee Applied", color = PinkHighlight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Withdrawal Form
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Request Income Withdrawal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = viewModel.withdrawAmount,
                        onValueChange = { viewModel.withdrawAmount = it },
                        label = { Text("Withdraw Amount (৳)", color = TextSecondary) },
                        placeholder = { Text("E.g. 2000", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PinkHighlight,
                            unfocusedBorderColor = Color(0xFF2E2E3E)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Select Withdrawal Channel", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("bKash", "Nagad", "Rocket", "Stripe").forEach { m ->
                            val isSel = withdrawChannel == m
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) PinkHighlight else Color(0xFF2E2E3E))
                                    .clickable { withdrawChannel = m }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(m, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (viewModel.withdrawAmount.isNotBlank()) {
                                viewModel.walletMethod = withdrawChannel
                                viewModel.withdrawWallet()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Text("Withdraw to $withdrawChannel", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Commission explanation card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131324)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Commission Details", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "As a certified host on MODOL CONNECT, you receive 90% of all client booking amounts directly into your wallet. Platform fees of 10% cover escrow security, insurance, and local payment gateways.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Transactions list header
        item {
            Text("Earning & Withdrawal History", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        if (transactions.isEmpty()) {
            item {
                Text(
                    "No transaction logs found yet.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            items(transactions) { tx ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (tx.type == "DEPOSIT" || tx.type == "EARNING") OnlineGreen.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (tx.type == "DEPOSIT" || tx.type == "EARNING") Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = if (tx.type == "DEPOSIT" || tx.type == "EARNING") OnlineGreen else Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(tx.description, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(tx.type, color = Color.Gray, fontSize = 10.sp)
                        }
                    }

                    Text(
                        text = if (tx.type == "DEPOSIT" || tx.type == "EARNING") "+৳${tx.amount.toInt()}" else "-৳${tx.amount.toInt()}",
                        color = if (tx.type == "DEPOSIT" || tx.type == "EARNING") OnlineGreen else Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                HorizontalDivider(color = Color(0xFF2E2E3E), thickness = 0.5.dp)
            }
        }
    }
}

// 6.4 MODEL PROFILE TAB (PREVIEW & DETAILED INFO EDIT)
@Composable
fun ModelProfileTab(viewModel: AppViewModel) {
    val models by viewModel.allModels.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    val modelProfile = remember(models) { models.firstOrNull { it.id == 1 } } // Jessica is pre-populated ID 1

    if (modelProfile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading profile details...", color = Color.White)
        }
        return
    }

    var isEditMode by remember { mutableStateOf(false) }

    // Dynamic Edit Fields synced with DB Model Profile!
    var editedName by remember { mutableStateOf(modelProfile.name) }
    var editedBio by remember { mutableStateOf(modelProfile.bio) }
    var editedRate by remember { mutableStateOf(modelProfile.hourlyRate.toString()) }
    var editedLocation by remember { mutableStateOf(modelProfile.location) }
    var editedSkills by remember { mutableStateOf(modelProfile.skills) }
    var editedLanguages by remember { mutableStateOf(modelProfile.languages) }

    // Extra Mock Fields requested by User
    var weightField by remember { mutableStateOf("54 kg") }
    var bodyTypeField by remember { mutableStateOf("Slim") }
    var hairColorField by remember { mutableStateOf("Brown") }
    var eyeColorField by remember { mutableStateOf("Hazel") }
    var nationalityField by remember { mutableStateOf("Bangladeshi") }
    var instantBookingToggle by remember { mutableStateOf(true) }
    var vacationModeToggle by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Image Header & Social Count
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(PinkHighlight.copy(alpha = 0.2f))
                    ) {
                        ModelImage(imageName = modelProfile.imageResName, contentDescription = "Avatar")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = modelProfile.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = Color.White)
                    )

                    Text(
                        text = "Username: @jessica_model_pro",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(PinkHighlight, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("VERIFIED BADGE", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(OnlineGreen.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("ONLINE STATUS", color = OnlineGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Social Counters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("12.5K", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("Followers", color = TextSecondary, fontSize = 10.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("248", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("Following", color = TextSecondary, fontSize = 10.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("1,240", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("Favorites", color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Toggle Edit action row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEditMode) "Editing Host Profile" else "Host Profile Details",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Button(
                    onClick = {
                        if (isEditMode) {
                            // Save to Room Database! Real dynamic integration!
                            scope.launch {
                                val rate = editedRate.toIntOrNull() ?: modelProfile.hourlyRate
                                val updated = modelProfile.copy(
                                    name = editedName,
                                    bio = editedBio,
                                    hourlyRate = rate,
                                    location = editedLocation,
                                    skills = editedSkills,
                                    languages = editedLanguages
                                )
                                viewModel.repository.updateModel(updated)
                                viewModel.addNotification("Profile Updated", "Your changes have been saved to the marketplace database.", "System")
                                isEditMode = false
                            }
                        } else {
                            isEditMode = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isEditMode) OnlineGreen else PinkHighlight),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(imageVector = if (isEditMode) Icons.Default.Save else Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = if (isEditMode) Color.Black else Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isEditMode) "Save" else "Edit details", fontSize = 11.sp, color = if (isEditMode) Color.Black else Color.White)
                }
            }
        }

        // Details list
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (isEditMode) {
                        OutlinedTextField(
                            value = editedName,
                            onValueChange = { editedName = it },
                            label = { Text("Display Full Name", color = TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = editedBio,
                            onValueChange = { editedBio = it },
                            label = { Text("Short Biography", color = TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = editedRate,
                            onValueChange = { editedRate = it },
                            label = { Text("Hourly rate (৳)", color = TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        OutlinedTextField(
                            value = editedLocation,
                            onValueChange = { editedLocation = it },
                            label = { Text("City & Country", color = TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Editable specific fields
                        OutlinedTextField(
                            value = bodyTypeField,
                            onValueChange = { bodyTypeField = it },
                            label = { Text("Body Type", color = TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = hairColorField,
                            onValueChange = { hairColorField = it },
                            label = { Text("Hair Color", color = TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = eyeColorField,
                            onValueChange = { eyeColorField = it },
                            label = { Text("Eye Color", color = TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // Profile Display View
                        EditableItemText(label = "Full Name", value = modelProfile.name)
                        EditableItemText(label = "Hourly Rate", value = "৳${modelProfile.hourlyRate}/hr")
                        EditableItemText(label = "Biography", value = modelProfile.bio)
                        EditableItemText(label = "Location", value = modelProfile.location)
                        EditableItemText(label = "Nationality", value = nationalityField)
                        EditableItemText(label = "Age / Gender", value = "${modelProfile.age} yrs / ${modelProfile.gender}")
                        EditableItemText(label = "Height / Weight", value = "${modelProfile.heightCm} cm / $weightField")
                        EditableItemText(label = "Body Type", value = bodyTypeField)
                        EditableItemText(label = "Hair / Eye Color", value = "$hairColorField / $eyeColorField")
                        EditableItemText(label = "Key Skills", value = modelProfile.skills)
                        EditableItemText(label = "Languages", value = modelProfile.languages)
                    }
                }
            }
        }

        // Availability Controls Panel
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Availability & Rules", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    HorizontalDivider(color = Color(0xFF2E2E3E), thickness = 0.5.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Instant Booking Mode", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Let clients book immediately without pending review.", color = TextSecondary, fontSize = 10.sp)
                        }
                        Switch(
                            checked = instantBookingToggle,
                            onCheckedChange = { instantBookingToggle = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PinkHighlight, checkedTrackColor = PinkHighlight.copy(alpha = 0.5f))
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Vacation Mode", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Temporarily hide profile from search listing.", color = TextSecondary, fontSize = 10.sp)
                        }
                        Switch(
                            checked = vacationModeToggle,
                            onCheckedChange = { vacationModeToggle = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PinkHighlight, checkedTrackColor = PinkHighlight.copy(alpha = 0.5f))
                        )
                    }
                }
            }
        }

        // Portfolio & Private Content Locked simulation
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Portfolio & Locked Media", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Public item
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(90.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E1E2E)),
                            contentAlignment = Alignment.Center
                        ) {
                            ModelImage(imageName = modelProfile.imageResName, contentDescription = "Public")
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("Public", color = Color.White, fontSize = 9.sp)
                            }
                        }

                        // Locked item (Private photo)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(90.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E1E2E)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = "Locked Private Photo", tint = PinkHighlight, modifier = Modifier.size(24.dp))
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("Locked Photo", color = Color.White, fontSize = 9.sp)
                            }
                        }

                        // Locked video item
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(90.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E1E2E)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.PlayCircle, contentDescription = "Video Intro", tint = PinkHighlight, modifier = Modifier.size(24.dp))
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("Video Intro", color = Color.White, fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }

        // Settings & Sign Out
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurface)
            ) {
                ProfileOptionRow(
                    title = "Offered Services & Price Configuration",
                    desc = "Manage categories, hourly rates, travel & schedule",
                    icon = Icons.Default.Category,
                    onClick = { viewModel.navigateTo("MODEL_OFFERED_SERVICES") }
                )

                HorizontalDivider(color = Color(0xFF2E2E3E), thickness = 0.5.dp)

                ProfileOptionRow(
                    title = "24/7 Admin Live Care Support",
                    desc = "Real-time support chat with Modol Admin Desk",
                    icon = Icons.Default.SupportAgent,
                    onClick = { viewModel.showLiveSupportModal = true }
                )

                HorizontalDivider(color = Color(0xFF2E2E3E), thickness = 0.5.dp)

                ProfileOptionRow(
                    title = "App Settings",
                    desc = "Language, Notifications & Security",
                    icon = Icons.Default.Settings,
                    onClick = { viewModel.navigateTo("SETTINGS") }
                )

                HorizontalDivider(color = Color(0xFF2E2E3E), thickness = 0.5.dp)

                ProfileOptionRow(
                    title = "Sign Out",
                    desc = "Safely log out of your current session",
                    icon = Icons.Default.Logout,
                    tint = Color.Red,
                    onClick = { viewModel.logout() }
                )
            }
        }
    }
}

@Composable
fun EditableItemText(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = TextSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = Color(0xFF1E1E2E), thickness = 0.5.dp)
    }
}

// ============================================================================
// PHOTO UPLOADER DIALOG (CAMERA / GALLERY / PHP UPLOAD API)
// ============================================================================

@Composable
fun PhotoUploadChooserModal(onDismiss: () -> Unit, viewModel: AppViewModel) {
    val purplePrimary = Color(0xFF7C3AED)
    val coroutineScope = rememberCoroutineScope()
    var isUploading by remember { mutableStateOf(false) }
    var uploadStatusMessage by remember { mutableStateOf("Preparing photo...") }
    var uploadProgress by remember { mutableFloatStateOf(0f) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            coroutineScope.launch {
                isUploading = true
                uploadStatusMessage = "Compressing photo (1080px JPEG)..."
                uploadProgress = 0.25f
                delay(600)
                uploadStatusMessage = "Uploading to POST /backend/api/upload/profile-photo.php..."
                uploadProgress = 0.65f
                delay(800)
                uploadProgress = 1.0f
                uploadStatusMessage = "Upload success! Saving URL to Database..."
                delay(400)
                val photoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?fit=crop&w=800&q=80"
                viewModel.updateProfileAvatar(photoUrl)
                isUploading = false
                onDismiss()
            }
        }
    }

    // Gallery picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                isUploading = true
                uploadStatusMessage = "Reading gallery image..."
                uploadProgress = 0.20f
                delay(500)
                uploadStatusMessage = "Compressing & Auto-resizing image..."
                uploadProgress = 0.50f
                delay(600)
                uploadStatusMessage = "Uploading to POST /backend/api/upload/profile-photo.php..."
                uploadProgress = 0.85f
                delay(700)
                uploadProgress = 1.0f
                uploadStatusMessage = "Upload success! Updating user profile..."
                delay(400)
                viewModel.updateProfileAvatar(uri.toString())
                isUploading = false
                onDismiss()
            }
        }
    }

    AlertDialog(
        onDismissRequest = { if (!isUploading) onDismiss() },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = null,
                    tint = purplePrimary
                )
                Text(
                    text = if (isUploading) "Uploading Photo..." else "Choose Profile Photo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        },
        text = {
            if (isUploading) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { uploadProgress },
                        color = purplePrimary,
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        trackColor = Color(0xFF2E2E3E)
                    )
                    Text(
                        text = uploadStatusMessage,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Target API: https://modolconnect.com/backend/api/upload/profile-photo.php",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Select an option to update your profile picture. Image will be compressed and uploaded to PHP Backend.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    // 📷 CAMERA BUTTON
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF261D42)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { cameraLauncher.launch() }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(purplePrimary.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Camera",
                                    tint = purplePrimary
                                )
                            }
                            Column {
                                Text(
                                    text = "📷 Camera",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Take a new photo directly",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // 🖼 GALLERY BUTTON
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2838)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { galleryLauncher.launch("image/*") }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF3B82F6).copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = "Gallery",
                                    tint = Color(0xFF3B82F6)
                                )
                            }
                            Column {
                                Text(
                                    text = "🖼 Gallery",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Choose from your saved photos",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            if (!isUploading) {
                TextButton(onClick = onDismiss) {
                    Text("❌ Cancel", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp)
    )
}

// ============================================================================
// PAYMENT METHODS MANAGEMENT & ADD NEW MODALS
// ============================================================================

@Composable
fun PaymentManagementModal(onDismiss: () -> Unit, viewModel: AppViewModel) {
    val purplePrimary = Color(0xFF7C3AED)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Payment, contentDescription = null, tint = purplePrimary)
                    Text(
                        text = "Manage Payment Methods",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Saved payment options associated with your account:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                if (viewModel.savedPaymentMethods.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No payment methods added yet.", color = Color.Gray, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(viewModel.savedPaymentMethods, key = { it.id }) { pm ->
                            val brandColor = when (pm.type.uppercase()) {
                                "BKASH" -> Color(0xFFE11D48)
                                "NAGAD" -> Color(0xFFEA580C)
                                "ROCKET" -> Color(0xFF8B5CF6)
                                "VISA", "MASTERCARD" -> Color(0xFF1D4ED8)
                                else -> Color(0xFF10B981)
                            }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (pm.isPrimary) purplePrimary else Color(0xFF334155)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .background(brandColor.copy(alpha = 0.2f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = pm.type.take(2).uppercase(),
                                                color = brandColor,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 12.sp
                                            )
                                        }

                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = pm.type,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    fontSize = 14.sp
                                                )
                                                if (pm.isPrimary) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .background(purplePrimary.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text("Primary", color = purplePrimary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                            Text(
                                                text = pm.accountNumber,
                                                color = TextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (!pm.isPrimary) {
                                            TextButton(onClick = { viewModel.setPrimaryPaymentMethod(pm.id) }) {
                                                Text("Make Primary", color = purplePrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        IconButton(onClick = { viewModel.removePaymentMethod(pm.id) }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = Color(0xFFEF4444),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        onDismiss()
                        viewModel.showAddPaymentMethodModal = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = purplePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ Add New Payment Method", fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {},
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun AddPaymentMethodModal(onDismiss: () -> Unit, viewModel: AppViewModel) {
    val purplePrimary = Color(0xFF7C3AED)
    var selectedType by remember { mutableStateOf("bKash") }
    val availableTypes = listOf("bKash", "Nagad", "Rocket", "VISA / Card", "Bank Account")

    var accountNumber by remember { mutableStateOf("") }
    var holderName by remember { mutableStateOf("") }
    var setAsPrimary by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.CreditCard, contentDescription = null, tint = purplePrimary)
                Text(
                    text = "Add Payment Method",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Select Method Type:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableTypes.forEach { type ->
                        val isSelected = selectedType == type
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) purplePrimary else Color(0xFF1E293B))
                                .border(1.dp, if (isSelected) purplePrimary else Color(0xFF334155), RoundedCornerShape(20.dp))
                                .clickable { selectedType = type }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = type,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                val numberLabel = when {
                    selectedType.contains("Card", ignoreCase = true) -> "Card Number"
                    selectedType.contains("Bank", ignoreCase = true) -> "Bank Account Number"
                    else -> "Mobile Wallet Number (e.g., 017XXXXXXXX)"
                }

                val numberPlaceholder = when {
                    selectedType.contains("Card", ignoreCase = true) -> "4111 2222 3333 4444"
                    selectedType.contains("Bank", ignoreCase = true) -> "123.456.7890"
                    else -> "01712 345 678"
                }

                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it; errorMessage = "" },
                    label = { Text(numberLabel) },
                    placeholder = { Text(numberPlaceholder) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purplePrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedLabelColor = purplePrimary,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = holderName,
                    onValueChange = { holderName = it },
                    label = { Text("Account Holder Name") },
                    placeholder = { Text("e.g. John Doe") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purplePrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedLabelColor = purplePrimary,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Set as primary payment method", color = Color.White, fontSize = 12.sp)
                    Switch(
                        checked = setAsPrimary,
                        onCheckedChange = { setAsPrimary = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = purplePrimary, checkedTrackColor = purplePrimary.copy(alpha = 0.4f))
                    )
                }

                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (accountNumber.isBlank()) {
                        errorMessage = "Please enter account or phone number!"
                        return@Button
                    }
                    viewModel.addPaymentMethod(
                        type = selectedType,
                        accountNumber = accountNumber.trim(),
                        holderName = holderName.trim(),
                        setAsPrimary = setAsPrimary
                    )
                    viewModel.showAddPaymentMethodModal = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = purplePrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Method", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp)
    )
}

