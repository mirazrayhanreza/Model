package com.example.data.auth

import android.content.Context
import android.util.Log
import com.example.data.CurrentUser
import com.example.data.Repository
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager(
    private val context: Context,
    private val repository: Repository
) {
    companion object {
        val ADMIN_UIDS = setOf(
            "XbchICr95RRQWs8cB31RiFt3reJ2",
            "admin_1",
            "admin_root"
        )
        val ADMIN_EMAILS = setOf(
            "hmmirazreza2@gmail.com",
            "admin@modolconnect.com",
            "admin@example.com"
        )

        fun isAdmin(uid: String?, email: String?): Boolean {
            val cleanEmail = email?.trim()?.lowercase() ?: ""
            val cleanUid = uid?.trim() ?: ""
            return cleanUid in ADMIN_UIDS ||
                    cleanEmail in ADMIN_EMAILS ||
                    cleanEmail.contains("admin")
        }
    }

    private val auth: FirebaseAuth? by lazy {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w("FirebaseAuthManager", "FirebaseApp not initialized, using fallback auth: ${e.message}")
            null
        }
    }

    val currentFirebaseUser: FirebaseUser?
        get() = auth?.currentUser

    /**
     * Sign Up with Email and Password using Firebase Auth
     */
    suspend fun signUpWithEmail(
        email: String,
        password: String,
        name: String,
        role: String = "USER",
        phone: String = ""
    ): Result<CurrentUser> {
        val cleanEmail = email.trim()
        val cleanName = name.trim().ifEmpty { "User" }

        if (cleanEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters long."))
        }

        val firebaseAuth = auth
        if (firebaseAuth != null) {
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(cleanEmail, password).await()
                val firebaseUser = authResult.user
                    ?: return Result.failure(Exception("Failed to obtain created user from Firebase Auth."))

                // Update Firebase User Profile Display Name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(cleanName)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()

                val userId = firebaseUser.uid
                val user = CurrentUser(
                    id = userId,
                    name = cleanName,
                    role = role,
                    balance = if (role == "MODEL") 2450.0 else 500.0,
                    avatarUrl = if (role == "MODEL")
                        "https://images.unsplash.com/photo-1534528741775-53994a69daeb"
                    else
                        "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
                    isVerified = role == "USER",
                    email = cleanEmail,
                    city = "Dhaka"
                )

                repository.insertCurrentUser(user)
                return Result.success(user)
            } catch (e: FirebaseAuthException) {
                val friendlyMessage = when (e.errorCode) {
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "This email address is already registered. Please login instead."
                    "ERROR_INVALID_EMAIL" -> "The email address format is invalid."
                    "ERROR_WEAK_PASSWORD" -> "The password provided is too weak. Must be at least 6 characters."
                    else -> e.localizedMessage ?: "Authentication error occurred: ${e.errorCode}"
                }
                return Result.failure(Exception(friendlyMessage))
            } catch (e: Exception) {
                Log.e("FirebaseAuthManager", "Sign up failed: ${e.message}", e)
                return Result.failure(Exception(e.localizedMessage ?: "Failed to sign up with Firebase Auth."))
            }
        } else {
            // Offline / Fallback Local Registration
            val localUserId = "user_${System.currentTimeMillis()}"
            val user = CurrentUser(
                id = localUserId,
                name = cleanName,
                role = role,
                balance = if (role == "MODEL") 2450.0 else 500.0,
                avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
                isVerified = true,
                email = cleanEmail,
                city = "Dhaka"
            )
            repository.insertCurrentUser(user)
            return Result.success(user)
        }
    }

    /**
     * Sign In with Email and Password using Firebase Auth
     */
    suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<CurrentUser> {
        val cleanEmail = email.trim()

        if (cleanEmail.isEmpty()) {
            return Result.failure(IllegalArgumentException("Please enter your email address or phone."))
        }
        if (password.isEmpty()) {
            return Result.failure(IllegalArgumentException("Please enter your password."))
        }

        // Check for Demo / Role-based test emails
        val isDemoEmail = cleanEmail.contains("admin", true) ||
                cleanEmail.contains("model", true) ||
                cleanEmail.contains("agent", true) ||
                cleanEmail.contains("user@example.com", true)

        val firebaseAuth = auth
        if (firebaseAuth != null && !isDemoEmail && android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            try {
                val authResult = firebaseAuth.signInWithEmailAndPassword(cleanEmail, password).await()
                val firebaseUser = authResult.user
                    ?: return Result.failure(Exception("Failed to retrieve user details."))

                val role = when {
                    isAdmin(firebaseUser.uid, cleanEmail) -> "ADMIN"
                    cleanEmail.contains("model", true) -> "MODEL"
                    cleanEmail.contains("agent", true) -> "CASH_AGENT"
                    else -> "USER"
                }

                val displayName = firebaseUser.displayName?.ifEmpty { null }
                    ?: (if (role == "ADMIN") "System Admin (Miraz Reza)" else cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() })

                val user = CurrentUser(
                    id = firebaseUser.uid,
                    name = displayName,
                    role = role,
                    balance = if (role == "ADMIN") 50000.0 else (if (role == "MODEL") 2450.0 else 1500.0),
                    avatarUrl = if (role == "MODEL")
                        "https://images.unsplash.com/photo-1534528741775-53994a69daeb"
                    else
                        "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde",
                    isVerified = true,
                    email = cleanEmail,
                    city = "Dhaka"
                )

                repository.insertCurrentUser(user)
                return Result.success(user)
            } catch (e: FirebaseAuthException) {
                val friendlyMessage = when (e.errorCode) {
                    "ERROR_USER_NOT_FOUND" -> "No account found with this email. Please create an account."
                    "ERROR_WRONG_PASSWORD", "ERROR_INVALID_CREDENTIAL" -> "Incorrect password. Please try again."
                    "ERROR_USER_DISABLED" -> "This user account has been disabled by an administrator."
                    "ERROR_TOO_MANY_REQUESTS" -> "Too many failed attempts. Please try again in a few moments."
                    else -> e.localizedMessage ?: "Invalid email or password."
                }
                return Result.failure(Exception(friendlyMessage))
            } catch (e: Exception) {
                Log.e("FirebaseAuthManager", "Sign in failed: ${e.message}", e)
                return Result.failure(Exception(e.localizedMessage ?: "Authentication failed."))
            }
        } else {
            // Local Demo / Offline Fallback Auth
            val role = when {
                isAdmin(null, cleanEmail) -> "ADMIN"
                cleanEmail.contains("model", true) -> "MODEL"
                cleanEmail.contains("agent", true) -> "CASH_AGENT"
                else -> "USER"
            }
            val userId = when (role) {
                "ADMIN" -> "XbchICr95RRQWs8cB31RiFt3reJ2"
                "MODEL" -> "model_1"
                "CASH_AGENT" -> "agent_1"
                else -> "user_1"
            }
            val name = when (role) {
                "ADMIN" -> "System Admin (Miraz Reza)"
                "MODEL" -> "Jessica (Model)"
                "CASH_AGENT" -> "Agent Sumon"
                else -> if (cleanEmail.contains("@")) cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() } else "Miraz Reza"
            }

            val user = CurrentUser(
                id = userId,
                name = name,
                role = role,
                balance = if (role == "ADMIN") 50000.0 else (if (role == "MODEL") 2450.0 else 1500.0),
                avatarUrl = if (role == "MODEL")
                    "https://images.unsplash.com/photo-1534528741775-53994a69daeb"
                else
                    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde",
                isVerified = true,
                email = cleanEmail.ifEmpty { "hmmirazreza2@gmail.com" },
                city = "Dhaka"
            )
            repository.insertCurrentUser(user)
            return Result.success(user)
        }
    }

    /**
     * Send Password Reset Email
     */
    suspend fun sendPasswordReset(email: String): Result<Unit> {
        val cleanEmail = email.trim()
        if (cleanEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }

        val firebaseAuth = auth
        if (firebaseAuth != null) {
            try {
                firebaseAuth.sendPasswordResetEmail(cleanEmail).await()
                return Result.success(Unit)
            } catch (e: Exception) {
                return Result.failure(Exception(e.localizedMessage ?: "Failed to send password reset email."))
            }
        } else {
            return Result.success(Unit)
        }
    }

    /**
     * Sign Out
     */
    suspend fun signOut() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.w("FirebaseAuthManager", "Error during Firebase signOut: ${e.message}")
        }
        repository.deleteCurrentUser()
    }
}
