package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "models")
data class ModelProfile(
    @PrimaryKey val id: Int,
    val name: String,
    val rating: Float,
    val reviewCount: Int,
    val location: String,
    val isOnline: Boolean,
    val isVerified: Boolean,
    val bio: String,
    val skills: String, // Comma separated
    val languages: String, // Comma separated
    val services: String, // Comma separated
    val hourlyRate: Int,
    val availabilityDays: String, // Comma separated
    val gender: String,
    val age: Int,
    val heightCm: Int,
    val imageResName: String // Holds local asset drawable reference identifier
)

@Entity(tableName = "current_user")
data class CurrentUser(
    @PrimaryKey val id: String,
    val name: String,
    val role: String, // "USER", "MODEL", "ADMIN"
    val balance: Double,
    val avatarUrl: String,
    val isVerified: Boolean,
    val email: String,
    val city: String = "Dhaka"
)

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val modelId: Int,
    val modelName: String,
    val modelPhoto: String,
    val date: String,
    val time: String,
    val serviceType: String,
    val durationHours: Int,
    val location: String,
    val notes: String,
    val totalPrice: Double,
    val status: String, // "PENDING", "PAYMENT_RECEIVED", "ACCEPTED", "REJECTED", "IN_PROGRESS", "PROOF_UPLOADED", "USER_CONFIRMED", "ADMIN_REVIEW", "PAYMENT_RELEASED", "COMPLETED", "CANCELLED", "REFUNDED"
    val paymentStatus: String, // "UNPAID", "ESCROW_HELD", "RELEASED", "REFUNDED"
    val paymentMethod: String,
    val timestamp: Long = System.currentTimeMillis(),
    val proofSelfieUrl: String? = null,
    val proofPhotos: String? = null,
    val proofVideoUrl: String? = null,
    val proofGpsLocation: String? = null,
    val proofNotes: String? = null,
    val proofSubmittedTime: Long = 0L,
    val modelEarnings: Double = 0.0,
    val platformFee: Double = 0.0,
    val agentFee: Double = 0.0,
    val disputeReason: String? = null,
    val disputeStatus: String? = null, // "NONE", "RAISED", "RESOLVED"
    val userRating: Int = 0,
    val userFeedback: String? = null
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderId: String,
    val receiverId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: String? = null,
    val isRead: Boolean = false
)

@Entity(tableName = "favorites")
data class FavoriteModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val modelId: Int
)

@Entity(tableName = "wallet_transactions")
data class WalletTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val type: String, // "DEPOSIT", "WITHDRAW", "PAYMENT", "EARNING"
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val description: String
)

@Entity(tableName = "model_reviews")
data class ModelReview(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val modelId: Int,
    val reviewerName: String,
    val reviewerAvatar: String,
    val rating: Int,
    val comment: String,
    val date: String
)

data class CashCollectionRequest(
    val id: String,
    val agentName: String,
    val amount: Double,
    val bookingId: String,
    val status: String, // "PENDING", "PAID"
    val userEmail: String = "client@example.com",
    val date: String = "18 May 2025",
    val receiptPhotoUrl: String? = null
)

