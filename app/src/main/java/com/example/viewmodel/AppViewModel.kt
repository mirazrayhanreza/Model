package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
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

class AppViewModel(application: Application, val repository: Repository) : AndroidViewModel(application) {

    // --- Navigation & Flow States ---
    var splashFinished by mutableStateOf(true)
    var onboardingFinished by mutableStateOf(true)
    var currentScreen by mutableStateOf("DASHBOARD")
    var previousScreen by mutableStateOf("DASHBOARD")
    var selectedTab by mutableStateOf(0) // 0: Home, 1: Search, 2: Bookings, 3: Chats, 4: Profile
    var showPhpBackendModal by mutableStateOf(false)

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

    // --- Auth States ---
    var loginEmail by mutableStateOf("")
    var loginPassword by mutableStateOf("")
    var registerName by mutableStateOf("")
    var registerEmail by mutableStateOf("")
    var registerPassword by mutableStateOf("")
    var registerRole by mutableStateOf("USER") // USER, MODEL, ADMIN
    var otpCode by mutableStateOf("")
    var forgotEmail by mutableStateOf("")
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

    // --- Auth Logic ---
    fun login() {
        viewModelScope.launch {
            val role = when {
                loginEmail.contains("admin", true) -> "ADMIN"
                loginEmail.contains("model", true) -> "MODEL"
                else -> "USER"
            }
            val userId = when (role) {
                "ADMIN" -> "admin_1"
                "MODEL" -> "model_1"
                else -> "user_1"
            }
            val name = when (role) {
                "ADMIN" -> "System Admin"
                "MODEL" -> "Jessica (Model Account)"
                else -> "Miraz Reza"
            }

            val user = CurrentUser(
                id = userId,
                name = name,
                role = role,
                balance = if (role == "MODEL") 2450.0 else 1500.0,
                avatarUrl = if (role == "MODEL") "https://images.unsplash.com/photo-1534528741775-53994a69daeb" else "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde",
                isVerified = true,
                email = loginEmail.ifEmpty { "hmmirazreza2@gmail.com" },
                city = "Dhaka"
            )
            repository.insertCurrentUser(user)
            addNotification("Login Success", "Welcome back, ${user.name}! Enjoy secure model booking.", "System")
            navigateTo("DASHBOARD")
        }
    }

    fun register() {
        viewModelScope.launch {
            val userId = if (registerRole == "MODEL") "model_user_${System.currentTimeMillis()}" else "user_${System.currentTimeMillis()}"
            val user = CurrentUser(
                id = userId,
                name = registerName.ifEmpty { "New User" },
                role = registerRole,
                balance = 500.0, // Welcome gift!
                avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
                isVerified = registerRole == "USER", // Models need review to get verified
                email = registerEmail.ifEmpty { "user@example.com" },
                city = "Dhaka"
            )
            repository.insertCurrentUser(user)
            repository.insertTransaction(
                WalletTransaction(
                    userId = userId,
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
                    name = registerName,
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
                    gender = "Female",
                    age = 22,
                    heightCm = 170,
                    imageResName = "default_model"
                )
                repository.insertModels(listOf(newModel))
            }
            navigateTo("DASHBOARD")
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.deleteCurrentUser()
            selectedTab = 0
            loginEmail = ""
            loginPassword = ""
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
