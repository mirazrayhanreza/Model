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
import androidx.compose.foundation.text.KeyboardOptions
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
            unfocusedBorderColor = Color(0xFF2E2E3E),
            focusedLabelColor = PinkHighlight,
            unfocusedLabelColor = TextSecondary,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = DarkSurface,
            unfocusedContainerColor = DarkSurface
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
                            color = Color.White
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
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = Color(0xFF2E2E3E),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface
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
                                containerColor = Color(0xFF2E2E3E),
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
                                if (isSelected) PurplePrimary else Color(0xFF2E2E3E)
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
                                            color = Color.White,
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
                                        color = PurplePrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = PurplePrimary,
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
        containerColor = Color(0xFF1E103C)
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
                color = DarkSurface,
                border = BorderStroke(1.dp, Color(0xFF2E2E3E)),
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
                        color = Color.White,
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
                    unfocusedBorderColor = Color(0xFF2E2E3E),
                    focusedLabelColor = PinkHighlight,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
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
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 18.sp
            )
        )
        if (onViewAllClick != null) {
            Text(
                text = "View All",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
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
    LaunchedEffect(Unit) {
        delay(2500)
        viewModel.splashFinished = true
        viewModel.navigateTo("ONBOARDING")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBg, Color(0xFF14020B))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo Icon
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PinkHighlight, Color(0xFFFF69B4))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ConnectWithoutContact,
                    contentDescription = "MODOL CONNECT Logo",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "MODOL",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 4.sp
                )
            )
            Text(
                text = "CONNECT",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PinkHighlight,
                    letterSpacing = 2.sp
                )
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Premium Model Marketplace",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            CircularProgressIndicator(
                color = PinkHighlight,
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
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
            bgColor = listOf(DarkBg, Color(0xFF0F0B1E))
        ),
        OnboardingData(
            title = "Book Safely & Instantly",
            desc = "Select your preferred date, hour, and service type. Our fully automated scheduler secures your sessions hassle-free.",
            icon = Icons.Default.CalendarMonth,
            bgColor = listOf(DarkBg, Color(0xFF1E0B14))
        ),
        OnboardingData(
            title = "Secure Local Payments",
            desc = "Pay safely using bKash, Nagad, Rocket, or international cards. Funds are protected and released only after session completion.",
            icon = Icons.Default.AccountBalanceWallet,
            bgColor = listOf(DarkBg, Color(0xFF0B1E14))
        )
    )

    val currentPage = onboardingPages[step]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(currentPage.bgColor)
            )
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
                    Text("Skip", color = TextSecondary, fontWeight = FontWeight.SemiBold)
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
                        .size(160.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(PinkHighlight.copy(alpha = 0.3f), Color.Transparent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .shadow(16.dp, RoundedCornerShape(32.dp), ambientColor = PinkHighlight, spotColor = PinkHighlight)
                            .background(DarkSurface, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = currentPage.icon,
                            contentDescription = null,
                            tint = PinkHighlight,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                Text(
                    text = currentPage.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = currentPage.desc,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    ),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            // Bottom Navigation and Page Indicators
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    onboardingPages.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (index == step) 24.dp else 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (index == step) PinkHighlight else Color(0xFF2E2E3E))
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
                
                Spacer(modifier = Modifier.height(24.dp))
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

val PurplePrimary = Color(0xFF5D3EBC)
val PurpleLightBg = Color(0xFFF7F5FC)
val PurpleCardBorder = Color(0xFFE2D8FA)
val PurpleSoftAccent = Color(0xFFECE6FA)

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
                        colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
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
        Text(
            text = "MODOL",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 3.sp,
                fontSize = 20.sp
            )
        )
        Text(
            text = "CONNECT",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PurplePrimary,
                letterSpacing = 2.sp,
                fontSize = 12.sp
            )
        )
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
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, Color(0xFF2E2E3E)),
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
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, Color(0xFF2E2E3E)),
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
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, Color(0xFF2E2E3E)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }
        }
    }
}

@Composable
fun OtpSixBoxInput(code: String, onCodeChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        for (i in 0 until 6) {
            val charStr = if (i < code.length) code[i].toString() else ""
            val isFocused = i == code.length || (i == 5 && code.length == 6)
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(DarkSurface, shape = RoundedCornerShape(12.dp))
                    .border(
                        width = if (isFocused) 2.dp else 1.dp,
                        color = if (isFocused) PurplePrimary else Color(0xFF2E2E3E),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = charStr,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                )
            }
        }
    }
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
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Login to continue to your account",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                    textAlign = TextAlign.Center
                )

                // Error Message Banner
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
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = Color(0xFF2E2E3E),
                        focusedLabelColor = PurplePrimary,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface
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
                            colors = CheckboxDefaults.colors(checkedColor = PurplePrimary)
                        )
                        Text("Remember Me", color = TextSecondary, fontSize = 13.sp)
                    }

                    Text(
                        text = "Forgot Password?",
                        color = PurplePrimary,
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
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
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
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF2E2E3E))
                    Text(
                        text = "  or continue with  ",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF2E2E3E))
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

                // Quick Demo Accounts Bar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, Color(0xFF2E2E3E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("⚡ Quick Demo Accounts:", color = PurplePrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // User Demo
                            OutlinedButton(
                                onClick = {
                                    viewModel.loginEmail = "user@example.com"
                                    viewModel.loginPassword = "password"
                                    viewModel.login()
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(4.dp)
                            ) {
                                Text("Client", fontSize = 11.sp, color = Color.White)
                            }
                            // Model Demo
                            OutlinedButton(
                                onClick = {
                                    viewModel.loginEmail = "model@example.com"
                                    viewModel.loginPassword = "password"
                                    viewModel.login()
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(4.dp)
                            ) {
                                Text("Model", fontSize = 11.sp, color = Color.White)
                            }
                            // Admin Demo
                            OutlinedButton(
                                onClick = {
                                    viewModel.loginEmail = "admin@example.com"
                                    viewModel.loginPassword = "password"
                                    viewModel.login()
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(4.dp)
                            ) {
                                Text("Admin", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }

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
                    IconButton(onClick = { viewModel.navigateTo("LOGIN") }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                ModolConnectLogoHeader()
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
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
                        color = Color.White,
                        fontSize = 14.sp
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
                    colors = CardDefaults.cardColors(containerColor = PurpleSoftAccent.copy(alpha = 0.12f)),
                    border = BorderStroke(1.5.dp, PurplePrimary),
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
                                .background(PurplePrimary, shape = CircleShape),
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
                                    color = Color.White,
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4081).copy(alpha = 0.12f)),
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
                                    color = Color.White,
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
                    color = PurplePrimary,
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
                    // Top Icon Badge
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(PurplePrimary, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
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
                        text = "Join as a User",
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
                            unfocusedBorderColor = Color(0xFF2E2E3E),
                            focusedLabelColor = PurplePrimary,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface
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
                            unfocusedBorderColor = Color(0xFF2E2E3E),
                            focusedLabelColor = PurplePrimary,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface
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
                            unfocusedBorderColor = Color(0xFF2E2E3E),
                            focusedLabelColor = PurplePrimary,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface
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
                            unfocusedBorderColor = Color(0xFF2E2E3E),
                            focusedLabelColor = PurplePrimary,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface
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
                                        if (isSelected) PurplePrimary else Color(0xFF2E2E3E)
                                    ),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSelected) PurplePrimary.copy(alpha = 0.15f) else DarkSurface,
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

                    // Secondary: Send OTP option
                    OutlinedButton(
                        onClick = {
                            viewModel.verificationTarget = viewModel.registerPhone.ifEmpty { "+880 1712-345678" }
                            viewModel.verificationType = "PHONE"
                            viewModel.verificationNextScreen = "DASHBOARD"
                            viewModel.navigateTo("PHONE_VERIFICATION")
                        },
                        border = BorderStroke(1.dp, Color(0xFF2E2E3E)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Smartphone, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Register with Phone OTP", color = Color.White, fontSize = 13.sp)
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
                            unfocusedBorderColor = Color(0xFF2E2E3E),
                            focusedLabelColor = Color(0xFFFF4081),
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface
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
                            unfocusedBorderColor = Color(0xFF2E2E3E),
                            focusedLabelColor = Color(0xFFFF4081),
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface
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
                            unfocusedBorderColor = Color(0xFF2E2E3E),
                            focusedLabelColor = Color(0xFFFF4081),
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface
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
                            unfocusedBorderColor = Color(0xFF2E2E3E),
                            focusedLabelColor = Color(0xFFFF4081),
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface
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
                                        if (isSelected) Color(0xFFFF4081) else Color(0xFF2E2E3E)
                                    ),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSelected) Color(0xFFFF4081).copy(alpha = 0.15f) else DarkSurface,
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

                    // Secondary: Send OTP option
                    OutlinedButton(
                        onClick = {
                            viewModel.verificationTarget = viewModel.registerPhone.ifEmpty { "+880 1712-345678" }
                            viewModel.verificationType = "PHONE"
                            viewModel.verificationNextScreen = "DASHBOARD"
                            viewModel.navigateTo("PHONE_VERIFICATION")
                        },
                        border = BorderStroke(1.dp, Color(0xFF2E2E3E)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Smartphone, contentDescription = null, tint = Color(0xFFFF4081), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Register with Phone OTP", color = Color.White, fontSize = 13.sp)
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
                    IconButton(onClick = { viewModel.navigateTo("REGISTER_USER") }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Artwork illustration (Phone with Badge)
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(PurpleSoftAccent.copy(alpha = 0.12f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(DarkSurface, shape = RoundedCornerShape(16.dp))
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
                    text = "Phone Verification",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
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

                Spacer(modifier = Modifier.height(32.dp))

                // 6 Box OTP Input
                OtpSixBoxInput(
                    code = viewModel.otpCode,
                    onCodeChange = { if (it.length <= 6) viewModel.otpCode = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Didn't receive the code? ", color = TextSecondary, fontSize = 12.sp)
                    Text(
                        text = "Resend in 00:25",
                        color = PurplePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable {
                            viewModel.addNotification("OTP Resent", "A new 6-digit verification code has been sent.", "System")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.register()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Verify & Continue", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Change Phone Number",
                    color = PurplePrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { viewModel.navigateTo("REGISTER_USER") }
                )
            }
        }
    }
}

// ---------------- 6. EMAIL VERIFICATION SCREEN ----------------
@Composable
fun EmailVerificationScreen(viewModel: AppViewModel) {
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
                    IconButton(onClick = { viewModel.navigateTo("REGISTER_USER") }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Artwork illustration (Envelope with Badge)
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(PurpleSoftAccent.copy(alpha = 0.12f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(DarkSurface, shape = RoundedCornerShape(16.dp))
                            .border(1.dp, PurplePrimary, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(36.dp))
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
                    text = "Email Verification",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Enter the 6-digit code sent to\n${viewModel.registerEmail.ifEmpty { "example@email.com" }}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 6 Box OTP Input
                OtpSixBoxInput(
                    code = viewModel.otpCode,
                    onCodeChange = { if (it.length <= 6) viewModel.otpCode = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Didn't receive the code? ", color = TextSecondary, fontSize = 12.sp)
                    Text(
                        text = "Resend in 00:25",
                        color = PurplePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable {
                            viewModel.addNotification("Email OTP Resent", "A new verification code has been emailed.", "System")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.register()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Verify & Continue", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                    IconButton(onClick = { viewModel.navigateTo("LOGIN") }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                ModolConnectLogoHeader()
                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Login with OTP",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
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

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val fullTarget = viewModel.getFormattedPhoneNumber(viewModel.loginPhone)
                        viewModel.verificationTarget = fullTarget
                        viewModel.verificationType = "PHONE"
                        viewModel.verificationNextScreen = "DASHBOARD"
                        viewModel.navigateTo("PHONE_VERIFICATION")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Send OTP Code 🌍", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF2E2E3E))
                    Text("  or continue with  ", color = TextSecondary, fontSize = 12.sp)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF2E2E3E))
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
                    IconButton(onClick = { viewModel.navigateTo("LOGIN") }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                ModolConnectLogoHeader()
                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Forgot Password?",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
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

                // Success Message Banner
                if (viewModel.authSuccessMessage != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B3820)),
                        border = BorderStroke(1.dp, Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = viewModel.authSuccessMessage ?: "",
                                color = Color(0xFFC8E6C9),
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Direct Email Reset Section
                PremiumTextField(
                    value = viewModel.forgotEmail,
                    onValueChange = { 
                        viewModel.forgotEmail = it 
                        if (viewModel.authErrorMessage != null) viewModel.authErrorMessage = null
                    },
                    label = "Registered Email Address",
                    placeholder = "example@email.com",
                    leadingIcon = Icons.Default.Email,
                    testTag = "forgot_email_input"
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { viewModel.sendPasswordReset(viewModel.forgotEmail) },
                    enabled = !viewModel.isAuthLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("send_reset_email_button")
                ) {
                    if (viewModel.isAuthLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text("Send Password Reset Link", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF2E2E3E))
                    Text("  or other methods  ", color = TextSecondary, fontSize = 12.sp)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF2E2E3E))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Option 1: Reset via Phone
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.forgotResetMethod = "PHONE"
                            viewModel.navigateTo("FORGOT_OTP")
                        },
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.5.dp, PurplePrimary.copy(alpha = 0.5f)),
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
                                .size(48.dp)
                                .background(PurpleSoftAccent.copy(alpha = 0.2f), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(24.dp))
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Reset via Phone OTP",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "We will send OTP to your registered phone number",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Option 2: Reset via Email
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.forgotResetMethod = "EMAIL"
                            viewModel.navigateTo("FORGOT_OTP")
                        },
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.5.dp, PurplePrimary.copy(alpha = 0.5f)),
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
                                .size(48.dp)
                                .background(PurpleSoftAccent.copy(alpha = 0.2f), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(24.dp))
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Reset via Email",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "We will send OTP to your registered email address",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                            )
                        }
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

// ---------------- 9. FORGOT OTP (ENTER VERIFICATION CODE) SCREEN ----------------
@Composable
fun ForgotOtpScreen(viewModel: AppViewModel) {
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
                    IconButton(onClick = { viewModel.navigateTo("FORGOT_PASSWORD") }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Artwork Illustration
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(PurpleSoftAccent.copy(alpha = 0.12f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(DarkSurface, shape = RoundedCornerShape(16.dp))
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
                    text = "Enter Verification Code",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                val targetStr = if (viewModel.forgotResetMethod == "PHONE") viewModel.forgotPhone else viewModel.forgotEmail
                Text(
                    text = "Enter the 6-digit code sent to\n$targetStr",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                OtpSixBoxInput(
                    code = viewModel.otpCode,
                    onCodeChange = { if (it.length <= 6) viewModel.otpCode = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Didn't receive the code? ", color = TextSecondary, fontSize = 12.sp)
                    Text(
                        text = "Resend in 00:25",
                        color = PurplePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable {
                            viewModel.addNotification("Verification Code Resent", "A new password reset code has been sent.", "System")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.navigateTo("CREATE_NEW_PASSWORD")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Verify Code", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                    IconButton(onClick = { viewModel.navigateTo("FORGOT_OTP") }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Lock Badge Header
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(PurplePrimary, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Create New Password",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Your new password must be different\nfrom previous used passwords.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, textAlign = TextAlign.Center)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // New Password Input
                OutlinedTextField(
                    value = viewModel.newPasswordVal,
                    onValueChange = { viewModel.newPasswordVal = it },
                    label = { Text("New Password") },
                    placeholder = { Text("••••••••") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TextSecondary) },
                    trailingIcon = {
                        IconButton(onClick = { pass1Visible = !pass1Visible }) {
                            Icon(imageVector = if (pass1Visible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = TextSecondary)
                        }
                    },
                    visualTransformation = if (pass1Visible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = Color(0xFF2E2E3E),
                        focusedLabelColor = PurplePrimary,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Confirm New Password Input
                OutlinedTextField(
                    value = viewModel.confirmNewPasswordVal,
                    onValueChange = { viewModel.confirmNewPasswordVal = it },
                    label = { Text("Confirm New Password") },
                    placeholder = { Text("••••••••") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TextSecondary) },
                    trailingIcon = {
                        IconButton(onClick = { pass2Visible = !pass2Visible }) {
                            Icon(imageVector = if (pass2Visible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = TextSecondary)
                        }
                    },
                    visualTransformation = if (pass2Visible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = Color(0xFF2E2E3E),
                        focusedLabelColor = PurplePrimary,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Validation checklist
                Column(modifier = Modifier.fillMaxWidth()) {
                    val criteria = listOf(
                        "At least 8 characters",
                        "One uppercase letter",
                        "One number",
                        "One special character"
                    )
                    criteria.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(Color(0xFF4CAF50).copy(alpha = 0.2f), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(12.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(item, color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        viewModel.addNotification("Password Updated", "Your password has been reset successfully. Please log in.", "System")
                        viewModel.navigateTo("LOGIN")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Reset Password", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                            .size(36.dp)
                            .background(PinkHighlight, shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ConnectWithoutContact,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "MODOL CONNECT",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    )
                }

                // Top Bar Actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Notification bell with unread indicator
                    IconButton(onClick = { viewModel.navigateTo("NOTIFICATIONS") }) {
                        Box {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                            if (viewModel.notifications.collectAsStateWithLifecycle().value.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(PinkHighlight, shape = CircleShape)
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            val isModel = currentUser?.role == "MODEL"
            val isAdmin = currentUser?.role == "ADMIN"
            val isAgent = currentUser?.role == "CASH_AGENT"
            NavigationBar(
                containerColor = DarkSurface,
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
                    NavigationBarItem(
                        selected = viewModel.selectedTab == 0,
                        onClick = { viewModel.selectedTab = 0; viewModel.navigateTo("DASHBOARD") },
                        icon = { Icon(imageVector = Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Overview", fontSize = 8.sp, maxLines = 1) },
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
                        icon = {
                            Box {
                                Icon(imageVector = Icons.Default.Chat, contentDescription = "Messenger Support")
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(Color(0xFF00E676), CircleShape)
                                        .align(Alignment.TopEnd)
                                )
                            }
                        },
                        label = { Text("Messenger", fontSize = 8.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF0084FF),
                            selectedTextColor = Color(0xFF0084FF),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = Color(0xFF0084FF).copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = viewModel.selectedTab == 2,
                        onClick = { viewModel.selectedTab = 2; viewModel.navigateTo("DASHBOARD") },
                        icon = { Icon(imageVector = Icons.Default.People, contentDescription = "Users") },
                        label = { Text("Users", fontSize = 8.sp, maxLines = 1) },
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
                        icon = { Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = "Escrow & Proofs") },
                        label = { Text("Escrow", fontSize = 8.sp, maxLines = 1) },
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
                        icon = { Icon(imageVector = Icons.Default.Payments, contentDescription = "Collections") },
                        label = { Text("Cash", fontSize = 8.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PinkHighlight,
                            selectedTextColor = PinkHighlight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PinkHighlight.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = viewModel.selectedTab == 5,
                        onClick = { viewModel.selectedTab = 5; viewModel.navigateTo("DASHBOARD") },
                        icon = { Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = "Wallets") },
                        label = { Text("Wallets", fontSize = 8.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PinkHighlight,
                            selectedTextColor = PinkHighlight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PinkHighlight.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = viewModel.selectedTab == 6,
                        onClick = { viewModel.selectedTab = 6; viewModel.navigateTo("DASHBOARD") },
                        icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings", fontSize = 8.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PinkHighlight,
                            selectedTextColor = PinkHighlight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PinkHighlight.copy(alpha = 0.15f)
                        )
                    )
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
