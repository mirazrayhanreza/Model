package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import coil.compose.SubcomposeAsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.components.ModelImage
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

// ============================================================================
// 1. MODEL PROFILE SCREEN
// ============================================================================

@Composable
fun ModelProfileScreen(viewModel: AppViewModel) {
    val modelId = viewModel.selectedModelId ?: 1
    val models by viewModel.allModels.collectAsStateWithLifecycle()
    val model = models.firstOrNull { it.id == modelId } ?: return
    
    val reviews by viewModel.selectedModelReviews.collectAsStateWithLifecycle()
    val favorites by viewModel.userFavorites.collectAsStateWithLifecycle()
    val isFav = favorites.any { it.modelId == model.id }

    var selectedTabState by remember { mutableStateOf("About") } // "About", "Services", "Reviews"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Custom Top Bar with overlap photo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo("DASHBOARD") },
                modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Text("Model Profile", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            IconButton(
                onClick = { viewModel.toggleFavorite(model.id) },
                modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFav) PinkHighlight else Color.White
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Hero Photo Gallery
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    ModelImage(imageName = model.imageResName, contentDescription = model.name, modifier = Modifier.fillMaxSize())

                    // Page indicator mock
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("1/6 Photos", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    if (model.isOnline) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).background(OnlineGreen, CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Online", color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Name, Rating and Verification Info
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = model.name,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, color = Color.White)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        if (model.isVerified) {
                            Icon(imageVector = Icons.Default.Verified, contentDescription = "Verified Model", tint = PinkHighlight, modifier = Modifier.size(22.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = StarYellow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(model.rating.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("(${model.reviewCount} reviews)", color = TextSecondary, fontSize = 11.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${model.location}, ${model.country}", color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Tab Navigation inside profile: About, Services, Reviews
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .padding(4.dp)
                ) {
                    listOf("About", "Services", "Reviews").forEach { tab ->
                        val isSel = tab == selectedTabState
                        Button(
                            onClick = { selectedTabState = tab },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSel) PinkHighlight else Color.Transparent,
                                contentColor = if (isSel) Color.White else TextSecondary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Text(tab, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Tab content rendering
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (selectedTabState) {
                        "About" -> {
                            Text("Biography", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(model.bio, color = TextSecondary, fontSize = 13.sp, lineHeight = 20.sp)

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Details", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                DetailSpecChip(label = "Age", value = "${model.age} years", modifier = Modifier.weight(1f))
                                DetailSpecChip(label = "Height", value = "${model.heightCm} cm", modifier = Modifier.weight(1f))
                                DetailSpecChip(label = "Languages", value = model.languages.substringBefore(","), modifier = Modifier.weight(1f))
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Key Skills", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                model.skills.split(",").forEach { skill ->
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF2E2E3E), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(skill.trim(), color = Color.White, fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        "Services" -> {
                            Text("Offered Services & Prices", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(12.dp))

                            model.services.split(",").forEach { s ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(s.trim(), color = Color.White, fontSize = 14.sp)
                                    }
                                    Text("৳${model.hourlyRate}/hr", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                HorizontalDivider(color = Color(0xFF2E2E3E), thickness = 0.5.dp)
                            }
                        }

                        "Reviews" -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Customer Reviews", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                TextButton(onClick = {
                                    viewModel.submitReview(model.id, 5, "Fabulous experience. Breathtaking talent!")
                                }) {
                                    Text("+ Quick Review", color = PinkHighlight, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            if (reviews.isEmpty()) {
                                Text("No reviews yet. Be the first to book and write a review!", color = TextSecondary, fontSize = 13.sp)
                            } else {
                                reviews.forEach { r ->
                                    Column(modifier = Modifier.padding(vertical = 10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(modifier = Modifier.size(24.dp).background(PinkHighlight, CircleShape))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(r.reviewerName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = StarYellow, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(r.rating.toString(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(r.comment, color = TextSecondary, fontSize = 12.sp, lineHeight = 18.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(r.date, color = Color.Gray, fontSize = 10.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        HorizontalDivider(color = Color(0xFF2E2E3E), thickness = 0.5.dp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    viewModel.selectChatPartner(model.id.toString())
                    viewModel.selectedTab = 3
                    viewModel.navigateTo("DASHBOARD")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E3E)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(52.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(imageVector = Icons.Default.Chat, contentDescription = "Chat", tint = PinkHighlight)
            }

            Button(
                onClick = {
                    viewModel.navigateTo("SERVICES_PRICING")
                },
                colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("book_now_button")
            ) {
                Text("Book Now", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun DetailSpecChip(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(DarkSurface, RoundedCornerShape(10.dp))
            .border(0.5.dp, Color(0xFF2E2E3E), RoundedCornerShape(10.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = TextSecondary, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

// ============================================================================
// 2. BOOKING STEP 1: SERVICES & PRICING
// ============================================================================

@Composable
fun ServicesPricingScreen(viewModel: AppViewModel) {
    val modelId = viewModel.selectedModelId ?: 1
    val models by viewModel.allModels.collectAsStateWithLifecycle()
    val model = models.firstOrNull { it.id == modelId } ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo("MODEL_PROFILE") }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Select Service & Duration", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Text("Select Service Type", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Services Grid selection
            item {
                val servicesList = model.services.split(",")
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    servicesList.forEach { s ->
                        val trimmed = s.trim()
                        val isSel = viewModel.bookingService == trimmed
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) PinkHighlight else DarkSurface)
                                .clickable { viewModel.bookingService = trimmed }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(trimmed, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Select Duration", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Duration options
            item {
                val durations = listOf(
                    1 to "1 Hour (Base)",
                    2 to "2 Hours (Standard)",
                    4 to "4 Hours (Half Day)",
                    8 to "8 Hours (Full Day)",
                    12 to "12 Hours (Overnight)"
                )

                durations.forEach { (hours, label) ->
                    val isSel = viewModel.bookingDuration == hours
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                viewModel.updateBookingDurationAndPrice(hours)
                            },
                        colors = CardDefaults.cardColors(containerColor = if (isSel) PinkHighlight.copy(alpha = 0.15f) else DarkSurface),
                        border = BorderStroke(1.dp, if (isSel) PinkHighlight else Color(0xFF2E2E3E)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = isSel,
                                    onClick = { viewModel.updateBookingDurationAndPrice(hours) },
                                    colors = RadioButtonDefaults.colors(selectedColor = PinkHighlight)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Text("৳${model.hourlyRate * hours}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        // Bottom Cost and Next button
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Price", color = TextSecondary, fontSize = 12.sp)
                    Text("৳${viewModel.bookingPriceSummary.toInt()}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                }

                Button(
                    onClick = { viewModel.navigateTo("AVAILABILITY") },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(50.dp)
                ) {
                    Text("Select Time", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// ============================================================================
// 3. BOOKING STEP 2: AVAILABILITY CALENDAR & SLOT PICKER
// ============================================================================

@Composable
fun AvailabilityScreen(viewModel: AppViewModel) {
    var selectedDay by remember { mutableStateOf(20) } // Mock May 2025
    val daysList = (15..28).toList()

    val timeSlots = listOf(
        "10:00 AM", "11:00 AM", "12:00 PM", "01:00 PM",
        "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM",
        "06:00 PM", "07:00 PM", "08:00 PM", "09:00 PM"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo("SERVICES_PRICING") }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Select Date & Time", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            // Calendar Month header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("May 2025", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row {
                        Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = null, tint = TextSecondary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Days horizontal Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    daysList.forEach { d ->
                        val isSel = d == selectedDay
                        Box(
                            modifier = Modifier
                                .width(56.dp)
                                .height(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) PinkHighlight else DarkSurface)
                                .clickable {
                                    selectedDay = d
                                    viewModel.bookingDate = "$d May 2025"
                                }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = when (d % 7) {
                                        0 -> "Sun"
                                        1 -> "Mon"
                                        2 -> "Tue"
                                        3 -> "Wed"
                                        4 -> "Thu"
                                        5 -> "Fri"
                                        else -> "Sat"
                                    },
                                    color = if (isSel) Color.White else TextSecondary,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = d.toString(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            // Legend indicators
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LegendItem(color = OnlineGreen, label = "Available")
                    LegendItem(color = PinkHighlight, label = "Booked")
                    LegendItem(color = Color.Gray, label = "Unavailable")
                }
            }

            // Times selection grid
            item {
                Spacer(modifier = Modifier.height(28.dp))
                Text("Available Hours", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val chunked = timeSlots.chunked(3)
                    chunked.forEach { rowSlots ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowSlots.forEach { slot ->
                                val isSel = viewModel.bookingTime == slot
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSel) PinkHighlight else DarkSurface)
                                        .border(0.5.dp, if (isSel) PinkHighlight else Color(0xFF2E2E3E), RoundedCornerShape(10.dp))
                                        .clickable { viewModel.bookingTime = slot }
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = slot,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Action
        Button(
            onClick = { viewModel.navigateTo("BOOKING_SUMMARY") },
            colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(52.dp)
                .testTag("confirm_time_button")
        ) {
            Text("Confirm Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, color = TextSecondary, fontSize = 11.sp)
    }
}

// ============================================================================
// 4. BOOKING SUMMARY & BILLING SELECTION
// ============================================================================

@Composable
fun BookingSummaryScreen(viewModel: AppViewModel) {
    val modelId = viewModel.selectedModelId ?: 1
    val models by viewModel.allModels.collectAsStateWithLifecycle()
    val model = models.firstOrNull { it.id == modelId } ?: return
    val user by viewModel.currentUser.collectAsStateWithLifecycle()

    var customAddAmountText by remember { mutableStateOf("") }
    var showAddBalanceDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo("AVAILABILITY") }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Confirm Booking & Pay", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp))) {
                                ModelImage(imageName = model.imageResName, contentDescription = model.name)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(model.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Verified Model", color = PinkHighlight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFF2E2E3E), thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        SummaryRowSpec(label = "Date", value = viewModel.bookingDate)
                        SummaryRowSpec(label = "Time Slot", value = viewModel.bookingTime)
                        SummaryRowSpec(label = "Service Type", value = viewModel.bookingService)
                        SummaryRowSpec(label = "Duration", value = "${viewModel.bookingDuration} Hours")
                        SummaryRowSpec(label = "Total Cost", value = "৳${viewModel.bookingPriceSummary.toInt()}", valueColor = PinkHighlight)
                    }
                }
            }

            // --- WALLET BALANCE & QUICK TOP-UP CARD ---
            item {
                val currentBalance = user?.balance ?: 0.0
                val bookingCost = viewModel.bookingPriceSummary
                val hasSufficient = currentBalance >= bookingCost

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B2E)),
                    border = BorderStroke(1.dp, PinkHighlight.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(PinkHighlight.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = "Wallet Balance",
                                        tint = PinkHighlight,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("My Wallet Balance", color = TextSecondary, fontSize = 11.sp)
                                    Text("৳${currentBalance.toInt()}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                                }
                            }

                            Button(
                                onClick = { showAddBalanceDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Balance", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Top-Up Chips Row
                        Text("Quick Top-Up Wallet:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(500, 1000, 2000, 5000).forEach { amount ->
                                OutlinedButton(
                                    onClick = { viewModel.quickAddWalletBalance(amount.toDouble()) },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, PinkHighlight.copy(alpha = 0.6f)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.weight(1f).height(32.dp)
                                ) {
                                    Text("+৳$amount", color = PinkHighlight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (viewModel.bookingPaymentMethod == "Wallet Balance") {
                            Spacer(modifier = Modifier.height(12.dp))
                            if (hasSufficient) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(OnlineGreen.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = OnlineGreen, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Sufficient wallet balance available for instant checkout.", color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            } else {
                                val gap = (bookingCost - currentBalance).toInt()
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFEF3C7), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Short by ৳$gap in wallet balance.", color = Color(0xFFB45309), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        TextButton(
                                            onClick = { viewModel.quickAddWalletBalance(gap.toDouble()) },
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("Add ৳$gap Now", color = PinkHighlight, fontWeight = FontWeight.Black, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Additional Specs Inputs
            item {
                Text("Booking Specifications", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(12.dp))

                PremiumTextField(
                    value = viewModel.bookingLocation,
                    onValueChange = { viewModel.bookingLocation = it },
                    label = "Service Location / Address",
                    placeholder = "E.g., Banani, Dhaka",
                    leadingIcon = Icons.Default.LocationOn,
                    testTag = "booking_location_input"
                )

                Spacer(modifier = Modifier.height(12.dp))

                PremiumTextField(
                    value = viewModel.bookingNotes,
                    onValueChange = { viewModel.bookingNotes = it },
                    label = "Special Instructions / Notes",
                    placeholder = "E.g., Casual wear photoshoot for brand promotion.",
                    leadingIcon = Icons.Default.NoteAlt,
                    testTag = "booking_notes_input"
                )
            }

            // Payment Methods
            item {
                Text("Select Payment Gateway", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(12.dp))

                val payMethods = listOf(
                    "Wallet Balance" to Color(0xFF9C27B0),
                    "Google Play Billing" to Color(0xFF00E676),
                    "bKash" to Color(0xFFE91E63),
                    "Nagad" to Color(0xFFF57C00),
                    "Stripe" to Color(0xFF6772E5),
                    "Cash" to Color(0xFF4CAF50)
                )

                payMethods.forEach { (method, color) ->
                    val isSel = viewModel.bookingPaymentMethod == method
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { viewModel.bookingPaymentMethod = method },
                        colors = CardDefaults.cardColors(containerColor = if (isSel) color.copy(alpha = 0.15f) else DarkSurface),
                        border = BorderStroke(1.dp, if (isSel) color else Color(0xFF2E2E3E)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(color, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (method == "Wallet Balance") {
                                        Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    } else {
                                        Text(
                                            text = method.take(2).uppercase(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(method, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    if (method == "Wallet Balance") {
                                        Text("Available: ৳${user?.balance?.toInt() ?: 0}", color = TextSecondary, fontSize = 11.sp)
                                    }
                                }
                            }

                            RadioButton(
                                selected = isSel,
                                onClick = { viewModel.bookingPaymentMethod = method },
                                colors = RadioButtonDefaults.colors(selectedColor = color)
                            )
                        }
                    }
                }
            }
        }

        // Action submit button
        Button(
            onClick = { viewModel.createBooking() },
            colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(52.dp)
                .testTag("submit_booking_button")
        ) {
            Text("Confirm & Pay ৳${viewModel.bookingPriceSummary.toInt()}", fontWeight = FontWeight.Black, fontSize = 16.sp)
        }
    }

    // Modal Dialog to Add Wallet Balance
    if (showAddBalanceDialog) {
        AlertDialog(
            onDismissRequest = { showAddBalanceDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = PinkHighlight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Wallet Balance", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Enter amount to deposit into your account wallet:", color = TextSecondary, fontSize = 12.sp)

                    OutlinedTextField(
                        value = customAddAmountText,
                        onValueChange = { customAddAmountText = it },
                        label = { Text("Amount (৳)", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PinkHighlight,
                            unfocusedBorderColor = Color(0xFF2E2E3E)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(500, 1000, 2500, 5000).forEach { amt ->
                            OutlinedButton(
                                onClick = { customAddAmountText = amt.toString() },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("৳$amt", fontSize = 10.sp, color = Color.White)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = customAddAmountText.toDoubleOrNull() ?: 500.0
                        viewModel.quickAddWalletBalance(amount)
                        showAddBalanceDialog = false
                        customAddAmountText = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
                ) {
                    Text("Recharge Now", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBalanceDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
fun SummaryRowSpec(label: String, value: String, valueColor: Color = Color.White) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary, fontSize = 13.sp)
        Text(value, color = valueColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

// ============================================================================
// 5. BOOKING CONFIRMED SUCCESS SCREEN
// ============================================================================

@Composable
fun ConfirmBookingScreen(viewModel: AppViewModel) {
    val booking = viewModel.lastConfirmedBooking

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(OnlineGreen.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = OnlineGreen,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                "Booking Confirmed!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "We will notify you as soon as the model accepts your request.",
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Spec recap
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SummaryRowSpec(label = "Booked Model", value = booking?.modelName ?: "Jessica")
                    SummaryRowSpec(label = "Reserved Session", value = booking?.date ?: "20 May 2025")
                    SummaryRowSpec(label = "Reserved Hour", value = booking?.time ?: "11:00 AM")
                    SummaryRowSpec(label = "Amount Released", value = "৳${booking?.totalPrice?.toInt() ?: 120}")
                    SummaryRowSpec(label = "Payment Gateway", value = booking?.paymentMethod ?: "bKash")
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            PremiumButton(
                text = "Go to My Bookings",
                onClick = {
                    viewModel.selectedTab = 2
                    viewModel.navigateTo("DASHBOARD")
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "go_to_bookings_button"
            )

            Spacer(modifier = Modifier.height(12.dp))

            PremiumOutlinedButton(
                text = "Back to Home",
                onClick = {
                    viewModel.selectedTab = 0
                    viewModel.navigateTo("DASHBOARD")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ============================================================================
// 6. EDIT PROFILE SCREEN
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val purplePrimary = Color(0xFF7C3AED)
    val cardBg = Color.White
    val textDark = Color(0xFF1E202C)
    val textSub = Color(0xFF6B7280)

    var name by remember { mutableStateOf(currentUser?.name ?: "Rahul Verma") }
    var email by remember { mutableStateOf(currentUser?.email ?: "rahul.verma@email.com") }
    var phone by remember { mutableStateOf(viewModel.clientPhone) }
    var city by remember { mutableStateOf(currentUser?.city ?: "Dhaka") }
    var dob by remember { mutableStateOf(viewModel.clientDob) }
    var gender by remember { mutableStateOf(viewModel.clientGender) }

    var languages by remember { mutableStateOf(viewModel.clientPreferredLanguages) }
    var categories by remember { mutableStateOf(viewModel.clientPreferredCategories) }
    var budget by remember { mutableStateOf(viewModel.clientBudgetRange) }
    var bookingTime by remember { mutableStateOf(viewModel.clientBookingTime) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.goBack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E103C))
            )
        },
        containerColor = Color(0xFFF6F7FB)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Photo Header Card
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
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
                                .border(3.dp, purplePrimary, CircleShape)
                        ) {
                            SubcomposeAsyncImage(
                                model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
                                contentDescription = "Profile Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(purplePrimary, CircleShape)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Change Photo",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tap photo to change profile picture", color = textSub, fontSize = 11.sp)
                }
            }

            // Personal Information Card
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Personal Details", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textDark)

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purplePrimary)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purplePrimary)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purplePrimary)
                    )

                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City / Location") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purplePrimary)
                    )

                    OutlinedTextField(
                        value = dob,
                        onValueChange = { dob = it },
                        label = { Text("Date of Birth") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purplePrimary)
                    )

                    Text("Gender", color = textSub, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Male", "Female", "Other").forEach { g ->
                            val isSelected = gender.equals(g, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { gender = g },
                                label = { Text(g) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = purplePrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Booking Preferences Card
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Booking Preferences", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textDark)

                    OutlinedTextField(
                        value = languages,
                        onValueChange = { languages = it },
                        label = { Text("Preferred Languages") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purplePrimary)
                    )

                    OutlinedTextField(
                        value = categories,
                        onValueChange = { categories = it },
                        label = { Text("Preferred Categories") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purplePrimary)
                    )

                    OutlinedTextField(
                        value = budget,
                        onValueChange = { budget = it },
                        label = { Text("Budget Range") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purplePrimary)
                    )

                    OutlinedTextField(
                        value = bookingTime,
                        onValueChange = { bookingTime = it },
                        label = { Text("Preferred Booking Time") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purplePrimary)
                    )
                }
            }

            // Save Changes Button
            Button(
                onClick = {
                    viewModel.updateUserProfileDetails(
                        name = name,
                        email = email,
                        phone = phone,
                        city = city,
                        dob = dob,
                        gender = gender
                    )
                    viewModel.updateUserPreferences(
                        languages = languages,
                        categories = categories,
                        budget = budget,
                        bookingTime = bookingTime
                    )
                    viewModel.goBack()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = purplePrimary)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Profile Changes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

