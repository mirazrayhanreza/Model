package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import coil.compose.SubcomposeAsyncImage
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.components.ModelImage
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

// ============================================================================
// REUSABLE COMPONENTS & STYLES
// ============================================================================

@Composable
fun PremiumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = PinkHighlight,
    contentColor: Color = Color.White,
    icon: ImageVector? = null,
    testTag: String = ""
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .height(52.dp)
            .testTag(testTag),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
    }
}

@Composable
fun PremiumOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = PinkHighlight,
    contentColor: Color = PinkHighlight,
    icon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(52.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
    }
}

@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = leadingIcon?.let { { Icon(imageVector = it, contentDescription = null, tint = TextSecondary) } },
        trailingIcon = trailingIcon,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PinkHighlight,
            unfocusedBorderColor = Color(0xFFFF85A6),
            focusedLabelColor = PinkHighlight,
            unfocusedLabelColor = TextSecondary,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryCodePickerModal(
    onDismiss: () -> Unit,
    viewModel: AppViewModel
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredCountries = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            WORLDWIDE_COUNTRIES
        } else {
            val q = searchQuery.trim().lowercase()
            WORLDWIDE_COUNTRIES.filter {
                it.name.lowercase().contains(q) ||
                it.dialCode.lowercase().contains(q) ||
                it.code.lowercase().contains(q)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌍", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Select Country Code",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search country or dial code...", color = TextSecondary, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PinkHighlight,
                        unfocusedBorderColor = Color(0xFFFF85A6),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }
        },
        text = {
            Column(modifier = Modifier.height(360.dp)) {
                // Popular Country Quick Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("🇧🇩 BD", "🇺🇸 USA", "🇬🇧 UK", "🇮🇳 India", "🇦🇪 UAE", "🇸🇦 KSA", "🇨🇦 CA", "🇦🇺 AU").forEach { tag ->
                        val countryMatch = WORLDWIDE_COUNTRIES.firstOrNull {
                            val code = tag.split(" ").last()
                            it.name.contains(code, ignoreCase = true) || it.code.equals(code, ignoreCase = true)
                        }
                        val isSel = countryMatch?.dialCode == viewModel.selectedCountryDialCode && countryMatch?.code == viewModel.selectedCountryIso
                        FilterChip(
                            selected = isSel,
                            onClick = {
                                countryMatch?.let {
                                    viewModel.selectCountry(it)
                                    onDismiss()
                                }
                            },
                            label = { Text(tag, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PurplePrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFFF85A6),
                                labelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredCountries) { country ->
                        val isSelected = country.dialCode == viewModel.selectedCountryDialCode && country.code == viewModel.selectedCountryIso
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) PurpleSoftAccent.copy(alpha = 0.25f) else DarkSurface
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) PurplePrimary else Color(0xFFFF85A6)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectCountry(country)
                                    onDismiss()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = country.flag, fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = country.name,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = country.code,
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = country.dialCode,
                                        color = PinkHighlight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = PinkHighlight,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        containerColor = Color.White
    )
}

@Composable
fun CountryCodePhoneInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Phone Number",
    placeholder: String = "1XX-XXXXXXX",
    viewModel: AppViewModel,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    var showModal by remember { mutableStateOf(false) }

    if (showModal) {
        CountryCodePickerModal(
            onDismiss = { showModal = false },
            viewModel = viewModel
        )
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Country Code Selection Button
            Surface(
                onClick = { showModal = true },
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFFF85A6)),
                modifier = Modifier
                    .height(56.dp)
                    .width(115.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = viewModel.selectedCountryFlag, fontSize = 20.sp)
                    Text(
                        text = viewModel.selectedCountryDialCode,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Country",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Phone Input Field
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                placeholder = { Text(placeholder) },
                leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = TextSecondary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkHighlight,
                    unfocusedBorderColor = Color(0xFFFF85A6),
                    focusedLabelColor = PinkHighlight,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag(testTag)
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    onViewAllClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(PinkHighlight)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    fontSize = 18.sp
                )
            )
        }
        if (onViewAllClick != null) {
            Text(
                text = "View All",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PinkHighlight
                ),
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }
    }
}

// ============================================================================
// 1. SPLASH SCREEN
// ============================================================================

@Composable
fun SplashScreen(viewModel: AppViewModel) {
    // Splash screen is the primary first page (Fast Page) on startup and remains visible until the user interacts
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFF2A85),
                        Color(0xFFE91E63),
                        Color(0xFFC2185B),
                        Color(0xFF560027)
                    )
                )
            )
    ) {
        // High Fashion Brunette Model Portrait Background
        SubcomposeAsyncImage(
            model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=1200",
            contentDescription = "Modol Connect Splash Model",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient Filter Overlays (Vibrant Pink & Berry Radiance Matching the Design)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x33FF2D55),  // Soft translucent rosy pink on top
                            Color(0x66C2185B),  // Medium magenta glow across portrait
                            Color(0xD9E91E63),  // Rich hot pink branding layer
                            Color(0xFF880E4F),  // Deep berry bottom vignette
                            Color(0xFF4A0026)   // Deep solid bottom base
                        )
                    )
                )
        )

        // Top Glassmorphic Heart Badge
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(20.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.22f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)),
                shadowElevation = 4.dp,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Heart",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Central Logo & Branding + Bottom Call-to-Action
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Elegant Stylized Gradient "M" Monogram Badge
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = Color.White.copy(alpha = 0.18f),
                border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.5f)),
                shadowElevation = 8.dp,
                modifier = Modifier.size(84.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.size(48.dp)) {
                        val w = size.width
                        val h = size.height
                        val path = Path().apply {
                            moveTo(w * 0.16f, h * 0.82f)
                            lineTo(w * 0.16f, h * 0.22f)
                            lineTo(w * 0.50f, h * 0.62f)
                            lineTo(w * 0.84f, h * 0.22f)
                            lineTo(w * 0.84f, h * 0.82f)
                        }
                        drawPath(
                            path = path,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF00F2FE), // Vivid cyan
                                    Color(0xFF9B51E0), // Electric purple
                                    Color(0xFFFF2A85)  // Hot neon pink
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(w, h)
                            ),
                            style = Stroke(
                                width = 5.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Brand Title: MODOL CONNECT
            Text(
                text = "MODOL CONNECT",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 3.sp,
                    fontSize = 28.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle Tagline: Real People, Real Connection
            Text(
                text = "Real People, Real Connection",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.92f),
                    letterSpacing = 1.2.sp,
                    fontSize = 15.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Prominent "Get Started" Action Button
            Button(
                onClick = {
                    viewModel.splashFinished = true
                    if (viewModel.isLoggedIn) {
                        viewModel.navigateTo("DASHBOARD")
                    } else {
                        viewModel.navigateTo("LOGIN")
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFFD81B60)
                ),
                shape = RoundedCornerShape(28.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("splash_get_started_btn")
            ) {
                Text(
                    text = if (viewModel.isLoggedIn) "Go to Dashboard" else "Get Started",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD81B60)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Get Started",
                    tint = Color(0xFFD81B60),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!viewModel.isLoggedIn) {
                Row(
                    modifier = Modifier
                        .clickable {
                            viewModel.splashFinished = true
                            viewModel.navigateTo("LOGIN")
                        }
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account? ",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Login",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            } else {
                Text(
                    text = "Welcome Back • Active Session",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtle secondary indicator
            Text(
                text = "Explore World's #1 Model Marketplace",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ============================================================================
// 2. ONBOARDING SCREEN
// ============================================================================

@Composable
fun OnboardingScreen(viewModel: AppViewModel) {
    var step by remember { mutableStateOf(0) }
    
    val onboardingPages = listOf(
        OnboardingData(
            title = "Discover Professional Models",
            desc = "Find the perfect faces for your photoshoots, brand promotions, runways, and creative collaborations in seconds.",
            icon = Icons.Default.Search,
            bgColor = listOf(Color.White, Color(0xFFF8FAFC))
        ),
        OnboardingData(
            title = "Book Safely & Instantly",
            desc = "Select your preferred date, hour, and service type. Our fully automated scheduler secures your sessions hassle-free.",
            icon = Icons.Default.CalendarMonth,
            bgColor = listOf(Color.White, Color(0xFFF8FAFC))
        ),
        OnboardingData(
            title = "Secure Local Payments",
            desc = "Pay safely using bKash, Nagad, Rocket, or international cards. Funds are protected and released only after session completion.",
            icon = Icons.Default.AccountBalanceWallet,
            bgColor = listOf(Color.White, Color(0xFFF8FAFC))
        )
    )

    val currentPage = onboardingPages[step]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Row (Skip button)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    viewModel.onboardingFinished = true
                    viewModel.navigateTo("LOGIN")
                }) {
                    Text("Skip", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            // Central Illustration / Icon
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .background(Color(0xFFFFF0F5), CircleShape)
                        .border(1.dp, Color(0xFFFFD1DF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = currentPage.icon,
                        contentDescription = null,
                        tint = PinkHighlight,
                        modifier = Modifier.size(56.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(36.dp))
                
                Text(
                    text = currentPage.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                
                Spacer(modifier = Modifier.height(14.dp))
                
                Text(
                    text = currentPage.desc,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        fontSize = 15.sp
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Bottom Navigation and Page Indicators
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 28.dp)
                ) {
                    onboardingPages.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (index == step) 24.dp else 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (index == step) PinkHighlight else Color(0xFFFF85A6))
                        )
                    }
                }

                // Next / Get Started Button
                PremiumButton(
                    text = if (step == onboardingPages.size - 1) "Get Started" else "Next Step",
                    onClick = {
                        if (step < onboardingPages.size - 1) {
                            step++
                        } else {
                            viewModel.onboardingFinished = true
                            viewModel.navigateTo("LOGIN")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "onboarding_next_button"
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

data class OnboardingData(
    val title: String,
    val desc: String,
    val icon: ImageVector,
    val bgColor: List<Color>
)

// ============================================================================
// 3. AUTHENTICATION SCREENS MATCHING WIREFRAME MOCKUPS
// ============================================================================

val PurplePrimary = PinkHighlight
val PurpleLightBg = Color(0xFFFFF0F5)
val PurpleCardBorder = Color(0xFFFFD1DF)
val PurpleSoftAccent = Color(0xFFFFE4EC)

@Composable
fun ModolConnectLogoHeader(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(PinkHighlight, Color(0xFFFF6584))
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "M",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "MODOL ",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = 2.sp,
                    fontSize = 20.sp
                )
            )
            Text(
                text = "CONNECT",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = PinkHighlight,
                    letterSpacing = 2.sp,
                    fontSize = 20.sp
                )
            )
        }
    }
}

@Composable
fun SocialLoginRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Google Icon
        Card(
            modifier = Modifier
                .size(48.dp)
                .clickable { },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFFF85A6)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("G", color = Color(0xFFEA4335), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        // Facebook Icon
        Card(
            modifier = Modifier
                .size(48.dp)
                .clickable { },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFFF85A6)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("f", color = Color(0xFF1877F2), fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        // Apple Icon
        Card(
            modifier = Modifier
                .size(48.dp)
                .clickable { },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFFF85A6)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }
        }
    }
}

@Composable
fun OtpSixBoxInput(code: String, onCodeChange: (String) -> Unit) {
    val focusRequester = remember { FocusRequester() }

    BasicTextField(
        value = code,
        onValueChange = { newText ->
            val digits = newText.filter { it.isDigit() }.take(6)
            onCodeChange(digits)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        decorationBox = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { focusRequester.requestFocus() },
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                for (i in 0 until 6) {
                    val charStr = if (i < code.length) code[i].toString() else ""
                    val isFocused = i == code.length || (i == 5 && code.length == 6)

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White, shape = RoundedCornerShape(12.dp))
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = if (isFocused) PinkHighlight else Color(0xFFFF85A6),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = charStr,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 20.sp
                            )
                        )
                    }
                }
            }
        }
    )
}

// ---------------- 1. LOGIN SCREEN ----------------
@Composable
fun LoginScreen(viewModel: AppViewModel) {
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                ModolConnectLogoHeader()
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Welcome Back!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Login to continue to your account",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                    textAlign = TextAlign.Center
                )

                // 1-Tap Instant Demo Login Chips
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "ADMIN" to "Admin",
                        "MODEL" to "Model",
                        "USER" to "Client",
                        "CASH_AGENT" to "Agent"
                    ).forEach { (role, label) ->
                        OutlinedButton(
                            onClick = { viewModel.loginAsDemo(role) },
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp),
                            border = BorderStroke(1.dp, Color(0xFFFF85A6)),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                        ) {
                            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PurplePrimary)
                        }
                    }
                }

                // Error Message Banner
                if (viewModel.authErrorMessage != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F5)),
                        border = BorderStroke(1.dp, Color(0xFFFFB3BA)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = viewModel.authErrorMessage ?: "",
                                color = Color(0xFFD32F2F),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Input: Phone or Email
                PremiumTextField(
                    value = viewModel.loginEmail,
                    onValueChange = { 
                        viewModel.loginEmail = it 
                        if (viewModel.authErrorMessage != null) viewModel.authErrorMessage = null
                    },
                    label = "Phone Number or Email",
                    placeholder = "Enter phone or email",
                    leadingIcon = Icons.Default.Person,
                    testTag = "login_identity_input"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Input: Password
                OutlinedTextField(
                    value = viewModel.loginPassword,
                    onValueChange = { 
                        viewModel.loginPassword = it 
                        if (viewModel.authErrorMessage != null) viewModel.authErrorMessage = null
                    },
                    label = { Text("Password") },
                    placeholder = { Text("••••••••") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TextSecondary) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password visibility",
                                tint = TextSecondary
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PinkHighlight,
                        unfocusedBorderColor = Color(0xFFFF85A6),
                        focusedLabelColor = PinkHighlight,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_password_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Remember Me & Forgot Password
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = viewModel.rememberMe,
                            onCheckedChange = { viewModel.rememberMe = it },
                            colors = CheckboxDefaults.colors(checkedColor = PinkHighlight)
                        )
                        Text("Remember Me", color = TextSecondary, fontSize = 13.sp)
                    }

                    Text(
                        text = "Forgot Password?",
                        color = PinkHighlight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable { viewModel.navigateTo("FORGOT_PASSWORD") }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Primary Login Button
                Button(
                    onClick = { viewModel.login() },
                    enabled = !viewModel.isAuthLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_submit_button")
                ) {
                    if (viewModel.isAuthLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text("Login", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Divider: or continue with
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFFF85A6))
                    Text(
                        text = "  or continue with  ",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFFF85A6))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Login with OTP Outlined Button
                OutlinedButton(
                    onClick = { viewModel.navigateTo("LOGIN_WITH_OTP") },
                    border = BorderStroke(1.dp, PurplePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Smartphone, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Login with OTP", color = PurplePrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Social Icons Row
                SocialLoginRow()

                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Don't have an account? ", color = TextSecondary, fontSize = 13.sp)
                    Text(
                        text = "Register Now",
                        color = PurplePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable { viewModel.navigateTo("SELECT_ACCOUNT_TYPE") }
                    )
                }
            }
        }
    }
}

// ---------------- 2. SELECT ACCOUNT TYPE SCREEN ----------------
@Composable
fun SelectAccountTypeScreen(viewModel: AppViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Top Bar Back Button
                Row(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = { viewModel.navigateTo("LOGIN") },
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFFF85A6), CircleShape)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                ModolConnectLogoHeader()
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Join Modol Connect today!",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Select Account Type",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 15.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Card 1: User
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.registerRole = "USER"
                            viewModel.navigateTo("REGISTER_USER")
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, PinkHighlight),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(PinkHighlight, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "User",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary,
                                    fontSize = 16.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Join as a user and explore amazing features",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card 2: Model
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.registerRole = "MODEL"
                            viewModel.navigateTo("REGISTER_MODEL")
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, Color(0xFFFF4081)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color(0xFFFF4081), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Model",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Model",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary,
                                    fontSize = 16.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Create your model profile and grow your career",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            // Footer Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account? ", color = TextSecondary, fontSize = 13.sp)
                Text(
                    text = "Login",
                    color = PinkHighlight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { viewModel.navigateTo("LOGIN") }
                )
            }
        }
    }
}

// ---------------- 3. CREATE ACCOUNT - USER SCREEN ----------------
@Composable
fun RegisterUserScreen(viewModel: AppViewModel) {
    val activity = LocalContext.current as? android.app.Activity
    var passVisible by remember { mutableStateOf(false) }
    var confirmPassVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { viewModel.navigateTo("SELECT_ACCOUNT_TYPE") },
                    modifier = Modifier
                        .background(Color.White, CircleShape)
                        .border(1.dp, Color(0xFFFF85A6), CircleShape)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    // Top Icon Badge
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(PinkHighlight, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Join as a User",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )

                    // Auth Error Banner
                    if (viewModel.authErrorMessage != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F5)),
                            border = BorderStroke(1.dp, Color(0xFFFFB3BA)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = viewModel.authErrorMessage ?: "",
                                    color = Color(0xFFD32F2F),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. Full Name
                    PremiumTextField(
                        value = viewModel.registerName,
                        onValueChange = { viewModel.registerName = it },
                        label = "Full Name",
                        placeholder = "Enter full name",
                        leadingIcon = Icons.Default.Person,
                        testTag = "reg_user_name"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Phone Number
                    CountryCodePhoneInputField(
                        value = viewModel.registerPhone,
                        onValueChange = { viewModel.registerPhone = it },
                        label = "Phone Number",
                        placeholder = "1XX-XXXXXXX",
                        viewModel = viewModel,
                        testTag = "reg_user_phone"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Email Address
                    PremiumTextField(
                        value = viewModel.registerEmail,
                        onValueChange = { viewModel.registerEmail = it },
                        label = "Email Address",
                        placeholder = "example@email.com",
                        leadingIcon = Icons.Default.Email,
                        testTag = "reg_user_email"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. Password
                    OutlinedTextField(
                        value = viewModel.registerPassword,
                        onValueChange = { viewModel.registerPassword = it },
                        label = { Text("Password") },
                        placeholder = { Text("••••••••") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TextSecondary) },
                        trailingIcon = {
                            IconButton(onClick = { passVisible = !passVisible }) {
                                Icon(imageVector = if (passVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = TextSecondary)
                            }
                        },
                        visualTransformation = if (passVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = Color(0xFFFF85A6),
                            focusedLabelColor = PurplePrimary,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 5. Confirm Password
                    OutlinedTextField(
                        value = viewModel.registerConfirmPassword,
                        onValueChange = { viewModel.registerConfirmPassword = it },
                        label = { Text("Confirm Password") },
                        placeholder = { Text("••••••••") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TextSecondary) },
                        trailingIcon = {
                            IconButton(onClick = { confirmPassVisible = !confirmPassVisible }) {
                                Icon(imageVector = if (confirmPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = TextSecondary)
                            }
                        },
                        visualTransformation = if (confirmPassVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = Color(0xFFFF85A6),
                            focusedLabelColor = PurplePrimary,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 6. Country Dropdown / Selector
                    OutlinedTextField(
                        value = viewModel.registerCountry,
                        onValueChange = { viewModel.registerCountry = it },
                        label = { Text("Country") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = TextSecondary) },
                        trailingIcon = { Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = Color(0xFFFF85A6),
                            focusedLabelColor = PurplePrimary,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 7. Date of Birth
                    OutlinedTextField(
                        value = viewModel.registerDob,
                        onValueChange = { viewModel.registerDob = it },
                        label = { Text("Date of Birth") },
                        leadingIcon = { Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = TextSecondary) },
                        trailingIcon = { Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = Color(0xFFFF85A6),
                            focusedLabelColor = PurplePrimary,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 8. Gender Selection
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Gender", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val genders = listOf("Male", "Female", "Other")
                            genders.forEach { gender ->
                                val isSelected = viewModel.registerGender == gender
                                OutlinedButton(
                                    onClick = { viewModel.registerGender = gender },
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) PurplePrimary else Color(0xFFFF85A6)
                                    ),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSelected) PurplePrimary.copy(alpha = 0.12f) else Color.White,
                                        contentColor = if (isSelected) PurplePrimary else TextSecondary
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = when (gender) {
                                                "Male" -> Icons.Default.Male
                                                "Female" -> Icons.Default.Female
                                                else -> Icons.Default.Transgender
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(gender, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Checkbox Terms
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = viewModel.registerAgreeTerms,
                            onCheckedChange = { viewModel.registerAgreeTerms = it },
                            colors = CheckboxDefaults.colors(checkedColor = PurplePrimary)
                        )
                        Text(
                            text = "I agree to the Terms & Conditions and Privacy Policy",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Create Account Button
                    Button(
                        onClick = { viewModel.register() },
                        enabled = !viewModel.isAuthLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("register_user_submit_button")
                    ) {
                        if (viewModel.isAuthLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text("Create Account", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Secondary: Send Email OTP option (support@modolconncet.fun)
                    OutlinedButton(
                        onClick = {
                            viewModel.sendEmailVerificationOtp(viewModel.registerEmail)
                        },
                        border = BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Register with Email OTP", color = PurplePrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secondary: Send Phone OTP option (Firebase Live OTP)
                    OutlinedButton(
                        onClick = {
                            viewModel.sendRegisterOtp(viewModel.registerPhone, source = "REGISTER_USER", activity = activity)
                        },
                        border = BorderStroke(1.dp, Color(0xFFFF85A6)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Smartphone, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Register with Phone OTP", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Footer Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account? ", color = TextSecondary, fontSize = 13.sp)
                Text(
                    text = "Login",
                    color = PurplePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { viewModel.navigateTo("LOGIN") }
                )
            }
        }
    }
}

// ---------------- 4. CREATE ACCOUNT - MODEL SCREEN ----------------
@Composable
fun RegisterModelScreen(viewModel: AppViewModel) {
    val activity = LocalContext.current as? android.app.Activity
    var passVisible by remember { mutableStateOf(false) }
    var confirmPassVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { viewModel.navigateTo("SELECT_ACCOUNT_TYPE") }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    // Top Icon Badge - Pink Star
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(Color(0xFFFF4081), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Join as a Model",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )

                    // Auth Error Banner
                    if (viewModel.authErrorMessage != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF3B1219)),
                            border = BorderStroke(1.dp, Color(0xFFFF5252)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = viewModel.authErrorMessage ?: "",
                                    color = Color(0xFFFFCDD2),
                                    fontSize = 13.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. Full Name
                    PremiumTextField(
                        value = viewModel.registerName,
                        onValueChange = { viewModel.registerName = it },
                        label = "Full Name",
                        placeholder = "Enter full name",
                        leadingIcon = Icons.Default.Person,
                        testTag = "reg_model_name"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Phone Number
                    CountryCodePhoneInputField(
                        value = viewModel.registerPhone,
                        onValueChange = { viewModel.registerPhone = it },
                        label = "Phone Number",
                        placeholder = "1XX-XXXXXXX",
                        viewModel = viewModel,
                        testTag = "reg_model_phone"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Email Address
                    PremiumTextField(
                        value = viewModel.registerEmail,
                        onValueChange = { viewModel.registerEmail = it },
                        label = "Email Address",
                        placeholder = "example@email.com",
                        leadingIcon = Icons.Default.Email,
                        testTag = "reg_model_email"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. Password
                    OutlinedTextField(
                        value = viewModel.registerPassword,
                        onValueChange = { viewModel.registerPassword = it },
                        label = { Text("Password") },
                        placeholder = { Text("••••••••") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TextSecondary) },
                        trailingIcon = {
                            IconButton(onClick = { passVisible = !passVisible }) {
                                Icon(imageVector = if (passVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = TextSecondary)
                            }
                        },
                        visualTransformation = if (passVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF4081),
                            unfocusedBorderColor = Color(0xFFFF85A6),
                            focusedLabelColor = Color(0xFFFF4081),
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 5. Confirm Password
                    OutlinedTextField(
                        value = viewModel.registerConfirmPassword,
                        onValueChange = { viewModel.registerConfirmPassword = it },
                        label = { Text("Confirm Password") },
                        placeholder = { Text("••••••••") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TextSecondary) },
                        trailingIcon = {
                            IconButton(onClick = { confirmPassVisible = !confirmPassVisible }) {
                                Icon(imageVector = if (confirmPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = TextSecondary)
                            }
                        },
                        visualTransformation = if (confirmPassVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF4081),
                            unfocusedBorderColor = Color(0xFFFF85A6),
                            focusedLabelColor = Color(0xFFFF4081),
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 6. Country Dropdown / Selector
                    OutlinedTextField(
                        value = viewModel.registerCountry,
                        onValueChange = { viewModel.registerCountry = it },
                        label = { Text("Country") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = TextSecondary) },
                        trailingIcon = { Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF4081),
                            unfocusedBorderColor = Color(0xFFFF85A6),
                            focusedLabelColor = Color(0xFFFF4081),
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 7. Date of Birth
                    OutlinedTextField(
                        value = viewModel.registerDob,
                        onValueChange = { viewModel.registerDob = it },
                        label = { Text("Date of Birth") },
                        leadingIcon = { Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = TextSecondary) },
                        trailingIcon = { Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF4081),
                            unfocusedBorderColor = Color(0xFFFF85A6),
                            focusedLabelColor = Color(0xFFFF4081),
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 8. Gender Selection
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Gender", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val genders = listOf("Male", "Female", "Other")
                            genders.forEach { gender ->
                                val isSelected = viewModel.registerGender == gender
                                OutlinedButton(
                                    onClick = { viewModel.registerGender = gender },
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) Color(0xFFFF4081) else Color(0xFFFF85A6)
                                    ),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSelected) Color(0xFFFF4081).copy(alpha = 0.12f) else Color.White,
                                        contentColor = if (isSelected) Color(0xFFFF4081) else TextSecondary
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = when (gender) {
                                                "Male" -> Icons.Default.Male
                                                "Female" -> Icons.Default.Female
                                                else -> Icons.Default.Transgender
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(gender, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Checkbox Terms
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = viewModel.registerAgreeTerms,
                            onCheckedChange = { viewModel.registerAgreeTerms = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFF4081))
                        )
                        Text(
                            text = "I agree to the Terms & Conditions and Privacy Policy",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Create Account Button
                    Button(
                        onClick = { viewModel.register() },
                        enabled = !viewModel.isAuthLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("register_model_submit_button")
                    ) {
                        if (viewModel.isAuthLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text("Create Model Account", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Secondary: Send Email OTP option (support@modolconncet.fun)
                    OutlinedButton(
                        onClick = {
                            viewModel.sendEmailVerificationOtp(viewModel.registerEmail)
                        },
                        border = BorderStroke(1.dp, Color(0xFFFF4081).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = Color(0xFFFF4081), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Register with Email OTP", color = Color(0xFFFF4081), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secondary: Send Phone OTP option (Firebase Live OTP)
                    OutlinedButton(
                        onClick = {
                            viewModel.sendRegisterOtp(viewModel.registerPhone, source = "REGISTER_MODEL", activity = activity)
                        },
                        border = BorderStroke(1.dp, Color(0xFFFF85A6)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Smartphone, contentDescription = null, tint = Color(0xFFFF4081), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Register with Phone OTP", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Footer Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account? ", color = TextSecondary, fontSize = 13.sp)
                Text(
                    text = "Login",
                    color = PurplePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { viewModel.navigateTo("LOGIN") }
                )
            }
        }
    }
}

// ---------------- 5. PHONE VERIFICATION SCREEN ----------------
@Composable
fun PhoneVerificationScreen(viewModel: AppViewModel) {
    val activity = LocalContext.current as? android.app.Activity
    val isLoginMode = viewModel.verificationSource == "LOGIN_WITH_OTP"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = {
                            when (viewModel.verificationSource) {
                                "LOGIN_WITH_OTP" -> viewModel.navigateTo("LOGIN_WITH_OTP")
                                "REGISTER_MODEL" -> viewModel.navigateTo("REGISTER_MODEL")
                                else -> viewModel.navigateTo("REGISTER_USER")
                            }
                        },
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFFF85A6), CircleShape)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Artwork illustration (Phone with Badge)
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color(0xFFFFF0F5), shape = CircleShape)
                        .border(1.dp, Color(0xFFFFD1DF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color.White, shape = RoundedCornerShape(16.dp))
                            .border(1.dp, PurplePrimary, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Smartphone, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(36.dp))
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (-10).dp, y = (-10).dp)
                            .background(Color(0xFF4CAF50), shape = CircleShape)
                            .padding(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (isLoginMode) "Login OTP Verification" else "Phone Verification",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Enter the 6-digit code sent to\n${viewModel.verificationTarget}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Security Notice Card: Why OTP should never be shared!
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "নিরাপত্তা সতর্কতা: ওটিপি কোড কখনো কারো সাথে শেয়ার করবেন না!",
                                color = Color(0xFF92400E),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Modol Connect বা সাপোর্ট টিম কখনো আপনার ওটিপি চাইবে না।",
                                color = Color(0xFFB45309),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 6 Box OTP Input
                OtpSixBoxInput(
                    code = viewModel.otpCode,
                    onCodeChange = {
                        if (it.length <= 6) {
                            viewModel.otpCode = it
                            if (it.length == 6) {
                                viewModel.authErrorMessage = null
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Secure SMS Verification Gateway Status Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFFF85A6)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PhoneIphone, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Official SMS Verification Gateway",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "● LIVE SMS",
                                    color = Color(0xFF16A34A),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "আপনার মোবাইল নম্বরে ৬-সংখ্যার সুরক্ষিত ওটিপি কোড পাঠানো হয়েছে। কোডটি প্রদান করে অ্যাকাউন্ট যাচাই সম্পন্ন করুন।",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Didn't receive the code? ", color = TextSecondary, fontSize = 12.sp)
                    if (viewModel.isResendEnabled) {
                        Text(
                            text = "Resend Code",
                            color = PurplePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.clickable {
                                viewModel.resendOtp(activity)
                            }
                        )
                    } else {
                        Text(
                            text = "Resend in 00:${viewModel.otpResendCountdown.toString().padStart(2, '0')}",
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }

                if (viewModel.authErrorMessage != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = viewModel.authErrorMessage ?: "",
                        color = Color(0xFFFF5252),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.verifyOtpAndContinue()
                    },
                    enabled = !viewModel.isAuthLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("verify_otp_btn")
                ) {
                    if (viewModel.isAuthLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = if (isLoginMode) "Verify & Login" else "Verify & Continue",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Change Phone Number",
                    color = PurplePrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable {
                        when (viewModel.verificationSource) {
                            "LOGIN_WITH_OTP" -> viewModel.navigateTo("LOGIN_WITH_OTP")
                            "REGISTER_MODEL" -> viewModel.navigateTo("REGISTER_MODEL")
                            else -> viewModel.navigateTo("REGISTER_USER")
                        }
                    }
                )
            }
        }
    }
}

// ---------------- 6. EMAIL VERIFICATION SCREEN ----------------
@Composable
fun EmailVerificationScreen(viewModel: AppViewModel) {
    val targetEmail = if (viewModel.verificationTarget.contains("@")) viewModel.verificationTarget else viewModel.registerEmail.ifEmpty { "user@example.com" }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = { viewModel.navigateTo("REGISTER_USER") },
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFFF85A6), CircleShape)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Artwork illustration (Envelope with Badge)
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(Color(0xFFFFF0F5), shape = CircleShape)
                        .border(1.dp, Color(0xFFFFD1DF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(Color.White, shape = RoundedCornerShape(16.dp))
                            .border(1.dp, PurplePrimary, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(36.dp))
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (-8).dp, y = (-8).dp)
                            .background(Color(0xFF4CAF50), shape = CircleShape)
                            .padding(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Email Verification",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Enter the 6-digit code sent to\n$targetEmail",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Sender Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFFF85A6))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Sender: support@modolconncet.fun",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Security Notice Card: Why OTP should never be shared!
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "নিরাপত্তা সতর্কতা: ওটিপি কোড কারো সাথে শেয়ার করবেন না!",
                                color = Color(0xFF92400E),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Modol Connect বা সাপোর্ট টিম কখনো আপনার ওটিপি চাইবে না।",
                                color = Color(0xFFB45309),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 6 Box OTP Input
                OtpSixBoxInput(
                    code = viewModel.otpCode,
                    onCodeChange = {
                        if (it.length <= 6) {
                            viewModel.otpCode = it
                            if (it.length == 6) viewModel.authErrorMessage = null
                        }
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Email Verification Status Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFFF85A6)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "আপনার ইমেইলে ওটিপি পাঠানো হয়েছে। কোডটি দেখে ইনপুট করুন।",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Didn't receive the code? ", color = TextSecondary, fontSize = 12.sp)
                    if (viewModel.isResendEnabled) {
                        Text(
                            text = "Resend Email OTP",
                            color = PurplePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.clickable {
                                viewModel.resendEmailOtp()
                            }
                        )
                    } else {
                        Text(
                            text = "Resend in 00:${viewModel.otpResendCountdown.toString().padStart(2, '0')}",
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }

                if (viewModel.authErrorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = viewModel.authErrorMessage ?: "",
                        color = Color(0xFFFF5252),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.verifyEmailOtpAndContinue()
                    },
                    enabled = !viewModel.isAuthLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("verify_email_otp_button")
                ) {
                    if (viewModel.isAuthLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Verify Email & Continue", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Change Email Address",
                    color = PurplePrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { viewModel.navigateTo("REGISTER_USER") }
                )
            }
        }
    }
}

// ---------------- 7. LOGIN WITH OTP SCREEN ----------------
@Composable
fun LoginWithOtpScreen(viewModel: AppViewModel) {
    val activity = LocalContext.current as? android.app.Activity

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = { viewModel.navigateTo("LOGIN") },
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFFF85A6), CircleShape)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                ModolConnectLogoHeader()
                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Login with OTP",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Enter your phone number to\nreceive a login code",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, textAlign = TextAlign.Center)
                )

                Spacer(modifier = Modifier.height(28.dp))

                CountryCodePhoneInputField(
                    value = viewModel.loginPhone,
                    onValueChange = { viewModel.loginPhone = it },
                    label = "Phone Number",
                    placeholder = "1XX-XXXXXXX",
                    viewModel = viewModel,
                    testTag = "login_otp_phone_input"
                )

                if (viewModel.authErrorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = viewModel.authErrorMessage ?: "",
                        color = Color(0xFFFF5252),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val fullTarget = viewModel.getFormattedPhoneNumber(viewModel.loginPhone)
                        viewModel.sendLoginOtp(fullTarget, activity)
                    },
                    enabled = !viewModel.isAuthLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("send_otp_btn")
                ) {
                    if (viewModel.isAuthLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Send OTP Code 🌍", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFFF85A6))
                    Text("  or continue with  ", color = TextSecondary, fontSize = 12.sp)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFFF85A6))
                }

                Spacer(modifier = Modifier.height(18.dp))

                SocialLoginRow()
            }

            Text(
                text = "Back to Login",
                color = PurplePrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .clickable { viewModel.navigateTo("LOGIN") }
            )
        }
    }
}

// ---------------- 8. FORGOT PASSWORD SCREEN ----------------
@Composable
fun ForgotPasswordScreen(viewModel: AppViewModel) {
    val activity = LocalContext.current as? android.app.Activity
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = { viewModel.navigateTo("LOGIN") },
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFFF85A6), CircleShape)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                ModolConnectLogoHeader()
                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Forgot Password?",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Enter your email or phone to reset your password",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, textAlign = TextAlign.Center)
                )

                // Error Message Banner
                if (viewModel.authErrorMessage != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F5)),
                        border = BorderStroke(1.dp, Color(0xFFFFB3BA)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = viewModel.authErrorMessage ?: "",
                                color = Color(0xFFD32F2F),
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Success Message Banner
                if (viewModel.authSuccessMessage != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = viewModel.authSuccessMessage ?: "",
                                color = Color(0xFF2E7D32),
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Method Switcher: Phone vs Email
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFFF85A6), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val isPhone = viewModel.forgotResetMethod == "PHONE"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (isPhone) PurplePrimary else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.forgotResetMethod = "PHONE" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Smartphone,
                                contentDescription = null,
                                tint = if (isPhone) Color.White else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Phone OTP",
                                color = if (isPhone) Color.White else TextPrimary,
                                fontWeight = if (isPhone) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (!isPhone) PurplePrimary else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.forgotResetMethod = "EMAIL" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = if (!isPhone) Color.White else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Email OTP",
                                color = if (!isPhone) Color.White else TextPrimary,
                                fontWeight = if (!isPhone) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (viewModel.forgotResetMethod == "PHONE") {
                    // Phone Number Input
                    CountryCodePhoneInputField(
                        value = viewModel.forgotPhone,
                        onValueChange = {
                            viewModel.forgotPhone = it
                            if (viewModel.authErrorMessage != null) viewModel.authErrorMessage = null
                        },
                        label = "Registered Phone Number",
                        placeholder = "1XX-XXXXXXX",
                        viewModel = viewModel,
                        testTag = "forgot_phone_input"
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Primary Action: Send Live Phone OTP via Firebase
                    Button(
                        onClick = {
                            val formattedPhone = viewModel.getFormattedPhoneNumber(viewModel.forgotPhone)
                            viewModel.verificationTarget = formattedPhone
                            viewModel.verificationSource = "FORGOT_PASSWORD"
                            viewModel.verificationNextScreen = "CREATE_NEW_PASSWORD"
                            viewModel.sendLoginOtp(formattedPhone, activity = activity)
                            viewModel.navigateTo("FORGOT_OTP")
                        },
                        enabled = !viewModel.isAuthLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("send_phone_otp_button")
                    ) {
                        if (viewModel.isAuthLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                        } else {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Send Live OTP to Phone", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                } else {
                    // Direct Email Reset Section
                    PremiumTextField(
                        value = viewModel.forgotEmail,
                        onValueChange = {
                            viewModel.forgotEmail = it
                            if (viewModel.authErrorMessage != null) viewModel.authErrorMessage = null
                        },
                        label = "Registered Email Address",
                        placeholder = "Enter your registered email",
                        leadingIcon = Icons.Default.Email,
                        testTag = "forgot_email_input"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Primary Action: Send Email Reset OTP
                    Button(
                        onClick = { viewModel.sendEmailForgotPasswordOtp(viewModel.forgotEmail) },
                        enabled = !viewModel.isAuthLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("send_email_otp_button")
                    ) {
                        if (viewModel.isAuthLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Send Reset OTP to Email", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Alternative: Send Official Password Reset Link
                    OutlinedButton(
                        onClick = { viewModel.sendPasswordReset(viewModel.forgotEmail) },
                        enabled = !viewModel.isAuthLoading,
                        border = BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Text("Send Password Reset Link via Email", color = PurplePrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            Text(
                text = "Back to Login",
                color = PurplePrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .clickable { viewModel.navigateTo("LOGIN") }
            )
        }
    }
}

// ---------------- 9. FORGOT OTP (ENTER VERIFICATION CODE) SCREEN ----------------
@Composable
fun ForgotOtpScreen(viewModel: AppViewModel) {
    val activity = LocalContext.current as? android.app.Activity
    val isEmailMethod = viewModel.forgotResetMethod == "EMAIL"
    val targetStr = if (isEmailMethod) viewModel.forgotEmail else viewModel.forgotPhone

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = { viewModel.navigateTo("FORGOT_PASSWORD") },
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFFF85A6), CircleShape)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Artwork Illustration
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(Color(0xFFFFF0F5), shape = CircleShape)
                        .border(1.dp, Color(0xFFFFD1DF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(Color.White, shape = RoundedCornerShape(16.dp))
                            .border(1.dp, PurplePrimary, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isEmailMethod) Icons.Default.Email else Icons.Default.Smartphone,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (-8).dp, y = (-8).dp)
                            .background(Color(0xFF4CAF50), shape = CircleShape)
                            .padding(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = if (isEmailMethod) "Email Password Reset" else "Phone Password Reset",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Enter the 6-digit code sent to\n$targetStr",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (isEmailMethod) {
                    // Sender Badge (Email)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFFF85A6))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sender: support@modolconncet.fun",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                } else {
                    // Gateway Badge (Phone SMS)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFFF85A6))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PhoneIphone, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Gateway: Official SMS Gateway",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Security Notice Card: Why OTP should never be shared!
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "নিরাপত্তা সতর্কতা: ওটিপি কোড কখনো কারো সাথে শেয়ার করবেন না!",
                                color = Color(0xFF92400E),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Modol Connect বা সাপোর্ট টিম কখনো আপনার ওটিপি চাইবে না।",
                                color = Color(0xFFB45309),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                OtpSixBoxInput(
                    code = viewModel.otpCode,
                    onCodeChange = {
                        if (it.length <= 6) {
                            viewModel.otpCode = it
                            if (it.length == 6) viewModel.authErrorMessage = null
                        }
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFFF85A6)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isEmailMethod) Icons.Default.Email else Icons.Default.LockReset,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEmailMethod) 
                                "আপনার ইমেইলে ওটিপি কোড পাঠানো হয়েছে। কোডটি দেখে ইনপুট করুন।"
                            else 
                                "আপনার ফোনে সুরক্ষিত ওটিপি কোড পাঠানো হয়েছে। ওটিপি যাচাই করে পাসওয়ার্ড পরিবর্তন করুন।",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Didn't receive the code? ", color = TextSecondary, fontSize = 12.sp)
                    if (viewModel.isResendEnabled) {
                        Text(
                            text = "Resend Code",
                            color = PurplePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.clickable {
                                if (isEmailMethod) {
                                    viewModel.resendEmailOtp()
                                } else {
                                    viewModel.resendOtp(activity)
                                }
                            }
                        )
                    } else {
                        Text(
                            text = "Resend in 00:${viewModel.otpResendCountdown.toString().padStart(2, '0')}",
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }

                if (viewModel.authErrorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = viewModel.authErrorMessage ?: "",
                        color = Color(0xFFFF5252),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (isEmailMethod) {
                            viewModel.verifyEmailOtpAndContinue()
                        } else {
                            viewModel.verifyOtpAndContinue()
                        }
                    },
                    enabled = !viewModel.isAuthLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("verify_forgot_otp_btn")
                ) {
                    if (viewModel.isAuthLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Verify Code & Proceed", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Text(
                text = "Back to Login",
                color = PurplePrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .clickable { viewModel.navigateTo("LOGIN") }
            )
        }
    }
}

// ---------------- 10. CREATE NEW PASSWORD SCREEN ----------------
@Composable
fun CreateNewPasswordScreen(viewModel: AppViewModel) {
    var pass1Visible by remember { mutableStateOf(false) }
    var pass2Visible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = { viewModel.navigateTo("FORGOT_OTP") },
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFFF85A6), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Lock Badge Header
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(PrimaryPink, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Create New Password",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Your new password must be different\nfrom previous used passwords.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, textAlign = TextAlign.Center)
                )

                if (viewModel.authErrorMessage != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        border = BorderStroke(1.dp, Color(0xFFFF5252)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = viewModel.authErrorMessage ?: "",
                                color = Color(0xFFC62828),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // New Password Input
                OutlinedTextField(
                    value = viewModel.newPasswordVal,
                    onValueChange = { 
                        viewModel.newPasswordVal = it 
                        if (viewModel.authErrorMessage != null) viewModel.authErrorMessage = null
                    },
                    label = { Text("New Password") },
                    placeholder = { Text("••••••••") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = PrimaryPink) },
                    trailingIcon = {
                        IconButton(onClick = { pass1Visible = !pass1Visible }) {
                            Icon(imageVector = if (pass1Visible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = TextSecondary)
                        }
                    },
                    visualTransformation = if (pass1Visible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryPink,
                        unfocusedBorderColor = Color(0xFFFF85A6),
                        focusedLabelColor = PrimaryPink,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Confirm New Password Input
                OutlinedTextField(
                    value = viewModel.confirmNewPasswordVal,
                    onValueChange = { 
                        viewModel.confirmNewPasswordVal = it 
                        if (viewModel.authErrorMessage != null) viewModel.authErrorMessage = null
                    },
                    label = { Text("Confirm New Password") },
                    placeholder = { Text("••••••••") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = PrimaryPink) },
                    trailingIcon = {
                        IconButton(onClick = { pass2Visible = !pass2Visible }) {
                            Icon(imageVector = if (pass2Visible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = TextSecondary)
                        }
                    },
                    visualTransformation = if (pass2Visible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryPink,
                        unfocusedBorderColor = Color(0xFFFF85A6),
                        focusedLabelColor = PrimaryPink,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Validation checklist
                Column(modifier = Modifier.fillMaxWidth()) {
                    val criteria = listOf(
                        "At least 6 characters",
                        "Contains letters and numbers",
                        "Secure and confidential"
                    )
                    criteria.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(Color(0xFFE8F5E9), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(12.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(item, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                Button(
                    onClick = {
                        viewModel.completePasswordReset()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Reset Password", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Text(
                text = "Back to Login",
                color = PrimaryPink,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .clickable { viewModel.navigateTo("LOGIN") }
            )
        }
    }
}

// ============================================================================
// 4. MAIN CONTAINER WITH NAVIGATION
// ============================================================================

@Composable
fun DashboardContainer(viewModel: AppViewModel, content: @Composable (PaddingValues) -> Unit) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val unreadMessagesCount = viewModel.allMessages.collectAsStateWithLifecycle().value.count { !it.isRead }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(DarkBg)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .background(DarkBg)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header Title
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFFFF2A6D), Color(0xFFFF5E8A), Color(0xFFC2185B))
                                    ),
                                    shape = RoundedCornerShape(11.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Logo",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MODOL ",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "CONNECT",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = PinkHighlight,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    }

                    // Top Bar Actions
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quick Wallet Balance Pill Button
                        val userBal = currentUser?.balance ?: 1500.0
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            border = BorderStroke(1.2.dp, Color(0xFFFFD6E2)),
                            modifier = Modifier.clickable { viewModel.navigateTo("WALLET") }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = "Wallet", tint = PinkHighlight, modifier = Modifier.size(14.dp))
                                Text("৳ %,.0f".format(userBal), color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                            }
                        }

                        // Quick B2B P2P Pill Button
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFFFEFF3),
                            border = BorderStroke(1.dp, Color(0xFFFFD6E2)),
                            modifier = Modifier.clickable { viewModel.navigateTo("WALLET") }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(14.dp))
                                Text("B2B", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }

                        // Notification bell with unread indicator
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFFFDDE6)),
                            modifier = Modifier
                                .size(38.dp)
                                .clickable { viewModel.navigateTo("NOTIFICATIONS") }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.NotificationsNone, contentDescription = "Notifications", tint = TextPrimary, modifier = Modifier.size(20.dp))
                                if (viewModel.notifications.collectAsStateWithLifecycle().value.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(PinkHighlight, shape = CircleShape)
                                            .align(Alignment.TopEnd)
                                            .padding(top = 4.dp, end = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Dedicated Admin Backend Horizontal Sub-Tab Strip
                if (currentUser?.role == "ADMIN") {
                    val adminTabs = listOf(
                        Triple(0, "Overview", Icons.Default.Dashboard),
                        Triple(1, "Live GPS", Icons.Default.LocationOn),
                        Triple(2, "Messenger", Icons.Default.Chat),
                        Triple(3, "Users & Models", Icons.Default.People),
                        Triple(4, "Escrow", Icons.Default.VerifiedUser),
                        Triple(5, "Collections", Icons.Default.Payments),
                        Triple(6, "Wallets", Icons.Default.AccountBalanceWallet),
                        Triple(7, "Settings", Icons.Default.Settings)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        adminTabs.forEach { (idx, title, icon) ->
                            val isSel = viewModel.selectedTab == idx
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = if (isSel) PinkHighlight else DarkSurface,
                                border = BorderStroke(1.dp, if (isSel) PinkHighlight else PinkBorderSoft),
                                modifier = Modifier.clickable {
                                    viewModel.selectedTab = idx
                                    viewModel.navigateTo("DASHBOARD")
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = title,
                                        tint = if (isSel) Color.White else if (idx == 1) Color(0xFF00E676) else TextSecondary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = title,
                                        color = if (isSel) Color.White else TextPrimary,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 11.sp
                                    )
                                    if (idx == 1) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(if (isSel) Color.White else Color(0xFF00E676), CircleShape)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = PinkBorderSoft, thickness = 0.5.dp)
                }
            }
        },
        bottomBar = {
            val isModel = currentUser?.role == "MODEL"
            val isAdmin = currentUser?.role == "ADMIN"
            val isAgent = currentUser?.role == "CASH_AGENT"
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                if (isModel) {
                    NavigationBarItem(
                        selected = viewModel.selectedTab == 0,
                        onClick = { viewModel.selectedTab = 0; viewModel.navigateTo("DASHBOARD") },
                        icon = { Icon(imageVector = Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PinkHighlight,
                            selectedTextColor = PinkHighlight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PinkHighlight.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = viewModel.selectedTab == 1,
                        onClick = { viewModel.selectedTab = 1; viewModel.navigateTo("DASHBOARD") },
                        icon = { Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Bookings") },
                        label = { Text("Bookings", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PinkHighlight,
                            selectedTextColor = PinkHighlight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PinkHighlight.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = viewModel.selectedTab == 2,
                        onClick = { viewModel.selectedTab = 2; viewModel.navigateTo("DASHBOARD") },
                        icon = {
                            Box {
                                Icon(imageVector = Icons.Default.Chat, contentDescription = "Chats")
                                if (unreadMessagesCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .background(PinkHighlight, shape = CircleShape)
                                            .align(Alignment.TopEnd),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = unreadMessagesCount.toString(),
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                             }
                        },
                        label = { Text("Chats", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PinkHighlight,
                            selectedTextColor = PinkHighlight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PinkHighlight.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = viewModel.selectedTab == 3,
                        onClick = { viewModel.selectedTab = 3; viewModel.navigateTo("DASHBOARD") },
                        icon = { Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
                        label = { Text("Wallet", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PinkHighlight,
                            selectedTextColor = PinkHighlight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PinkHighlight.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = viewModel.selectedTab == 4,
                        onClick = { viewModel.selectedTab = 4; viewModel.navigateTo("DASHBOARD") },
                        icon = { Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PinkHighlight,
                            selectedTextColor = PinkHighlight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PinkHighlight.copy(alpha = 0.15f)
                        )
                    )
                } else if (isAdmin) {
                    val adminBottomTabs = listOf(
                        Triple(0, "Overview", Icons.Default.Dashboard),
                        Triple(1, "Live GPS", Icons.Default.LocationOn),
                        Triple(2, "Messenger", Icons.Default.Chat),
                        Triple(3, "Users", Icons.Default.People),
                        Triple(4, "Escrow", Icons.Default.VerifiedUser),
                        Triple(5, "Cash", Icons.Default.Payments),
                        Triple(6, "Wallets", Icons.Default.AccountBalanceWallet),
                        Triple(7, "Settings", Icons.Default.Settings)
                    )
                    adminBottomTabs.forEach { (idx, label, icon) ->
                        NavigationBarItem(
                            selected = viewModel.selectedTab == idx,
                            onClick = { viewModel.selectedTab = idx; viewModel.navigateTo("DASHBOARD") },
                            icon = {
                                if (idx == 1) {
                                    Box {
                                        Icon(imageVector = icon, contentDescription = label)
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .background(Color(0xFF00E676), CircleShape)
                                                .align(Alignment.TopEnd)
                                        )
                                    }
                                } else if (idx == 2) {
                                    Box {
                                        Icon(imageVector = icon, contentDescription = label)
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .background(Color(0xFF0084FF), CircleShape)
                                                .align(Alignment.TopEnd)
                                        )
                                    }
                                } else {
                                    Icon(imageVector = icon, contentDescription = label)
                                }
                            },
                            label = { Text(label, fontSize = 7.5.sp, maxLines = 1) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = if (idx == 1) Color(0xFF00E676) else PinkHighlight,
                                selectedTextColor = if (idx == 1) Color(0xFF00E676) else PinkHighlight,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = if (idx == 1) Color(0xFF00E676).copy(alpha = 0.15f) else PinkHighlight.copy(alpha = 0.15f)
                            )
                        )
                    }
                } else if (isAgent) {
                    NavigationBarItem(
                        selected = viewModel.selectedTab == 0,
                        onClick = { viewModel.selectedTab = 0; viewModel.navigateTo("DASHBOARD") },
                        icon = { Icon(imageVector = Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PinkHighlight,
                            selectedTextColor = PinkHighlight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PinkHighlight.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = viewModel.selectedTab == 1,
                        onClick = { viewModel.selectedTab = 1; viewModel.navigateTo("DASHBOARD") },
                        icon = { Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = "Collections") },
                        label = { Text("Collections", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PinkHighlight,
                            selectedTextColor = PinkHighlight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PinkHighlight.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = viewModel.selectedTab == 2,
                        onClick = { viewModel.selectedTab = 2; viewModel.navigateTo("DASHBOARD") },
                        icon = { Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
                        label = { Text("Wallet", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PinkHighlight,
                            selectedTextColor = PinkHighlight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PinkHighlight.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = viewModel.selectedTab == 3,
                        onClick = { viewModel.selectedTab = 3; viewModel.navigateTo("DASHBOARD") },
                        icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PinkHighlight,
                            selectedTextColor = PinkHighlight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PinkHighlight.copy(alpha = 0.15f)
                        )
                    )
                } else {
                    NavigationBarItem(
                        selected = viewModel.selectedTab == 0,
                        onClick = { viewModel.selectedTab = 0; viewModel.navigateTo("DASHBOARD") },
                        icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PinkHighlight,
                            selectedTextColor = PinkHighlight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PinkHighlight.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = viewModel.selectedTab == 1,
                        onClick = { viewModel.selectedTab = 1; viewModel.navigateTo("DASHBOARD") },
                        icon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                        label = { Text("Search", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PinkHighlight,
                            selectedTextColor = PinkHighlight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PinkHighlight.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = viewModel.selectedTab == 2,
                        onClick = { viewModel.selectedTab = 2; viewModel.navigateTo("DASHBOARD") },
                        icon = {
                            Box {
                                Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Bookings")
                                val bookingCount = viewModel.userBookings.collectAsStateWithLifecycle().value.size
                                if (bookingCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .background(PinkHighlight, shape = CircleShape)
                                            .align(Alignment.TopEnd),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = bookingCount.toString(),
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        },
                        label = { Text("Bookings", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PinkHighlight,
                            selectedTextColor = PinkHighlight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PinkHighlight.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = viewModel.selectedTab == 3,
                        onClick = { viewModel.selectedTab = 3; viewModel.navigateTo("DASHBOARD") },
                        icon = {
                            Box {
                                Icon(imageVector = Icons.Default.Chat, contentDescription = "Chats")
                                if (unreadMessagesCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .background(PinkHighlight, shape = CircleShape)
                                            .align(Alignment.TopEnd),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = unreadMessagesCount.toString(),
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        },
                        label = { Text("Chats", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PinkHighlight,
                            selectedTextColor = PinkHighlight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PinkHighlight.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = viewModel.selectedTab == 4,
                        onClick = { viewModel.selectedTab = 4; viewModel.navigateTo("DASHBOARD") },
                        icon = { Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PinkHighlight,
                            selectedTextColor = PinkHighlight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PinkHighlight.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        },
        containerColor = DarkBg
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            content(innerPadding)
        }

        if (viewModel.showPhpBackendModal) {
            PhpBackendCodeViewerModal(
                onDismiss = { viewModel.showPhpBackendModal = false },
                viewModel = viewModel
            )
        }

        if (viewModel.showPhotoUploadDialog) {
            PhotoUploadChooserModal(
                onDismiss = { viewModel.showPhotoUploadDialog = false },
                viewModel = viewModel
            )
        }

        if (viewModel.showPaymentManagementModal) {
            PaymentManagementModal(
                onDismiss = { viewModel.showPaymentManagementModal = false },
                viewModel = viewModel
            )
        }

        if (viewModel.showLiveSupportModal) {
            LiveSupportChatModal(
                onDismiss = { viewModel.showLiveSupportModal = false },
                viewModel = viewModel
            )
        }

        if (viewModel.showAddPaymentMethodModal) {
            AddPaymentMethodModal(
                onDismiss = { viewModel.showAddPaymentMethodModal = false },
                viewModel = viewModel
            )
        }
    }
}
