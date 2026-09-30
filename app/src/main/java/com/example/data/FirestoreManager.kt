package com.example.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirestoreManager {
    private const val TAG = "FirestoreManager"
    private val firestore: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    /**
     * Fetch all models from Firestore collection 'models'
     */
    suspend fun getModelsFromFirestore(): List<ModelProfile> {
        return try {
            val snapshot = firestore.collection("models").get().await()
            val list = mutableListOf<ModelProfile>()
            for (doc in snapshot.documents) {
                val name = doc.getString("name") ?: continue
                val hourlyRate = doc.getDouble("hourly_rate")?.toInt() ?: 1500
                val category = doc.getString("category") ?: "Fashion"
                val location = doc.getString("location") ?: "Dhaka"
                val rating = doc.getDouble("rating")?.toFloat() ?: 4.9f
                val id = doc.id.replace("model_", "").toIntOrNull() ?: (1000 + list.size)

                list.add(
                    ModelProfile(
                        id = id,
                        name = name,
                        rating = rating,
                        reviewCount = 50,
                        location = location,
                        isOnline = true,
                        isVerified = true,
                        bio = "Professional $category Model based in $location.",
                        skills = category,
                        languages = "English, Bengali",
                        services = "Photoshoot, Fashion, Commercial",
                        hourlyRate = hourlyRate,
                        availabilityDays = "Mon, Tue, Wed, Thu, Fri, Sat",
                        gender = "Female",
                        age = 24,
                        heightCm = 172,
                        imageResName = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                        country = "Bangladesh"
                    )
                )
            }
            list
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching from Firestore", e)
            emptyList()
        }
    }

    /**
     * Save booking to Firestore in realtime
     */
    suspend fun saveBookingToFirestore(booking: Booking): Boolean {
        return try {
            val bookingMap = hashMapOf(
                "booking_id" to booking.id,
                "model_id" to booking.modelId,
                "model_name" to booking.modelName,
                "user_id" to booking.userId,
                "total_amount" to booking.totalPrice,
                "status" to booking.status,
                "payment_status" to booking.paymentStatus,
                "payment_method" to booking.paymentMethod,
                "created_at" to System.currentTimeMillis()
            )
            firestore.collection("bookings").document("booking_${booking.id}").set(bookingMap).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed saving booking to Firestore", e)
            false
        }
    }
}
