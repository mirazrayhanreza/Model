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
// 3. AUTHENTICATION SCREENS (LOGIN, REGISTER, OTP, FORGOT, RESET)
// ============================================================================

@Composable
fun LoginScreen(viewModel: AppViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header back button & spacing
            Row(modifier = Modifier.fillMaxWidth()) {
                // Spacer
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // Top Branding
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(PinkHighlight.copy(alpha = 0.15f), shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = PinkHighlight,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Sign in to connect with top verified models",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
                
                Spacer(modifier = Modifier.height(36.dp))

                // Inputs
                PremiumTextField(
                    value = viewModel.loginEmail,
                    onValueChange = { viewModel.loginEmail = it },
                    label = "Email Address",
                    placeholder = "Enter email or use: admin / model",
                    leadingIcon = Icons.Default.Email,
                    testTag = "login_email_input"
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                PremiumTextField(
                    value = viewModel.loginPassword,
                    onValueChange = { viewModel.loginPassword = it },
                    label = "Password",
                    placeholder = "••••••••",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    testTag = "login_password_input"
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Forgot Password?",
                        color = PinkHighlight,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { viewModel.navigateTo("FORGOT_PASSWORD") }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Demo Logins
                Text(
                    text = "Quick Demo Accounts (ডেমো লগইন করুন):",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PinkHighlight,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Client Account
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.loginEmail = "user@example.com"
                                viewModel.loginPassword = "password"
                                viewModel.login()
                            },
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, Color(0xFF2E2E3E)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User",
                                tint = PinkHighlight,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Client (User)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 11.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Model Account
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.loginEmail = "model@example.com"
                                viewModel.loginPassword = "password"
                                viewModel.login()
                            },
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, Color(0xFF2E2E3E)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Model",
                                tint = Color(0xFFFFD700), // Gold
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Model (Jessica)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 11.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Admin Account
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.loginEmail = "admin@example.com"
                                viewModel.loginPassword = "password"
                                viewModel.login()
                            },
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, Color(0xFF2E2E3E)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Admin",
                                tint = Color(0xFF4CAF50), // Green
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Admin Panel",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 11.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sign In Button
                PremiumButton(
                    text = "Log In",
                    onClick = { viewModel.login() },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "login_submit_button"
                )
            }

            // Bottom Sign Up link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account? ", color = TextSecondary)
                Text(
                    text = "Register",
                    color = PinkHighlight,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { viewModel.navigateTo("REGISTER") }
                )
            }
        }
    }
}

@Composable
fun RegisterScreen(viewModel: AppViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { viewModel.navigateTo("LOGIN") }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Join as a booking client or a professional model",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // Role Chooser
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface)
                            .padding(4.dp)
                    ) {
                        Button(
                            onClick = { viewModel.registerRole = "USER" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (viewModel.registerRole == "USER") PinkHighlight else Color.Transparent,
                                contentColor = if (viewModel.registerRole == "USER") Color.White else TextSecondary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Booker (User)")
                        }
                        
                        Button(
                            onClick = { viewModel.registerRole = "MODEL" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (viewModel.registerRole == "MODEL") PinkHighlight else Color.Transparent,
                                contentColor = if (viewModel.registerRole == "MODEL") Color.White else TextSecondary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Face, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Model Profile")
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Inputs
                    PremiumTextField(
                        value = viewModel.registerName,
                        onValueChange = { viewModel.registerName = it },
                        label = "Full Name",
                        placeholder = "E.g., Jessica Simpson",
                        leadingIcon = Icons.Default.Person,
                        testTag = "register_name_input"
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    PremiumTextField(
                        value = viewModel.registerEmail,
                        onValueChange = { viewModel.registerEmail = it },
                        label = "Email Address",
                        placeholder = "jessica@example.com",
                        leadingIcon = Icons.Default.Email,
                        testTag = "register_email_input"
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    PremiumTextField(
                        value = viewModel.registerPassword,
                        onValueChange = { viewModel.registerPassword = it },
                        label = "Password",
                        placeholder = "Minimum 6 characters",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true,
                        testTag = "register_password_input"
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    PremiumButton(
                        text = "Register Now",
                        onClick = { viewModel.navigateTo("OTP") },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "register_submit_button"
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Bottom link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account? ", color = TextSecondary)
                Text(
                    text = "Log In",
                    color = PinkHighlight,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { viewModel.navigateTo("LOGIN") }
                )
            }
        }
    }
}

@Composable
fun OtpScreen(viewModel: AppViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(PinkHighlight.copy(alpha = 0.15f), shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Smartphone,
                    contentDescription = null,
                    tint = PinkHighlight,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "OTP Verification",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "We have sent a verification code to your email address",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, textAlign = TextAlign.Center),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            PremiumTextField(
                value = viewModel.otpCode,
                onValueChange = { if (it.length <= 6) viewModel.otpCode = it },
                label = "Verification Code",
                placeholder = "123456",
                keyboardType = KeyboardType.Number,
                leadingIcon = Icons.Default.LockReset,
                testTag = "otp_input"
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            PremiumButton(
                text = "Verify & Complete",
                onClick = { viewModel.register() },
                modifier = Modifier.fillMaxWidth(),
                testTag = "otp_submit_button"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Resend Code",
                color = TextSecondary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable {
                    viewModel.addNotification("OTP Code Resent", "A new OTP code has been sent successfully.", "System")
                }
            )
        }
    }
}

@Composable
fun ForgotPasswordScreen(viewModel: AppViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { viewModel.navigateTo("LOGIN") },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(PinkHighlight.copy(alpha = 0.15f), shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MarkEmailRead,
                    contentDescription = null,
                    tint = PinkHighlight,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Forgot Password",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Enter your email to receive a password reset link",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, textAlign = TextAlign.Center),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            PremiumTextField(
                value = viewModel.forgotEmail,
                onValueChange = { viewModel.forgotEmail = it },
                label = "Registered Email Address",
                placeholder = "yourname@example.com",
                leadingIcon = Icons.Default.Email,
                testTag = "forgot_email_input"
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            PremiumButton(
                text = "Send Reset Link",
                onClick = {
                    viewModel.addNotification("Reset Link Sent", "Reset instructions sent to ${viewModel.forgotEmail}.", "System")
                    viewModel.navigateTo("LOGIN")
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "forgot_submit_button"
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
                        label = { Text("Dashboard", fontSize = 9.sp) },
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
                        icon = { Icon(imageVector = Icons.Default.People, contentDescription = "Users") },
                        label = { Text("Users", fontSize = 9.sp) },
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
                        icon = { Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = "Escrow & Proofs") },
                        label = { Text("Escrow", fontSize = 9.sp) },
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
                        icon = { Icon(imageVector = Icons.Default.Payments, contentDescription = "Collections") },
                        label = { Text("Cash", fontSize = 9.sp) },
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
                        icon = { Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = "Wallets") },
                        label = { Text("Wallets", fontSize = 9.sp) },
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
                        icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings", fontSize = 9.sp) },
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
    }
}
