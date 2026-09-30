package com.example

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Repository
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.AppViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Repository and ViewModel
        val repository = Repository.getInstance(applicationContext)
        val factory = AppViewModelFactory(application, repository)
        viewModel = ViewModelProvider(this, factory)[AppViewModel::class.java]

        // Route based on session:
        // "LOGIN NA KORLE FAST PAGE THEKE SURU HBE ARE AKBER LOGIN KORLE ARE LOGIN KRA LAGBE NA"
        if (viewModel.isLoggedIn) {
            viewModel.currentScreen = "DASHBOARD"
            viewModel.splashFinished = true
        } else {
            viewModel.currentScreen = "SPLASH"
            viewModel.splashFinished = false
        }

        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                MainContent(viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: AppViewModel) {
    val context = LocalContext.current
    val prefs = remember(context) { context.getSharedPreferences("app_permissions_prefs", Context.MODE_PRIVATE) }

    // Request app permissions: SMS, CAMERA, CONTACTS, MICROPHONE, NOTIFICATIONS, LOCATION, NEARBY DEVICES, PHOTOS/VIDEOS, PHONE
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Permissions handled once; persist immediately so user is never prompted repeatedly
        prefs.edit().putBoolean("permissions_already_requested", true).commit()
    }

    LaunchedEffect(Unit) {
        val alreadyRequested = prefs.getBoolean("permissions_already_requested", false)
        val permissions = buildList {
            add(android.Manifest.permission.CAMERA)
            add(android.Manifest.permission.RECORD_AUDIO)
            add(android.Manifest.permission.ACCESS_FINE_LOCATION)
            add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            add(android.Manifest.permission.READ_CONTACTS)
            add(android.Manifest.permission.READ_SMS)
            add(android.Manifest.permission.RECEIVE_SMS)
            add(android.Manifest.permission.READ_PHONE_STATE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(android.Manifest.permission.POST_NOTIFICATIONS)
                add(android.Manifest.permission.READ_MEDIA_IMAGES)
                add(android.Manifest.permission.READ_MEDIA_VIDEO)
            } else {
                add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(android.Manifest.permission.BLUETOOTH_CONNECT)
                add(android.Manifest.permission.BLUETOOTH_SCAN)
            }
        }

        // Only query permissions that are NOT currently granted
        val ungranted = permissions.filter { p ->
            ContextCompat.checkSelfPermission(context, p) != PackageManager.PERMISSION_GRANTED
        }
        val anyGranted = permissions.any { p ->
            ContextCompat.checkSelfPermission(context, p) == PackageManager.PERMISSION_GRANTED
        }

        if (ungranted.isEmpty() || alreadyRequested || anyGranted) {
            // All or some permissions already granted, or previously requested! Never prompt again
            prefs.edit().putBoolean("permissions_already_requested", true).commit()
        } else {
            // Ask only once on very first launch, and never prompt again
            prefs.edit().putBoolean("permissions_already_requested", true).commit()
            permissionLauncher.launch(ungranted.toTypedArray())
        }
    }

    // Handle back navigation: Dashboard minimizes app when logged in; Login returns to Splash when not logged in; sub-screens go back
    BackHandler(enabled = viewModel.currentScreen != "SPLASH") {
        if (viewModel.currentScreen == "DASHBOARD") {
            (context as? ComponentActivity)?.moveTaskToBack(true)
        } else if (viewModel.currentScreen == "LOGIN" && !viewModel.isLoggedIn) {
            viewModel.currentScreen = "SPLASH"
        } else {
            viewModel.goBack()
        }
    }

    Crossfade(targetState = viewModel.currentScreen, label = "ScreenTransition") { screen ->
        when (screen) {
            "SPLASH" -> SplashScreen(viewModel)
            "ONBOARDING" -> OnboardingScreen(viewModel)
            "LOGIN" -> LoginScreen(viewModel)
            "SELECT_ACCOUNT_TYPE" -> SelectAccountTypeScreen(viewModel)
            "REGISTER_USER" -> RegisterUserScreen(viewModel)
            "REGISTER_MODEL" -> RegisterModelScreen(viewModel)
            "REGISTER" -> SelectAccountTypeScreen(viewModel) // Alias for backwards compatibility
            "PHONE_VERIFICATION" -> PhoneVerificationScreen(viewModel)
            "EMAIL_VERIFICATION" -> EmailVerificationScreen(viewModel)
            "OTP" -> PhoneVerificationScreen(viewModel) // Alias for backwards compatibility
            "LOGIN_WITH_OTP" -> LoginWithOtpScreen(viewModel)
            "FORGOT_PASSWORD" -> ForgotPasswordScreen(viewModel)
            "FORGOT_OTP" -> ForgotOtpScreen(viewModel)
            "CREATE_NEW_PASSWORD" -> CreateNewPasswordScreen(viewModel)
            "DASHBOARD" -> {
                val userState = viewModel.currentUser.collectAsStateWithLifecycle().value
                DashboardContainer(viewModel = viewModel) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        when (userState?.role) {
                            "ADMIN" -> {
                                when (viewModel.selectedTab) {
                                    0 -> AdminDashboardTab(viewModel)
                                    1 -> AdminLiveLocationTrackingTab(viewModel, onBack = { viewModel.selectedTab = 0 })
                                    2 -> AdminSupportMessengerTab(viewModel)
                                    3 -> AdminUsersTab(viewModel)
                                    4 -> AdminEscrowReviewTab(viewModel)
                                    5 -> AdminCollectionsTab(viewModel)
                                    6 -> AdminWalletsTab(viewModel)
                                    7 -> AdminSettingsTab(viewModel)
                                    else -> AdminDashboardTab(viewModel)
                                }
                            }
                            "MODEL" -> {
                                when (viewModel.selectedTab) {
                                    0 -> ModelDashboardTab(viewModel)
                                    1 -> ModelBookingsTab(viewModel)
                                    2 -> ChatTab(viewModel)
                                    3 -> ModelWalletTab(viewModel)
                                    4 -> ModelProfileTab(viewModel)
                                }
                            }
                            "CASH_AGENT" -> {
                                when (viewModel.selectedTab) {
                                    0 -> CashAgentDashboardTab(viewModel)
                                    1 -> CashAgentCollectionsTab(viewModel)
                                    2 -> CashAgentWalletTab(viewModel)
                                    3 -> CashAgentProfileTab(viewModel)
                                }
                            }
                            else -> {
                                // Default Client Tabs (Public User)
                                when (viewModel.selectedTab) {
                                    0 -> HomeTab(viewModel)
                                    1 -> SearchTab(viewModel)
                                    2 -> BookingsTab(viewModel)
                                    3 -> ChatTab(viewModel)
                                    4 -> ProfileTab(viewModel)
                                }
                            }
                        }
                    }
                }
            }
            "MODEL_PROFILE" -> ModelProfileScreen(viewModel)
            "SERVICES_PRICING" -> ServicesPricingScreen(viewModel)
            "AVAILABILITY" -> AvailabilityScreen(viewModel)
            "BOOKING_SUMMARY" -> BookingSummaryScreen(viewModel)
            "CONFIRM_BOOKING" -> ConfirmBookingScreen(viewModel)
            "WALLET" -> WalletScreen(viewModel)
            "NOTIFICATIONS" -> NotificationsScreen(viewModel)
            "MODEL_DASHBOARD" -> ModelDashboardScreen(viewModel)
            "ADMIN_PANEL" -> AdminPanelScreen(viewModel)
            "MODEL_OFFERED_SERVICES" -> ModelOfferedServicesScreen(viewModel)
            "SETTINGS" -> SettingsScreen(viewModel)
            "EDIT_PROFILE" -> EditProfileScreen(viewModel)
            else -> SplashScreen(viewModel)
        }
    }
}
