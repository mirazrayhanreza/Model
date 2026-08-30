package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
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

@Dao
interface DepositDao {
    @Query("SELECT * FROM deposit_requests ORDER BY timestamp DESC")
    fun getAllDeposits(): Flow<List<DepositRequest>>

    @Query("SELECT * FROM deposit_requests WHERE userId = :userId ORDER BY timestamp DESC")
    fun getDepositsForUser(userId: String): Flow<List<DepositRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeposit(deposit: DepositRequest)

    @Update
    suspend fun updateDeposit(deposit: DepositRequest)
}

@Dao
interface WithdrawalDao {
    @Query("SELECT * FROM withdrawal_requests ORDER BY timestamp DESC")
    fun getAllWithdrawals(): Flow<List<WithdrawalRequest>>

    @Query("SELECT * FROM withdrawal_requests WHERE applicantId = :applicantId ORDER BY timestamp DESC")
    fun getWithdrawalsForUser(applicantId: String): Flow<List<WithdrawalRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(withdrawal: WithdrawalRequest)

    @Update
    suspend fun updateWithdrawal(withdrawal: WithdrawalRequest)
}

@Dao
interface LedgerDao {
    @Query("SELECT * FROM ledger_entries ORDER BY timestamp DESC")
    fun getAllLedgerEntries(): Flow<List<LedgerEntry>>

    @Query("SELECT * FROM ledger_entries WHERE userId = :userId ORDER BY timestamp DESC")
    fun getLedgerEntriesForUser(userId: String): Flow<List<LedgerEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLedgerEntry(entry: LedgerEntry)
}

@Dao
interface PaymentAgentDao {
    @Query("SELECT * FROM payment_agents")
    fun getAllPaymentAgents(): Flow<List<PaymentAgent>>

    @Query("SELECT * FROM payment_agents WHERE country = :country")
    fun getPaymentAgentsByCountry(country: String): Flow<List<PaymentAgent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentAgent(agent: PaymentAgent)

    @Update
    suspend fun updatePaymentAgent(agent: PaymentAgent)

    @Delete
    suspend fun deletePaymentAgent(agent: PaymentAgent)
}

@Dao
interface B2BOrderDao {
    @Query("SELECT * FROM b2b_orders ORDER BY createdAt DESC")
    fun getAllB2BOrders(): Flow<List<B2BOrder>>

    @Query("SELECT * FROM b2b_orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getB2BOrdersForUser(userId: String): Flow<List<B2BOrder>>

    @Query("SELECT * FROM b2b_orders WHERE orderId = :orderId")
    fun getB2BOrderById(orderId: String): Flow<B2BOrder?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertB2BOrder(order: B2BOrder)

    @Update
    suspend fun updateB2BOrder(order: B2BOrder)
}

@Dao
interface B2BChatDao {
    @Query("SELECT * FROM b2b_chat_messages WHERE orderId = :orderId ORDER BY timestamp ASC")
    fun getChatMessagesForOrder(orderId: String): Flow<List<B2BChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: B2BChatMessage)
}

@Database(
    entities = [
        ModelProfile::class,
        CurrentUser::class,
        Booking::class,
        ChatMessage::class,
        FavoriteModel::class,
        WalletTransaction::class,
        ModelReview::class,
        DepositRequest::class,
        WithdrawalRequest::class,
        LedgerEntry::class,
        PaymentAgent::class,
        B2BOrder::class,
        B2BChatMessage::class
    ],
    version = 6,
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
    abstract fun depositDao(): DepositDao
    abstract fun withdrawalDao(): WithdrawalDao
    abstract fun ledgerDao(): LedgerDao
    abstract fun paymentAgentDao(): PaymentAgentDao
    abstract fun b2bOrderDao(): B2BOrderDao
    abstract fun b2bChatDao(): B2BChatDao
}


