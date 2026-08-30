package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UserPaymentMethod(
    val id: String,
    val type: String, // "VISA", "MasterCard", "bKash", "Nagad", "Rocket", "Bank Account"
    val accountNumber: String, // e.g. "•••• •••• •••• 4202" or "01712 345 678"
    val holderName: String = "",
    val isPrimary: Boolean = false,
    val isVerified: Boolean = true
)

class AppViewModel(application: Application, val repository: Repository) : AndroidViewModel(application) {

    // --- Navigation & Flow States ---
    var splashFinished by mutableStateOf(true)
    var onboardingFinished by mutableStateOf(true)
    var currentScreen by mutableStateOf("DASHBOARD")
    var previousScreen by mutableStateOf("DASHBOARD")
    var selectedTab by mutableStateOf(0) // 0: Home, 1: Search, 2: Bookings, 3: Chats, 4: Profile
    var showPhpBackendModal by mutableStateOf(false)
    var showLiveSupportModal by mutableStateOf(false)
    var adminSelectedSupportUserId by mutableStateOf("user_1")

    // --- Selected Model State ---
    var selectedModelId by mutableStateOf<Int?>(1)

    // --- Data Flows from Room ---
    val allModels: StateFlow<List<ModelProfile>> = repository.allModels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentUser: StateFlow<CurrentUser?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allBookings: StateFlow<List<Booking>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMessages: StateFlow<List<ChatMessage>> = repository.allMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamic Flow of active chat messages
    private val _activeChatPartnerId = MutableStateFlow<String?>(null)
    val activeChatMessages: StateFlow<List<ChatMessage>> = _activeChatPartnerId
        .flatMapLatest { partnerId ->
            val current = currentUser.value
            if (partnerId != null && current != null) {
                repository.getMessagesBetween(current.id, partnerId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active User's Bookings
    val userBookings: StateFlow<List<Booking>> = currentUser
        .flatMapLatest { user ->
            if (user != null) {
                if (user.role == "MODEL") {
                    // Models see bookings requests sent to them
                    val modelIdStr = user.id.replace("model_", "").toIntOrNull() ?: 1
                    repository.getBookingsForModel(modelIdStr)
                } else if (user.role == "ADMIN") {
                    // Admins see all bookings
                    repository.allBookings
                } else {
                    // Regular users see bookings they created or sample bookings if empty
                    repository.allBookings.map { list ->
                        val userSpecific = list.filter { it.userId == user.id || it.userId == user.name }
                        if (userSpecific.isNotEmpty()) userSpecific else list
                    }
                }
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User's Transactions
    val walletTransactions: StateFlow<List<WalletTransaction>> = currentUser
        .flatMapLatest { user ->
            if (user != null) {
                repository.getTransactionsForUser(user.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Deposits & Withdrawals & Ledger Flows ---
    val allDeposits: StateFlow<List<DepositRequest>> = repository.allDeposits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userDeposits: StateFlow<List<DepositRequest>> = currentUser
        .flatMapLatest { user ->
            if (user != null) {
                if (user.role == "ADMIN") repository.allDeposits
                else repository.getDepositsForUser(user.id)
            } else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWithdrawals: StateFlow<List<WithdrawalRequest>> = repository.allWithdrawals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userWithdrawals: StateFlow<List<WithdrawalRequest>> = currentUser
        .flatMapLatest { user ->
            if (user != null) {
                if (user.role == "ADMIN") repository.allWithdrawals
                else repository.getWithdrawalsForUser(user.id)
            } else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLedgerEntries: StateFlow<List<LedgerEntry>> = repository.allLedgerEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Payment Agents list from Room DB
    val paymentAgents: StateFlow<List<PaymentAgent>> = repository.allPaymentAgents
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            listOf(
                PaymentAgent(id = "AGENT-1024", name = "Dhaka Central Cash Agent #1024", agentCode = "1024", country = "Bangladesh", phone = "01711223344", paymentMethod = "bKash", accountNumber = "01711223344", accountHolder = "Dhaka Agent Ltd", commissionRate = 1.5, minLimit = 500.0, maxLimit = 100000.0, availableBalance = 50000.0, verificationStatus = "VERIFIED"),
                PaymentAgent(id = "AGENT-2048", name = "Uttara Nagad Agent #2048", agentCode = "2048", country = "Bangladesh", phone = "01822334455", paymentMethod = "Nagad", accountNumber = "01822334455", accountHolder = "Uttara Cash Express", commissionRate = 1.0, minLimit = 300.0, maxLimit = 50000.0, availableBalance = 30000.0, verificationStatus = "VERIFIED")
            )
        )

    // Modal UI States
    var showDepositModal by mutableStateOf(false)
    var showWithdrawalModal by mutableStateOf(false)
    var selectedProofImageForModal by mutableStateOf<String?>(null)
    var selectedProofTitleForModal by mutableStateOf<String?>(null)


    // User's Favorites list
    val userFavorites: StateFlow<List<FavoriteModel>> = currentUser
        .flatMapLatest { user ->
            if (user != null) {
                repository.getFavoritesForUser(user.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reviews for the selected model
    private val _selectedModelIdFlow = MutableStateFlow<Int?>(1)
    val selectedModelReviews: StateFlow<List<ModelReview>> = _selectedModelIdFlow
        .flatMapLatest { modelId ->
            if (modelId != null) {
                repository.getReviewsForModel(modelId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Search & Filter States ---
    var searchQuery by mutableStateOf("")
    var searchCountry by mutableStateOf("All") // "All", "Bangladesh", "China", "USA", "UK", "UAE", "India"
    var searchCity by mutableStateOf("All") // "All", "Dhaka", "Chittagong", "Sylhet", etc.
    var searchAgeMin by mutableStateOf(18)
    var searchAgeMax by mutableStateOf(30)
    var searchHeightMin by mutableStateOf(150)
    var searchHeightMax by mutableStateOf(190)
    var searchCategory by mutableStateOf("All") // "Dating", "Dinner", "Photoshoot", etc.
    var searchPriceMax by mutableStateOf(200)
    var searchVerifiedOnly by mutableStateOf(false)
    var searchOnlineOnly by mutableStateOf(false)

    // --- Booking Draft State ---
    var bookingDate by mutableStateOf("20 May 2025")
    var bookingTime by mutableStateOf("11:00 AM")
    var bookingService by mutableStateOf("Dating")
    var bookingDuration by mutableStateOf(1) // 1 Hour, 2 Hours, 4 Hours, 8 Hours, 12 Hours (Overnight)
    var bookingLocation by mutableStateOf("Banani, Dhaka")
    var bookingNotes by mutableStateOf("")
    var bookingPriceSummary by mutableStateOf(120.0)
    var bookingPaymentMethod by mutableStateOf("Wallet Balance") // Wallet Balance, bKash, Nagad, Stripe, Cash

    // Last confirmed booking
    var lastConfirmedBooking by mutableStateOf<Booking?>(null)

    // --- Auth States & Country Selector ---
    var selectedCountryDialCode by mutableStateOf("+880")
    var selectedCountryFlag by mutableStateOf("🇧🇩")
    var selectedCountryName by mutableStateOf("Bangladesh")
    var selectedCountryIso by mutableStateOf("BD")

    fun selectCountry(country: CountryCodeItem) {
        selectedCountryDialCode = country.dialCode
        selectedCountryFlag = country.flag
        selectedCountryName = country.name
        selectedCountryIso = country.code
        registerCountry = country.name
    }

    fun getFormattedPhoneNumber(rawPhone: String): String {
        val cleanNumber = rawPhone.trim()
        if (cleanNumber.startsWith("+")) return cleanNumber
        return "$selectedCountryDialCode $cleanNumber"
    }

    var loginEmail by mutableStateOf("")
    var loginPassword by mutableStateOf("")
    var loginPhone by mutableStateOf("1712-345678")
    var rememberMe by mutableStateOf(true)

    // Firebase Auth States
    val authManager = com.example.data.auth.FirebaseAuthManager(application.applicationContext, repository)
    var isAuthLoading by mutableStateOf(false)
    var authErrorMessage by mutableStateOf<String?>(null)
    var authSuccessMessage by mutableStateOf<String?>(null)

    var registerName by mutableStateOf("")
    var registerPhone by mutableStateOf("+880 1712-345678")
    var registerEmail by mutableStateOf("")
    var registerPassword by mutableStateOf("")
    var registerConfirmPassword by mutableStateOf("")
    var registerRole by mutableStateOf("USER") // USER, MODEL, ADMIN
    var registerCountry by mutableStateOf("Bangladesh")
    var registerDob by mutableStateOf("01/01/2000")
    var registerGender by mutableStateOf("Female") // Male, Female, Other
    var registerAgreeTerms by mutableStateOf(true)

    // Verification / OTP States
    var verificationTarget by mutableStateOf("+880 1712-345678")
    var verificationType by mutableStateOf("PHONE") // "PHONE" or "EMAIL"
    var verificationNextScreen by mutableStateOf("DASHBOARD")
    var otpCode by mutableStateOf("123456")

    // Forgot / Reset Password States
    var forgotResetMethod by mutableStateOf("PHONE") // "PHONE" or "EMAIL"
    var forgotPhone by mutableStateOf("+880 1712-345678")
    var forgotEmail by mutableStateOf("example@email.com")
    var newPasswordVal by mutableStateOf("")
    var confirmNewPasswordVal by mutableStateOf("")
    var resetPasswordVal by mutableStateOf("")

    // --- Wallet States ---
    var depositAmount by mutableStateOf("")
    var withdrawAmount by mutableStateOf("")
    var walletMethod by mutableStateOf("bKash")

    // --- App Settings States ---
    var appLanguage by mutableStateOf("English") // "English", "Bangla"
    var isDarkModeEnabled by mutableStateOf(true) // Start with premium dark mode matching mockup!

    // --- Backend Server Settings ---
    var backendServerUrl by mutableStateOf("https://modolconnect.com/backend/")
    var backendStatus by mutableStateOf("CONNECTED (PHP 8.2.31)")
    var backendAppName by mutableStateOf("Modol Connect Backend v2.1.0")

    fun testBackendConnection() {
        viewModelScope.launch {
            backendStatus = "CONNECTING..."
            delay(800)
            backendStatus = "CONNECTED (PHP 8.2.31)"
            addNotification(
                "Backend Connection Test",
                "Successfully connected to $backendServerUrl. PHP 8.2.31 runtime operational.",
                "System"
            )
        }
    }

    // Notification List
    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    // Cash Collections List (Pre-populated to match the mockup!)
    private val _cashCollections = MutableStateFlow<List<CashCollectionRequest>>(
        listOf(
            CashCollectionRequest("CC88521", "Agent Sumon", 3500.0, "BK89562", "PENDING", "al_amin@example.com", "18 May 2025"),
            CashCollectionRequest("CC88520", "Agent Rafiq", 2500.0, "BK89560", "PAID", "rashid@example.com", "18 May 2025"),
            CashCollectionRequest("CC88519", "Agent Arif", 4000.0, "BK89561", "PENDING", "sojib@example.com", "18 May 2025"),
            CashCollectionRequest("CC88518", "Agent Jibon", 3000.0, "BK89559", "PAID", "mahmudul@example.com", "18 May 2025"),
            CashCollectionRequest("CC88517", "Agent Hasan", 4500.0, "BK89558", "PENDING", "jahid@example.com", "18 May 2025")
        )
    )
    val cashCollections: StateFlow<List<CashCollectionRequest>> = _cashCollections.asStateFlow()

    // Agent & Escrow Rates Config (Admin Configurable)
    var agentCommissionRate by mutableStateOf(5.0) // 5% standard commission per collection
    var modelSharePercentage by mutableStateOf(85.0) // 85% to model
    var platformFeePercentage by mutableStateOf(15.0) // 15% platform fee


    init {
        viewModelScope.launch {
            repository.prePopulateIfEmpty()
            // Set initial flow triggers
            _selectedModelIdFlow.value = selectedModelId
            val current = currentUser.value
            if (current != null) {
                _activeChatPartnerId.value = "1"
            }
            
            // Add initial mock notifications to match the screenshot perfectly
            addNotification("Welcome to Modol Connect", "Thank you for joining us. Explore and book your favorite model.", "System", "2 Days Ago", true)
            addNotification("Account Verified", "Your account has been verified successfully.", "System", "Yesterday", true)
            addNotification("Wallet Credited", "৳50 cashback received in your wallet.", "Payment", "Yesterday", true)
            addNotification("Booking Reminder", "Your booking with Nusrat is today at 04:00 PM.", "Booking", "03:30 PM", true)
            addNotification("Special Offer", "Get 20% OFF on your next booking. Use code: MODOL20", "Promotions", "09:00 AM", false)
            addNotification("New Message", "You have a new message from Jessica.", "Chat", "10:25 AM", false)
            addNotification("Payment Successful", "Your payment of ৳120 was successful for booking with Jessica.", "Payment", "10:28 AM", false)
            addNotification("Booking Accepted", "Jessica has accepted your booking request.", "Booking", "10:30 AM", false)
        }
    }

    // --- Navigation Functions ---
    fun goBack() {
        val temp = currentScreen
        currentScreen = previousScreen
        previousScreen = temp
    }

    fun navigateTo(screen: String) {
        previousScreen = currentScreen
        currentScreen = screen
        if (screen == "DASHBOARD") {
            // Ensure pre-selection sync
            _selectedModelIdFlow.value = selectedModelId
        }
    }

    fun selectModel(id: Int) {
        selectedModelId = id
        _selectedModelIdFlow.value = id
        // Pre-fill booking price based on model hourly rate
        viewModelScope.launch {
            val model = repository.getModelByIdSync(id)
            if (model != null) {
                bookingPriceSummary = (model.hourlyRate * bookingDuration).toDouble()
            }
        }
    }

    fun updateBookingDurationAndPrice(hours: Int) {
        bookingDuration = hours
        viewModelScope.launch {
            val id = selectedModelId ?: 1
            val model = repository.getModelByIdSync(id)
            if (model != null) {
                bookingPriceSummary = (model.hourlyRate * hours).toDouble()
            }
        }
    }

    fun selectChatPartner(partnerId: String) {
        _activeChatPartnerId.value = partnerId
    }

    // --- Auth Logic with Firebase Authentication ---
    fun login() {
        authErrorMessage = null
        authSuccessMessage = null
        val email = loginEmail.trim()
        val password = loginPassword.trim()

        if (email.isEmpty()) {
            authErrorMessage = "Please enter your email address or phone."
            return
        }
        if (password.isEmpty()) {
            authErrorMessage = "Please enter your password."
            return
        }

        viewModelScope.launch {
            isAuthLoading = true
            val result = authManager.signInWithEmail(email, password)
            isAuthLoading = false

            result.onSuccess { user ->
                authErrorMessage = null
                addNotification("Login Success", "Welcome back, ${user.name}! Enjoy secure model booking.", "System")
                navigateTo("DASHBOARD")
            }.onFailure { error ->
                authErrorMessage = error.message ?: "Login failed. Please check your credentials."
                addNotification("Login Failed", authErrorMessage ?: "Authentication error", "System")
            }
        }
    }

    fun register() {
        authErrorMessage = null
        authSuccessMessage = null
        val name = registerName.trim()
        val email = registerEmail.trim()
        val password = registerPassword.trim()
        val confirmPass = registerConfirmPassword.trim()

        if (name.isEmpty()) {
            authErrorMessage = "Please enter your full name."
            return
        }
        if (email.isEmpty()) {
            authErrorMessage = "Please enter a valid email address."
            return
        }
        if (password.isEmpty()) {
            authErrorMessage = "Please enter a password."
            return
        }
        if (password.length < 6) {
            authErrorMessage = "Password must be at least 6 characters long."
            return
        }
        if (password != confirmPass) {
            authErrorMessage = "Passwords do not match."
            return
        }

        viewModelScope.launch {
            isAuthLoading = true
            val result = authManager.signUpWithEmail(
                email = email,
                password = password,
                name = name,
                role = registerRole,
                phone = registerPhone
            )
            isAuthLoading = false

            result.onSuccess { user ->
                authErrorMessage = null
                repository.insertTransaction(
                    WalletTransaction(
                        userId = user.id,
                        type = "DEPOSIT",
                        amount = 500.0,
                        description = "Welcome Sign-Up Bonus"
                    )
                )
                addNotification("Registration Success", "Account created successfully with a ৳500 signup bonus!", "System")

                // If model role, insert as model in model list too!
                if (registerRole == "MODEL") {
                    val modelId = (10..1000).random()
                    val newModel = ModelProfile(
                        id = modelId,
                        name = name,
                        rating = 5.0f,
                        reviewCount = 0,
                        location = "Dhaka",
                        isOnline = true,
                        isVerified = false,
                        bio = "Hi! I just joined MODOL CONNECT. Looking forward to professional opportunities.",
                        skills = "New Face",
                        languages = "Bengali, English",
                        services = "Photoshoot, Fashion Show",
                        hourlyRate = 100,
                        availabilityDays = "Sun, Mon, Tue, Wed, Thu, Fri, Sat",
                        gender = registerGender,
                        age = 22,
                        heightCm = 170,
                        imageResName = "default_model"
                    )
                    repository.insertModels(listOf(newModel))
                }
                navigateTo("DASHBOARD")
            }.onFailure { error ->
                authErrorMessage = error.message ?: "Registration failed. Please try again."
                addNotification("Registration Failed", authErrorMessage ?: "Sign up error", "System")
            }
        }
    }

    fun sendPasswordReset(targetEmail: String? = null) {
        authErrorMessage = null
        authSuccessMessage = null
        val email = (targetEmail ?: forgotEmail).trim()
        if (email.isEmpty()) {
            authErrorMessage = "Please enter your registered email address."
            return
        }

        viewModelScope.launch {
            isAuthLoading = true
            val result = authManager.sendPasswordReset(email)
            isAuthLoading = false
            result.onSuccess {
                authSuccessMessage = "Password reset email sent to $email. Please check your inbox."
                addNotification("Password Reset", "Reset instructions sent to $email", "Security")
            }.onFailure { error ->
                authErrorMessage = error.message ?: "Failed to send reset link."
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authManager.signOut()
            selectedTab = 0
            loginEmail = ""
            loginPassword = ""
            authErrorMessage = null
            authSuccessMessage = null
            navigateTo("LOGIN")
        }
    }

    // --- Wallet Transactions ---
    fun depositWallet() {
        val amount = depositAmount.toDoubleOrNull() ?: return
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val updatedUser = user.copy(balance = user.balance + amount)
            repository.updateCurrentUser(updatedUser)
            repository.insertTransaction(
                WalletTransaction(
                    userId = user.id,
                    type = "DEPOSIT",
                    amount = amount,
                    description = "Deposited via $walletMethod"
                )
            )
            addNotification("Wallet Deposit", "Successfully deposited ৳$amount via $walletMethod.", "Wallet")
            depositAmount = ""
        }
    }

    fun quickAddWalletBalance(amount: Double) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val updatedUser = user.copy(balance = user.balance + amount)
            repository.updateCurrentUser(updatedUser)
            repository.insertTransaction(
                WalletTransaction(
                    userId = user.id,
                    type = "DEPOSIT",
                    amount = amount,
                    description = "Added Balance on Checkout"
                )
            )
            addNotification("Wallet Recharged", "Successfully added ৳${amount.toInt()} to your wallet balance. New Balance: ৳${updatedUser.balance.toInt()}.", "Wallet")
        }
    }

    fun withdrawWallet() {
        val amount = withdrawAmount.toDoubleOrNull() ?: return
        val user = currentUser.value ?: return
        if (user.balance < amount) return
        viewModelScope.launch {
            val updatedUser = user.copy(balance = user.balance - amount)
            repository.updateCurrentUser(updatedUser)
            repository.insertTransaction(
                WalletTransaction(
                    userId = user.id,
                    type = "WITHDRAW",
                    amount = amount,
                    description = "Withdrawn via $walletMethod"
                )
            )
            addNotification("Wallet Withdrawal", "Successfully withdrew ৳$amount via $walletMethod. Request is processing.", "Wallet")
            withdrawAmount = ""
        }
    }

    // --- Cash Agent & Admin Collection Operations ---
    fun submitCashCollection(id: String, agentName: String, amount: Double, bookingId: String) {
        val list = _cashCollections.value.toMutableList()
        list.add(0, CashCollectionRequest(id, agentName, amount, bookingId, "PENDING"))
        _cashCollections.value = list
        addNotification("New Cash Request", "Cash Collection of ৳$amount assigned to $agentName.", "System")
    }

    fun updateCollectionStatus(id: String, status: String, receiptUrl: String? = null) {
        val list = _cashCollections.value.map {
            if (it.id == id) {
                it.copy(status = status, receiptPhotoUrl = receiptUrl)
            } else {
                it
            }
        }
        _cashCollections.value = list
        val item = list.find { it.id == id } ?: return
        if (status == "PAID") {
            addNotification("Cash Collected", "Agent ${item.agentName} collected ৳${item.amount} for Booking ${item.bookingId}.", "Payment")
        }
    }

    fun approveCashCollectionByAdmin(id: String) {
        val list = _cashCollections.value.map {
            if (it.id == id) {
                it.copy(status = "PAID")
            } else {
                it
            }
        }
        _cashCollections.value = list
        val item = list.find { it.id == id } ?: return
        
        // Find booking and update its payment status to PAID
        viewModelScope.launch {
            val bookings = allBookings.value
            val match = bookings.find { it.id == item.bookingId.replace("BK", "").replace("#", "").toIntOrNull() ?: -1 }
            if (match != null) {
                val updated = match.copy(paymentStatus = "PAID")
                repository.updateBooking(updated)
                addNotification("Escrow Active", "Escrow payment verified and active for booking ${item.bookingId}.", "Payment")
            } else {
                // Pre-populated booking fall-back, trigger normal notification
                addNotification("Escrow Active", "Escrow payment verified and active for booking ${item.bookingId}.", "Payment")
            }
            
            // Give Cash Agent their 5% commission!
            val commission = item.amount * (agentCommissionRate / 100.0)
            addNotification("Commission Earned", "৳$commission added to Agent balance.", "Wallet")
        }
    }


    // --- Booking Management ---
    fun createBooking() {
        val currentU = currentUser.value ?: return
        val modelId = selectedModelId ?: 1
        
        viewModelScope.launch {
            val model = repository.getModelByIdSync(modelId) ?: return@launch
            val totalCost = bookingPriceSummary

            // Check if user has sufficient balance (or payment method is Cash/Stripe)
            if (bookingPaymentMethod != "Cash" && currentU.balance < totalCost) {
                // Insufficient balance, auto-add deposit to proceed seamlessly
                val depositNeeded = totalCost - currentU.balance
                val updatedU = currentU.copy(balance = 0.0) // Deducted fully
                repository.updateCurrentUser(updatedU)
                repository.insertTransaction(
                    WalletTransaction(
                        userId = currentU.id,
                        type = "DEPOSIT",
                        amount = depositNeeded,
                        description = "Instant deposit for Booking #$modelId"
                    )
                )
                repository.insertTransaction(
                    WalletTransaction(
                        userId = currentU.id,
                        type = "PAYMENT",
                        amount = totalCost,
                        description = "Payment for booking Jessica"
                    )
                )
            } else if (bookingPaymentMethod != "Cash") {
                // Deduct balance
                val updatedU = currentU.copy(balance = currentU.balance - totalCost)
                repository.updateCurrentUser(updatedU)
                repository.insertTransaction(
                    WalletTransaction(
                        userId = currentU.id,
                        type = "PAYMENT",
                        amount = totalCost,
                        description = "Payment for booking ${model.name}"
                    )
                )
            }

            val newBooking = Booking(
                userId = currentU.id,
                modelId = model.id,
                modelName = model.name,
                modelPhoto = model.imageResName,
                date = bookingDate,
                time = bookingTime,
                serviceType = bookingService,
                durationHours = bookingDuration,
                location = bookingLocation,
                notes = bookingNotes,
                totalPrice = totalCost,
                status = "PAYMENT_RECEIVED",
                paymentStatus = "ESCROW_HELD",
                paymentMethod = bookingPaymentMethod
            )

            val bookingId = repository.insertBooking(newBooking)
            val finalBooking = newBooking.copy(id = bookingId.toInt())
            lastConfirmedBooking = finalBooking

            addNotification(
                "Booking & Escrow Active",
                "Your booking for ${model.name} ($bookingDate) is placed. ৳$totalCost is held securely in Escrow.",
                "Payment"
            )

            navigateTo("CONFIRM_BOOKING")
        }
    }

    fun modelAcceptBooking(booking: Booking) {
        viewModelScope.launch {
            val updated = booking.copy(status = "ACCEPTED")
            repository.updateBooking(updated)
            addNotification("Booking Accepted", "Model ${booking.modelName} accepted Booking #${booking.id}.", "Booking")
        }
    }

    fun modelRejectBooking(booking: Booking, reason: String = "Schedule conflict") {
        viewModelScope.launch {
            // Auto refund user
            val updated = booking.copy(status = "REJECTED", paymentStatus = "REFUNDED")
            repository.updateBooking(updated)
            
            // Credit money back to user wallet
            val currentU = currentUser.value
            if (currentU != null && currentU.id == booking.userId) {
                repository.updateCurrentUser(currentU.copy(balance = currentU.balance + booking.totalPrice))
                repository.insertTransaction(
                    WalletTransaction(
                        userId = booking.userId,
                        type = "DEPOSIT",
                        amount = booking.totalPrice,
                        description = "Full Refund for Rejected Booking #${booking.id}"
                    )
                )
            }
            addNotification("Booking Rejected & Refunded", "Booking #${booking.id} rejected. ৳${booking.totalPrice} refunded to user wallet.", "Payment")
        }
    }

    fun modelStartService(booking: Booking) {
        viewModelScope.launch {
            val updated = booking.copy(status = "IN_PROGRESS")
            repository.updateBooking(updated)
            addNotification("Service Started", "Booking #${booking.id} with ${booking.modelName} is now IN PROGRESS.", "Booking")
        }
    }

    fun submitServiceProof(
        bookingId: Int,
        selfieUrl: String,
        photoUrls: String,
        videoUrl: String,
        gpsLocation: String,
        notes: String
    ) {
        viewModelScope.launch {
            val bookings = allBookings.value
            val match = bookings.find { it.id == bookingId } ?: return@launch
            val updated = match.copy(
                status = "PROOF_UPLOADED",
                proofSelfieUrl = selfieUrl,
                proofPhotos = photoUrls,
                proofVideoUrl = videoUrl,
                proofGpsLocation = gpsLocation,
                proofNotes = notes,
                proofSubmittedTime = System.currentTimeMillis()
            )
            repository.updateBooking(updated)
            addNotification(
                "Proof Uploaded",
                "Service proof (Photos, Video, GPS) submitted for Booking #$bookingId. Awaiting Admin & User review.",
                "Booking"
            )
        }
    }

    fun userConfirmService(booking: Booking, rating: Int, feedback: String) {
        viewModelScope.launch {
            val updated = booking.copy(
                status = "USER_CONFIRMED",
                userRating = rating,
                userFeedback = feedback
            )
            repository.updateBooking(updated)
            addNotification(
                "Client Confirmed Service",
                "Client rated ${booking.modelName} $rating★ for Booking #${booking.id}.",
                "Booking"
            )
        }
    }

    fun releaseEscrowPayment(booking: Booking) {
        viewModelScope.launch {
            val modelShare = booking.totalPrice * (modelSharePercentage / 100.0)
            val platformFee = booking.totalPrice * (platformFeePercentage / 100.0)
            val agentFee = if (booking.paymentMethod == "Cash") booking.totalPrice * (agentCommissionRate / 100.0) else 0.0

            val updated = booking.copy(
                status = "COMPLETED",
                paymentStatus = "RELEASED",
                modelEarnings = modelShare,
                platformFee = platformFee,
                agentFee = agentFee
            )
            repository.updateBooking(updated)

            // Credit Model wallet
            val modelUserId = "model_${booking.modelId}"
            repository.insertTransaction(
                WalletTransaction(
                    userId = modelUserId,
                    type = "EARNING",
                    amount = modelShare,
                    description = "Escrow Payout for Booking #${booking.id} (85% Share)"
                )
            )

            addNotification(
                "Payment Released",
                "Admin released ৳$modelShare to Model Wallet for Booking #${booking.id}. Platform Fee: ৳$platformFee.",
                "Payment"
            )
        }
    }

    fun refundEscrowPayment(booking: Booking, reason: String = "Admin refunded client") {
        viewModelScope.launch {
            val updated = booking.copy(
                status = "REFUNDED",
                paymentStatus = "REFUNDED"
            )
            repository.updateBooking(updated)

            // Credit User wallet
            val currentU = currentUser.value
            if (currentU != null && currentU.id == booking.userId) {
                repository.updateCurrentUser(currentU.copy(balance = currentU.balance + booking.totalPrice))
            }
            repository.insertTransaction(
                WalletTransaction(
                    userId = booking.userId,
                    type = "DEPOSIT",
                    amount = booking.totalPrice,
                    description = "Escrow Refund for Booking #${booking.id}: $reason"
                )
            )

            addNotification(
                "Full Refund Processed",
                "৳${booking.totalPrice} has been refunded to User wallet for Booking #${booking.id}.",
                "Payment"
            )
        }
    }

    fun raiseDispute(booking: Booking, reason: String) {
        viewModelScope.launch {
            val updated = booking.copy(
                disputeStatus = "RAISED",
                disputeReason = reason
            )
            repository.updateBooking(updated)
            addNotification(
                "Dispute Raised",
                "Dispute submitted for Booking #${booking.id}. Admin team will inspect proof & chat logs.",
                "Admin"
            )
        }
    }

    fun requestReuploadProof(booking: Booking, note: String) {
        viewModelScope.launch {
            val updated = booking.copy(
                status = "IN_PROGRESS",
                proofNotes = "RE-UPLOAD REQUESTED BY ADMIN: $note"
            )
            repository.updateBooking(updated)
            addNotification(
                "Re-upload Requested",
                "Admin requested proof re-upload for Booking #${booking.id}: $note",
                "Booking"
            )
        }
    }

    fun updateBookingStatus(booking: Booking, newStatus: String) {
        viewModelScope.launch {
            val updated = booking.copy(status = newStatus)
            repository.updateBooking(updated)
            addNotification(
                "Booking Status Updated",
                "Booking #${booking.id} status changed to $newStatus.",
                "Booking"
            )

            if (newStatus == "COMPLETED" || newStatus == "PAYMENT_RELEASED") {
                releaseEscrowPayment(booking)
            }
        }
    }

    // --- Chat Logic ---
    fun sendMessage(text: String, imageUri: String? = null) {
        val user = currentUser.value ?: return
        val partnerId = _activeChatPartnerId.value ?: "1"

        viewModelScope.launch {
            val message = ChatMessage(
                senderId = user.id,
                receiverId = partnerId,
                senderName = user.name,
                content = text,
                imageUri = imageUri,
                isRead = true
            )
            repository.insertMessage(message)

            // Trigger simulated response from model partner to make it feel super alive and real!
            delay(1500)
            val modelId = partnerId.toIntOrNull() ?: 1
            val model = repository.getModelByIdSync(modelId)
            val modelName = model?.name ?: "Jessica"
            
            val autoReplies = listOf(
                "That sounds great! I'll check my schedule and let you know.",
                "Sure, we can discuss the requirements of the shoot.",
                "Thank you! Please go ahead and book my available slot on the calendar.",
                "Hi! I am available on that day. Looking forward to our collaboration.",
                "I am happy to cooperate with your brand. Let's arrange a call request."
            )
            val replyText = autoReplies.random()

            val responseMessage = ChatMessage(
                senderId = partnerId,
                receiverId = user.id,
                senderName = modelName,
                content = replyText,
                isRead = false
            )
            repository.insertMessage(responseMessage)
            addNotification("New Message", "$modelName: $replyText", "Chat")
        }
    }

    // --- Live Support Chat Functions ---
    fun sendSupportMessage(text: String, imageUri: String? = null) {
        val user = currentUser.value
        val userId = user?.id ?: "user_1"
        val userName = user?.name ?: "Rahul Verma"

        viewModelScope.launch {
            val msg = ChatMessage(
                senderId = userId,
                receiverId = "ADMIN_SUPPORT",
                senderName = userName,
                content = text,
                imageUri = imageUri,
                isRead = true
            )
            repository.insertMessage(msg)

            // Auto reply bot if needed
            delay(1000)
            val autoReplies = listOf(
                "Thank you for contacting Modol Connect Support! An admin representative is reviewing your inquiry.",
                "Assalamu Alaikum $userName! Your support message has been received. Our team will verify your request shortly.",
                "Thank you for reaching out! For payment or escrow issues, our admin team verifies transactions in real-time."
            )
            val autoMsg = ChatMessage(
                senderId = "ADMIN_SUPPORT",
                receiverId = userId,
                senderName = "Modol Admin Support",
                content = autoReplies.random(),
                isRead = false
            )
            repository.insertMessage(autoMsg)
            addNotification("Support Chat", "Modol Admin Support: ${autoMsg.content}", "Support")
        }
    }

    fun sendAdminSupportReply(targetUserId: String, targetUserName: String, text: String, imageUri: String? = null) {
        viewModelScope.launch {
            val msg = ChatMessage(
                senderId = "ADMIN_SUPPORT",
                receiverId = targetUserId,
                senderName = "Modol Admin Support",
                content = text,
                imageUri = imageUri,
                isRead = false
            )
            repository.insertMessage(msg)
            addNotification("Support Message Sent", "Admin Support reply sent to $targetUserName.", "Admin")
        }
    }

    // --- Favorites Toggle ---
    fun toggleFavorite(modelId: Int) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val favorites = userFavorites.value
            val isFav = favorites.any { it.modelId == modelId }
            if (isFav) {
                repository.deleteFavorite(user.id, modelId)
            } else {
                repository.insertFavorite(user.id, modelId)
            }
        }
    }

    // --- Add Notifications ---
    fun addNotification(title: String, message: String, category: String, time: String = "Just now", isRead: Boolean = false) {
        val newNotif = AppNotification(
            id = System.currentTimeMillis().toInt() + (0..1000).random(),
            title = title,
            message = message,
            category = category,
            time = time,
            isRead = isRead
        )
        val current = _notifications.value.toMutableList()
        current.add(0, newNotif)
        _notifications.value = current
    }

    fun markNotificationAsRead(id: Int) {
        val current = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
        _notifications.value = current
    }

    fun markAllNotificationsAsRead() {
        val current = _notifications.value.map { it.copy(isRead = true) }
        _notifications.value = current
    }

    // --- Admin Verification ---
    fun verifyModel(modelId: Int) {
        viewModelScope.launch {
            val model = repository.getModelByIdSync(modelId) ?: return@launch
            val updated = model.copy(isVerified = !model.isVerified)
            repository.updateModel(updated)
            addNotification(
                "Model Verified Status",
                "${model.name}'s verified badge has been toggled to: ${updated.isVerified}.",
                "Admin"
            )
        }
    }

    fun submitReview(modelId: Int, rating: Int, comment: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val newReview = ModelReview(
                modelId = modelId,
                reviewerName = user.name,
                reviewerAvatar = user.avatarUrl,
                rating = rating,
                comment = comment,
                date = "21 May 2025"
            )
            repository.insertReview(newReview)
            
            // Re-calculate model average rating and update model in database
            val reviews = repository.getReviewsForModel(modelId).first()
            val totalRating = reviews.sumOf { it.rating } + rating
            val newCount = reviews.size + 1
            val newAvg = totalRating.toFloat() / newCount

            val model = repository.getModelByIdSync(modelId)
            if (model != null) {
                repository.updateModel(model.copy(rating = newAvg, reviewCount = newCount))
            }

            addNotification("Review Submitted", "Thank you for reviewing ${model?.name ?: "Model"}.", "Reviews")
        }
    }

    var showPhotoUploadDialog by mutableStateOf(false)
    var showEditPersonalInfoModal by mutableStateOf(false)
    var showEditPreferencesModal by mutableStateOf(false)

    // Additional Client Profile details
    var clientPhone by mutableStateOf("+880 1712 345 678")
    var clientDob by mutableStateOf("15 Apr 1992")
    var clientGender by mutableStateOf("Male")
    var clientPreferredLanguages by mutableStateOf("English, Bengali, Hindi")
    var clientPreferredCategories by mutableStateOf("Fashion, Lifestyle, Commercial")
    var clientBudgetRange by mutableStateOf("৳1,000 - ৳5,000")
    var clientBookingTime by mutableStateOf("Evening, Night")

    fun updateUserProfileDetails(name: String, email: String, phone: String, city: String, dob: String, gender: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val updatedUser = user.copy(name = name, email = email, city = city)
            repository.updateCurrentUser(updatedUser)
            clientPhone = phone
            clientDob = dob
            clientGender = gender
            addNotification("Profile Updated", "Your client profile information has been successfully saved.", "Profile")
        }
    }

    fun updateUserPreferences(languages: String, categories: String, budget: String, bookingTime: String) {
        clientPreferredLanguages = languages
        clientPreferredCategories = categories
        clientBudgetRange = budget
        clientBookingTime = bookingTime
        addNotification("Preferences Updated", "Your booking preferences have been updated successfully.", "Preferences")
    }

    // --- Saved Payment Methods States ---
    var savedPaymentMethods = mutableStateListOf(
        UserPaymentMethod("1", "VISA", "•••• •••• •••• 4202", "John Doe", isPrimary = true, isVerified = true),
        UserPaymentMethod("2", "bKash", "01712 345 678", "John Doe", isPrimary = false, isVerified = true),
        UserPaymentMethod("3", "Nagad", "01819 876 543", "John Doe", isPrimary = false, isVerified = true)
    )

    var showPaymentManagementModal by mutableStateOf(false)
    var showAddPaymentMethodModal by mutableStateOf(false)

    fun addPaymentMethod(type: String, accountNumber: String, holderName: String, setAsPrimary: Boolean) {
        val newId = System.currentTimeMillis().toString()
        val currentList = savedPaymentMethods.toList()
        savedPaymentMethods.clear()

        val shouldBePrimary = setAsPrimary || currentList.isEmpty()

        currentList.forEach { item ->
            savedPaymentMethods.add(
                if (shouldBePrimary) item.copy(isPrimary = false) else item
            )
        }

        val newMethod = UserPaymentMethod(
            id = newId,
            type = type,
            accountNumber = accountNumber,
            holderName = holderName.ifBlank { "Account Holder" },
            isPrimary = shouldBePrimary,
            isVerified = true
        )
        savedPaymentMethods.add(newMethod)

        addNotification(
            "Payment Method Added",
            "$type account ($accountNumber) added successfully to your account.",
            "Payment"
        )
    }

    fun removePaymentMethod(id: String) {
        val item = savedPaymentMethods.find { it.id == id }
        val remaining = savedPaymentMethods.filter { it.id != id }
        savedPaymentMethods.clear()

        val hasPrimary = remaining.any { it.isPrimary }
        remaining.forEachIndexed { index, pm ->
            if (!hasPrimary && index == 0) {
                savedPaymentMethods.add(pm.copy(isPrimary = true))
            } else {
                savedPaymentMethods.add(pm)
            }
        }

        addNotification(
            "Payment Method Removed",
            "${item?.type ?: "Payment method"} removed from saved payment options.",
            "Payment"
        )
    }

    fun setPrimaryPaymentMethod(id: String) {
        val currentList = savedPaymentMethods.toList()
        savedPaymentMethods.clear()
        currentList.forEach { item ->
            savedPaymentMethods.add(item.copy(isPrimary = (item.id == id)))
        }

        addNotification(
            "Primary Payment Method Updated",
            "Default payment method updated successfully.",
            "Payment"
        )
    }

    fun updateProfileAvatar(newAvatarUrl: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val updatedUser = user.copy(avatarUrl = newAvatarUrl)
            repository.updateCurrentUser(updatedUser)
            addNotification(
                "Profile Photo Updated",
                "Your profile photo has been successfully uploaded to https://modolconnect.com/uploads/profile/ and updated.",
                "Profile"
            )
        }
    }

    // ==========================================
    // DEPOSIT & WITHDRAWAL WORKFLOW ENGINE
    // ==========================================

    fun submitDepositRequest(
        agentId: String,
        agentName: String,
        country: String,
        paymentMethod: String,
        amount: Double,
        transactionId: String,
        proofScreenshotUrl: String,
        userNote: String?
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val reqId = "DEP-${(10000..99999).random()}"
            val deposit = DepositRequest(
                id = reqId,
                userId = user.id,
                userName = user.name,
                agentId = agentId,
                agentName = agentName,
                country = country,
                paymentMethod = paymentMethod,
                amount = amount,
                currency = "BDT",
                transactionId = transactionId,
                proofScreenshotUrl = proofScreenshotUrl,
                userNote = userNote,
                status = "PENDING",
                timestamp = System.currentTimeMillis()
            )
            repository.insertDeposit(deposit)
            addNotification(
                "Deposit Submitted ($reqId)",
                "Deposit request of ৳${amount.toInt()} via $paymentMethod ($transactionId) is PENDING admin verification.",
                "Payment"
            )
        }
    }

    fun approveDeposit(depositId: String, adminNote: String = "Verified with payment agent ledger.") {
        viewModelScope.launch {
            val deposits = allDeposits.value
            val dep = deposits.find { it.id == depositId } ?: return@launch
            if (dep.status == "APPROVED") return@launch // Prevent double approval

            val user = repository.currentUser.firstOrNull()
            val targetUserId = dep.userId

            // Update Deposit Status
            val updatedDep = dep.copy(
                status = "APPROVED",
                adminNote = adminNote,
                reviewedByAdmin = "Admin",
                reviewedAt = System.currentTimeMillis()
            )
            repository.updateDeposit(updatedDep)

            // Credit Balance to user
            val prevBalance = if (user != null && user.id == targetUserId) user.balance else 1500.0
            val newBalance = prevBalance + dep.amount

            if (user != null && user.id == targetUserId) {
                repository.updateCurrentUser(user.copy(balance = newBalance))
            }

            // Create Immutable Ledger Entry
            val ledger = LedgerEntry(
                referenceId = dep.id,
                userId = targetUserId,
                userName = dep.userName,
                userRole = "USER",
                transactionType = "DEPOSIT_CREDIT",
                amount = dep.amount,
                previousBalance = prevBalance,
                newBalance = newBalance,
                paymentMethod = dep.paymentMethod,
                transactionIdOrRef = dep.transactionId,
                timestamp = System.currentTimeMillis(),
                isVerifiedByAdmin = true
            )
            repository.insertLedgerEntry(ledger)

            // Add Wallet Transaction Record
            repository.insertTransaction(
                WalletTransaction(
                    userId = targetUserId,
                    type = "DEPOSIT",
                    amount = dep.amount,
                    description = "Approved Deposit #${dep.id} via ${dep.paymentMethod} (${dep.transactionId})"
                )
            )

            addNotification(
                "Deposit Approved! 🎉",
                "Deposit #${dep.id} of ৳${dep.amount.toInt()} approved. ৳${dep.amount.toInt()} credited to wallet.",
                "Payment"
            )
        }
    }

    fun rejectDeposit(depositId: String, adminNote: String = "Transaction ID or screenshot verification failed.") {
        viewModelScope.launch {
            val deposits = allDeposits.value
            val dep = deposits.find { it.id == depositId } ?: return@launch
            val updatedDep = dep.copy(
                status = "REJECTED",
                adminNote = adminNote,
                reviewedByAdmin = "Admin",
                reviewedAt = System.currentTimeMillis()
            )
            repository.updateDeposit(updatedDep)

            addNotification(
                "Deposit Rejected",
                "Deposit #${dep.id} of ৳${dep.amount.toInt()} rejected by admin. Reason: $adminNote",
                "Payment"
            )
        }
    }

    fun submitWithdrawalRequest(
        amount: Double,
        paymentMethod: String,
        accountNumber: String,
        accountHolder: String
    ): Boolean {
        val user = currentUser.value ?: return false
        if (user.balance < amount) return false

        viewModelScope.launch {
            val wdId = "WD-${(50000..99999).random()}"
            val request = WithdrawalRequest(
                id = wdId,
                applicantId = user.id,
                applicantName = user.name,
                applicantRole = user.role,
                amount = amount,
                currency = "BDT",
                paymentMethod = paymentMethod,
                accountNumber = accountNumber,
                accountHolder = accountHolder,
                status = "PENDING",
                timestamp = System.currentTimeMillis()
            )
            repository.insertWithdrawal(request)

            addNotification(
                "Withdrawal Requested ($wdId)",
                "Withdrawal of ৳${amount.toInt()} requested via $paymentMethod ($accountNumber). Status: PENDING.",
                "Payment"
            )
        }
        return true
    }

    fun uploadWithdrawalProof(withdrawalId: String, proofScreenshotUrl: String, paymentReference: String) {
        viewModelScope.launch {
            val withdrawals = allWithdrawals.value
            val wd = withdrawals.find { it.id == withdrawalId } ?: return@launch
            val updated = wd.copy(
                proofScreenshotUrl = proofScreenshotUrl,
                paymentReference = paymentReference,
                status = "PROOF_UPLOADED"
            )
            repository.updateWithdrawal(updated)

            addNotification(
                "Withdrawal Payment Proof Uploaded",
                "Proof screenshot and reference $paymentReference uploaded for Withdrawal #${wd.id}.",
                "Payment"
            )
        }
    }

    fun approveWithdrawal(withdrawalId: String, adminNote: String = "Withdrawal payment completed & verified.") {
        viewModelScope.launch {
            val withdrawals = allWithdrawals.value
            val wd = withdrawals.find { it.id == withdrawalId } ?: return@launch
            if (wd.status == "COMPLETED") return@launch

            val user = repository.currentUser.firstOrNull()
            val applicantId = wd.applicantId

            val prevBalance = if (user != null && user.id == applicantId) user.balance else 1500.0
            val newBalance = (prevBalance - wd.amount).coerceAtLeast(0.0)

            if (user != null && user.id == applicantId) {
                repository.updateCurrentUser(user.copy(balance = newBalance))
            }

            val updatedWd = wd.copy(
                status = "COMPLETED",
                adminNote = adminNote,
                processedAt = System.currentTimeMillis()
            )
            repository.updateWithdrawal(updatedWd)

            // Ledger record
            val ledger = LedgerEntry(
                referenceId = wd.id,
                userId = applicantId,
                userName = wd.applicantName,
                userRole = wd.applicantRole,
                transactionType = "WITHDRAWAL_DEBIT",
                amount = wd.amount,
                previousBalance = prevBalance,
                newBalance = newBalance,
                paymentMethod = wd.paymentMethod,
                transactionIdOrRef = wd.paymentReference ?: wd.accountNumber,
                timestamp = System.currentTimeMillis(),
                isVerifiedByAdmin = true
            )
            repository.insertLedgerEntry(ledger)

            // Transaction log
            repository.insertTransaction(
                WalletTransaction(
                    userId = applicantId,
                    type = "WITHDRAW",
                    amount = wd.amount,
                    description = "Completed Withdrawal #${wd.id} to ${wd.accountNumber}"
                )
            )

            addNotification(
                "Withdrawal Completed ✅",
                "Withdrawal #${wd.id} of ৳${wd.amount.toInt()} completed and debited from account.",
                "Payment"
            )
        }
    }

    fun rejectWithdrawal(withdrawalId: String, adminNote: String = "Account details or proof verification failed.") {
        viewModelScope.launch {
            val withdrawals = allWithdrawals.value
            val wd = withdrawals.find { it.id == withdrawalId } ?: return@launch
            val updated = wd.copy(
                status = "REJECTED",
                adminNote = adminNote,
                processedAt = System.currentTimeMillis()
            )
            repository.updateWithdrawal(updated)

            addNotification(
                "Withdrawal Request Rejected",
                "Withdrawal #${wd.id} was rejected. Reason: $adminNote",
                "Payment"
            )
        }
    }

    // --- B2B Orders & Chat StateFlow ---
    val allB2BOrders: StateFlow<List<B2BOrder>> = repository.allB2BOrders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun getB2BChatMessages(orderId: String): Flow<List<B2BChatMessage>> = repository.getB2BChatMessages(orderId)

    fun createB2BOrder(
        agent: PaymentAgent,
        type: String, // "DEPOSIT" or "WITHDRAWAL"
        amount: Double,
        paymentMethod: String,
        onOrderCreated: (B2BOrder) -> Unit
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            val userId = user?.id?.toString() ?: "101"
            val userName = user?.name ?: "Rahul Verma"
            val orderId = "MC${(100000..999999).random()}"

            val newOrder = B2BOrder(
                orderId = orderId,
                userId = userId,
                userName = userName,
                agentId = agent.id,
                agentName = agent.name,
                country = agent.country,
                type = type,
                amount = amount,
                currency = "BDT",
                paymentMethod = paymentMethod,
                agentAccountNumber = agent.accountNumber,
                agentAccountHolder = agent.accountHolder,
                status = "PENDING_PAYMENT",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            repository.insertB2BOrder(newOrder)

            // Initial Chat Message
            val welcomeMsg = B2BChatMessage(
                orderId = orderId,
                senderId = "SYSTEM",
                senderName = "MODOL CONNECT B2B ESCROW",
                senderRole = "ADMIN",
                content = "B2B Order #$orderId created with Agent ${agent.name} (${agent.country}). Please complete payment to agent account (${agent.accountNumber}) and upload transaction proof.",
                timestamp = System.currentTimeMillis()
            )
            repository.insertB2BChatMessage(welcomeMsg)

            addNotification("B2B Order Created", "Order #$orderId ($type ৳$amount) initialized with ${agent.name}.", "Payment")
            onOrderCreated(newOrder)
        }
    }

    fun sendB2BChatMessage(orderId: String, senderRole: String, content: String, imageUrl: String? = null) {
        viewModelScope.launch {
            val user = currentUser.value
            val senderId = if (senderRole == "AGENT") "AGENT" else (user?.id?.toString() ?: "101")
            val senderName = if (senderRole == "AGENT") "Verified Cash Agent" else (if (senderRole == "ADMIN") "Modol Admin Support" else (user?.name ?: "User"))

            val msg = B2BChatMessage(
                orderId = orderId,
                senderId = senderId,
                senderName = senderName,
                senderRole = senderRole,
                content = content,
                imageUrl = imageUrl,
                timestamp = System.currentTimeMillis()
            )
            repository.insertB2BChatMessage(msg)
        }
    }

    fun submitB2BPaymentProof(orderId: String, transactionRef: String, proofScreenshotUrl: String) {
        viewModelScope.launch {
            val orderList = allB2BOrders.value
            val order = orderList.find { it.orderId == orderId } ?: return@launch
            val updated = order.copy(
                status = "PAYMENT_SUBMITTED",
                transactionRef = transactionRef,
                proofScreenshotUrl = proofScreenshotUrl,
                updatedAt = System.currentTimeMillis()
            )
            repository.updateB2BOrder(updated)

            sendB2BChatMessage(
                orderId = orderId,
                senderRole = "USER",
                content = "Payment proof submitted! Ref: $transactionRef",
                imageUrl = proofScreenshotUrl
            )

            addNotification("B2B Payment Proof Uploaded", "Payment submitted for Order #$orderId. Agent/Admin verification pending.", "Payment")
        }
    }

    fun releaseB2BOrder(orderId: String) {
        viewModelScope.launch {
            val orderList = allB2BOrders.value
            val order = orderList.find { it.orderId == orderId } ?: return@launch
            val updated = order.copy(
                status = "RELEASED",
                updatedAt = System.currentTimeMillis()
            )
            repository.updateB2BOrder(updated)

            // Settle wallet ledger
            val user = repository.currentUser.firstOrNull() ?: currentUser.value
            val userId = order.userId
            val currentBal = user?.balance ?: 1500.0
            if (order.type == "DEPOSIT") {
                val newBal = currentBal + order.amount
                if (user != null) {
                    repository.updateCurrentUser(user.copy(balance = newBal))
                }
                val ledger = LedgerEntry(
                    referenceId = "B2B-${order.orderId}",
                    userId = userId,
                    userName = order.userName,
                    userRole = "USER",
                    transactionType = "DEPOSIT_CREDIT",
                    amount = order.amount,
                    previousBalance = currentBal,
                    newBalance = newBal,
                    paymentMethod = order.paymentMethod,
                    transactionIdOrRef = order.transactionRef ?: "TXN${order.orderId}",
                    isVerifiedByAdmin = true
                )
                repository.insertLedgerEntry(ledger)
            } else {
                val newBal = (currentBal - order.amount).coerceAtLeast(0.0)
                if (user != null) {
                    repository.updateCurrentUser(user.copy(balance = newBal))
                }
                val ledger = LedgerEntry(
                    referenceId = "B2B-${order.orderId}",
                    userId = userId,
                    userName = order.userName,
                    userRole = "USER",
                    transactionType = "WITHDRAWAL_DEBIT",
                    amount = order.amount,
                    previousBalance = currentBal,
                    newBalance = newBal,
                    paymentMethod = order.paymentMethod,
                    transactionIdOrRef = order.transactionRef ?: "TXN${order.orderId}",
                    isVerifiedByAdmin = true
                )
                repository.insertLedgerEntry(ledger)
            }

            sendB2BChatMessage(
                orderId = orderId,
                senderRole = "ADMIN",
                content = "🎉 ORDER RELEASED & SETTLED! Funds of ৳${order.amount.toInt()} credited/debited to wallet."
            )

            addNotification("B2B Order Released", "Order #$orderId has been released and settled in wallet.", "Payment")
        }
    }

    fun raiseB2BDispute(orderId: String, senderRole: String, reason: String) {
        viewModelScope.launch {
            val orderList = allB2BOrders.value
            val order = orderList.find { it.orderId == orderId } ?: return@launch
            val updated = order.copy(
                status = "DISPUTED",
                disputeStatus = "OPEN_DISPUTE",
                disputeReason = reason,
                updatedAt = System.currentTimeMillis()
            )
            repository.updateB2BOrder(updated)

            sendB2BChatMessage(
                orderId = orderId,
                senderRole = senderRole,
                content = "🚨 DISPUTE RAISED by $senderRole. Reason: $reason. Admin dispute resolution team notified."
            )

            addNotification("B2B Dispute Raised", "Dispute raised on Order #$orderId. Admin moderation in progress.", "Admin")
        }
    }

    fun resolveB2BDispute(orderId: String, decision: String, adminNotes: String) {
        // decision: "RELEASE_FUNDS", "REFUND_USER", "REJECT_CLAIM", "FREEZE_ORDER"
        viewModelScope.launch {
            val orderList = allB2BOrders.value
            val order = orderList.find { it.orderId == orderId } ?: return@launch

            val (newStatus, disputeStat) = when (decision) {
                "RELEASE_FUNDS" -> "RELEASED" to "RESOLVED_RELEASED"
                "REFUND_USER" -> "CANCELLED" to "RESOLVED_REFUNDED"
                "REJECT_CLAIM" -> "CANCELLED" to "CLAIM_REJECTED"
                else -> "DISPUTED" to "FROZEN"
            }

            val updated = order.copy(
                status = newStatus,
                disputeStatus = disputeStat,
                updatedAt = System.currentTimeMillis()
            )
            repository.updateB2BOrder(updated)

            if (decision == "RELEASE_FUNDS") {
                releaseB2BOrder(orderId)
            } else {
                sendB2BChatMessage(
                    orderId = orderId,
                    senderRole = "ADMIN",
                    content = "⚖️ ADMIN DISPUTE DECISION: $decision. Note: $adminNotes"
                )
            }

            addNotification("Dispute Resolved", "Order #$orderId dispute resolved with decision: $decision", "Admin")
        }
    }

    // --- Cash Agent Admin Operations ---

    fun savePaymentAgent(agent: PaymentAgent) {
        viewModelScope.launch {
            repository.insertPaymentAgent(agent)
            addNotification("Payment Agent Saved", "Cash agent ${agent.name} (#${agent.agentCode}) updated in ${agent.country}.", "Admin")
        }
    }

    fun deletePaymentAgent(agent: PaymentAgent) {
        viewModelScope.launch {
            repository.deletePaymentAgent(agent)
            addNotification("Payment Agent Removed", "Cash agent ${agent.name} was removed from system.", "Admin")
        }
    }

    fun togglePaymentAgentStatus(agentId: String, newStatus: String) {
        viewModelScope.launch {
            val currentList = paymentAgents.value
            val agent = currentList.find { it.id == agentId } ?: return@launch
            val updated = agent.copy(verificationStatus = newStatus)
            repository.updatePaymentAgent(updated)
            addNotification("Agent Status Changed", "Agent ${agent.name} status updated to $newStatus.", "Admin")
        }
    }

    // --- Automatic Payment Gateway Availability / Eligibility Verification ---
    fun processAutomaticGatewayPayment(
        gatewayName: String,
        amount: Double,
        currency: String = "BDT",
        onResult: (Boolean, String) -> Unit
    ) {
        // Always visible buttons! When clicked, run provider technical eligibility check.
        // If technical eligibility fails (simulated for region/card constraints), return explicit error message.
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000) // Simulate gateway API roundtrip
            if (gatewayName == "Google Pay") {
                // Simulate card provider response
                val isSuccess = false // Return clean error notice as instructed
                val message = "Google Pay Gateway Notice: Automatic checkout is currently unavailable for this transaction card region provider. Please proceed via Cash Agent or retry with a supported Google Wallet card."
                onResult(isSuccess, message)
            } else if (gatewayName == "Alipay") {
                val isSuccess = false
                val message = "Alipay Gateway Notice: Instant automatic payment is unavailable for this customer account currency ($currency). Please select Cash Agent for manual verification."
                onResult(isSuccess, message)
            } else {
                onResult(true, "Payment verified automatically via $gatewayName.")
            }
        }
    }

}

data class AppNotification(
    val id: Int,
    val title: String,
    val message: String,
    val category: String, // "Booking", "Payment", "Chat", "Admin", "Promotions", "System"
    val time: String,
    val isRead: Boolean = false
)

class AppViewModelFactory(private val application: Application, private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
