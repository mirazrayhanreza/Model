package com.example.data

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class Repository(private val db: AppDatabase) {

    val allModels: Flow<List<ModelProfile>> = db.modelDao().getAllModels()
    val currentUser: Flow<CurrentUser?> = db.currentUserDao().getCurrentUser()
    val allBookings: Flow<List<Booking>> = db.bookingDao().getAllBookings()

    fun getBookingsForUser(userId: String): Flow<List<Booking>> = db.bookingDao().getBookingsForUser(userId)
    fun getBookingsForModel(modelId: Int): Flow<List<Booking>> = db.bookingDao().getBookingsForModel(modelId)
    fun getModelById(id: Int): Flow<ModelProfile?> = db.modelDao().getModelById(id)
    suspend fun getModelByIdSync(id: Int): ModelProfile? = db.modelDao().getModelByIdSync(id)

    fun getMessagesBetween(user1: String, user2: String): Flow<List<ChatMessage>> = db.chatDao().getMessagesBetween(user1, user2)
    val allMessages: Flow<List<ChatMessage>> = db.chatDao().getAllMessages()

    fun getFavoritesForUser(userId: String): Flow<List<FavoriteModel>> = db.favoriteDao().getFavoritesForUser(userId)
    fun getReviewsForModel(modelId: Int): Flow<List<ModelReview>> = db.reviewDao().getReviewsForModel(modelId)

    suspend fun insertModels(models: List<ModelProfile>) = db.modelDao().insertModels(models)
    suspend fun updateModel(model: ModelProfile) = db.modelDao().updateModel(model)

    suspend fun insertCurrentUser(user: CurrentUser) = db.currentUserDao().insertCurrentUser(user)
    suspend fun updateCurrentUser(user: CurrentUser) = db.currentUserDao().updateCurrentUser(user)
    suspend fun deleteCurrentUser() = db.currentUserDao().deleteCurrentUser()

    suspend fun insertBooking(booking: Booking): Long = db.bookingDao().insertBooking(booking)
    suspend fun updateBooking(booking: Booking) = db.bookingDao().updateBooking(booking)
    suspend fun deleteBookingById(id: Int) = db.bookingDao().deleteBookingById(id)

    suspend fun insertMessage(message: ChatMessage) = db.chatDao().insertMessage(message)

    suspend fun insertFavorite(userId: String, modelId: Int) = db.favoriteDao().insertFavorite(FavoriteModel(userId = userId, modelId = modelId))
    suspend fun deleteFavorite(userId: String, modelId: Int) = db.favoriteDao().deleteFavorite(userId, modelId)

    fun getTransactionsForUser(userId: String): Flow<List<WalletTransaction>> = db.walletDao().getTransactionsForUser(userId)
    suspend fun insertTransaction(tx: WalletTransaction) = db.walletDao().insertTransaction(tx)

    suspend fun insertReview(review: ModelReview) = db.reviewDao().insertReview(review)

    // --- Deposits ---
    val allDeposits: Flow<List<DepositRequest>> = db.depositDao().getAllDeposits()
    fun getDepositsForUser(userId: String): Flow<List<DepositRequest>> = db.depositDao().getDepositsForUser(userId)
    suspend fun insertDeposit(deposit: DepositRequest) = db.depositDao().insertDeposit(deposit)
    suspend fun updateDeposit(deposit: DepositRequest) = db.depositDao().updateDeposit(deposit)

    // --- Withdrawals ---
    val allWithdrawals: Flow<List<WithdrawalRequest>> = db.withdrawalDao().getAllWithdrawals()
    fun getWithdrawalsForUser(applicantId: String): Flow<List<WithdrawalRequest>> = db.withdrawalDao().getWithdrawalsForUser(applicantId)
    suspend fun insertWithdrawal(withdrawal: WithdrawalRequest) = db.withdrawalDao().insertWithdrawal(withdrawal)
    suspend fun updateWithdrawal(withdrawal: WithdrawalRequest) = db.withdrawalDao().updateWithdrawal(withdrawal)

    // --- Ledger ---
    val allLedgerEntries: Flow<List<LedgerEntry>> = db.ledgerDao().getAllLedgerEntries()
    fun getLedgerEntriesForUser(userId: String): Flow<List<LedgerEntry>> = db.ledgerDao().getLedgerEntriesForUser(userId)
    suspend fun insertLedgerEntry(entry: LedgerEntry) = db.ledgerDao().insertLedgerEntry(entry)

    // --- Payment Agents ---
    val allPaymentAgents: Flow<List<PaymentAgent>> = db.paymentAgentDao().getAllPaymentAgents()
    fun getPaymentAgentsByCountry(country: String): Flow<List<PaymentAgent>> = db.paymentAgentDao().getPaymentAgentsByCountry(country)
    suspend fun insertPaymentAgent(agent: PaymentAgent) = db.paymentAgentDao().insertPaymentAgent(agent)
    suspend fun updatePaymentAgent(agent: PaymentAgent) = db.paymentAgentDao().updatePaymentAgent(agent)
    suspend fun deletePaymentAgent(agent: PaymentAgent) = db.paymentAgentDao().deletePaymentAgent(agent)

    // --- B2B Orders & Chat ---
    val allB2BOrders: Flow<List<B2BOrder>> = db.b2bOrderDao().getAllB2BOrders()
    fun getB2BOrdersForUser(userId: String): Flow<List<B2BOrder>> = db.b2bOrderDao().getB2BOrdersForUser(userId)
    fun getB2BOrderById(orderId: String): Flow<B2BOrder?> = db.b2bOrderDao().getB2BOrderById(orderId)
    suspend fun insertB2BOrder(order: B2BOrder) = db.b2bOrderDao().insertB2BOrder(order)
    suspend fun updateB2BOrder(order: B2BOrder) = db.b2bOrderDao().updateB2BOrder(order)

    fun getB2BChatMessages(orderId: String): Flow<List<B2BChatMessage>> = db.b2bChatDao().getChatMessagesForOrder(orderId)
    suspend fun insertB2BChatMessage(msg: B2BChatMessage) = db.b2bChatDao().insertChatMessage(msg)



    // Pre-populates the database with models, reviews, default user, and chat histories
    suspend fun prePopulateIfEmpty() {
        val models = db.modelDao().getAllModels().firstOrNull()
        if (models.isNullOrEmpty()) {
            val defaultModels = listOf(
                ModelProfile(
                    id = 1,
                    name = "Jessica",
                    rating = 4.8f,
                    reviewCount = 128,
                    location = "Dhaka",
                    isOnline = true,
                    isVerified = true,
                    bio = "Hi, I'm Jessica. I am a professional model. I love to meet new people and travel. Specialized in editorial, fashion catalog, and commercial photoshoots.",
                    skills = "Runway, High Fashion, Fitting, Editorial",
                    languages = "English, Bengali, Hindi",
                    services = "Dating, Photoshoot, Dinner, Travel, Outcall, Others",
                    hourlyRate = 120,
                    availabilityDays = "Sun, Mon, Tue, Wed, Thu, Fri, Sat",
                    gender = "Female",
                    age = 22,
                    heightCm = 172,
                    imageResName = "jessica",
                    country = "Bangladesh"
                ),
                ModelProfile(
                    id = 2,
                    name = "Nusrat",
                    rating = 4.7f,
                    reviewCount = 95,
                    location = "Chittagong",
                    isOnline = true,
                    isVerified = true,
                    bio = "Professional editorial and runway model with 3+ years of experience in premium brand promotions and television commercial shoots.",
                    skills = "Commercials, Runway, Styling, Makeover",
                    languages = "English, Bengali, Chittagonian",
                    services = "Photoshoot, Travel, Dinner, Others",
                    hourlyRate = 140,
                    availabilityDays = "Mon, Tue, Wed, Thu, Fri",
                    gender = "Female",
                    age = 24,
                    heightCm = 168,
                    imageResName = "nusrat",
                    country = "Bangladesh"
                ),
                ModelProfile(
                    id = 3,
                    name = "Tania",
                    rating = 4.6f,
                    reviewCount = 80,
                    location = "Sylhet",
                    isOnline = false,
                    isVerified = true,
                    bio = "Freelance model specializing in casual wear, bridal makeovers, and commercial promotions. Loves aesthetic arts and traveling.",
                    skills = "Bridal, Makeup, Casual Wear, Editorial",
                    languages = "English, Bengali, Sylheti",
                    services = "Dating, Photoshoot, Dinner, Outcall",
                    hourlyRate = 130,
                    availabilityDays = "Fri, Sat, Sun",
                    gender = "Female",
                    age = 21,
                    heightCm = 175,
                    imageResName = "tania",
                    country = "Bangladesh"
                ),
                ModelProfile(
                    id = 4,
                    name = "Mei Ling",
                    rating = 4.9f,
                    reviewCount = 110,
                    location = "Shanghai",
                    isOnline = true,
                    isVerified = true,
                    bio = "International high fashion model based in Shanghai. Passionate about luxury brand editorials, runway shows, and artistic campaigns.",
                    skills = "High Fashion, Creative Posing, Runway, Luxury Editorial",
                    languages = "English, Mandarin, Cantonese",
                    services = "Photoshoot, Runway, Commercial, Travel",
                    hourlyRate = 160,
                    availabilityDays = "Sun, Mon, Tue, Wed, Thu",
                    gender = "Female",
                    age = 23,
                    heightCm = 178,
                    imageResName = "maya",
                    country = "China"
                ),
                ModelProfile(
                    id = 5,
                    name = "Aaliyah",
                    rating = 4.8f,
                    reviewCount = 142,
                    location = "Dubai",
                    isOnline = true,
                    isVerified = true,
                    bio = "Dubai-based luxury brand model & influencer. Specializing in high-end jewelry campaigns, commercial spots, and VIP events.",
                    skills = "Jewelry, VIP Hosting, Commercials, Influencer",
                    languages = "English, Arabic, French",
                    services = "Event Appearance, Photoshoot, Dinner, Travel",
                    hourlyRate = 180,
                    availabilityDays = "Fri, Sat, Sun, Mon",
                    gender = "Female",
                    age = 25,
                    heightCm = 174,
                    imageResName = "riya",
                    country = "UAE"
                ),
                ModelProfile(
                    id = 6,
                    name = "Sophia",
                    rating = 4.9f,
                    reviewCount = 195,
                    location = "New York",
                    isOnline = true,
                    isVerified = true,
                    bio = "New York fashion week runway & print model. Experienced in top magazine covers, designer collections, and video productions.",
                    skills = "NYFW Runway, Cover Model, Designer Fitting, Video Shoot",
                    languages = "English, Spanish",
                    services = "Runway, Photoshoot, Commercial, Travel",
                    hourlyRate = 190,
                    availabilityDays = "Mon, Tue, Wed, Thu, Fri, Sat",
                    gender = "Female",
                    age = 24,
                    heightCm = 179,
                    imageResName = "liza",
                    country = "USA"
                ),
                ModelProfile(
                    id = 7,
                    name = "Priya Sharma",
                    rating = 4.7f,
                    reviewCount = 88,
                    location = "Mumbai",
                    isOnline = true,
                    isVerified = true,
                    bio = "Mumbai commercial print model and video ad specialist. Enthusiastic about traditional fusion wear, catalog, and brand endorsement.",
                    skills = "Traditional Wear, Brand Endorsement, Catalog, Commercials",
                    languages = "English, Hindi, Marathi",
                    services = "Photoshoot, Commercial, Event Appearance, Dinner",
                    hourlyRate = 135,
                    availabilityDays = "Sun, Mon, Tue, Thu, Fri",
                    gender = "Female",
                    age = 22,
                    heightCm = 170,
                    imageResName = "jessica",
                    country = "India"
                ),
                ModelProfile(
                    id = 8,
                    name = "Gemma",
                    rating = 4.8f,
                    reviewCount = 104,
                    location = "London",
                    isOnline = false,
                    isVerified = true,
                    bio = "London-based editorial & portrait model with extensive portfolio in high street fashion, lifestyle shoots, and creative visual projects.",
                    skills = "High Street Fashion, Portrait, Editorial, Lookbook",
                    languages = "English",
                    services = "Photoshoot, Lookbook, Dinner, Outcall",
                    hourlyRate = 150,
                    availabilityDays = "Wed, Thu, Fri, Sat, Sun",
                    gender = "Female",
                    age = 23,
                    heightCm = 176,
                    imageResName = "tania",
                    country = "UK"
                )
            )
            db.modelDao().insertModels(defaultModels)

            // Insert standard reviews for model 1 (Jessica)
            val defaultReviews = listOf(
                ModelReview(
                    modelId = 1,
                    reviewerName = "Rashed H.",
                    reviewerAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde",
                    rating = 5,
                    comment = "She is very professional and friendly. I had a great time. Highly recommended.",
                    date = "18 May 2025"
                ),
                ModelReview(
                    modelId = 1,
                    reviewerName = "Tanvir Ahmed",
                    reviewerAvatar = "https://images.unsplash.com/photo-1599566150163-29194dcaad36",
                    rating = 4,
                    comment = "Very punctual and cooperative throughout the photoshoot. Excellent skills.",
                    date = "15 May 2025"
                ),
                ModelReview(
                    modelId = 2,
                    reviewerName = "Fahim A.",
                    reviewerAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
                    rating = 5,
                    comment = "Great attitude and highly professional. Excellent camera presence.",
                    date = "10 May 2025"
                ),
                ModelReview(
                    modelId = 3,
                    reviewerName = "Niaz Chowdhury",
                    reviewerAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
                    rating = 4,
                    comment = "Stunning performance in our commercial. Easy to work with.",
                    date = "05 May 2025"
                )
            )
            for (review in defaultReviews) {
                db.reviewDao().insertReview(review)
            }

            // Insert some initial default messages to show in Chat list
            val defaultMessages = listOf(
                ChatMessage(
                    senderId = "1", // Model Jessica
                    receiverId = "user_1",
                    senderName = "Jessica",
                    content = "Hey! I am available on May 20th for the photoshoot. Let me know if you want to proceed.",
                    timestamp = System.currentTimeMillis() - 7200000,
                    isRead = false
                ),
                ChatMessage(
                    senderId = "user_1",
                    receiverId = "1",
                    senderName = "Miraz",
                    content = "Sure Jessica! I am preparing the details and will send a booking soon.",
                    timestamp = System.currentTimeMillis() - 3600000,
                    isRead = true
                ),
                ChatMessage(
                    senderId = "2", // Model Nusrat
                    receiverId = "user_1",
                    senderName = "Nusrat",
                    content = "Hello, thanks for reaching out. Please send me the timing for dinner event.",
                    timestamp = System.currentTimeMillis() - 86400000,
                    isRead = true
                ),
                ChatMessage(
                    senderId = "user_1",
                    receiverId = "ADMIN_SUPPORT",
                    senderName = "Rahul Verma",
                    content = "Assalamu Alaikum Admin Support! I submitted a bKash payment for booking #101. Please confirm my escrow deposit status.",
                    timestamp = System.currentTimeMillis() - 7200000,
                    isRead = false
                ),
                ChatMessage(
                    senderId = "ADMIN_SUPPORT",
                    receiverId = "user_1",
                    senderName = "Modol Admin Support",
                    content = "Walaikum Assalam Rahul! We verified your bKash payment proof of ৳150. Your escrow booking is now active and protected.",
                    timestamp = System.currentTimeMillis() - 3600000,
                    isRead = true
                ),
                ChatMessage(
                    senderId = "1",
                    receiverId = "ADMIN_SUPPORT",
                    senderName = "Jessica",
                    content = "Hello Admin Team! I updated my profile bio and portfolio photos. Could you please review and verify my model account?",
                    timestamp = System.currentTimeMillis() - 5400000,
                    isRead = false
                ),
                ChatMessage(
                    senderId = "3",
                    receiverId = "ADMIN_SUPPORT",
                    senderName = "Tanvir Agent",
                    content = "Hello Admin, cash collection of ৳12,000 completed at Uttara branch. Transaction receipt uploaded for approval.",
                    timestamp = System.currentTimeMillis() - 1800000,
                    isRead = false
                )
            )
            for (msg in defaultMessages) {
                db.chatDao().insertMessage(msg)
            }

            // Insert sample initial bookings covering Escrow Workflow statuses & mockup cards
            val sampleBookings = listOf(
                Booking(
                    id = 101,
                    userId = "Rahul Verma",
                    modelId = 1,
                    modelName = "Jessica",
                    modelPhoto = "jessica",
                    date = "25 Jul 2026",
                    time = "08:00 PM",
                    serviceType = "Photoshoot",
                    durationHours = 3,
                    location = "Gulshan, Dhaka",
                    notes = "Booking request from client. Please accept or reject.",
                    totalPrice = 150.0,
                    status = "PAYMENT_RECEIVED",
                    paymentStatus = "ESCROW_HELD",
                    paymentMethod = "bKash",
                    userRating = 5,
                    userFeedback = "Premium Client • 4.9 (32 reviews)"
                ),
                Booking(
                    id = 102,
                    userId = "Arjun Kapoor",
                    modelId = 1,
                    modelName = "Jessica",
                    modelPhoto = "jessica",
                    date = "22 Jul 2026",
                    time = "10:00 PM",
                    serviceType = "Video Shoot",
                    durationHours = 2,
                    location = "Banani, Dhaka",
                    notes = "Started 1h ago • Commercial video promo",
                    totalPrice = 120.0,
                    status = "IN_PROGRESS",
                    paymentStatus = "ESCROW_HELD",
                    paymentMethod = "Stripe",
                    userRating = 5,
                    userFeedback = "4.8 (28 reviews)"
                ),
                Booking(
                    id = 103,
                    userId = "Pooja Singh",
                    modelId = 1,
                    modelName = "Jessica",
                    modelPhoto = "jessica",
                    date = "20 Jul 2026",
                    time = "08:00 PM",
                    serviceType = "Event Appearance",
                    durationHours = 4,
                    location = "Dhanmondi, Dhaka",
                    notes = "Gala evening appearance",
                    totalPrice = 200.0,
                    status = "PROOF_UPLOADED",
                    paymentStatus = "ESCROW_HELD",
                    paymentMethod = "bKash",
                    proofSelfieUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                    proofPhotos = "https://images.unsplash.com/photo-1517841905240-472988babdf9,https://images.unsplash.com/photo-1524504388940-b1c1722653e1",
                    proofVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    proofGpsLocation = "23.7461° N, 90.3742° E (Dhanmondi, Dhaka)",
                    proofNotes = "Event completed. Poses and client interaction delivered.",
                    proofSubmittedTime = System.currentTimeMillis() - 7200000,
                    userRating = 5,
                    userFeedback = "4.9 (45 reviews)"
                ),
                Booking(
                    id = 104,
                    userId = "Vikram Singh",
                    modelId = 1,
                    modelName = "Jessica",
                    modelPhoto = "jessica",
                    date = "18 Jul 2026",
                    time = "09:00 PM",
                    serviceType = "Private Booking",
                    durationHours = 3,
                    location = "Uttara, Dhaka",
                    notes = "Private catalog shoot",
                    totalPrice = 180.0,
                    status = "COMPLETED",
                    paymentStatus = "RELEASED",
                    paymentMethod = "bKash",
                    proofSelfieUrl = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1",
                    proofPhotos = "https://images.unsplash.com/photo-1534528741775-53994a69daeb,https://images.unsplash.com/photo-1517841905240-472988babdf9,https://images.unsplash.com/photo-1524504388940-b1c1722653e1",
                    proofGpsLocation = "23.8759° N, 90.3795° E (Uttara)",
                    proofNotes = "Awesome professional experience!",
                    proofSubmittedTime = System.currentTimeMillis() - 172800000,
                    userRating = 5,
                    userFeedback = "Rated 5.0"
                ),
                Booking(
                    id = 105,
                    userId = "Amit Patel",
                    modelId = 1,
                    modelName = "Jessica",
                    modelPhoto = "jessica",
                    date = "15 Jul 2026",
                    time = "07:00 PM",
                    serviceType = "Photoshoot",
                    durationHours = 2,
                    location = "Mirpur, Dhaka",
                    notes = "Outdoor shoot for magazine cover",
                    totalPrice = 140.0,
                    status = "ADMIN_REVIEW",
                    paymentStatus = "ESCROW_HELD",
                    paymentMethod = "bKash",
                    proofSelfieUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                    proofPhotos = "https://images.unsplash.com/photo-1517841905240-472988babdf9",
                    proofGpsLocation = "23.8223° N, 90.3654° E (Mirpur, Dhaka)",
                    proofNotes = "Under review by admin for escrow release.",
                    proofSubmittedTime = System.currentTimeMillis() - 86400000,
                    userRating = 5,
                    userFeedback = "4.7 (21 reviews)"
                )
            )
            for (b in sampleBookings) {
                db.bookingDao().insertBooking(b)
            }
        }

        // Check if there is an active logged-in user
        val currentUserVal = db.currentUserDao().getCurrentUser().firstOrNull()
        if (currentUserVal == null) {
            val user = CurrentUser(
                id = "user_1",
                name = "Miraz Reza",
                role = "USER", // Client mode
                balance = 1500.0,
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde",
                isVerified = true,
                email = "hmmirazreza2@gmail.com",
                city = "Dhaka"
            )
            db.currentUserDao().insertCurrentUser(user)

            // Insert matching wallet transaction
            db.walletDao().insertTransaction(
                WalletTransaction(
                    userId = "user_1",
                    type = "DEPOSIT",
                    amount = 1500.0,
                    description = "Initial Wallet Setup Bonus"
                )
            )

            // Seed initial deposit requests for admin review & ledger proof testing
            val sampleDeposits = listOf(
                DepositRequest(
                    id = "DEP-10025",
                    userId = "user_1",
                    userName = "Miraz Reza",
                    agentId = "AGENT-1024",
                    agentName = "Dhaka Central bKash Agent",
                    country = "Bangladesh",
                    paymentMethod = "bKash",
                    amount = 500.0,
                    currency = "BDT",
                    transactionId = "TXN987654321",
                    proofScreenshotUrl = "https://images.unsplash.com/photo-1556742049-0a670f4a4591",
                    userNote = "Deposit for upcoming photoshoot booking #101.",
                    status = "PENDING",
                    timestamp = System.currentTimeMillis() - 1800000
                ),
                DepositRequest(
                    id = "DEP-10024",
                    userId = "Rahul Verma",
                    userName = "Rahul Verma",
                    agentId = "AGENT-2048",
                    agentName = "Uttara Nagad Agent #2048",
                    country = "Bangladesh",
                    paymentMethod = "Nagad",
                    amount = 1000.0,
                    currency = "BDT",
                    transactionId = "TXN554433221",
                    proofScreenshotUrl = "https://images.unsplash.com/photo-1563013544-824ae1b704d3",
                    userNote = "Nagad cash deposit confirmed with agent.",
                    status = "APPROVED",
                    timestamp = System.currentTimeMillis() - 86400000,
                    adminNote = "Verified in Nagad merchant portal.",
                    reviewedByAdmin = "SuperAdmin",
                    reviewedAt = System.currentTimeMillis() - 82800000
                )
            )
            for (dep in sampleDeposits) {
                db.depositDao().insertDeposit(dep)
            }

            // Seed initial withdrawal requests
            val sampleWithdrawals = listOf(
                WithdrawalRequest(
                    id = "WD-50021",
                    applicantId = "model_1",
                    applicantName = "Jessica",
                    applicantRole = "MODEL",
                    amount = 350.0,
                    currency = "BDT",
                    paymentMethod = "bKash Personal",
                    accountNumber = "01712345678",
                    accountHolder = "Jessica Model",
                    proofScreenshotUrl = null,
                    status = "PENDING",
                    timestamp = System.currentTimeMillis() - 3600000
                ),
                WithdrawalRequest(
                    id = "WD-50020",
                    applicantId = "AGENT-1024",
                    applicantName = "Dhaka Agent #1024",
                    applicantRole = "AGENT",
                    amount = 800.0,
                    currency = "BDT",
                    paymentMethod = "City Bank Transfer",
                    accountNumber = "2051234567890",
                    accountHolder = "Modol Cash Agent Ltd",
                    proofScreenshotUrl = "https://images.unsplash.com/photo-1559526324-4b87b5e36e44",
                    paymentReference = "FT20260809-901",
                    status = "PROOF_UPLOADED",
                    timestamp = System.currentTimeMillis() - 172800000,
                    adminNote = "Bank transfer advice uploaded.",
                    processedAt = System.currentTimeMillis() - 86400000
                )
            )
            for (wd in sampleWithdrawals) {
                db.withdrawalDao().insertWithdrawal(wd)
            }

            // Seed initial verified Ledger Entries
            val sampleLedger = listOf(
                LedgerEntry(
                    referenceId = "DEP-10024",
                    userId = "Rahul Verma",
                    userName = "Rahul Verma",
                    userRole = "USER",
                    transactionType = "DEPOSIT_CREDIT",
                    amount = 1000.0,
                    previousBalance = 0.0,
                    newBalance = 1000.0,
                    paymentMethod = "Nagad",
                    transactionIdOrRef = "TXN554433221",
                    timestamp = System.currentTimeMillis() - 82800000,
                    isVerifiedByAdmin = true
                )
            )
            for (le in sampleLedger) {
                db.ledgerDao().insertLedgerEntry(le)
            }
        }

        val existingAgents = db.paymentAgentDao().getAllPaymentAgents().firstOrNull()
        if (existingAgents.isNullOrEmpty()) {
            val defaultAgents = listOf(
                PaymentAgent(id = "AGENT-1024", name = "Dhaka Central Cash Agent #1024", agentCode = "1024", country = "Bangladesh", phone = "01711223344", paymentMethod = "bKash", accountNumber = "01711223344", accountHolder = "Dhaka Agent Ltd", commissionRate = 1.5, minLimit = 500.0, maxLimit = 100000.0, availableBalance = 50000.0, verificationStatus = "VERIFIED"),
                PaymentAgent(id = "AGENT-2048", name = "Uttara Nagad Agent #2048", agentCode = "2048", country = "Bangladesh", phone = "01822334455", paymentMethod = "Nagad", accountNumber = "01822334455", accountHolder = "Uttara Cash Express", commissionRate = 1.0, minLimit = 300.0, maxLimit = 50000.0, availableBalance = 30000.0, verificationStatus = "VERIFIED"),
                PaymentAgent(id = "AGENT-3090", name = "City Bank Fast Pay Agent #3090", agentCode = "3090", country = "Bangladesh", phone = "01933445566", paymentMethod = "Bank Transfer", accountNumber = "2051234567890", accountHolder = "Modol Cash Escrow", commissionRate = 1.2, minLimit = 1000.0, maxLimit = 250000.0, availableBalance = 150000.0, verificationStatus = "VERIFIED"),
                PaymentAgent(id = "AGENT-8801", name = "Beijing Alipay Agent #8801", agentCode = "8801", country = "China", phone = "+8613800138000", paymentMethod = "Alipay", accountNumber = "agent8801@alipay.cn", accountHolder = "Beijing Modol Agent Co", commissionRate = 0.8, minLimit = 100.0, maxLimit = 50000.0, availableBalance = 80000.0, verificationStatus = "VERIFIED"),
                PaymentAgent(id = "AGENT-8802", name = "Shanghai WeChat Agent #8802", agentCode = "8802", country = "China", phone = "+8613900139000", paymentMethod = "WeChat Pay", accountNumber = "wxid_shanghai_agent8802", accountHolder = "Shanghai Modol Pay", commissionRate = 0.8, minLimit = 100.0, maxLimit = 50000.0, availableBalance = 60000.0, verificationStatus = "VERIFIED"),
                PaymentAgent(id = "AGENT-3001", name = "New York Zelle Agent #3001", agentCode = "3001", country = "USA", phone = "+12125550199", paymentMethod = "Zelle", accountNumber = "zelle@modolconnect.us", accountHolder = "US Cash Escrow Corp", commissionRate = 1.2, minLimit = 20.0, maxLimit = 10000.0, availableBalance = 40000.0, verificationStatus = "VERIFIED"),
                PaymentAgent(id = "AGENT-4001", name = "London Wise Agent #4001", agentCode = "4001", country = "UK", phone = "+442079460912", paymentMethod = "Wise", accountNumber = "wise@modolconnect.co.uk", accountHolder = "UK Agent Services Ltd", commissionRate = 1.0, minLimit = 10.0, maxLimit = 5000.0, availableBalance = 25000.0, verificationStatus = "VERIFIED"),
                PaymentAgent(id = "AGENT-5001", name = "Dubai Exchange Agent #5001", agentCode = "5001", country = "UAE", phone = "+97143214321", paymentMethod = "Bank Transfer", accountNumber = "AE123456789012345678", accountHolder = "Gulf Cash Agent LLC", commissionRate = 1.5, minLimit = 50.0, maxLimit = 20000.0, availableBalance = 70000.0, verificationStatus = "VERIFIED"),
                PaymentAgent(id = "AGENT-6001", name = "Mumbai Fast UPI Agent #6001", agentCode = "6001", country = "India", phone = "+919820098200", paymentMethod = "UPI", accountNumber = "agent6001@upi", accountHolder = "India Fast Agent Ltd", commissionRate = 0.5, minLimit = 200.0, maxLimit = 100000.0, availableBalance = 90000.0, verificationStatus = "VERIFIED")
            )
            for (agent in defaultAgents) {
                db.paymentAgentDao().insertPaymentAgent(agent)
            }
        }

        val existingB2BOrders = db.b2bOrderDao().getAllB2BOrders().firstOrNull()
        if (existingB2BOrders.isNullOrEmpty()) {
            val sampleOrder = B2BOrder(
                orderId = "MC102458",
                userId = "101",
                userName = "Rahul Verma",
                agentId = "AGENT-1024",
                agentName = "Dhaka Central Cash Agent #1024",
                country = "Bangladesh",
                type = "DEPOSIT",
                amount = 2000.0,
                currency = "BDT",
                paymentMethod = "bKash",
                agentAccountNumber = "01711223344",
                agentAccountHolder = "Dhaka Agent Ltd",
                status = "PAYMENT_SUBMITTED",
                proofScreenshotUrl = "https://images.unsplash.com/photo-1556742049-0a670f4a4591",
                transactionRef = "TXN98127301",
                createdAt = System.currentTimeMillis() - 3600000,
                updatedAt = System.currentTimeMillis() - 1800000
            )
            db.b2bOrderDao().insertB2BOrder(sampleOrder)

            val sampleMsgs = listOf(
                B2BChatMessage(
                    id = "MSG_1",
                    orderId = "MC102458",
                    senderId = "101",
                    senderName = "Rahul Verma",
                    senderRole = "USER",
                    content = "Hello Agent, I have sent ৳2,000 via bKash personal cash-in.",
                    timestamp = System.currentTimeMillis() - 3000000
                ),
                B2BChatMessage(
                    id = "MSG_2",
                    orderId = "MC102458",
                    senderId = "AGENT-1024",
                    senderName = "Dhaka Central Cash Agent #1024",
                    senderRole = "AGENT",
                    content = "Please upload the payment proof screenshot or mention Transaction ID.",
                    timestamp = System.currentTimeMillis() - 2500000
                ),
                B2BChatMessage(
                    id = "MSG_3",
                    orderId = "MC102458",
                    senderId = "101",
                    senderName = "Rahul Verma",
                    senderRole = "USER",
                    content = "Screenshot uploaded. Ref: TXN98127301",
                    imageUrl = "https://images.unsplash.com/photo-1556742049-0a670f4a4591",
                    timestamp = System.currentTimeMillis() - 1800000
                ),
                B2BChatMessage(
                    id = "MSG_4",
                    orderId = "MC102458",
                    senderId = "AGENT-1024",
                    senderName = "Dhaka Central Cash Agent #1024",
                    senderRole = "AGENT",
                    content = "Received. Checking statement with bKash API...",
                    timestamp = System.currentTimeMillis() - 1200000
                )
            )
            for (msg in sampleMsgs) {
                db.b2bChatDao().insertChatMessage(msg)
            }
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getInstance(context: Context): Repository {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "modol_connect_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                val repo = Repository(db)
                INSTANCE = repo
                repo
            }
        }
    }
}
