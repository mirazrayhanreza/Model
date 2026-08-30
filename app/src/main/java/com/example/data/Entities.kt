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
    val imageResName: String, // Holds local asset drawable reference identifier
    val country: String = "Bangladesh"
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

@Entity(tableName = "deposit_requests")
data class DepositRequest(
    @PrimaryKey val id: String, // e.g. "DEP-10025"
    val userId: String,
    val userName: String,
    val agentId: String,
    val agentName: String,
    val country: String = "Bangladesh",
    val paymentMethod: String, // "bKash", "Nagad", "Rocket", "Bank Transfer"
    val amount: Double,
    val currency: String = "BDT",
    val transactionId: String, // e.g. "TXN123456"
    val proofScreenshotUrl: String,
    val userNote: String? = null,
    val status: String = "PENDING", // PENDING, UNDER_REVIEW, APPROVED, REJECTED, CANCELLED
    val timestamp: Long = System.currentTimeMillis(),
    val adminNote: String? = null,
    val reviewedByAdmin: String? = null,
    val reviewedAt: Long? = null
)

@Entity(tableName = "withdrawal_requests")
data class WithdrawalRequest(
    @PrimaryKey val id: String, // e.g. "WD-50021"
    val applicantId: String,
    val applicantName: String,
    val applicantRole: String, // "USER", "MODEL", "AGENT"
    val amount: Double,
    val currency: String = "BDT",
    val paymentMethod: String,
    val accountNumber: String,
    val accountHolder: String = "",
    val proofScreenshotUrl: String? = null,
    val paymentReference: String? = null,
    val status: String = "PENDING", // PENDING, PROCESSING, PROOF_UPLOADED, UNDER_REVIEW, APPROVED, REJECTED, COMPLETED
    val timestamp: Long = System.currentTimeMillis(),
    val adminNote: String? = null,
    val processedAt: Long? = null
)

@Entity(tableName = "ledger_entries")
data class LedgerEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val referenceId: String, // e.g. "DEP-10025" or "WD-50021"
    val userId: String,
    val userName: String,
    val userRole: String,
    val transactionType: String, // "DEPOSIT_CREDIT", "WITHDRAWAL_DEBIT", "ESCROW_LOCK", "ESCROW_RELEASE"
    val amount: Double,
    val previousBalance: Double,
    val newBalance: Double,
    val paymentMethod: String,
    val transactionIdOrRef: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isVerifiedByAdmin: Boolean = true
)

@Entity(tableName = "payment_agents")
data class PaymentAgent(
    @PrimaryKey val id: String,
    val name: String,
    val agentCode: String,
    val country: String,
    val phone: String,
    val paymentMethod: String,
    val accountNumber: String,
    val accountHolder: String = "",
    val commissionRate: Double = 1.5,
    val minLimit: Double = 100.0,
    val maxLimit: Double = 100000.0,
    val availableBalance: Double = 50000.0,
    val allowedMethods: String = "bKash, Bank, Nagad, Alipay",
    val supportsDeposit: Boolean = true,
    val supportsWithdraw: Boolean = true,
    val verificationStatus: String = "VERIFIED", // "VERIFIED", "PENDING_VERIFICATION", "SUSPENDED"
    val isOnline: Boolean = true,
    val rating: Float = 4.9f
)

@Entity(tableName = "b2b_orders")
data class B2BOrder(
    @PrimaryKey val orderId: String,
    val userId: String,
    val userName: String,
    val agentId: String,
    val agentName: String,
    val country: String,
    val type: String, // "DEPOSIT" or "WITHDRAWAL"
    val amount: Double,
    val currency: String = "BDT",
    val paymentMethod: String,
    val agentAccountNumber: String,
    val agentAccountHolder: String = "",
    val status: String = "PENDING_PAYMENT", // PENDING_PAYMENT, PAYMENT_SUBMITTED, RELEASED, DISPUTED, CANCELLED
    val proofScreenshotUrl: String? = null,
    val transactionRef: String? = null,
    val disputeReason: String? = null,
    val disputeStatus: String = "NONE", // NONE, OPEN_DISPUTE, RESOLVED_RELEASED, RESOLVED_REFUNDED
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "b2b_chat_messages")
data class B2BChatMessage(
    @PrimaryKey val id: String = "MSG_${System.currentTimeMillis()}_${(100..999).random()}",
    val orderId: String,
    val senderId: String,
    val senderName: String,
    val senderRole: String, // "USER", "AGENT", "ADMIN"
    val content: String,
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)



