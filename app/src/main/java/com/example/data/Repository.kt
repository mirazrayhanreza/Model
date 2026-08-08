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
                    imageResName = "jessica"
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
                    imageResName = "nusrat"
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
                    imageResName = "tania"
                ),
                ModelProfile(
                    id = 4,
                    name = "Maya",
                    rating = 4.7f,
                    reviewCount = 110,
                    location = "Rajshahi",
                    isOnline = true,
                    isVerified = true,
                    bio = "Artistic and highly expressive model. Passionate about creative conceptual shoots, fitness, and fashion magazines.",
                    skills = "Creative Posing, Fitness, Glamour, Artistic",
                    languages = "English, Bengali",
                    services = "Dating, Photoshoot, Travel, Dinner",
                    hourlyRate = 110,
                    availabilityDays = "Sun, Mon, Tue, Wed, Thu",
                    gender = "Female",
                    age = 23,
                    heightCm = 170,
                    imageResName = "maya"
                ),
                ModelProfile(
                    id = 5,
                    name = "Riya",
                    rating = 4.5f,
                    reviewCount = 75,
                    location = "Khulna",
                    isOnline = false,
                    isVerified = true,
                    bio = "Bubbly and enthusiastic influencer and model. Available for corporate events, print catalogs, and promotional brand campaigns.",
                    skills = "Influencer Marketing, Hosting, Commercials, Catalog",
                    languages = "English, Bengali",
                    services = "Dating, Photoshoot, Dinner, Others",
                    hourlyRate = 100,
                    availabilityDays = "Fri, Sat",
                    gender = "Female",
                    age = 20,
                    heightCm = 167,
                    imageResName = "riya"
                ),
                ModelProfile(
                    id = 6,
                    name = "Liza",
                    rating = 4.6f,
                    reviewCount = 90,
                    location = "Barishal",
                    isOnline = true,
                    isVerified = true,
                    bio = "Experienced international model. Versatile in catalog styling, ramp, commercial video advertisements, and prints.",
                    skills = "Ramp, Catalog, Voice acting, Video Ad",
                    languages = "English, Bengali",
                    services = "Photoshoot, Dinner, Travel, Others",
                    hourlyRate = 115,
                    availabilityDays = "Sun, Tue, Thu, Sat",
                    gender = "Female",
                    age = 25,
                    heightCm = 171,
                    imageResName = "liza"
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
