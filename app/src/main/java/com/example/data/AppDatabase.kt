package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDao {
    @Query("SELECT * FROM models")
    fun getAllModels(): Flow<List<ModelProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModels(models: List<ModelProfile>)

    @Update
    suspend fun updateModel(model: ModelProfile)

    @Query("SELECT * FROM models WHERE id = :id")
    fun getModelById(id: Int): Flow<ModelProfile?>

    @Query("SELECT * FROM models WHERE id = :id")
    suspend fun getModelByIdSync(id: Int): ModelProfile?
}

@Dao
interface CurrentUserDao {
    @Query("SELECT * FROM current_user LIMIT 1")
    fun getCurrentUser(): Flow<CurrentUser?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentUser(user: CurrentUser)

    @Update
    suspend fun updateCurrentUser(user: CurrentUser)

    @Query("DELETE FROM current_user")
    suspend fun deleteCurrentUser()
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY timestamp DESC")
    fun getAllBookings(): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE userId = :userId ORDER BY timestamp DESC")
    fun getBookingsForUser(userId: String): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE modelId = :modelId ORDER BY timestamp DESC")
    fun getBookingsForModel(modelId: Int): Flow<List<Booking>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking): Long

    @Update
    suspend fun updateBooking(booking: Booking)

    @Query("DELETE FROM bookings WHERE id = :id")
    suspend fun deleteBookingById(id: Int)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE (senderId = :user1 AND receiverId = :user2) OR (senderId = :user2 AND receiverId = :user1) ORDER BY timestamp ASC")
    fun getMessagesBetween(user1: String, user2: String): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE userId = :userId")
    fun getFavoritesForUser(userId: String): Flow<List<FavoriteModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(fav: FavoriteModel)

    @Query("DELETE FROM favorites WHERE userId = :userId AND modelId = :modelId")
    suspend fun deleteFavorite(userId: String, modelId: Int)
}

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallet_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsForUser(userId: String): Flow<List<WalletTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: WalletTransaction)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM model_reviews WHERE modelId = :modelId ORDER BY id DESC")
    fun getReviewsForModel(modelId: Int): Flow<List<ModelReview>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ModelReview)
}

@Database(
    entities = [
        ModelProfile::class,
        CurrentUser::class,
        Booking::class,
        ChatMessage::class,
        FavoriteModel::class,
        WalletTransaction::class,
        ModelReview::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun modelDao(): ModelDao
    abstract fun currentUserDao(): CurrentUserDao
    abstract fun bookingDao(): BookingDao
    abstract fun chatDao(): ChatDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun walletDao(): WalletDao
    abstract fun reviewDao(): ReviewDao
}
