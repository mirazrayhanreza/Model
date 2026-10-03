package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.launch
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
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
                            colors = listOf(PinkHighlight, Color(0xFFFF5E8A))
                        )
                    )
            ) {
                // Background artistic pattern
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)
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
                                .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "FEATURED BANNER",
                                color = Color.White,
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
                            .background(if (isSel) PinkHighlight else Color.White)
                            .then(if (!isSel) Modifier.border(1.dp, PinkBorderSoft, RoundedCornerShape(12.dp)) else Modifier)
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
                            color = if (isSel) Color.White else TextPrimary,
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, PinkBorderSoft),
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
                        .background(PinkHighlight, shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("৳${model.hourlyRate}/hr", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
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
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
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
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
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
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "(${model.reviewCount})",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                    Text(
                        text = "৳${model.hourlyRate}/hr",
                        color = PinkHighlight,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
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
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PinkHighlight,
                        unfocusedBorderColor = Color(0xFFFF85A6),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
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
                    color = TextPrimary,
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
                        Text("No Models Found", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
                title = { Text("Filters & Preferences", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Country Filter
                        Text("Country Preference", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                                        .background(if (isSel) PinkHighlight else Color(0xFFF1F5F9))
                                        .then(if (!isSel) Modifier.border(1.dp, Color(0xFFFF85A6), RoundedCornerShape(8.dp)) else Modifier)
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
                                    Text(label, color = if (isSel) Color.White else TextPrimary, fontSize = 12.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // City Filter
                        Text("City", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                                        .background(if (isSel) PinkHighlight else Color(0xFFF1F5F9))
                                        .then(if (!isSel) Modifier.border(1.dp, Color(0xFFFF85A6), RoundedCornerShape(8.dp)) else Modifier)
                                        .clickable { viewModel.searchCity = city }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(city, color = if (isSel) Color.White else TextPrimary, fontSize = 12.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Category Filter
                        Text("Main Category", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                                        .background(if (isSel) PinkHighlight else Color(0xFFF1F5F9))
                                        .then(if (!isSel) Modifier.border(1.dp, Color(0xFFFF85A6), RoundedCornerShape(8.dp)) else Modifier)
                                        .clickable { viewModel.searchCategory = cat }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(cat, color = if (isSel) Color.White else TextPrimary, fontSize = 12.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Max Price Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Max Hourly Rate", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("৳${viewModel.searchPriceMax}/hr", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Slider(
                            value = viewModel.searchPriceMax.toFloat(),
                            onValueChange = { viewModel.searchPriceMax = it.toInt() },
                            valueRange = 80f..200f,
                            colors = SliderDefaults.colors(
                                thumbColor = PinkHighlight,
                                activeTrackColor = PinkHighlight,
                                inactiveTrackColor = Color(0xFFFF85A6)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Toggles
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Verified Only", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
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
                            Text("Online Now", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Switch(
                                checked = viewModel.searchOnlineOnly,
                                onCheckedChange = { viewModel.searchOnlineOnly = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = PinkHighlight, checkedTrackColor = PinkHighlight.copy(alpha = 0.5f))
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showFilterSheet = false },
                        colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Apply Filters", color = Color.White, fontWeight = FontWeight.Bold)
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
                        Text("Clear All", color = TextSecondary, fontWeight = FontWeight.SemiBold)
                    }
                },
                containerColor = Color.White,
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, PinkBorderSoft),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        .background(PinkHighlight, shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("৳${model.hourlyRate}/hr", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
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
                Text(model.name, color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text("${model.location}, ${model.country}", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = StarYellow, modifier = Modifier.size(14.dp))
                        Text(model.rating.toString(), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("(${model.reviewCount})", color = TextSecondary, fontSize = 10.sp)
                    }
                    Text("৳${model.hourlyRate}/hr", color = PinkHighlight, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
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
        // Top Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, PinkBorderSoft),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(PinkLight, CircleShape)
                            .border(1.dp, PinkBorderSoft, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = "Bookings",
                            tint = PinkHighlight,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "My Bookings",
                            color = TextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Escrow secured reservations & sessions",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    color = PinkLight,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PinkBorderSoft)
                ) {
                    Text(
                        text = "${bookings.size} Total",
                        color = PinkHighlight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Bookings state filters Row with pink borders
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { f ->
                val isSel = f.equals(selectedFilter, ignoreCase = true)
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { selectedFilter = f },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSel) PinkHighlight else Color.White,
                    border = BorderStroke(1.2.dp, if (isSel) PinkHighlight else PinkBorderSoft),
                    shadowElevation = if (isSel) 2.dp else 0.dp
                ) {
                    Text(
                        text = f,
                        color = if (isSel) Color.White else TextPrimary,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
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
                    .weight(1f)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.5.dp, PinkBorderSoft),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .background(PinkLight, CircleShape)
                                .border(1.5.dp, PinkBorderSoft, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "No bookings",
                                tint = PinkHighlight,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No $selectedFilter Bookings", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Bookings in this category will appear here in real-time.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = { viewModel.selectedTab = 0 },
                            colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Browse Available Models", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        },
                        onChatClick = {
                            viewModel.selectChatPartner(booking.modelId.toString())
                            viewModel.selectedTab = 2 // Switch to chat tab
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
    onCardClick: () -> Unit,
    onChatClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.5.dp, PinkBorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Model Photo, Name, Service Type, and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.5.dp, PinkBorderSoft, RoundedCornerShape(14.dp))
                    ) {
                        ModelImage(imageName = booking.modelPhoto, contentDescription = booking.modelName)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = booking.modelName,
                                color = TextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, contentDescription = "Verified", tint = Color(0xFF3B82F6), modifier = Modifier.size(15.dp))
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            color = PinkLight,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(0.8.dp, PinkBorderSoft)
                        ) {
                            Text(
                                text = booking.serviceType,
                                color = PinkHighlight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Status Badge Chip
                val (statusBg, statusBorder, statusText, statusLabel) = when (booking.status.uppercase()) {
                    "PENDING", "ADMIN_REVIEW", "PAYMENT_PENDING" ->
                        listOf(Color(0xFFFEF3C7), Color(0xFFF59E0B), Color(0xFFB45309), "Pending")
                    "ACCEPTED" ->
                        listOf(Color(0xFFDBEAFE), Color(0xFF3B82F6), Color(0xFF1D4ED8), "Accepted")
                    "ONGOING", "IN_PROGRESS" ->
                        listOf(PinkLight, PinkBorderSoft, PinkHighlight, "In Session")
                    "COMPLETED", "PAYMENT_RELEASED" ->
                        listOf(Color(0xFFDCFCE7), OnlineGreen, Color(0xFF15803D), "Completed")
                    "CANCELLED", "REJECTED", "REFUNDED" ->
                        listOf(Color(0xFFFEE2E2), Color(0xFFEF4444), Color(0xFFB91C1C), "Cancelled")
                    else ->
                        listOf(PinkLight, PinkBorderSoft, PinkHighlight, booking.status)
                }

                Surface(
                    color = statusBg as Color,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, statusBorder as Color)
                ) {
                    Text(
                        text = statusLabel as String,
                        color = statusText as Color,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = PinkBorderLight, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Booking specs: Date, Time & Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Event, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(booking.date, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${booking.durationHours} hrs (${booking.time})", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(booking.location, color = TextSecondary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            if (booking.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(0.8.dp, PinkBorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Note: \"${booking.notes}\"",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = PinkBorderLight, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Pricing & Escrow Security Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Escrow", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Text(
                        text = "৳ %,d BDT".format(booking.totalPrice.toInt()),
                        color = PinkHighlight,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (booking.paymentStatus == "PAID") Color(0xFFDCFCE7) else PinkLight,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (booking.paymentStatus == "PAID") OnlineGreen else PinkBorderSoft)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = if (booking.paymentStatus == "PAID") OnlineGreen else PinkHighlight,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (booking.paymentStatus == "PAID") "Escrow Held" else booking.paymentStatus,
                                color = if (booking.paymentStatus == "PAID") Color(0xFF15803D) else PinkHighlight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Quick Chat Button
                    IconButton(
                        onClick = onChatClick,
                        modifier = Modifier
                            .size(34.dp)
                            .background(PinkLight, CircleShape)
                            .border(1.dp, PinkBorderSoft, CircleShape)
                    ) {
                        Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Chat", tint = PinkHighlight, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Quick Actions panel based on Status & Role! Allows dynamic testing.
            if (booking.status == "PENDING" && (currentUserRole == "MODEL" || currentUserRole == "ADMIN")) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onStatusChange("ACCEPTED") },
                        colors = ButtonDefaults.buttonColors(containerColor = OnlineGreen, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Accept", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    }

                    Button(
                        onClick = { onStatusChange("CANCELLED") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    }
                }
            } else if (booking.status == "ACCEPTED" && (currentUserRole == "USER" || currentUserRole == "ADMIN")) {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { onStatusChange("ONGOING") },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(38.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Start Booking Session", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                }
            } else if (booking.status == "ONGOING" && (currentUserRole == "USER" || currentUserRole == "ADMIN")) {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { onStatusChange("COMPLETED") },
                    colors = ButtonDefaults.buttonColors(containerColor = OnlineGreen, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(38.dp)
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Complete & Release Payment", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
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
    val context = LocalContext.current
    val messages by viewModel.allMessages.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val models by viewModel.allModels.collectAsStateWithLifecycle()

    var isChatActive by remember { mutableStateOf(false) }
    var chatPartnerId by remember { mutableStateOf<String?>(null) }
    var chatPartnerName by remember { mutableStateOf("") }
    var chatPartnerPhoto by remember { mutableStateOf("jessica") }
    var searchQuery by remember { mutableStateOf("") }

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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, PinkBorderSoft)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { isChatActive = false },
                        modifier = Modifier
                            .size(36.dp)
                            .background(PinkLight, CircleShape)
                            .border(1.dp, PinkBorderSoft, CircleShape)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PinkHighlight, modifier = Modifier.size(18.dp))
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, PinkBorderSoft, CircleShape)
                        ) {
                            ModelImage(imageName = chatPartnerPhoto, contentDescription = chatPartnerName)
                        }
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .background(OnlineGreen, CircleShape)
                                .border(1.5.dp, Color.White, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                chatPartnerName,
                                color = TextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, contentDescription = "Verified", tint = Color(0xFF3B82F6), modifier = Modifier.size(14.dp))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).background(OnlineGreen, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Active Now • Escrow Protected", color = OnlineGreen, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Audio Call Action Only (Video Call removed per user request)
                    IconButton(
                        onClick = {
                            viewModel.addNotification("Audio Call", "Initiating secure audio call with $chatPartnerName...", "Chat")
                            Toast.makeText(context, "Initiating secure audio call with $chatPartnerName...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .background(PinkLight, CircleShape)
                            .border(1.2.dp, PinkBorderSoft, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "Audio Call", tint = PinkHighlight, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Message timeline
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Safety Escrow Notice
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = PinkLight,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, PinkBorderSoft)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Messages & payments are end-to-end encrypted under Modol Escrow protection.",
                                color = TextPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                items(chatMessages) { msg ->
                    val isSenderMe = msg.senderId == currentUserId
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isSenderMe) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        if (isSenderMe) {
                            // My message: Pink gradient bubble
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PinkHighlight),
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = 16.dp,
                                    bottomEnd = 4.dp
                                ),
                                border = BorderStroke(1.dp, PinkDark),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                modifier = Modifier.widthIn(max = 290.dp)
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                    Text(
                                        text = msg.content,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Row(
                                        modifier = Modifier.align(Alignment.End),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Just now",
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontSize = 9.sp
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Icon(Icons.Default.DoneAll, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        } else {
                            // Partner message: Clean white card with pink border and dark text
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = 4.dp,
                                    bottomEnd = 16.dp
                                ),
                                border = BorderStroke(1.2.dp, PinkBorderSoft),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                modifier = Modifier.widthIn(max = 290.dp)
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                    Text(
                                        text = msg.content,
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "Online",
                                        color = TextSecondary,
                                        fontSize = 9.sp,
                                        modifier = Modifier.align(Alignment.Start)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick suggestion chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val suggestions = listOf(
                    "👋 Hi, are you available today?",
                    "📍 Can we meet at Banani/Gulshan?",
                    "💼 What is your 2-hour rate?",
                    "📸 Can you do portfolio shoots?"
                )
                items(suggestions) { sug ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { textToSend = sug },
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, PinkBorderSoft)
                    ) {
                        Text(
                            text = sug,
                            color = PinkHighlight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Chat input row
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, PinkBorderSoft)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Attach button
                    IconButton(
                        onClick = {
                            viewModel.sendMessage("📸 [Photo Attached: Reservation Spec]", "mock_image_uri")
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(PinkLight, CircleShape)
                            .border(1.dp, PinkBorderSoft, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = "Attach", tint = PinkHighlight, modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = textToSend,
                        onValueChange = { textToSend = it },
                        placeholder = { Text("Write a message...", color = TextSecondary, fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PinkHighlight,
                            unfocusedBorderColor = PinkBorderSoft,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC)
                        ),
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("chat_input_text"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = true
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
                            .border(1.dp, PinkDark, CircleShape)
                            .testTag("chat_send_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    } else {
        // Chat inbox conversation listing
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
        ) {
            // Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, PinkBorderSoft),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(PinkLight, CircleShape)
                                .border(1.dp, PinkBorderSoft, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubble,
                                contentDescription = "Inbox",
                                tint = PinkHighlight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Messages & Chats",
                                color = TextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Real-time communication with models",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Surface(
                        color = PinkLight,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, PinkBorderSoft)
                    ) {
                        Text(
                            text = "${recentChats.size} Active",
                            color = PinkHighlight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Search in chats
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search conversations...", color = TextSecondary, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(18.dp)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkHighlight,
                    unfocusedBorderColor = PinkBorderSoft,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .height(48.dp),
                singleLine = true
            )

            val displayChats = recentChats.filter { chat ->
                if (searchQuery.isBlank()) true
                else {
                    val partnerId = if (chat.senderId == currentUserId) chat.receiverId else chat.senderId
                    val partnerModel = models.firstOrNull { it.id.toString() == partnerId }
                    val name = partnerModel?.name ?: chat.senderName
                    name.contains(searchQuery, ignoreCase = true) || chat.content.contains(searchQuery, ignoreCase = true)
                }
            }

            if (displayChats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.5.dp, PinkBorderSoft),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .background(PinkLight, CircleShape)
                                    .border(1.5.dp, PinkBorderSoft, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = "Inbox Empty", tint = PinkHighlight, modifier = Modifier.size(34.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Your Inbox is Clean", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Select any model from the Home feed to initiate real-time chat & escrow bookings.", color = TextSecondary, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Spacer(modifier = Modifier.height(18.dp))
                            Button(
                                onClick = { viewModel.selectedTab = 0 },
                                colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(42.dp)
                            ) {
                                Icon(Icons.Default.Explore, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Find Models to Chat", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayChats) { chat ->
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
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.5.dp, PinkBorderSoft),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(contentAlignment = Alignment.BottomEnd) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .border(1.5.dp, PinkBorderSoft, CircleShape)
                                    ) {
                                        ModelImage(imageName = photo, contentDescription = name)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(OnlineGreen, CircleShape)
                                            .border(1.5.dp, Color.White, CircleShape)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = name,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.Verified, contentDescription = "Verified", tint = Color(0xFF3B82F6), modifier = Modifier.size(14.dp))
                                    }
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
                                    Spacer(modifier = Modifier.height(4.dp))
                                    if (!chat.isRead && chat.senderId != currentUserId) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(PinkHighlight, CircleShape)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = null,
                                            tint = PinkHighlight,
                                            modifier = Modifier.size(18.dp)
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
                        .border(1.5.dp, PinkBorderSoft, RoundedCornerShape(20.dp))
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
                                    val safeAvatar = if (userAvatar.startsWith("/") && !userAvatar.startsWith("file://")) "file://$userAvatar" else userAvatar
                                    SubcomposeAsyncImage(
                                        model = safeAvatar,
                                        contentDescription = "Profile Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop,
                                        loading = {
                                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = PinkHighlight)
                                            }
                                        },
                                        error = {
                                            Box(modifier = Modifier.fillMaxSize().background(PinkLight), contentAlignment = Alignment.Center) {
                                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(34.dp))
                                            }
                                        }
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(PinkHighlight, CircleShape)
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
                                    Icon(imageVector = Icons.Default.Mail, contentDescription = null, tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = userEmail, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = userPhone, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "$userCity, Bangladesh", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Right: Wallet Balance Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.2.dp, PinkBorderSoft),
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

                                val userCurrency = currentUser?.currency ?: "BDT"
                                val currSymbol = CountryPaymentMaster.getCurrencySymbol(userCurrency)
                                Text(
                                    text = "$currSymbol${(currentUser?.balance ?: 0.0).toInt()} $userCurrency",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp
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
                        border = BorderStroke(1.2.dp, PinkBorderSoft),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                                Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("$totalBookingsCount", color = textDark, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("Total Bookings", color = textDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("All Time", color = textSub, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Completed Bookings
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, PinkBorderSoft),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                            Text("Completed", color = textDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("Bookings", color = textSub, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Upcoming Bookings
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, PinkBorderSoft),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                            Text("Upcoming", color = textDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("Bookings", color = textSub, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Cancelled Bookings
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, PinkBorderSoft),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                            Text("Cancelled", color = textDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("Bookings", color = textSub, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
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
                        border = BorderStroke(1.2.dp, PinkBorderSoft),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.PersonOutline, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Personal Information", color = textDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }

                                Text("Edit", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.clickable { viewModel.showEditPersonalInfoModal = true })
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
                                    Text(label, color = textDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(value, color = textDark, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                                }
                                if (index < infoList.size - 1) {
                                    HorizontalDivider(color = PinkBorderLight, thickness = 0.8.dp)
                                }
                            }
                        }
                    }

                    // Booking Summary Donut Chart Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.2.dp, PinkBorderSoft),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.DonutLarge, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Booking Summary", color = textDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }

                                Text("View All", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.clickable { viewModel.selectedTab = 1 })
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
                                            color = PinkHighlight,
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
                                        Box(modifier = Modifier.size(8.dp).background(PinkHighlight, CircleShape))
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
                        border = BorderStroke(1.2.dp, PinkBorderSoft),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Recent Bookings", color = textDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("View All", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.clickable { viewModel.selectedTab = 1 })
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

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = PinkBorderLight, thickness = 0.8.dp)

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

                    // Dynamic Verification Status Card (Phone & Email Verification)
                    val isPhoneVerified = viewModel.isUserPhoneVerified
                    val isEmailVerified = viewModel.isUserEmailVerified
                    val isFullyVerified = isPhoneVerified && isEmailVerified

                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.2.dp, PinkBorderSoft),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Account Verification", color = textDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }

                                Surface(
                                    color = if (isFullyVerified) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, if (isFullyVerified) Color(0xFF86EFAC) else Color(0xFFFDE68A))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isFullyVerified) Icons.Default.CheckCircle else Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = if (isFullyVerified) greenSuccess else Color(0xFFD97706),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isFullyVerified) "Fully Verified ✓" else if (!isPhoneVerified && !isEmailVerified) "2 Pending" else "1 Pending",
                                            color = if (isFullyVerified) greenSuccess else Color(0xFFD97706),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 1. Phone Verification Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
                                    .border(1.dp, PinkBorderLight, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(if (isPhoneVerified) Color(0xFFDCFCE7) else Color(0xFFFFF1F2), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PhoneAndroid,
                                            contentDescription = null,
                                            tint = if (isPhoneVerified) greenSuccess else PinkHighlight,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Mobile Phone", color = textDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isPhoneVerified) "Verified ✓" else "Not Verified",
                                                color = if (isPhoneVerified) greenSuccess else Color(0xFFD97706),
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 10.sp
                                            )
                                        }
                                        Text(userPhone, color = textSub, fontSize = 11.sp)
                                    }
                                }

                                if (!isPhoneVerified) {
                                    Button(
                                        onClick = { viewModel.startProfileVerification("PHONE") },
                                        colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("Verify Phone", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = greenSuccess,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 2. Email Verification Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
                                    .border(1.dp, PinkBorderLight, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(if (isEmailVerified) Color(0xFFDCFCE7) else Color(0xFFFFF1F2), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Mail,
                                            contentDescription = null,
                                            tint = if (isEmailVerified) greenSuccess else PinkHighlight,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Email Address", color = textDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isEmailVerified) "Verified ✓" else "Not Verified",
                                                color = if (isEmailVerified) greenSuccess else Color(0xFFD97706),
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 10.sp
                                            )
                                        }
                                        Text(userEmail, color = textSub, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }

                                if (!isEmailVerified) {
                                    Button(
                                        onClick = { viewModel.startProfileVerification("EMAIL") },
                                        colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("Verify Email", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = greenSuccess,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            // 3. Problem & Admin Manual Code Assistance Box
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = Color(0xFFFFF8F0),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFFFFE0B2)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.SupportAgent,
                                            contentDescription = null,
                                            tint = Color(0xFFE65100),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Problem with OTP? Admin Manual Code & Verification Available",
                                            color = Color(0xFFE65100),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "If you don't receive OTP via SMS or Email, Admin can generate a Manual Code or directly verify your account from Backend Admin Panel.",
                                        color = Color(0xFF795548),
                                        fontSize = 10.sp,
                                        lineHeight = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(
                                            onClick = {
                                                viewModel.requestAdminVerificationSupport("PHONE & EMAIL")
                                                Toast.makeText(context, "Admin notified! Admin will inspect and dispatch manual code.", Toast.LENGTH_LONG).show()
                                            },
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("Request Admin Manual Verification >", color = PinkHighlight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }

                                        if (viewModel.profilePendingOtp.isNotBlank()) {
                                            Text(
                                                text = "Current Code: ${viewModel.profilePendingOtp}",
                                                color = Color(0xFF16A34A),
                                                fontWeight = FontWeight.Black,
                                                fontSize = 10.sp
                                            )
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
                        border = BorderStroke(1.2.dp, PinkBorderSoft),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Payment, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Payment Methods", color = textDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }

                                Text("Manage", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.clickable { viewModel.showPaymentManagementModal = true })
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
                                        border = BorderStroke(1.dp, if (pm.isPrimary) PinkHighlight else Color(0xFFFF85A6)),
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
                                    border = BorderStroke(1.dp, Color(0xFFFF85A6)),
                                    modifier = Modifier
                                        .width(90.dp)
                                        .clickable { viewModel.showAddPaymentMethodModal = true }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add New", tint = PinkHighlight, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("Add New", color = PinkHighlight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Preferences Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.2.dp, PinkBorderSoft),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Preferences", color = textDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }

                                Text("Edit", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.clickable { viewModel.showEditPreferencesModal = true })
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
                                    Text(label, color = textDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(value, color = textDark, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                                }
                                if (index < prefList.size - 1) {
                                    HorizontalDivider(color = PinkBorderLight, thickness = 0.8.dp)
                                }
                            }
                        }
                    }

                    // 24/7 LIVE SUPPORT CHAT BANNER CARD
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, PinkBorderSoft),
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
                        border = BorderStroke(1.2.dp, PinkBorderSoft),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                                Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = PinkHighlight)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Customer Support & Live Chat", color = textDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("24/7 Help Desk & Real-time Admin Chat", color = textSub, fontSize = 11.sp)
                                }
                                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = textSub, modifier = Modifier.size(18.dp))
                            }

                            HorizontalDivider(color = PinkBorderLight, thickness = 0.8.dp)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.navigateTo("SETTINGS") }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = PinkHighlight)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("App Settings", color = textDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Language, Notifications & Preferences", color = textSub, fontSize = 11.sp)
                                }
                                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = textSub, modifier = Modifier.size(18.dp))
                            }

                            HorizontalDivider(color = PinkBorderLight, thickness = 0.8.dp)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.showChangePasswordDialog = true }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFFFF2A6D).copy(alpha = 0.12f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.LockReset, contentDescription = null, tint = Color(0xFFFF2A6D), modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Change Password (পাসওয়ার্ড পরিবর্তন)", color = textDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Update login credentials & safeguard account", color = textSub, fontSize = 11.sp)
                                }
                                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = textSub, modifier = Modifier.size(18.dp))
                            }

                            HorizontalDivider(color = PinkBorderLight, thickness = 0.8.dp)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.showAllLogoutConfirmDialog = true }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFFDC2626).copy(alpha = 0.12f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Devices, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("Log Out All Devices (সকল ডিভাইস লগআউট)", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFEF2F2)) {
                                            Text("${viewModel.activeSessionsList.size} Sessions", color = Color(0xFFDC2626), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                    }
                                    Text("Terminate active sessions on other phones & PCs", color = textSub, fontSize = 11.sp)
                                }
                                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = textSub, modifier = Modifier.size(18.dp))
                            }

                            HorizontalDivider(color = PinkBorderLight, thickness = 0.8.dp)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.showSignOutConfirmDialog = true }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Logout, contentDescription = null, tint = redDanger)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Sign Out", color = redDanger, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Safely log out of this device", color = textSub, fontSize = 11.sp)
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

    if (viewModel.showProfileVerificationDialog) {
        ProfileVerificationModal(
            viewModel = viewModel,
            onDismiss = { viewModel.showProfileVerificationDialog = false }
        )
    }
}

@Composable
fun ProfileVerificationModal(viewModel: AppViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val isPhone = viewModel.profileVerificationType == "PHONE"
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val target = if (isPhone) viewModel.clientPhone else (currentUser?.email ?: "rahul.verma@email.com")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(PinkHighlight.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPhone) Icons.Default.PhoneAndroid else Icons.Default.Mail,
                            contentDescription = null,
                            tint = PinkHighlight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isPhone) "Verify Phone Number" else "Verify Email Address",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Text(target, color = TextSecondary, fontSize = 11.sp)
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "A 6-digit OTP verification code has been dispatched to $target. Enter the code below to complete profile verification.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                // OTP Input Field
                OutlinedTextField(
                    value = viewModel.profileOtpInput,
                    onValueChange = { 
                        if (it.length <= 6) {
                            viewModel.profileOtpInput = it 
                            viewModel.profileOtpError = null
                        }
                    },
                    label = { Text("6-Digit Verification Code") },
                    placeholder = { Text("e.g. 849201") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PinkHighlight,
                        unfocusedBorderColor = PinkBorderSoft,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Error / Success message
                if (viewModel.profileOtpError != null) {
                    Text(
                        text = viewModel.profileOtpError!!,
                        color = Color(0xFFEF4444),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (viewModel.profileOtpSuccess != null) {
                    Text(
                        text = viewModel.profileOtpSuccess!!,
                        color = Color(0xFF16A34A),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Problem Helper Card (Admin manual dispatch assistance)
                Surface(
                    color = Color(0xFFFFF1F2),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFFFCCD5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Active Code on File: ${viewModel.profilePendingOtp}", color = PinkHighlight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "💡 Problem receiving code? Admin can generate a Manual Code or directly verify your account from Backend Admin Panel.",
                            color = Color(0xFF881337),
                            fontSize = 10.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    viewModel.requestAdminVerificationSupport(if (isPhone) "PHONE" else "EMAIL")
                                    Toast.makeText(context, "Admin notified! Admin will inspect and dispatch code from Admin Panel.", Toast.LENGTH_SHORT).show()
                                },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Request Admin Help >", color = PinkHighlight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            TextButton(
                                onClick = {
                                    viewModel.profileOtpInput = viewModel.profilePendingOtp
                                },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Auto-fill Code", color = Color(0xFF16A34A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val success = viewModel.submitProfileOtp()
                    if (success) {
                        Toast.makeText(context, "Verification Successful! ✓", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Verify Now ✓", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun EditPersonalInfoModal(onDismiss: () -> Unit, viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val PinkHighlight = PinkBorderSoft

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
                Icon(Icons.Default.Edit, contentDescription = null, tint = PinkHighlight)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Personal Information", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Full Name", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PinkHighlight, unfocusedBorderColor = PinkBorderSoft, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White),
                    singleLine = true
                )

                Text("Email Address", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                OutlinedTextField(
                    value = editedEmail,
                    onValueChange = { editedEmail = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PinkHighlight, unfocusedBorderColor = PinkBorderSoft, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White),
                    singleLine = true
                )

                Text("Phone Number", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                OutlinedTextField(
                    value = editedPhone,
                    onValueChange = { editedPhone = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PinkHighlight, unfocusedBorderColor = PinkBorderSoft, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White),
                    singleLine = true
                )

                Text("City / Location", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                OutlinedTextField(
                    value = editedCity,
                    onValueChange = { editedCity = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PinkHighlight, unfocusedBorderColor = PinkBorderSoft, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White),
                    singleLine = true
                )

                Text("Date of Birth", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                OutlinedTextField(
                    value = editedDob,
                    onValueChange = { editedDob = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PinkHighlight, unfocusedBorderColor = PinkBorderSoft, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White),
                    singleLine = true
                )

                Text("Gender", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Male", "Female", "Other").forEach { gender ->
                        val isSel = editedGender.equals(gender, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) PinkHighlight else Color(0xFFFF85A6))
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
                colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
            ) {
                Text("Save Changes", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun EditPreferencesModal(onDismiss: () -> Unit, viewModel: AppViewModel) {
    val PinkHighlight = PinkBorderSoft

    var editedLanguages by remember { mutableStateOf(viewModel.clientPreferredLanguages) }
    var editedCategories by remember { mutableStateOf(viewModel.clientPreferredCategories) }
    var editedBudget by remember { mutableStateOf(viewModel.clientBudgetRange) }
    var editedBookingTime by remember { mutableStateOf(viewModel.clientBookingTime) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = PinkHighlight)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Booking Preferences", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Preferred Languages", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                OutlinedTextField(
                    value = editedLanguages,
                    onValueChange = { editedLanguages = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PinkHighlight, unfocusedBorderColor = PinkBorderSoft, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White),
                    singleLine = true
                )

                Text("Preferred Categories", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                OutlinedTextField(
                    value = editedCategories,
                    onValueChange = { editedCategories = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PinkHighlight, unfocusedBorderColor = PinkBorderSoft, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White),
                    singleLine = true
                )

                Text("Budget Range", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                OutlinedTextField(
                    value = editedBudget,
                    onValueChange = { editedBudget = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PinkHighlight, unfocusedBorderColor = PinkBorderSoft, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White),
                    singleLine = true
                )

                Text("Preferred Booking Times", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                OutlinedTextField(
                    value = editedBookingTime,
                    onValueChange = { editedBookingTime = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PinkHighlight, unfocusedBorderColor = PinkBorderSoft, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White),
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
                colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
            ) {
                Text("Save Preferences", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        },
        containerColor = Color.White
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
            Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
    val PinkHighlight = PinkBorderSoft
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

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
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

                            // Quick Model Sign Out Button
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFDC2626).copy(alpha = 0.25f), CircleShape)
                                    .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), CircleShape)
                                    .clickable { viewModel.showSignOutConfirmDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "Sign Out",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
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
                                        tint = PinkHighlight,
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
                                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
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
                colors = CardDefaults.cardColors(containerColor = Color.White),
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
                        color = PinkHighlight,
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
                            iconTint = PinkHighlight,
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
                            iconTint = PinkHighlight,
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
                        color = PinkHighlight,
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
                    color = PinkHighlight,
                    trackColor = Color(0xFFFF85A6)
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
                                tint = PinkHighlight,
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
                        Text("My Bookings", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
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
                                        if (isSelected) Color.White.copy(alpha = 0.25f) else Color(0xFFFF85A6),
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
                    Text("No Bookings Found", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = proofPhotosInput,
                        onValueChange = { proofPhotosInput = it },
                        label = { Text("Service Photos URLs (Comma separated)", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = proofGpsInput,
                        onValueChange = { proofGpsInput = it },
                        label = { Text("GPS Location Tag", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = proofNotesInput,
                        onValueChange = { proofNotesInput = it },
                        label = { Text("Service Completion Notes", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)),
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
            title = { Text("Submitted Service Proof", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Status: ${b.status} | Escrow: ${b.paymentStatus}", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("GPS Tag: ${b.proofGpsLocation ?: "Verified on-location"}", color = TextSecondary, fontSize = 11.sp)
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
        border = BorderStroke(1.2.dp, PinkBorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
        border = BorderStroke(1.2.dp, PinkBorderSoft),
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
                            color = if (isCompleted) Color(0xFF16A34A) else PinkBorderSoft,
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
                                            isCurrent -> PinkBorderSoft
                                            else -> Color(0xFFFF85A6)
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
                            border = BorderStroke(1.dp, PinkBorderSoft),
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
                            colors = ButtonDefaults.buttonColors(containerColor = PinkBorderSoft),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("💬 Chat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, PinkBorderSoft),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("📞 Call", color = PinkBorderSoft, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onEndServiceClick,
                            colors = ButtonDefaults.buttonColors(containerColor = PinkBorderSoft),
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
                            border = BorderStroke(1.dp, PinkBorderSoft),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("🖼️ View Proof", color = PinkBorderSoft, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.selectChatPartner("user_1"); viewModel.navigateTo("CHAT_ROOM") },
                            colors = ButtonDefaults.buttonColors(containerColor = PinkBorderSoft),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("💬 Chat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, PinkBorderSoft),
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
                            colors = ButtonDefaults.buttonColors(containerColor = PinkBorderSoft),
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
                                border = BorderStroke(1.dp, PinkBorderSoft),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("View Review", color = PinkBorderSoft, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                    val userCurrency = currentUser?.currency ?: "BDT"
                    val currSymbol = CountryPaymentMaster.getCurrencySymbol(userCurrency)
                    Text("Available Wallet Balance ($userCurrency)", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "$currSymbol${currentUser?.balance?.toInt() ?: 0} $userCurrency",
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

                    val userCurrency = currentUser?.currency ?: "BDT"
                    val currSymbol = CountryPaymentMaster.getCurrencySymbol(userCurrency)
                    OutlinedTextField(
                        value = viewModel.withdrawAmount,
                        onValueChange = { viewModel.withdrawAmount = it },
                        label = { Text("Withdraw Amount ($currSymbol $userCurrency)", color = TextSecondary) },
                        placeholder = { Text("E.g. 2000", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PinkHighlight,
                            unfocusedBorderColor = Color(0xFFFF85A6)
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
                                    .background(if (isSel) PinkHighlight else Color(0xFFFF85A6))
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
                    Text("Commission Details", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
            Text("Earning & Withdrawal History", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
                            Text(tx.description, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                HorizontalDivider(color = Color(0xFFFF85A6), thickness = 0.5.dp)
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
            Text("Loading profile details...", color = TextSecondary)
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
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, PinkBorderSoft),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier.clickable { viewModel.showPhotoUploadDialog = true }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .border(2.5.dp, PinkHighlight, CircleShape)
                                .background(PinkHighlight.copy(alpha = 0.15f))
                        ) {
                            val customAvatar = currentUser?.avatarUrl
                            if (!customAvatar.isNullOrBlank()) {
                                val safeAvatar = if (customAvatar.startsWith("/") && !customAvatar.startsWith("file://")) "file://$customAvatar" else customAvatar
                                SubcomposeAsyncImage(
                                    model = safeAvatar,
                                    contentDescription = "Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    error = {
                                        ModelImage(imageName = modelProfile.imageResName, contentDescription = "Avatar")
                                    }
                                )
                            } else {
                                ModelImage(imageName = modelProfile.imageResName, contentDescription = "Avatar")
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(PinkHighlight, CircleShape)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Change Model Photo",
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = modelProfile.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = TextPrimary)
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
                            Text("12.5K", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("Followers", color = TextSecondary, fontSize = 10.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("248", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("Following", color = TextSecondary, fontSize = 10.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("1,240", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
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
                    color = TextPrimary,
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
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, PinkBorderSoft),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = PinkBorderSoft,
                                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = editedBio,
                            onValueChange = { editedBio = it },
                            label = { Text("Short Biography", color = TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = PinkBorderSoft,
                                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = editedRate,
                            onValueChange = { editedRate = it },
                            label = { Text("Hourly rate (৳)", color = TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = PinkBorderSoft,
                                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = PinkBorderSoft,
                                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = PinkBorderSoft,
                                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = hairColorField,
                            onValueChange = { hairColorField = it },
                            label = { Text("Hair Color", color = TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = PinkBorderSoft,
                                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = eyeColorField,
                            onValueChange = { eyeColorField = it },
                            label = { Text("Eye Color", color = TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = PinkBorderSoft,
                                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, PinkBorderSoft),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Availability & Rules", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    HorizontalDivider(color = PinkBorderLight, thickness = 0.8.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Instant Booking Mode", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
                            Text("Vacation Mode", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, PinkBorderSoft),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Portfolio & Locked Media", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                                .border(1.dp, PinkBorderSoft, RoundedCornerShape(8.dp))
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
                                .border(1.dp, PinkBorderSoft, RoundedCornerShape(8.dp))
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
                                .border(1.dp, PinkBorderSoft, RoundedCornerShape(8.dp))
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
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, PinkBorderSoft),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    ProfileOptionRow(
                        title = "Offered Services & Price Configuration",
                        desc = "Manage categories, hourly rates, travel & schedule",
                        icon = Icons.Default.Category,
                        onClick = { viewModel.navigateTo("MODEL_OFFERED_SERVICES") }
                    )

                    HorizontalDivider(color = PinkBorderLight, thickness = 0.8.dp)

                    ProfileOptionRow(
                        title = "24/7 Admin Live Care Support",
                        desc = "Real-time support chat with Modol Admin Desk",
                        icon = Icons.Default.SupportAgent,
                        onClick = { viewModel.showLiveSupportModal = true }
                    )

                    HorizontalDivider(color = PinkBorderLight, thickness = 0.8.dp)

                    ProfileOptionRow(
                        title = "App Settings",
                        desc = "Language, Notifications & Preferences",
                        icon = Icons.Default.Settings,
                        onClick = { viewModel.navigateTo("SETTINGS") }
                    )

                    HorizontalDivider(color = PinkBorderLight, thickness = 0.8.dp)

                    ProfileOptionRow(
                        title = "Change Password (পাসওয়ার্ড পরিবর্তন)",
                        desc = "Update login credentials & safeguard account",
                        icon = Icons.Default.LockReset,
                        tint = Color(0xFFFF2A6D),
                        onClick = { viewModel.showChangePasswordDialog = true }
                    )

                    HorizontalDivider(color = PinkBorderLight, thickness = 0.8.dp)

                    ProfileOptionRow(
                        title = "Log Out All Devices (সকল ডিভাইস লগআউট)",
                        desc = "Terminate active sessions on other phones & PCs",
                        icon = Icons.Default.Devices,
                        tint = Color(0xFFDC2626),
                        onClick = { viewModel.showAllLogoutConfirmDialog = true }
                    )

                    HorizontalDivider(color = PinkBorderLight, thickness = 0.8.dp)

                    ProfileOptionRow(
                        title = "Sign Out",
                        desc = "Safely log out of your current session",
                        icon = Icons.Default.Logout,
                        tint = Color.Red,
                        onClick = { viewModel.showSignOutConfirmDialog = true }
                    )
                }
            }
        }
    }
}

@Composable
fun EditableItemText(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = TextSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = PinkBorderLight, thickness = 0.8.dp)
    }
}

// ============================================================================
// PHOTO UPLOADER DIALOG (CAMERA / GALLERY / PHP UPLOAD API)
// ============================================================================

@Composable
fun PhotoUploadChooserModal(onDismiss: () -> Unit, viewModel: AppViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isUploading by remember { mutableStateOf(false) }
    var uploadStatusMessage by remember { mutableStateOf("Preparing photo...") }
    var uploadProgress by remember { mutableFloatStateOf(0f) }

    fun saveUploadedPhoto(bitmap: Bitmap?, uri: Uri?): Pair<String, Long> {
        val uploadsDir = File(context.filesDir, "uploads/profile").apply { if (!exists()) mkdirs() }
        val filename = "profile_${System.currentTimeMillis()}.jpg"
        val destFile = File(uploadsDir, filename)
        try {
            if (bitmap != null) {
                FileOutputStream(destFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
                }
            } else if (uri != null) {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(destFile).use { out ->
                        input.copyTo(out)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val fileUriString = "file://${destFile.absolutePath}"
        return Pair(fileUriString, if (destFile.exists()) destFile.length() else 245000L)
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            coroutineScope.launch {
                isUploading = true
                uploadStatusMessage = "Capturing photo from camera..."
                uploadProgress = 0.25f
                delay(250)

                uploadStatusMessage = "Compressing JPEG & storing in backend directory..."
                uploadProgress = 0.55f
                val (savedPath, size) = saveUploadedPhoto(bitmap, null)
                delay(300)

                uploadStatusMessage = "Syncing with Room Database & Admin Panel..."
                uploadProgress = 1.0f
                delay(200)

                val filename = File(savedPath.removePrefix("file://")).name
                viewModel.uploadPhotoToBackend(savedPath, filename, size, "PROFILE_AVATAR")
                isUploading = false
                Toast.makeText(context, "Photo uploaded & synced successfully! ✓", Toast.LENGTH_SHORT).show()
                onDismiss()
            }
        }
    }

    // Camera permission request launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                cameraLauncher.launch()
            } catch (e: Exception) {
                Toast.makeText(context, "Could not launch camera: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Camera permission is required to capture photos.", Toast.LENGTH_LONG).show()
        }
    }

    // Fallback gallery picker (ACTION_GET_CONTENT)
    val fallbackGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                isUploading = true
                uploadStatusMessage = "Reading photo from gallery..."
                uploadProgress = 0.25f
                delay(250)

                uploadStatusMessage = "Optimizing & saving to storage..."
                uploadProgress = 0.60f
                val (savedPath, size) = saveUploadedPhoto(null, uri)
                delay(300)

                uploadStatusMessage = "Updating profile avatar in database..."
                uploadProgress = 1.0f
                delay(200)

                val filename = File(savedPath.removePrefix("file://")).name
                viewModel.uploadPhotoToBackend(savedPath, filename, size, "PROFILE_AVATAR")
                isUploading = false
                Toast.makeText(context, "Photo uploaded & synced successfully! ✓", Toast.LENGTH_SHORT).show()
                onDismiss()
            }
        }
    }

    // Primary gallery picker launcher (Zero-permission Android Photo Picker)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                isUploading = true
                uploadStatusMessage = "Reading photo from device storage..."
                uploadProgress = 0.20f
                delay(250)

                uploadStatusMessage = "Compressing & storing in profile directory..."
                uploadProgress = 0.55f
                val (savedPath, size) = saveUploadedPhoto(null, uri)
                delay(300)

                uploadStatusMessage = "Updating profile avatar in database..."
                uploadProgress = 1.0f
                delay(200)

                val filename = File(savedPath.removePrefix("file://")).name
                viewModel.uploadPhotoToBackend(savedPath, filename, size, "PROFILE_AVATAR")
                isUploading = false
                Toast.makeText(context, "Photo uploaded & synced successfully! ✓", Toast.LENGTH_SHORT).show()
                onDismiss()
            }
        }
    }

    val presetAvatars = listOf(
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80"
    )

    AlertDialog(
        onDismissRequest = { if (!isUploading) onDismiss() },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(PinkLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = null,
                        tint = PinkHighlight,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = if (isUploading) "Uploading Photo..." else "Update Profile Photo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextPrimary
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
                        color = PinkHighlight,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        trackColor = PinkBorderSoft
                    )
                    Text(
                        text = uploadStatusMessage,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Storing in backend & refreshing live database...",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Take a camera photo, select from your device gallery, or choose from verified instant avatars.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    // 📷 CAMERA OPTION
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFF5F8),
                        border = BorderStroke(1.dp, PinkBorderSoft),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val hasCam = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                                if (hasCam) {
                                    try {
                                        cameraLauncher.launch()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Camera launch error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(PinkHighlight.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Camera",
                                    tint = PinkHighlight,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Take New Photo",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Capture directly with device camera",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // 🖼 GALLERY OPTION
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                try {
                                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                } catch (e: Exception) {
                                    try {
                                        fallbackGalleryLauncher.launch("image/*")
                                    } catch (ex: Exception) {
                                        Toast.makeText(context, "Gallery open error: ${ex.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(Color(0xFF10B981).copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = "Gallery",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Choose from Gallery",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Pick saved photos from phone storage",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = PinkBorderSoft, thickness = 0.8.dp)

                    // ✨ INSTANT AVATAR PRESETS
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✨ Instant Verified Presets",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Tap to apply",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(presetAvatars) { avatarUrl ->
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, PinkBorderSoft, CircleShape)
                                        .clickable {
                                            viewModel.updateProfileAvatar(avatarUrl)
                                            Toast.makeText(context, "Profile photo updated! ✓", Toast.LENGTH_SHORT).show()
                                            onDismiss()
                                        }
                                ) {
                                    SubcomposeAsyncImage(
                                        model = avatarUrl,
                                        contentDescription = "Preset Avatar",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
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
                    Text("Cancel", color = TextSecondary, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

// ============================================================================
// PAYMENT METHODS MANAGEMENT & ADD NEW MODALS
// ============================================================================

@Composable
fun PaymentManagementModal(onDismiss: () -> Unit, viewModel: AppViewModel) {
    val PinkHighlight = PinkBorderSoft

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
                    Icon(imageVector = Icons.Default.Payment, contentDescription = null, tint = PinkHighlight)
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
                                border = BorderStroke(1.dp, if (pm.isPrimary) PinkHighlight else PinkBorderSoft),
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
                                                            .background(PinkHighlight.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text("Primary", color = PinkHighlight, fontSize = 8.sp, fontWeight = FontWeight.Bold)
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
                                                Text("Make Primary", color = PinkHighlight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
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
    val PinkHighlight = PinkBorderSoft
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
                Icon(imageVector = Icons.Default.CreditCard, contentDescription = null, tint = PinkHighlight)
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
                                .background(if (isSelected) PinkHighlight else Color(0xFF1E293B))
                                .border(1.dp, if (isSelected) PinkHighlight else PinkBorderSoft, RoundedCornerShape(20.dp))
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
                        focusedBorderColor = PinkHighlight,
                        unfocusedBorderColor = PinkBorderSoft,
                        focusedLabelColor = PinkHighlight,
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
                        focusedBorderColor = PinkHighlight,
                        unfocusedBorderColor = PinkBorderSoft,
                        focusedLabelColor = PinkHighlight,
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
                    Text("Set as primary payment method", color = TextPrimary, fontSize = 12.sp)
                    Switch(
                        checked = setAsPrimary,
                        onCheckedChange = { setAsPrimary = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PinkHighlight, checkedTrackColor = PinkHighlight.copy(alpha = 0.4f))
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
                colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
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

