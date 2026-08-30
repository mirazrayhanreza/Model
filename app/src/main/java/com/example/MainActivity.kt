package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
                                    1 -> AdminSupportMessengerTab(viewModel)
                                    2 -> AdminUsersTab(viewModel)
                                    3 -> AdminEscrowReviewTab(viewModel)
                                    4 -> AdminCollectionsTab(viewModel)
                                    5 -> AdminWalletsTab(viewModel)
                                    6 -> AdminSettingsTab(viewModel)
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
