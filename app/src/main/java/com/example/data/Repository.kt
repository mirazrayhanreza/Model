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
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                val repo = Repository(db)
                INSTANCE = repo
                repo
            }
        }
    }
}
