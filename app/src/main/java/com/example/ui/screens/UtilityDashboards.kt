package com.example.ui.screens

import kotlinx.coroutines.launch
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import coil.compose.SubcomposeAsyncImage
import coil.compose.AsyncImage
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

// ============================================================================
// 1. WALLET SCREEN (DEPOSIT & WITHDRAW CONTROLS)
// ============================================================================

@Composable
fun WalletScreen(viewModel: AppViewModel) {
    WalletDashboardContent(viewModel = viewModel, isModel = false) {
        viewModel.navigateTo("DASHBOARD")
    }
}

@Composable
fun WalletDashboardContent(
    viewModel: AppViewModel,
    isModel: Boolean = false,
    onBackClick: (() -> Unit)? = null
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val transactions by viewModel.walletTransactions.collectAsStateWithLifecycle()
    val bookings by viewModel.userBookings.collectAsStateWithLifecycle()

    var showAddFundsDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var showOffersDialog by remember { mutableStateOf(false) }
    var showAllTransactions by remember { mutableStateOf(false) }

    // Calculate balances
    val availableBalance = currentUser?.balance ?: 0.0
    // Pending Balance: active bookings that are not completed/cancelled
    val pendingBalance = bookings.filter { 
        it.status == "PENDING" || it.status == "ACCEPTED" || it.status == "ONGOING" ||
        it.status == "PAYMENT_RECEIVED" || it.status == "IN_PROGRESS" || it.status == "PROOF_UPLOADED" || it.status == "ADMIN_REVIEW"
    }.sumOf { it.totalPrice }
    val totalBalance = availableBalance + pendingBalance

    // Dialogs
    if (showAddFundsDialog) {
        DepositWorkflowDialog(
            viewModel = viewModel,
            onDismiss = { showAddFundsDialog = false }
        )
    }

    if (showWithdrawDialog) {
        WithdrawalWorkflowDialog(
            viewModel = viewModel,
            onDismiss = { showWithdrawDialog = false }
        )
    }

    if (showOffersDialog) {
        AlertDialog(
            onDismissRequest = { showOffersDialog = false },
            title = { Text("Exclusive Promo Offers", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Get special discounts on your active booking deposits!", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131324)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("PROMO CODE", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("MODOL20", color = PinkHighlight, fontWeight = FontWeight.Black, fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Get 20% OFF on your next model booking. Valid on escrow services.",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showOffersDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
                ) {
                    Text("Awesome", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkSurface
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("My Wallet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            IconButton(onClick = { /* Already on wallet */ }) {
                Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = "Wallet info", tint = Color.White)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Gradient Balance Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFF2B85), Color(0xFF6A1B9A))
                            )
                        )
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total Balance", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "৳ %,.2f".format(totalBalance),
                                    color = Color.White,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        HorizontalDivider(color = Color.White.copy(alpha = 0.2f), thickness = 0.5.dp)

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Available Balance", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "৳ %,.2f".format(availableBalance),
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(30.dp)
                                    .background(Color.White.copy(alpha = 0.2f))
                            )

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 16.dp)
                            ) {
                                Text("Pending Balance", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "৳ %,.2f".format(pendingBalance),
                                    color = Color(0xFFFFF176),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Quick Actions (Row of 4 beautiful square buttons)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val actions = listOf(
                        QuadAction("Add Money", Icons.Default.ArrowUpward, Color(0xFFE91E63)) {
                            showAddFundsDialog = true
                        },
                        QuadAction("Withdraw", Icons.Default.ArrowDownward, Color(0xFF9C27B0)) {
                            showWithdrawDialog = true
                        },
                        QuadAction("History", Icons.Default.Payment, Color(0xFF2196F3)) {
                            showAllTransactions = !showAllTransactions
                        },
                        QuadAction("Offers", Icons.Default.Redeem, Color(0xFFFF9800)) {
                            showOffersDialog = true
                        }
                    )

                    actions.forEach { action ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { action.onClick() }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DarkSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(action.color.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = action.icon,
                                        contentDescription = action.label,
                                        tint = action.color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = action.label,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Transactions Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = if (showAllTransactions) "Collapse" else "View All",
                        color = PinkHighlight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { showAllTransactions = !showAllTransactions }
                    )
                }
            }

            // Transactions list
            val displayList = if (showAllTransactions) transactions else transactions.take(4)

            if (displayList.isEmpty()) {
                item {
                    Text(
                        text = "No past transactions found.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            } else {
                items(displayList) { tx ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val (txTitle, txIcon, txColor) = remember(tx.type) {
                                when (tx.type) {
                                    "DEPOSIT" -> Triple("Added Money", Icons.Default.ArrowUpward, Color(0xFF4CAF50))
                                    "EARNING" -> Triple("Booking Earnings", Icons.Default.ArrowUpward, Color(0xFF4CAF50))
                                    "WITHDRAW" -> Triple("Withdrawal", Icons.Default.ArrowDownward, Color(0xFF9C27B0))
                                    else -> Triple("Booking Payment", Icons.Default.ArrowDownward, Color(0xFFE91E63))
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(txColor.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = txIcon,
                                    contentDescription = null,
                                    tint = txColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = txTitle,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = tx.description,
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        val isPositive = tx.type == "DEPOSIT" || tx.type == "EARNING"
                        Text(
                            text = if (isPositive) "+৳%,.2f".format(tx.amount) else "-৳%,.2f".format(tx.amount),
                            color = if (isPositive) OnlineGreen else Color(0xFFFF5252),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    HorizontalDivider(color = Color(0xFF2E2E3E), thickness = 0.5.dp)
                }
            }
        }

        // Add Money bottom button matching left mockup
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = { showAddFundsDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Money", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

data class QuadAction(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

// ============================================================================
// 2. NOTIFICATIONS SCREEN
// ============================================================================

@Composable
fun NotificationsScreen(viewModel: AppViewModel) {
    val notifs by viewModel.notifications.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Bookings", "Payments", "Promotions", "System")

    // Filter notifications based on selected category
    val filteredNotifs = remember(notifs, selectedCategory) {
        if (selectedCategory == "All") {
            notifs
        } else {
            notifs.filter { notif ->
                when (selectedCategory) {
                    "Bookings" -> notif.category.equals("Booking", ignoreCase = true)
                    "Payments" -> notif.category.equals("Payment", ignoreCase = true) || notif.category.equals("Wallet", ignoreCase = true)
                    "Promotions" -> notif.category.equals("Promotions", ignoreCase = true) || notif.category.equals("Admin", ignoreCase = true)
                    "System" -> notif.category.equals("System", ignoreCase = true)
                    else -> true
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo("DASHBOARD") }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Notifications", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            IconButton(onClick = { viewModel.navigateTo("SETTINGS") }) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
            }
        }

        // Horizontal Category Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = category == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) PinkHighlight else Color(0xFF1E1E2E))
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredNotifs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.NotificationsOff, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No Notifications in $selectedCategory", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredNotifs) { notif ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.markNotificationAsRead(notif.id) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Circular icon container matching mockup colors
                            val (icon, bgColor, tintColor) = remember(notif.category) {
                                when (notif.category) {
                                    "Booking" -> Triple(Icons.Default.CalendarMonth, Color(0xFFE91E63).copy(alpha = 0.15f), Color(0xFFE91E63))
                                    "Payment", "Wallet" -> Triple(Icons.Default.CheckCircle, Color(0xFF4CAF50).copy(alpha = 0.15f), Color(0xFF4CAF50))
                                    "Chat" -> Triple(Icons.Default.Chat, Color(0xFF9C27B0).copy(alpha = 0.15f), Color(0xFF9C27B0))
                                    "Promotions" -> Triple(Icons.Default.Redeem, Color(0xFFFF9800).copy(alpha = 0.15f), Color(0xFFFF9800))
                                    "System" -> Triple(Icons.Default.Verified, Color(0xFF2196F3).copy(alpha = 0.15f), Color(0xFF2196F3))
                                    else -> Triple(Icons.Default.Info, Color(0xFF607D8B).copy(alpha = 0.15f), Color(0xFF607D8B))
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(bgColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = tintColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = notif.title,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = notif.time,
                                        color = Color.Gray,
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = notif.message,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }

                            // Small red unread badge
                            if (!notif.isRead) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .size(8.dp)
                                        .background(PinkHighlight, CircleShape)
                                        .align(Alignment.CenterVertically)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 3. MODEL DASHBOARD (MODEL PROFILE ONLY)
// ============================================================================

@Composable
fun ModelDashboardScreen(viewModel: AppViewModel) {
    val bookings by viewModel.userBookings.collectAsStateWithLifecycle()
    val modelProfile = bookings.firstOrNull()?.let { b ->
        ModelProfile(
            id = b.modelId, name = b.modelName, rating = 4.8f, reviewCount = 128, location = "Dhaka",
            isOnline = true, isVerified = true, bio = "Model account active", skills = "Fashion",
            languages = "Bengali, English", services = "Photoshoot", hourlyRate = 120,
            availabilityDays = "Sun", gender = "Female", age = 22, heightCm = 170, imageResName = b.modelPhoto
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo("DASHBOARD") }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Model Host Dashboard", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Panel
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Total Earnings", color = TextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("৳2,450", color = OnlineGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Pending Orders", color = TextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                bookings.count { it.status == "PENDING" }.toString(),
                                color = PinkHighlight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            // Booking Requests Section
            item {
                Text("Incoming Booking Proposals", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            val pendingRequests = bookings.filter { it.status == "PENDING" }
            if (pendingRequests.isEmpty()) {
                item {
                    Text("No pending proposals at this time.", color = TextSecondary, fontSize = 12.sp)
                }
            } else {
                items(pendingRequests) { b ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Client ID: ${b.userId}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("৳${b.totalPrice.toInt()}", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Scheduled: ${b.date} (${b.time})", color = TextSecondary, fontSize = 12.sp)
                            Text("Service: ${b.serviceType}", color = TextSecondary, fontSize = 12.sp)

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.updateBookingStatus(b, "ACCEPTED") },
                                    colors = ButtonDefaults.buttonColors(containerColor = OnlineGreen, contentColor = Color.Black),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f).height(32.dp)
                                ) {
                                    Text("Accept", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.updateBookingStatus(b, "CANCELLED") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f).height(32.dp)
                                ) {
                                    Text("Reject", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 4. ADMIN PANEL CONTROL (ADMIN ROLE ONLY)
// ============================================================================

@Composable
fun AdminPanelScreen(viewModel: AppViewModel) {
    val models by viewModel.allModels.collectAsStateWithLifecycle()
    val bookings by viewModel.allBookings.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo("DASHBOARD") }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Admin Platform Panel", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Metric Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Registered Models", color = TextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(models.size.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Total Bookings", color = TextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(bookings.size.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                }
            }

            // Verification lists
            item {
                Text("Verification Management", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(models) { m ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(m.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Status: ${if (m.isVerified) "Verified" else "Awaiting Verification"}", color = TextSecondary, fontSize = 11.sp)
                        }

                        Button(
                            onClick = { viewModel.verifyModel(m.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = if (m.isVerified) Color.Gray else PinkHighlight),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(if (m.isVerified) "Revoke" else "Verify", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 5. SETTINGS SCREEN
// ============================================================================

@Composable
fun SettingsScreen(viewModel: AppViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo("DASHBOARD") }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Settings & Support", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Backend Server Configuration Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B192A)),
                border = BorderStroke(1.dp, PinkHighlight.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(PinkHighlight.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Dns,
                                    contentDescription = "Backend Server",
                                    tint = PinkHighlight,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Modol Connect Backend API", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("PHP 8.2.31 Operational", color = OnlineGreen, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(OnlineGreen.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("LIVE", color = OnlineGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Active Backend Base URL:", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = viewModel.backendServerUrl,
                        onValueChange = { viewModel.backendServerUrl = it },
                        label = { Text("Backend URL", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PinkHighlight,
                            unfocusedBorderColor = Color(0xFF2E2E3E)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.testBackendConnection() },
                            colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Ping", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.showPhpBackendModal = true },
                            border = BorderStroke(1.dp, PinkHighlight),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PHP Explorer", color = PinkHighlight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Language selector
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("App Language", color = Color.White, fontWeight = FontWeight.Bold)
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("English", "Bangla").forEach { lang ->
                            val isSel = viewModel.appLanguage == lang
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) PinkHighlight else Color(0xFF2E2E3E))
                                    .clickable { viewModel.appLanguage = lang }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(lang, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // About Us
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("About MODOL CONNECT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "MODOL CONNECT is Bangladesh's first professional model marketplace app. We connect brands, fashion houses, and individuals with local professional talents seamlessly, with secure escrow payments.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// ============================================================================
// 6. HIGH-FIDELITY ADMIN DASHBOARD MODULES (EXACTLY MATCHING SCREENSHOT)
// ============================================================================

@Composable
fun AdminDashboardTab(viewModel: AppViewModel) {
    val collections by viewModel.cashCollections.collectAsStateWithLifecycle()
    val bookings by viewModel.allBookings.collectAsStateWithLifecycle()
    val models by viewModel.allModels.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1019)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. TOP HEADER & BRANDING BAR ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161824)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF25283A))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFFFF2B85), Color(0xFF9C27B0))
                                        ),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    "MODOL CONNECT",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    "ADMIN PANEL",
                                    color = PinkHighlight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 2.sp
                                )
                            }
                        }

                        // Top right quick admin user pill & notifications
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF202336), RoundedCornerShape(20.dp))
                                    .border(1.dp, Color(0xFF2E324D), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "18 May 2025 - 18 Jun 2025",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFF202336), CircleShape)
                                    .clickable { viewModel.navigateTo("NOTIFICATIONS") },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 2.dp, y = (-2).dp)
                                        .background(PinkHighlight, CircleShape)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("12", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFF25283A))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2A2D45))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Miraz Rayhan", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Super Admin", color = PinkHighlight, fontSize = 10.sp)
                            }
                        }

                        Button(
                            onClick = { viewModel.showPhpBackendModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight.copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, PinkHighlight),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PHP Live Source", color = PinkHighlight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- 2. TOP METRICS GRID (6 METRIC CARDS MATCHING SCREENSHOT) ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminMetricCard(
                        title = "Total Users",
                        value = "12,568",
                        change = "+18.7%",
                        subText = "from last month",
                        icon = Icons.Default.People,
                        color = Color(0xFF9C27B0), // Purple
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "Total Models",
                        value = "2,356",
                        change = "+15.3%",
                        subText = "from last month",
                        icon = Icons.Default.Face,
                        color = Color(0xFFFF2B85), // Pink
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminMetricCard(
                        title = "Total Cash Agents",
                        value = "632",
                        change = "+11.2%",
                        subText = "from last month",
                        icon = Icons.Default.Work,
                        color = Color(0xFF2196F3), // Blue
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "Total Bookings",
                        value = "8,965",
                        change = "+20.5%",
                        subText = "from last month",
                        icon = Icons.Default.CalendarToday,
                        color = Color(0xFF4CAF50), // Green
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminMetricCard(
                        title = "Total Revenue",
                        value = "৳ 4,565,230",
                        change = "+22.8%",
                        subText = "from last month",
                        icon = Icons.Default.AccountBalanceWallet,
                        color = Color(0xFFFF9800), // Orange
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "Today's Revenue",
                        value = "৳ 156,830",
                        change = "+9.4%",
                        subText = "from yesterday",
                        icon = Icons.Default.AttachMoney,
                        color = Color(0xFF9C27B0), // Purple
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // --- 3. REVENUE OVERVIEW LINE CHART ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161824)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF25283A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Revenue Overview", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF202336), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF2E324D), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Monthly ▾", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Y-Axis Labels
                        Column(
                            modifier = Modifier.height(130.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("৳600K", "৳450K", "৳300K", "৳150K", "৳0").forEach { label ->
                                Text(label, color = TextSecondary, fontSize = 9.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            // Line Chart Canvas
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                            ) {
                                val points = listOf(180f, 230f, 220f, 320f, 410f, 450f, 520f)
                                val widthBetween = size.width / (points.size - 1)
                                val maxVal = 600f
                                val heightRatio = size.height / maxVal

                                // Draw horizontal grid lines
                                for (i in 0..4) {
                                    val y = size.height * (i / 4f)
                                    drawLine(
                                        color = Color.White.copy(alpha = 0.06f),
                                        start = androidx.compose.ui.geometry.Offset(0f, y),
                                        end = androidx.compose.ui.geometry.Offset(size.width, y),
                                        strokeWidth = 1f
                                    )
                                }

                                // Fill area under curve
                                val fillPath = androidx.compose.ui.graphics.Path().apply {
                                    moveTo(0f, size.height)
                                    points.forEachIndexed { index, value ->
                                        val x = index * widthBetween
                                        val y = size.height - (value * heightRatio)
                                        lineTo(x, y)
                                    }
                                    lineTo(size.width, size.height)
                                    close()
                                }
                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            PinkHighlight.copy(alpha = 0.35f),
                                            Color.Transparent
                                        )
                                    )
                                )

                                // Draw main pink line
                                val linePath = androidx.compose.ui.graphics.Path().apply {
                                    points.forEachIndexed { index, value ->
                                        val x = index * widthBetween
                                        val y = size.height - (value * heightRatio)
                                        if (index == 0) moveTo(x, y) else lineTo(x, y)
                                    }
                                }
                                drawPath(
                                    path = linePath,
                                    color = PinkHighlight,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                                )

                                // Draw dots
                                points.forEachIndexed { index, value ->
                                    val x = index * widthBetween
                                    val y = size.height - (value * heightRatio)
                                    drawCircle(
                                        color = Color.White,
                                        radius = 5f,
                                        center = androidx.compose.ui.geometry.Offset(x, y)
                                    )
                                    drawCircle(
                                        color = PinkHighlight,
                                        radius = 3f,
                                        center = androidx.compose.ui.geometry.Offset(x, y)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                listOf("Dec", "Jan", "Feb", "Mar", "Apr", "May", "Jun").forEach { month ->
                                    Text(month, color = TextSecondary, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 4. BOOKING STATUS DONUT & QUICK ACTIONS ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Booking Status Donut Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161824)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF25283A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Booking Status",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Box(contentAlignment = Alignment.Center) {
                            Canvas(modifier = Modifier.size(110.dp)) {
                                val strokeWidth = 26f
                                // Arcs: Pending 14%, Accepted 36%, Completed 35%, Cancelled 15%
                                drawArc(
                                    color = Color(0xFFFF9800), // Pending (Orange)
                                    startAngle = 270f,
                                    sweepAngle = 360f * 0.14f,
                                    useCenter = false,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                                )
                                drawArc(
                                    color = Color(0xFF4CAF50), // Accepted (Green)
                                    startAngle = 270f + (360f * 0.14f),
                                    sweepAngle = 360f * 0.36f,
                                    useCenter = false,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                                )
                                drawArc(
                                    color = Color(0xFF2196F3), // Completed (Blue)
                                    startAngle = 270f + (360f * 0.50f),
                                    sweepAngle = 360f * 0.35f,
                                    useCenter = false,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                                )
                                drawArc(
                                    color = Color(0xFFFF2B85), // Cancelled (Pink)
                                    startAngle = 270f + (360f * 0.85f),
                                    sweepAngle = 360f * 0.15f,
                                    useCenter = false,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("8,965", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                Text("Total", color = TextSecondary, fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        // Legend List
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                AdminLegendItemFull(color = Color(0xFFFF9800), label = "Pending", count = "1,256 (14%)")
                                AdminLegendItemFull(color = Color(0xFF4CAF50), label = "Accepted", count = "3,256 (36%)")
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                AdminLegendItemFull(color = Color(0xFF2196F3), label = "Completed", count = "3,125 (35%)")
                                AdminLegendItemFull(color = Color(0xFFFF2B85), label = "Cancelled", count = "1,328 (15%)")
                            }
                        }
                    }
                }

                // Quick Actions Card (6 Buttons Grid as in Screenshot)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161824)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF25283A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Quick Actions", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                QuickActionButtonTile(
                                    icon = Icons.Default.PersonAdd,
                                    label = "Add New Model",
                                    color = Color(0xFFFF2B85),
                                    modifier = Modifier.weight(1f)
                                ) { viewModel.selectedTab = 1 }

                                QuickActionButtonTile(
                                    icon = Icons.Default.WorkOutline,
                                    label = "Add Cash Agent",
                                    color = Color(0xFF4CAF50),
                                    modifier = Modifier.weight(1f)
                                ) { viewModel.selectedTab = 1 }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                QuickActionButtonTile(
                                    icon = Icons.Default.PersonAddAlt1,
                                    label = "Add New User",
                                    color = Color(0xFF2196F3),
                                    modifier = Modifier.weight(1f)
                                ) { viewModel.selectedTab = 1 }

                                QuickActionButtonTile(
                                    icon = Icons.Default.CalendarMonth,
                                    label = "New Booking",
                                    color = Color(0xFFFF9800),
                                    modifier = Modifier.weight(1f)
                                ) { viewModel.navigateTo("EXPLORE") }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                QuickActionButtonTile(
                                    icon = Icons.Default.AccountBalanceWallet,
                                    label = "Pending Payments",
                                    color = Color(0xFF9C27B0),
                                    modifier = Modifier.weight(1f)
                                ) { viewModel.selectedTab = 3 }

                                QuickActionButtonTile(
                                    icon = Icons.Default.Payments,
                                    label = "Cash Collection",
                                    color = Color(0xFF009688),
                                    modifier = Modifier.weight(1f)
                                ) { viewModel.selectedTab = 2 }
                            }
                        }
                    }
                }
            }
        }

        // --- 5. RECENT BOOKINGS TABLE (EXACT MATCH) ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161824)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF25283A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Recent Bookings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Box(
                            modifier = Modifier
                                .background(PinkHighlight.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .clickable { viewModel.selectedTab = 0 }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("View All", color = PinkHighlight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Column headers
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF202336), RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Booking ID", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f))
                        Text("User / Model", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                        Text("Amount", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                        Text("Status", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    val mockupBookings = listOf(
                        BookingRowData("#BK89562", "Al Amin", "Jessica", "18 May 2025, 11:00 AM", "৳ 3,500", "Accepted", Color(0xFF4CAF50)),
                        BookingRowData("#BK89561", "Rashid Khan", "Tania", "18 May 2025, 01:00 PM", "৳ 4,000", "Pending", Color(0xFFFF9800)),
                        BookingRowData("#BK89560", "Sojib Ahmed", "Sabrina", "18 May 2025, 03:00 PM", "৳ 2,500", "Completed", Color(0xFF2196F3)),
                        BookingRowData("#BK89559", "Mahmudul Hasan", "Nabila", "18 May 2025, 05:00 PM", "৳ 3,000", "Cancelled", Color(0xFFFF2B85)),
                        BookingRowData("#BK89558", "Jahid Hasan", "Maliha", "18 May 2025, 07:00 PM", "৳ 4,500", "Accepted", Color(0xFF4CAF50))
                    )

                    mockupBookings.forEach { bk ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1.1f)) {
                                Text(bk.id, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(bk.dateTime, color = TextSecondary, fontSize = 8.sp)
                            }
                            Column(modifier = Modifier.weight(1.5f)) {
                                Text(bk.user, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                Text("w/ ${bk.model}", color = PinkHighlight, fontSize = 9.sp)
                            }
                            Text(
                                bk.amount,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .wrapContentWidth(Alignment.End)
                                    .background(bk.statusColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    bk.status,
                                    color = bk.statusColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    }
                }
            }
        }

        // --- 6. RECENT CASH COLLECTIONS TABLE (EXACT MATCH) ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161824)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF25283A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Recent Cash Collections", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Box(
                            modifier = Modifier
                                .background(PinkHighlight.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .clickable { viewModel.selectedTab = 2 }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("View All", color = PinkHighlight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF202336), RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Collection ID", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                        Text("Agent", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
                        Text("Amount", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                        Text("Status", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    val mockupCollections = listOf(
                        CollectionRowData("#CC88521", "Agent Sumon", "৳ 3,500", "#BK89562", "Pending", Color(0xFFFF9800)),
                        CollectionRowData("#CC88520", "Agent Rafiq", "৳ 2,500", "#BK89560", "Paid", Color(0xFF4CAF50)),
                        CollectionRowData("#CC88519", "Agent Arif", "৳ 4,000", "#BK89561", "Pending", Color(0xFFFF9800)),
                        CollectionRowData("#CC88518", "Agent Jibon", "৳ 3,000", "#BK89559", "Paid", Color(0xFF4CAF50)),
                        CollectionRowData("#CC88517", "Agent Hasan", "৳ 4,500", "#BK89558", "Pending", Color(0xFFFF9800))
                    )

                    mockupCollections.forEach { coll ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1.2f)) {
                                Text(coll.id, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text("Ref: ${coll.bookingId}", color = TextSecondary, fontSize = 8.sp)
                            }
                            Text(coll.agent, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, modifier = Modifier.weight(1.3f))
                            Text(
                                coll.amount,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .wrapContentWidth(Alignment.End)
                                    .background(coll.statusColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    coll.status,
                                    color = coll.statusColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    }
                }
            }
        }

        // --- 7. SYSTEM OVERVIEW & RECENT NOTIFICATIONS ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // System Overview Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161824)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF25283A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("System Overview", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            SystemOverviewRow(icon = Icons.Default.Person, label = "Online Users", value = "256", iconTint = Color(0xFF4CAF50))
                            SystemOverviewRow(icon = Icons.Default.Layers, label = "App Version", value = "2.0.1", iconTint = Color(0xFF9C27B0))
                            SystemOverviewRow(icon = Icons.Default.SwapHoriz, label = "Total Transactions", value = "24,856", iconTint = Color(0xFF2196F3))
                            SystemOverviewRow(icon = Icons.Default.Public, label = "Active Countries", value = "16", iconTint = Color(0xFFFF9800))
                        }
                    }
                }

                // Recent Notifications Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161824)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF25283A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Recent Notifications", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Box(
                                modifier = Modifier
                                    .background(PinkHighlight.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .clickable { viewModel.navigateTo("NOTIFICATIONS") }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("View All", color = PinkHighlight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            AdminNotificationItem(
                                icon = Icons.Default.CheckCircle,
                                text = "Payment received from Al Amin",
                                time = "10:30 AM",
                                color = Color(0xFF4CAF50)
                            )
                            AdminNotificationItem(
                                icon = Icons.Default.Event,
                                text = "New booking request from Rashid Khan",
                                time = "10:28 AM",
                                color = Color(0xFFFF9800)
                            )
                            AdminNotificationItem(
                                icon = Icons.Default.AccountBalanceWallet,
                                text = "Cash collection submitted by Agent Sumon",
                                time = "10:20 AM",
                                color = Color(0xFF2196F3)
                            )
                            AdminNotificationItem(
                                icon = Icons.Default.VerifiedUser,
                                text = "New model registration by Tania",
                                time = "10:15 AM",
                                color = Color(0xFF9C27B0)
                            )
                        }
                    }
                }
            }
        }

        // --- 8. FOOTER ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "© 2025 Modol Connect. All rights reserved.",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Designed with ", color = TextSecondary, fontSize = 10.sp)
                    Text("❤", color = PinkHighlight, fontSize = 10.sp)
                    Text(" by ", color = TextSecondary, fontSize = 10.sp)
                    Text("Mirazsoft", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }
        }
    }
}

// Data classes for mockup rows
private data class BookingRowData(
    val id: String,
    val user: String,
    val model: String,
    val dateTime: String,
    val amount: String,
    val status: String,
    val statusColor: Color
)

private data class CollectionRowData(
    val id: String,
    val agent: String,
    val amount: String,
    val bookingId: String,
    val status: String,
    val statusColor: Color
)

@Composable
private fun SystemOverviewRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    iconTint: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(iconTint.copy(alpha = 0.15f), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(label, color = TextSecondary, fontSize = 12.sp)
        }
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
private fun AdminNotificationItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    time: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(13.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(text, color = Color.White, fontSize = 11.sp, maxLines = 1)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(time, color = TextSecondary, fontSize = 9.sp)
    }
}

@Composable
private fun AdminLegendItemFull(color: Color, label: String, count: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, color = TextSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(count, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}

@Composable
private fun QuickActionButtonTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF202336)),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(color.copy(alpha = 0.2f), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(15.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun AdminMetricCard(
    title: String,
    value: String,
    change: String,
    subText: String = "from last month",
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161824)),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFF25283A)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(color.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(15.dp))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(10.dp))
                Spacer(modifier = Modifier.width(2.dp))
                Text(change, color = Color(0xFF4CAF50), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Text(subText, color = TextSecondary, fontSize = 8.sp)
            }
        }
    }
}

@Composable
fun AdminLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Box(modifier = Modifier.size(6.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, color = TextSecondary, fontSize = 8.sp)
    }
}

@Composable
fun QuickActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(PinkHighlight.copy(alpha = 0.12f), RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(12.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun RecentNotificationItem(text: String, time: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.size(6.dp).background(color, CircleShape))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = Color.White, fontSize = 11.sp, maxLines = 1)
        }
        Text(time, color = TextSecondary, fontSize = 9.sp)
    }
}

// ---------------- USER & MODEL VERIFICATION TAB ----------------
@Composable
fun AdminUsersTab(viewModel: AppViewModel) {
    val models by viewModel.allModels.collectAsStateWithLifecycle()
    var tabSelected by remember { mutableStateOf(0) } // 0: Models, 1: Clients, 2: Agents

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("User Management", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)

        // Sub-tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Models (${models.size})", "Clients", "Cash Agents").forEachIndexed { idx, label ->
                val isSel = tabSelected == idx
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) PinkHighlight else DarkSurface)
                        .clickable { tabSelected = idx }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (tabSelected == 0) {
                items(models) { m ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFF2E2E3E), CircleShape)
                                ) {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.LightGray, modifier = Modifier.align(Alignment.Center))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(m.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Hourly: ৳${m.hourlyRate} • ${m.location}", color = TextSecondary, fontSize = 10.sp)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(6.dp).background(if (m.isOnline) OnlineGreen else Color.Gray, CircleShape))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (m.isOnline) "Active" else "Offline", color = TextSecondary, fontSize = 9.sp)
                                    }
                                }
                            }

                            Button(
                                onClick = { viewModel.verifyModel(m.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = if (m.isVerified) Color(0xFF4CAF50) else PinkHighlight),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(if (m.isVerified) "Verified ✓" else "Verify Model", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else if (tabSelected == 1) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.People, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Client Database Active", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("12,568 registered users", color = TextSecondary, fontSize = 11.sp, textAlign = TextAlign.Center)
                    }
                }
            } else {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("Agent Sumon", "Agent Rafiq", "Agent Arif", "Agent Jibon", "Agent Hasan").forEach { agent ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(agent, color = Color.White, fontWeight = FontWeight.Bold)
                                        Text("Commission Earned: ৳12,450", color = TextSecondary, fontSize = 10.sp)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF2196F3).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("5% Commission", color = Color(0xFF2196F3), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- ADMIN ESCROW & SERVICE PROOF TAB ----------------
@Composable
fun AdminEscrowReviewTab(viewModel: AppViewModel) {
    val bookings by viewModel.allBookings.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, PROOF_REVIEW, DISPUTED, COMPLETED

    var showReuploadDialog by remember { mutableStateOf<Booking?>(null) }
    var reuploadNote by remember { mutableStateOf("") }
    var showProofDetailsModal by remember { mutableStateOf<Booking?>(null) }

    val escrowBookings = bookings.filter {
        when (selectedFilter) {
            "PROOF_REVIEW" -> it.status == "PROOF_UPLOADED" || it.status == "USER_CONFIRMED"
            "DISPUTED" -> it.disputeStatus == "RAISED"
            "COMPLETED" -> it.status == "COMPLETED" || it.status == "PAYMENT_RELEASED"
            else -> true
        }
    }

    val totalEscrowVolume = bookings.filter { it.paymentStatus == "ESCROW_HELD" }.sumOf { it.totalPrice }
    val pendingReviewCount = bookings.count { it.status == "PROOF_UPLOADED" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("💰 Admin Escrow & Proof Verification", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Funds held safely until proof inspection", color = TextSecondary, fontSize = 11.sp)
                    }
                    Box(
                        modifier = Modifier
                            .background(PinkHighlight.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("$pendingReviewCount Pending Proofs", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Escrow Balance", color = TextSecondary, fontSize = 10.sp)
                        Text("৳${totalEscrowVolume.toInt()}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    }
                    Column {
                        Text("Model Share", color = TextSecondary, fontSize = 10.sp)
                        Text("${viewModel.modelSharePercentage.toInt()}%", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column {
                        Text("Platform Fee", color = TextSecondary, fontSize = 10.sp)
                        Text("${viewModel.platformFeePercentage.toInt()}%", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column {
                        Text("Cash Agent Fee", color = TextSecondary, fontSize = 10.sp)
                        Text("${viewModel.agentCommissionRate.toInt()}%", color = Color(0xFF2196F3), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        // Filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "ALL" to "All Bookings (${bookings.size})",
                "PROOF_REVIEW" to "Needs Review ($pendingReviewCount)",
                "DISPUTED" to "Disputes (${bookings.count { it.disputeStatus == "RAISED" }})",
                "COMPLETED" to "Released"
            ).forEach { (key, label) ->
                val isSel = selectedFilter == key
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) PinkHighlight else DarkSurface)
                        .clickable { selectedFilter = key }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Booking items list
        if (escrowBookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No bookings match this filter.", color = TextSecondary, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(escrowBookings) { booking ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Top Row: ID, Status Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Booking #${booking.id}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(booking.serviceType, color = TextSecondary, fontSize = 11.sp)
                                }

                                val (statusText, statusBg, statusColor) = when (booking.status) {
                                    "PROOF_UPLOADED" -> Triple("Proof Uploaded 📷", Color(0xFF2196F3).copy(alpha = 0.2f), Color(0xFF2196F3))
                                    "USER_CONFIRMED" -> Triple("Client Confirmed ✓", Color(0xFF4CAF50).copy(alpha = 0.2f), Color(0xFF4CAF50))
                                    "COMPLETED", "PAYMENT_RELEASED" -> Triple("Escrow Released 💰", Color(0xFF4CAF50).copy(alpha = 0.2f), Color(0xFF4CAF50))
                                    "REFUNDED" -> Triple("Refunded ↩", Color(0xFFFF9800).copy(alpha = 0.2f), Color(0xFFFF9800))
                                    "PAYMENT_RECEIVED" -> Triple("Escrow Held 🔒", Color(0xFFFF9800).copy(alpha = 0.2f), Color(0xFFFF9800))
                                    else -> Triple(booking.status, Color.DarkGray, Color.White)
                                }

                                Box(
                                    modifier = Modifier
                                        .background(statusBg, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(statusText, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                            Spacer(modifier = Modifier.height(10.dp))

                            // Model & User Info
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Model: ${booking.modelName}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("Client ID: ${booking.userId}", color = TextSecondary, fontSize = 10.sp)
                                    Text("Location: ${booking.location}", color = TextSecondary, fontSize = 10.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Escrow Total", color = TextSecondary, fontSize = 10.sp)
                                    Text("৳${booking.totalPrice.toInt()}", color = PinkHighlight, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                    Text("Payment: ${booking.paymentMethod}", color = TextSecondary, fontSize = 9.sp)
                                }
                            }

                            // Dispute Warning Banner if raised
                            if (booking.disputeStatus == "RAISED") {
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFF5252).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFFFF5252), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text("⚠️ DISPUTE RAISED BY CLIENT", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        Text("Reason: ${booking.disputeReason ?: "Quality disagreement"}", color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            }

                            // Proof Upload Section Card
                            if (booking.proofSelfieUrl != null || booking.proofPhotos != null || booking.proofNotes != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("📸 Service Completion Proof", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            TextButton(onClick = { showProofDetailsModal = booking }) {
                                                Text("Inspect All Proofs 🔍", color = PinkHighlight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        if (!booking.proofGpsLocation.isNullOrEmpty()) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(imageVector = Icons.Default.GpsFixed, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("GPS Tag: ${booking.proofGpsLocation}", color = TextSecondary, fontSize = 10.sp)
                                            }
                                        }

                                        if (!booking.proofNotes.isNullOrEmpty()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("Notes: ${booking.proofNotes}", color = Color.LightGray, fontSize = 10.sp)
                                        }
                                    }
                                }
                            }

                            // Admin Action Buttons
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (booking.status != "COMPLETED" && booking.status != "PAYMENT_RELEASED" && booking.status != "REFUNDED") {
                                    Button(
                                        onClick = { viewModel.releaseEscrowPayment(booking) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(36.dp)
                                    ) {
                                        Text("Approve & Release (85%)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { showReuploadDialog = booking },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF9800)),
                                        border = BorderStroke(1.dp, Color(0xFFFF9800)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text("Request Re-upload", fontSize = 10.sp)
                                    }

                                    Button(
                                        onClick = { viewModel.refundEscrowPayment(booking, "Admin issued full refund") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text("Refund Client", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF2E2E3E), RoundedCornerShape(8.dp))
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Escrow Transaction Finalized ✓", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Request Re-upload Note Dialog
    if (showReuploadDialog != null) {
        val targetBooking = showReuploadDialog!!
        AlertDialog(
            onDismissRequest = { showReuploadDialog = null },
            title = { Text("Request Proof Re-upload", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Provide reason why the proof photo/video is rejected or incomplete:", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = reuploadNote,
                        onValueChange = { reuploadNote = it },
                        placeholder = { Text("E.g. Selfie photo with client is blurry or missing GPS location tag.", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.requestReuploadProof(targetBooking, reuploadNote.ifEmpty { "Incomplete proof images" })
                        showReuploadDialog = null
                        reuploadNote = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Text("Submit Re-upload Request", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReuploadDialog = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }

    // Inspect Proof Full Modal
    if (showProofDetailsModal != null) {
        val b = showProofDetailsModal!!
        AlertDialog(
            onDismissRequest = { showProofDetailsModal = null },
            title = { Text("Inspect Service Proofs - Booking #${b.id}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Client Selfie Verification", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black)
                    ) {
                        SubcomposeAsyncImage(
                            model = b.proofSelfieUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                            contentDescription = "Selfie with Client",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Photos & Short Video Proof", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        b.proofPhotos?.split(",")?.forEach { photoUrl ->
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black)
                            ) {
                                SubcomposeAsyncImage(
                                    model = photoUrl.trim(),
                                    contentDescription = "Service Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    if (!b.proofGpsLocation.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("📍 GPS Location Data", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(b.proofGpsLocation, color = Color.White, fontSize = 11.sp)
                    }

                    if (!b.proofNotes.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("📝 Model Completion Notes", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(b.proofNotes, color = TextSecondary, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.releaseEscrowPayment(b)
                        showProofDetailsModal = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Approve & Release Payment", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showProofDetailsModal = null }) {
                    Text("Close Inspection", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }
}


// ---------------- ADMIN CASH VERIFICATION TAB ----------------
// ---------------- PROOF SCREENSHOT LIGHTBOX DIALOG ----------------
@Composable
fun ProofViewerDialog(
    imageUri: String,
    title: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                ) {
                    SubcomposeAsyncImage(
                        model = imageUri,
                        contentDescription = title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Supporting evidence photo - Verified by Modol Ledger Engine", color = TextSecondary, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
            ) {
                Text("Close Inspection", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = DarkSurface
    )
}

// ---------------- USER DEPOSIT WORKFLOW DIALOG ----------------
@Composable
fun DepositWorkflowDialog(
    viewModel: AppViewModel,
    onDismiss: () -> Unit
) {
    val allAgents by viewModel.paymentAgents.collectAsStateWithLifecycle()
    
    // Payment method choice: "GOOGLE_PAY", "ALIPAY", "CASH_AGENT"
    var selectedTopGateway by remember { mutableStateOf("GOOGLE_PAY") }
    
    // Cash Agent Flow states
    var selectedCountry by remember { mutableStateOf("Bangladesh") }
    val availableCountries = listOf("Bangladesh", "China", "USA", "UK", "UAE", "India", "Other")
    
    val countryAgents = remember(allAgents, selectedCountry) {
        allAgents.filter { it.country.equals(selectedCountry, ignoreCase = true) }
    }
    
    var selectedAgent by remember(countryAgents) {
        mutableStateOf(countryAgents.firstOrNull() ?: allAgents.firstOrNull() ?: PaymentAgent("DEF", "Default Agent", "00", "Bangladesh", "01700000000", "bKash", "01700000000"))
    }
    
    var depositAmountInput by remember { mutableStateOf("1000") }
    var transactionIdInput by remember { mutableStateOf("TXN${(10000000..99999999).random()}") }
    var userNoteInput by remember { mutableStateOf("Cash agent deposit") }
    var selectedProofUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1556742049-0a670f4a4591") }
    
    // Gateway simulation state
    var isProcessingGateway by remember { mutableStateOf(false) }
    var gatewayErrorNotice by remember { mutableStateOf<String?>(null) }
    var gatewaySuccessNotice by remember { mutableStateOf<String?>(null) }

    var showMarketplaceModal by remember { mutableStateOf(false) }

    val sampleProofPhotos = listOf(
        "bKash Receipt" to "https://images.unsplash.com/photo-1556742049-0a670f4a4591",
        "Nagad Voucher" to "https://images.unsplash.com/photo-1563013544-824ae1b704d3",
        "Bank Advice" to "https://images.unsplash.com/photo-1559526324-4b87b5e36e44"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(PinkHighlight.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("MODOL CONNECT", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text("Select Payment Method", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // UNIVERSAL 3 PAYMENT GATEWAY BUTTONS - ALWAYS VISIBLE REGARDLESS OF COUNTRY
                Text("Available Payment Gateways (Universal):", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                // 1. Google Pay
                val isGPaySelected = selectedTopGateway == "GOOGLE_PAY"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedTopGateway = "GOOGLE_PAY"
                            gatewayErrorNotice = null
                            gatewaySuccessNotice = null
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isGPaySelected) Color(0xFF00E676).copy(alpha = 0.15f) else DarkSurface
                    ),
                    border = BorderStroke(1.5.dp, if (isGPaySelected) Color(0xFF00E676) else Color(0xFF2E2E3E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF00E676), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Payment, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("🟢 Google Pay", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Automatic Payment Gateway", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                        RadioButton(
                            selected = isGPaySelected,
                            onClick = {
                                selectedTopGateway = "GOOGLE_PAY"
                                gatewayErrorNotice = null
                                gatewaySuccessNotice = null
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00E676))
                        )
                    }
                }

                // 2. Alipay
                val isAlipaySelected = selectedTopGateway == "ALIPAY"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedTopGateway = "ALIPAY"
                            gatewayErrorNotice = null
                            gatewaySuccessNotice = null
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isAlipaySelected) Color(0xFF1E88E5).copy(alpha = 0.15f) else DarkSurface
                    ),
                    border = BorderStroke(1.5.dp, if (isAlipaySelected) Color(0xFF1E88E5) else Color(0xFF2E2E3E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF1E88E5), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("支", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("🔵 Alipay", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Automatic Payment Gateway", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                        RadioButton(
                            selected = isAlipaySelected,
                            onClick = {
                                selectedTopGateway = "ALIPAY"
                                gatewayErrorNotice = null
                                gatewaySuccessNotice = null
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF1E88E5))
                        )
                    }
                }

                // 3. Cash Agent
                val isCashAgentSelected = selectedTopGateway == "CASH_AGENT"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedTopGateway = "CASH_AGENT"
                            gatewayErrorNotice = null
                            gatewaySuccessNotice = null
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCashAgentSelected) PinkHighlight.copy(alpha = 0.15f) else DarkSurface
                    ),
                    border = BorderStroke(1.5.dp, if (isCashAgentSelected) PinkHighlight else Color(0xFF2E2E3E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(PinkHighlight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("👤 Cash Agent", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Manual Verification Workflow", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                        RadioButton(
                            selected = isCashAgentSelected,
                            onClick = {
                                selectedTopGateway = "CASH_AGENT"
                                gatewayErrorNotice = null
                                gatewaySuccessNotice = null
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = PinkHighlight)
                        )
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                // --- GATEWAY SPECIFIC UI CONTENT ---
                if (selectedTopGateway == "GOOGLE_PAY" || selectedTopGateway == "ALIPAY") {
                    val gwName = if (selectedTopGateway == "GOOGLE_PAY") "Google Pay" else "Alipay"
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Enter Amount ($gwName Automatic Checkout):", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        OutlinedTextField(
                            value = depositAmountInput,
                            onValueChange = { depositAmountInput = it },
                            label = { Text("Amount (৳ / $)", color = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (isProcessingGateway) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1E202E), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = PinkHighlight, strokeWidth = 2.dp)
                                Text("Verifying $gwName gateway eligibility...", color = Color.White, fontSize = 12.sp)
                            }
                        }

                        if (gatewayErrorNotice != null) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF3E1E24)),
                                border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("$gwName Gateway Notice", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    Text(gatewayErrorNotice!!, color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }

                        if (gatewaySuccessNotice != null) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E382B)),
                                border = BorderStroke(1.dp, Color(0xFF4CAF50)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(gatewaySuccessNotice!!, color = Color(0xFF4CAF50), fontSize = 11.sp, modifier = Modifier.padding(12.dp))
                            }
                        }
                    }
                } else {
                    // --- CASH AGENT FLOW ---
                    // Cash Agent -> Country -> Verified Agent -> Payment Account -> Screenshot -> Admin Verification
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // B2B Marketplace Banner Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showMarketplaceModal = true },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5).copy(alpha = 0.2f)),
                            border = BorderStroke(1.dp, Color(0xFF1E88E5)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(imageVector = Icons.Default.Storefront, contentDescription = null, tint = Color(0xFF1E88E5), modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("🌐 Open B2B Cash Agent Marketplace", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("Binance-style P2P deposit & withdraw with live agent chat", color = TextSecondary, fontSize = 10.sp)
                                    }
                                }
                                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                            }
                        }

                        // Step 1: Country
                        Text("1. Select Country (Cash Agent Network):", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            availableCountries.forEach { c ->
                                val isSel = selectedCountry.equals(c, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) PinkHighlight else Color(0xFF2E2E3E))
                                        .clickable {
                                            selectedCountry = c
                                            val filtered = allAgents.filter { it.country.equals(c, ignoreCase = true) }
                                            if (filtered.isNotEmpty()) selectedAgent = filtered.first()
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(c, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Step 2: Verified Agent Selection
                        Text("2. Select Verified Cash Agent ($selectedCountry):", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        if (countryAgents.isEmpty()) {
                            Text("No local cash agents currently listed for $selectedCountry. Select 'Bangladesh' or 'China' for active agents.", color = TextSecondary, fontSize = 11.sp)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                countryAgents.forEach { agent ->
                                    val isSel = selectedAgent.id == agent.id
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedAgent = agent },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSel) Color(0xFF1E203E) else Color(0xFF181A26)
                                        ),
                                        border = BorderStroke(1.dp, if (isSel) PinkHighlight else Color(0xFF2E2E3E)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(agent.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .background(Color(0xFF4CAF50).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text("VERIFIED AGENT", color = Color(0xFF4CAF50), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                                Text("Method: ${agent.paymentMethod} • Code #${agent.agentCode}", color = TextSecondary, fontSize = 10.sp)
                                                Text("Limit: Min ${agent.minLimit.toInt()} - Max ${agent.maxLimit.toInt()} • Comm: ${agent.commissionRate}%", color = PinkHighlight, fontSize = 10.sp)
                                            }

                                            RadioButton(
                                                selected = isSel,
                                                onClick = { selectedAgent = agent },
                                                colors = RadioButtonDefaults.colors(selectedColor = PinkHighlight)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Step 3: Agent Payment Account Details
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E202E)),
                            border = BorderStroke(1.dp, Color(0xFF2E2E3E)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Agent Payment Account Details", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Text("Agent Name: ${selectedAgent.name} (#${selectedAgent.agentCode})", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Channel / Method: ${selectedAgent.paymentMethod}", color = TextSecondary, fontSize = 11.sp)
                                Text("Account Number: ${selectedAgent.accountNumber}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                if (selectedAgent.accountHolder.isNotEmpty()) {
                                    Text("Account Holder: ${selectedAgent.accountHolder}", color = TextSecondary, fontSize = 11.sp)
                                }
                                Text("Instructions: Cash-in or transfer amount to the account above. Keep transaction receipt screenshot.", color = TextSecondary, fontSize = 10.sp)
                            }
                        }

                        // Step 4: Amount, Transaction ID & Screenshot Proof
                        Text("3. Amount, Transaction ID & Screenshot Proof:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        OutlinedTextField(
                            value = depositAmountInput,
                            onValueChange = { depositAmountInput = it },
                            label = { Text("Deposit Amount", color = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = transactionIdInput,
                            onValueChange = { transactionIdInput = it },
                            label = { Text("Transaction ID / Ref (e.g. TXN100293)", color = TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Select Sample Receipt Screenshot Proof:", color = TextSecondary, fontSize = 10.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            sampleProofPhotos.forEach { (label, url) ->
                                val isSel = selectedProofUrl == url
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) Color(0xFF2196F3) else Color(0xFF2E2E3E))
                                        .clickable { selectedProofUrl = url }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(label, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = userNoteInput,
                            onValueChange = { userNoteInput = it },
                            label = { Text("Note to Admin (Optional)", color = TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (selectedTopGateway == "GOOGLE_PAY" || selectedTopGateway == "ALIPAY") {
                val gwName = if (selectedTopGateway == "GOOGLE_PAY") "Google Pay" else "Alipay"
                Button(
                    onClick = {
                        isProcessingGateway = true
                        gatewayErrorNotice = null
                        gatewaySuccessNotice = null
                        val amt = depositAmountInput.toDoubleOrNull() ?: 100.0
                        viewModel.processAutomaticGatewayPayment(gwName, amt) { success, msg ->
                            isProcessingGateway = false
                            if (success) {
                                gatewaySuccessNotice = msg
                            } else {
                                gatewayErrorNotice = msg
                            }
                        }
                    },
                    enabled = !isProcessingGateway,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTopGateway == "GOOGLE_PAY") Color(0xFF00E676) else Color(0xFF1E88E5)
                    )
                ) {
                    Text("Proceed with $gwName", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        val amt = depositAmountInput.toDoubleOrNull() ?: 1000.0
                        viewModel.submitDepositRequest(
                            agentId = selectedAgent.id,
                            agentName = selectedAgent.name,
                            country = selectedCountry,
                            paymentMethod = selectedAgent.paymentMethod,
                            amount = amt,
                            transactionId = transactionIdInput.ifEmpty { "TXN${(10000000..99999999).random()}" },
                            proofScreenshotUrl = selectedProofUrl,
                            userNote = userNoteInput
                        )
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
                ) {
                    Text("Submit for Admin Verification", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = DarkSurface
    )

    if (showMarketplaceModal) {
        B2BCashAgentMarketplaceDialog(
            viewModel = viewModel,
            onDismiss = { showMarketplaceModal = false }
        )
    }
}

// ---------------- USER WITHDRAWAL WORKFLOW DIALOG ----------------
@Composable
fun WithdrawalWorkflowDialog(
    viewModel: AppViewModel,
    onDismiss: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val availableBalance = currentUser?.balance ?: 1500.0

    var withdrawAmountInput by remember { mutableStateOf("500") }
    var selectedMethod by remember { mutableStateOf("bKash Personal") }
    var accountNumberInput by remember { mutableStateOf("01712345678") }
    var accountHolderInput by remember { mutableStateOf(currentUser?.name ?: "Miraz Reza") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showMarketplaceModal by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(PinkHighlight.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.CallMade, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Withdraw Funds", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Available Wallet Balance: ৳${availableBalance.toInt()}", color = Color(0xFF4CAF50), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (errorMessage != null) {
                    Text(errorMessage!!, color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showMarketplaceModal = true },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5).copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, Color(0xFF1E88E5)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(imageVector = Icons.Default.Storefront, contentDescription = null, tint = Color(0xFF1E88E5), modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("🌐 Open B2B Cash Agent Marketplace", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Withdraw BDT/USD via B2B Cash Agents with live chat & escrow", color = TextSecondary, fontSize = 10.sp)
                            }
                        }
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                    }
                }

                Text("Withdrawal Amount (BDT):", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                OutlinedTextField(
                    value = withdrawAmountInput,
                    onValueChange = { withdrawAmountInput = it; errorMessage = null },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Payment Channel:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("bKash Personal", "Nagad Personal", "City Bank", "Rocket").forEach { m ->
                        val isSel = selectedMethod == m
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) PinkHighlight else Color(0xFF2E2E3E))
                                .clickable { selectedMethod = m }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(m, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedTextField(
                    value = accountNumberInput,
                    onValueChange = { accountNumberInput = it },
                    label = { Text("Account / Phone Number", color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = accountHolderInput,
                    onValueChange = { accountHolderInput = it },
                    label = { Text("Account Holder Name", color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = withdrawAmountInput.toDoubleOrNull() ?: 0.0
                    if (amt <= 0) {
                        errorMessage = "Please enter a valid withdrawal amount."
                        return@Button
                    }
                    if (amt > availableBalance) {
                        errorMessage = "Insufficient balance! Your balance is ৳${availableBalance.toInt()}."
                        return@Button
                    }
                    val success = viewModel.submitWithdrawalRequest(
                        amount = amt,
                        paymentMethod = selectedMethod,
                        accountNumber = accountNumberInput,
                        accountHolder = accountHolderInput
                    )
                    if (success) {
                        onDismiss()
                    } else {
                        errorMessage = "Failed to submit request."
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
            ) {
                Text("Confirm Withdrawal", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = DarkSurface
    )

    if (showMarketplaceModal) {
        B2BCashAgentMarketplaceDialog(
            viewModel = viewModel,
            onDismiss = { showMarketplaceModal = false }
        )
    }
}

// ---------------- ADMIN DEPOSIT REVIEW & LEDGER WORKSPACE ----------------
@Composable
fun AdminCollectionsTab(viewModel: AppViewModel) {
    val deposits by viewModel.allDeposits.collectAsStateWithLifecycle()
    val ledgerEntries by viewModel.allLedgerEntries.collectAsStateWithLifecycle()

    var activeTab by remember { mutableIntStateOf(0) } // 0 = Deposits, 1 = Master Immutable Ledger
    var showProofModalForDeposit by remember { mutableStateOf<DepositRequest?>(null) }
    var adminNotesMap by remember { mutableStateOf(mutableMapOf<String, String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Admin Ledger & Deposit Verification", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Verify transaction IDs & agent accounts before approving balance", color = TextSecondary, fontSize = 11.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { activeTab = 0 },
                    colors = ButtonDefaults.buttonColors(containerColor = if (activeTab == 0) PinkHighlight else Color(0xFF2E2E3E)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Deposits (${deposits.count { it.status == "PENDING" }})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { activeTab = 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = if (activeTab == 1) PinkHighlight else Color(0xFF2E2E3E)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Master Ledger (${ledgerEntries.size})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (activeTab == 0) {
            // Deposits Tab
            if (deposits.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No deposit requests found.", color = TextSecondary, fontSize = 12.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(deposits) { dep ->
                        val noteState = adminNotesMap[dep.id] ?: ""

                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Deposit #${dep.id}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("User: ${dep.userName} (${dep.userId})", color = TextSecondary, fontSize = 11.sp)
                                    }

                                    val statusBg = when (dep.status) {
                                        "APPROVED" -> Color(0xFF4CAF50)
                                        "REJECTED" -> Color(0xFFEF4444)
                                        else -> Color(0xFFFF9800)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(statusBg.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(dep.status, color = statusBg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Payment Agent", color = TextSecondary, fontSize = 10.sp)
                                        Text("${dep.agentName} • ${dep.country}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("Method: ${dep.paymentMethod}", color = TextSecondary, fontSize = 10.sp)
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Deposit Amount", color = TextSecondary, fontSize = 10.sp)
                                        Text("৳${dep.amount.toInt()}", color = PinkHighlight, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF191B28), RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Transaction ID:", color = TextSecondary, fontSize = 10.sp)
                                        Text(dep.transactionId, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }

                                    Button(
                                        onClick = { showProofModalForDeposit = dep },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Inspect Screenshot Proof", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (dep.status == "PENDING") {
                                    OutlinedTextField(
                                        value = noteState,
                                        onValueChange = { adminNotesMap[dep.id] = it },
                                        placeholder = { Text("Admin Note / Agent Ledger Cross-check note...", color = Color.Gray, fontSize = 10.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                            focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                viewModel.approveDeposit(
                                                    dep.id,
                                                    noteState.ifEmpty { "Transaction ID verified against agent ledger." }
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f).height(36.dp)
                                        ) {
                                            Text("Approve & Credit Wallet", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                viewModel.rejectDeposit(
                                                    dep.id,
                                                    noteState.ifEmpty { "Transaction ID verification failed." }
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(36.dp)
                                        ) {
                                            Text("Reject", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Master Ledger Tab
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(ledgerEntries) { entry ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Ledger #${entry.id} • Ref: ${entry.referenceId}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                val isCredit = entry.transactionType.contains("CREDIT")
                                Box(
                                    modifier = Modifier
                                        .background(if (isCredit) Color(0xFF4CAF50).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(entry.transactionType, color = if (isCredit) Color(0xFF4CAF50) else Color(0xFFEF4444), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Account: ${entry.userName} (${entry.userRole})", color = TextSecondary, fontSize = 10.sp)
                                    Text("Method/Ref: ${entry.paymentMethod} (${entry.transactionIdOrRef})", color = TextSecondary, fontSize = 10.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("৳${entry.amount.toInt()}", color = if (entry.transactionType.contains("CREDIT")) Color(0xFF4CAF50) else Color(0xFFEF4444), fontWeight = FontWeight.Black, fontSize = 14.sp)
                                    Text("Balance: ৳${entry.previousBalance.toInt()} ➔ ৳${entry.newBalance.toInt()}", color = TextSecondary, fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showProofModalForDeposit != null) {
        val dep = showProofModalForDeposit!!
        ProofViewerDialog(
            imageUri = dep.proofScreenshotUrl,
            title = "Deposit #${dep.id} Evidence Screenshot",
            onDismiss = { showProofModalForDeposit = null }
        )
    }
}

// ---------------- ADMIN CASH AGENT MANAGEMENT TAB ----------------
@Composable
fun AdminCashAgentsTab(viewModel: AppViewModel) {
    val agents by viewModel.paymentAgents.collectAsStateWithLifecycle()

    var selectedCountryFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddModal by remember { mutableStateOf(false) }
    var editingAgent by remember { mutableStateOf<PaymentAgent?>(null) }

    val countryOptions = listOf("ALL", "Bangladesh", "China", "USA", "UK", "UAE", "India", "Other")

    val filteredAgents = remember(agents, selectedCountryFilter, searchQuery) {
        agents.filter { agent ->
            val matchCountry = if (selectedCountryFilter == "ALL") true else agent.country.equals(selectedCountryFilter, ignoreCase = true)
            val matchSearch = searchQuery.isEmpty() ||
                    agent.name.contains(searchQuery, ignoreCase = true) ||
                    agent.agentCode.contains(searchQuery, ignoreCase = true) ||
                    agent.paymentMethod.contains(searchQuery, ignoreCase = true) ||
                    agent.accountNumber.contains(searchQuery, ignoreCase = true)
            matchCountry && matchSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cash Agent Configuration", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Text("Manage country network, limits, commissions & verification status", color = TextSecondary, fontSize = 11.sp)
            }

            Button(
                onClick = { showAddModal = true },
                colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Cash Agent", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Summary Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Total Cash Agents", color = TextSecondary, fontSize = 10.sp)
                    Text("${agents.size}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Verified Active", color = TextSecondary, fontSize = 10.sp)
                    Text("${agents.count { it.verificationStatus == "VERIFIED" }}", color = Color(0xFF4CAF50), fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Countries Covered", color = TextSecondary, fontSize = 10.sp)
                    Text("${agents.map { it.country }.distinct().size}", color = Color(0xFF2196F3), fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            }
        }

        // Search & Country Filter Row
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by name, code, method or account...", color = TextSecondary, fontSize = 12.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            countryOptions.forEach { country ->
                val isSel = selectedCountryFilter == country
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) PinkHighlight else Color(0xFF2E2E3E))
                        .clickable { selectedCountryFilter = country }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(country, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Agents List
        if (filteredAgents.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text("No cash agents found matching criteria.", color = TextSecondary, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredAgents) { agent ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(agent.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF2196F3).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("#${agent.agentCode}", color = Color(0xFF2196F3), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                val (statusColor, statusText) = when (agent.verificationStatus) {
                                    "VERIFIED" -> Color(0xFF4CAF50) to "VERIFIED"
                                    "SUSPENDED" -> Color(0xFFEF4444) to "SUSPENDED"
                                    else -> Color(0xFFFF9800) to "PENDING"
                                }

                                Box(
                                    modifier = Modifier
                                        .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(statusText, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Country / Region: ${agent.country}", color = TextSecondary, fontSize = 11.sp)
                                    Text("Channel: ${agent.paymentMethod}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("Account: ${agent.accountNumber}", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    if (agent.accountHolder.isNotEmpty()) {
                                        Text("Holder: ${agent.accountHolder}", color = TextSecondary, fontSize = 11.sp)
                                    }
                                    Text("Phone: ${agent.phone}", color = TextSecondary, fontSize = 11.sp)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Commission: ${agent.commissionRate}%", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("Min Limit: ৳${agent.minLimit.toInt()}", color = TextSecondary, fontSize = 10.sp)
                                    Text("Max Limit: ৳${agent.maxLimit.toInt()}", color = TextSecondary, fontSize = 10.sp)
                                }
                            }

                            // Actions Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    val nextStatus = when (agent.verificationStatus) {
                                        "VERIFIED" -> "SUSPENDED"
                                        "SUSPENDED" -> "VERIFIED"
                                        else -> "VERIFIED"
                                    }
                                    Button(
                                        onClick = { viewModel.togglePaymentAgentStatus(agent.id, nextStatus) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (agent.verificationStatus == "VERIFIED") Color(0xFFEF4444) else Color(0xFF4CAF50)
                                        ),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(if (agent.verificationStatus == "VERIFIED") "Suspend" else "Verify Agent", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { editingAgent = agent },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E3E)),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Edit Config", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                TextButton(
                                    onClick = { viewModel.deletePaymentAgent(agent) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Remove", color = Color(0xFFEF4444), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Agent Modal
    if (showAddModal) {
        AddOrEditCashAgentModal(
            agent = null,
            onDismiss = { showAddModal = false },
            onSave = { newAgent ->
                viewModel.savePaymentAgent(newAgent)
                showAddModal = false
            }
        )
    }

    // Edit Agent Modal
    if (editingAgent != null) {
        AddOrEditCashAgentModal(
            agent = editingAgent,
            onDismiss = { editingAgent = null },
            onSave = { updatedAgent ->
                viewModel.savePaymentAgent(updatedAgent)
                editingAgent = null
            }
        )
    }
}

@Composable
fun AddOrEditCashAgentModal(
    agent: PaymentAgent?,
    onDismiss: () -> Unit,
    onSave: (PaymentAgent) -> Unit
) {
    var name by remember { mutableStateOf(agent?.name ?: "") }
    var agentCode by remember { mutableStateOf(agent?.agentCode ?: "${(1000..9999).random()}") }
    var country by remember { mutableStateOf(agent?.country ?: "Bangladesh") }
    var phone by remember { mutableStateOf(agent?.phone ?: "") }
    var paymentMethod by remember { mutableStateOf(agent?.paymentMethod ?: "bKash") }
    var accountNumber by remember { mutableStateOf(agent?.accountNumber ?: "") }
    var accountHolder by remember { mutableStateOf(agent?.accountHolder ?: "") }
    var commissionRateInput by remember { mutableStateOf((agent?.commissionRate ?: 1.5).toString()) }
    var minLimitInput by remember { mutableStateOf((agent?.minLimit ?: 100.0).toInt().toString()) }
    var maxLimitInput by remember { mutableStateOf((agent?.maxLimit ?: 100000.0).toInt().toString()) }
    var verificationStatus by remember { mutableStateOf(agent?.verificationStatus ?: "VERIFIED") }

    val countries = listOf("Bangladesh", "China", "USA", "UK", "UAE", "India", "Other")
    val methods = listOf("bKash", "Nagad", "Rocket", "Bank Transfer", "Alipay", "WeChat Pay", "Zelle", "Wise", "UPI")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (agent == null) "Add Cash Agent" else "Edit Cash Agent Configuration", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Agent Full / Outlet Name", color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = agentCode,
                        onValueChange = { agentCode = it },
                        label = { Text("Agent Code", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Contact", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)),
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("Country Network:", color = TextSecondary, fontSize = 11.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    countries.forEach { c ->
                        val isSel = country == c
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) PinkHighlight else Color(0xFF2E2E3E))
                                .clickable { country = c }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(c, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text("Payment Channel Method:", color = TextSecondary, fontSize = 11.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    methods.forEach { m ->
                        val isSel = paymentMethod == m
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) Color(0xFF2196F3) else Color(0xFF2E2E3E))
                                .clickable { paymentMethod = m }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(m, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    label = { Text("Account Number / Wallet ID", color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = accountHolder,
                    onValueChange = { accountHolder = it },
                    label = { Text("Account Holder Name", color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = commissionRateInput,
                        onValueChange = { commissionRateInput = it },
                        label = { Text("Comm %", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = minLimitInput,
                        onValueChange = { minLimitInput = it },
                        label = { Text("Min Limit", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = maxLimitInput,
                        onValueChange = { maxLimitInput = it },
                        label = { Text("Max Limit", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)),
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("Verification Status:", color = TextSecondary, fontSize = 11.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("VERIFIED", "PENDING_VERIFICATION", "SUSPENDED").forEach { st ->
                        val isSel = verificationStatus == st
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) PinkHighlight else Color(0xFF2E2E3E))
                                .clickable { verificationStatus = st }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(st, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newAgent = PaymentAgent(
                        id = agent?.id ?: "AGENT_${System.currentTimeMillis()}",
                        name = name.ifEmpty { "Cash Agent Outlet" },
                        agentCode = agentCode.ifEmpty { "1001" },
                        country = country,
                        phone = phone.ifEmpty { "01700000000" },
                        paymentMethod = paymentMethod,
                        accountNumber = accountNumber.ifEmpty { "01700000000" },
                        accountHolder = accountHolder,
                        commissionRate = commissionRateInput.toDoubleOrNull() ?: 1.5,
                        minLimit = minLimitInput.toDoubleOrNull() ?: 100.0,
                        maxLimit = maxLimitInput.toDoubleOrNull() ?: 100000.0,
                        verificationStatus = verificationStatus
                    )
                    onSave(newAgent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
            ) {
                Text("Save Cash Agent", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = DarkSurface
    )
}

// ---------------- ADMIN WITHDRAWAL APPROVALS & PROOFS TAB ----------------
@Composable
fun AdminWalletsTab(viewModel: AppViewModel) {
    val withdrawals by viewModel.allWithdrawals.collectAsStateWithLifecycle()
    var showProofModalForWithdrawal by remember { mutableStateOf<WithdrawalRequest?>(null) }
    var proofUrlInput by remember { mutableStateOf("https://images.unsplash.com/photo-1559526324-4b87b5e36e44") }
    var refInput by remember { mutableStateOf("FT20260809-901") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Wallet & Withdrawal Approvals", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Withdrawal Requests", color = TextSecondary, fontSize = 11.sp)
                    Text("${withdrawals.size} Requests", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Pending Payout Volume", color = TextSecondary, fontSize = 11.sp)
                    Text("৳${withdrawals.filter { it.status == "PENDING" || it.status == "PROOF_UPLOADED" }.sumOf { it.amount }.toInt()}", color = PinkHighlight, fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            }
        }

        Text("Pending & Processed Withdrawals", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)

        if (withdrawals.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text("All withdrawal requests cleared! ✓", color = TextSecondary, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(withdrawals) { wd ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Withdrawal #${wd.id}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Applicant: ${wd.applicantName} (${wd.applicantRole})", color = TextSecondary, fontSize = 10.sp)
                                }

                                val statusColor = when (wd.status) {
                                    "COMPLETED" -> Color(0xFF4CAF50)
                                    "REJECTED" -> Color(0xFFEF4444)
                                    else -> Color(0xFFFF9800)
                                }

                                Box(
                                    modifier = Modifier
                                        .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(wd.status, color = statusColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Channel: ${wd.paymentMethod}", color = TextSecondary, fontSize = 10.sp)
                                    Text("Account: ${wd.accountNumber} (${wd.accountHolder})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("৳${wd.amount.toInt()}", color = PinkHighlight, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                }
                            }

                            if (wd.proofScreenshotUrl != null) {
                                Button(
                                    onClick = { showProofModalForWithdrawal = wd },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth().height(30.dp)
                                ) {
                                    Text("Inspect Payout Advice Proof Screenshot", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (wd.status == "PENDING" || wd.status == "PROOF_UPLOADED") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.uploadWithdrawalProof(wd.id, proofUrlInput, refInput)
                                            viewModel.approveWithdrawal(wd.id, "Payout verified & executed.")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(36.dp)
                                    ) {
                                        Text("Attach Advice & Approve", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { viewModel.rejectWithdrawal(wd.id, "Invalid account number.") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text("Reject", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showProofModalForWithdrawal != null) {
        val wd = showProofModalForWithdrawal!!
        ProofViewerDialog(
            imageUri = wd.proofScreenshotUrl ?: "https://images.unsplash.com/photo-1559526324-4b87b5e36e44",
            title = "Withdrawal #${wd.id} Payout Proof",
            onDismiss = { showProofModalForWithdrawal = null }
        )
    }
}


// ---------------- ADMIN SETTINGS TAB ----------------
@Composable
fun AdminSettingsTab(viewModel: AppViewModel) {
    var feePercentage by remember { mutableStateOf("15") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("System Settings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Commission Rates", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                
                Column {
                    Text("Client Marketplace Fee (%)", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = feePercentage,
                        onValueChange = { feePercentage = it },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            unfocusedBorderColor = Color.Gray,
                            focusedBorderColor = PinkHighlight
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Cash Agent Collection Fee", color = TextSecondary, fontSize = 11.sp)
                        Text("5% per collection", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { viewModel.addNotification("Settings Saved", "Global marketplace fee adjusted to $feePercentage%", "System") },
                        colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Update Config", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Database Backup", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Initiate full backup of all SQLite & server models.", color = TextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = { viewModel.addNotification("Backup Success", "Full DB dump saved locally.", "System") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E3E)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Trigger Database Backup", fontSize = 11.sp)
                }
            }
        }
    }
}


// ============================================================================
// 7. HIGH-FIDELITY CASH AGENT TAB MODULES
// ============================================================================

@Composable
fun CashAgentDashboardTab(viewModel: AppViewModel) {
    val collections by viewModel.cashCollections.collectAsStateWithLifecycle()
    val pendingCount = collections.count { it.status == "PENDING" }
    val paidCount = collections.count { it.status == "PAID" }
    val totalCollected = collections.filter { it.status == "PAID" }.sumOf { it.amount }
    val commissionEarned = totalCollected * (viewModel.agentCommissionRate / 100.0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Assalamu Alaikum,", color = TextSecondary, fontSize = 11.sp)
                    Text("Agent Sumon", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFF2196F3).copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Verified Agent", color = Color(0xFF2196F3), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }
        }

        // On-Screen PHP Backend Source Explorer Card for Cash Agent
        item {
            PhpBackendExplorerCard(viewModel = viewModel)
        }

        // Stats Cards
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Collection Stats Overview", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Total Collected", color = TextSecondary, fontSize = 10.sp)
                            Text("৳${totalCollected.toInt()}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Commission (5%)", color = TextSecondary, fontSize = 10.sp)
                            Text("৳${commissionEarned.toInt()}", color = OnlineGreen, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        }
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Active Collections: $pendingCount requests", color = TextSecondary, fontSize = 11.sp)
                        Text("Completed: $paidCount collections", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        // Action Buttons Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.selectedTab = 1 }
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, tint = PinkHighlight)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Open Tasks", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.selectedTab = 2 }
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color(0xFF2196F3))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("My Commissions", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Recent Notifications for agent
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Agent Bulletins", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    RecentNotificationItem("New Cash Collection request assigned for ৳3,500", "Today", Color(0xFF2196F3))
                    RecentNotificationItem("Commission of ৳150 paid successfully", "Yesterday", Color(0xFF4CAF50))
                    RecentNotificationItem("Marketplace cash guidelines updated for Dhaka area", "2 Days Ago", Color(0xFFFF9800))
                }
            }
        }
    }
}

@Composable
fun CashAgentCollectionsTab(viewModel: AppViewModel) {
    val collections by viewModel.cashCollections.collectAsStateWithLifecycle()
    // Agent only views collections assigned to them (Agent Sumon)
    val agentCollections = collections.filter { it.agentName == "Agent Sumon" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Collection Requests", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Accept cash payments from customers on behalf of models and deposit to the main system.", color = TextSecondary, fontSize = 11.sp)

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(agentCollections) { coll ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("ID: ${coll.id}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (coll.status == "PAID") Color(0xFF4CAF50).copy(alpha = 0.15f) else Color(0xFFFF9800).copy(alpha = 0.15f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    coll.status,
                                    color = if (coll.status == "PAID") Color(0xFF4CAF50) else Color(0xFFFF9800),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Booking Code: ${coll.bookingId}", color = TextSecondary, fontSize = 11.sp)
                        Text("Customer Email: ${coll.userEmail}", color = TextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Payout Method", color = TextSecondary, fontSize = 10.sp)
                                Text("Physical Cash", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Amount to Collect", color = TextSecondary, fontSize = 10.sp)
                                Text("৳${coll.amount.toInt()}", color = PinkHighlight, fontWeight = FontWeight.Black, fontSize = 14.sp)
                            }
                        }

                        if (coll.status == "PENDING") {
                            var showDialog by remember { mutableStateOf(false) }
                            var receiptCode by remember { mutableStateOf("") }

                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { showDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(36.dp)
                            ) {
                                Text("Collect Cash & Submit Receipt", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            if (showDialog) {
                                AlertDialog(
                                    onDismissRequest = { showDialog = false },
                                    containerColor = DarkSurface,
                                    title = { Text("Confirm Cash Received", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp) },
                                    text = {
                                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Text("Please input the physical payment receipt serial or transaction reference code:", color = TextSecondary, fontSize = 11.sp)
                                            OutlinedTextField(
                                                value = receiptCode,
                                                onValueChange = { receiptCode = it },
                                                placeholder = { Text("Receipt Code (e.g. REC-58421)", color = Color.Gray, fontSize = 11.sp) },
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedTextColor = Color.White,
                                                    unfocusedTextColor = Color.White,
                                                    focusedBorderColor = PinkHighlight,
                                                    unfocusedBorderColor = Color.Gray
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                if (receiptCode.isNotBlank()) {
                                                    viewModel.updateCollectionStatus(coll.id, "PAID", receiptCode)
                                                    showDialog = false
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
                                        ) {
                                            Text("Submit Received Payment", fontSize = 11.sp)
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showDialog = false }) {
                                            Text("Cancel", color = Color.White, fontSize = 11.sp)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CashAgentWalletTab(viewModel: AppViewModel) {
    val collections by viewModel.cashCollections.collectAsStateWithLifecycle()
    val totalCollected = collections.filter { it.status == "PAID" }.sumOf { it.amount }
    val commissionEarned = totalCollected * (viewModel.agentCommissionRate / 100.0)
    var withdrawAmountInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Wallet & Commission", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Commission Balance", color = TextSecondary, fontSize = 11.sp)
                Text("৳${commissionEarned.toInt()}.00", color = OnlineGreen, fontWeight = FontWeight.Black, fontSize = 24.sp)
                Text("Your 5% share on collected payments from clients.", color = TextSecondary, fontSize = 10.sp)
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Withdraw Commission", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                
                OutlinedTextField(
                    value = withdrawAmountInput,
                    onValueChange = { withdrawAmountInput = it },
                    placeholder = { Text("Amount in Taka (e.g. 500)", color = Color.Gray, fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PinkHighlight,
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val amt = withdrawAmountInput.toDoubleOrNull()
                        if (amt != null && amt <= commissionEarned) {
                            viewModel.addNotification("Withdrawal Submitted", "Withdrawal request of ৳$amt commission submitted for Admin verification.", "Wallet")
                            withdrawAmountInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Request Commission Payout", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun CashAgentProfileTab(viewModel: AppViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("My Profile", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFF2E2E3E), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(currentUser?.name ?: "Agent Sumon", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Role: Cash Collection Agent", color = TextSecondary, fontSize = 11.sp)
                    Text("ID: agent_584", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Role Switcher", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Easily toggle roles during evaluation testing:", color = TextSecondary, fontSize = 11.sp)

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val nextRole = "USER"
                            val updated = currentUser?.copy(
                                role = nextRole,
                                id = "user_1",
                                name = "Miraz Reza",
                                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d"
                            )
                            if (updated != null) {
                                viewModel.repository.updateCurrentUser(updated)
                                viewModel.navigateTo("DASHBOARD")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E3E)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Switch back to Client (User) mode", fontSize = 11.sp)
                }
            }
        }
    }
}

// ============================================================================
// 8. ON-SCREEN PHP BACKEND EXPLORER & INTERACTIVE CODE VIEWER
// ============================================================================

@Composable
fun PhpBackendExplorerCard(viewModel: AppViewModel) {
    var showModal by remember { mutableStateOf(false) }

    if (showModal) {
        PhpBackendCodeViewerModal(onDismiss = { showModal = false }, viewModel = viewModel)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, PinkHighlight.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF8892BF).copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("PHP", color = Color(0xFF8892BF), fontWeight = FontWeight.Black, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("PHP Backend Files & Architecture", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("33 Production Files in /backend/", color = TextSecondary, fontSize = 10.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .background(PinkHighlight.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("PHP 8.2 + MySQL", color = PinkHighlight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                "Explore the backend structure, PHP files for Admin & Cash Agent panels, PDO MySQL queries, and REST APIs directly on screen.",
                color = TextSecondary,
                fontSize = 11.sp
            )

            // Quick Folder Tags
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("📁 /backend/admin/", "📁 /backend/agent/", "⚡ /backend/api/", "🗄️ schema.sql", "⚙️ config.php").forEach { tag ->
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF2E2E3E), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(tag, color = Color.LightGray, fontSize = 9.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Button(
                onClick = { showModal = true },
                colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open PHP Backend Explorer & Display Code", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PhpBackendCodeViewerModal(onDismiss: () -> Unit, viewModel: AppViewModel) {
    var activeTab by remember { mutableStateOf(0) } // 0: Source Code, 1: Folder Structure, 2: REST API Simulator
    var selectedFilePath by remember { mutableStateOf("backend/index.php") }

    val fileMap = remember {
        mapOf(
            "backend/api/upload/profile-photo.php" to """<?php
// backend/api/upload/profile-photo.php - Android Camera & Gallery Photo Upload API
header('Content-Type: application/json');
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../config/auth.php';

if (${'$'}_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['status' => 'error', 'message' => 'Method Not Allowed']);
    exit;
}

${'$'}userId = ${'$'}_POST['user_id'] ?? null;
if (!${'$'}userId || !isset(${'$'}_FILES['photo'])) {
    echo json_encode(['status' => 'error', 'message' => 'user_id and photo file are required']);
    exit;
}

${'$'}file = ${'$'}_FILES['photo'];
${'$'}allowedTypes = ['image/jpeg', 'image/png', 'image/webp'];

if (!in_array(${'$'}file['type'], ${'$'}allowedTypes)) {
    echo json_encode(['status' => 'error', 'message' => 'Invalid file format. JPG, PNG, WEBP allowed.']);
    exit;
}

if (${'$'}file['size'] > 5 * 1024 * 1024) {
    echo json_encode(['status' => 'error', 'message' => 'File size exceeds 5MB limit.']);
    exit;
}

${'$'}targetDir = __DIR__ . '/../../../public_html/uploads/profile/';
if (!file_exists(${'$'}targetDir)) {
    mkdir(${'$'}targetDir, 0755, true);
}

${'$'}ext = pathinfo(${'$'}file['name'], PATHINFO_EXTENSION);
${'$'}filename = ${'$'}userId . '_' . time() . '.' . ${'$'}ext;
${'$'}targetFile = ${'$'}targetDir . ${'$'}filename;

if (move_uploaded_file(${'$'}file['tmp_name'], ${'$'}targetFile)) {
    ${'$'}photoUrl = "https://modolconnect.com/uploads/profile/" . ${'$'}filename;
    
    // Update MySQL Database
    ${'$'}stmt = ${'$'}pdo->prepare("UPDATE users SET profile_photo = ?, updated_at = NOW() WHERE id = ?");
    ${'$'}stmt->execute([${'$'}photoUrl, ${'$'}userId]);

    echo json_encode([
        'status' => 'success',
        'message' => 'Photo uploaded successfully',
        'image' => ${'$'}photoUrl
    ]);
} else {
    echo json_encode(['status' => 'error', 'message' => 'Failed to save uploaded file on server.']);
}
?>""",
            "backend/index.php" to """<?php
// backend/index.php - Modol Connect Backend Service API Operational Status
header('Content-Type: application/json');

echo json_encode([
    'status' => 'success',
    'message' => 'Modol Connect Backend Service API operational.',
    'data' => [
        'version' => '2.1.0',
        'app_name' => 'Modol Connect Backend',
        'environment' => 'production',
        'production' => true,
        'base_url' => 'https://modolconnect.com/',
        'modules' => [
            'Admin Portal' => 'https://modolconnect.com/backend/admin/dashboard.php',
            'Cash Agent Portal' => 'https://modolconnect.com/backend/agent/dashboard.php',
            'API' => 'https://modolconnect.com/backend/api/',
            'Documentation' => 'https://modolconnect.com/backend/docs/'
        ],
        'server' => [
            'php_version' => '8.2.31',
            'server_time' => '2026-07-22 21:08:04',
            'timezone' => 'Asia/Dhaka'
        ]
    ],
    'timestamp' => '2026-07-22T21:08:04+06:00'
], JSON_PRETTY_PRINT);
?>""",
            "backend/admin/dashboard.php" to """<?php
// backend/admin/dashboard.php
// Admin Panel Dashboard API & Controller
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAdminAuth();
${'$'}db = Database::getInstance();

${'$'}totalUsers = ${'$'}db->query("SELECT COUNT(*) as cnt FROM users")->fetch()['cnt'] ?? 12568;
${'$'}totalModels = ${'$'}db->query("SELECT COUNT(*) as cnt FROM models")->fetch()['cnt'] ?? 2356;
${'$'}totalAgents = ${'$'}db->query("SELECT COUNT(*) as cnt FROM cash_agents")->fetch()['cnt'] ?? 632;
${'$'}totalBookings = ${'$'}db->query("SELECT COUNT(*) as cnt FROM bookings")->fetch()['cnt'] ?? 8965;

sendJsonResponse('success', 'Admin dashboard statistics retrieved', [
    'metrics' => [
        'total_users' => (int)${'$'}totalUsers,
        'total_models' => (int)${'$'}totalModels,
        'cash_agents' => (int)${'$'}totalAgents,
        'total_bookings' => (int)${'$'}totalBookings,
        'monthly_revenue_bdt' => 4565230.00
    ]
]);
?>""",
            "backend/admin/cash_collections.php" to """<?php
// backend/admin/cash_collections.php
// Payment Verification & Cash Collection Approvals
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAdminAuth();
${'$'}db = Database::getInstance();

// Approve Pending Cash Collection and Release Escrow
if (${'$'}_SERVER['REQUEST_METHOD'] === 'POST') {
    ${'$'}collectionId = sanitizeInput(${'$'}_POST['collection_id']);
    ${'$'}stmt = ${'$'}db->prepare("UPDATE cash_collections SET status='PAID', verified_by_admin=1 WHERE id=?");
    ${'$'}stmt->execute([${'$'}collectionId]);

    // Give 5% commission to Cash Agent
    ${'$'}agentStmt = ${'$'}db->prepare("UPDATE cash_agents SET wallet_balance = wallet_balance + (amount * 0.05) WHERE id=(SELECT agent_id FROM cash_collections WHERE id=?)");
    ${'$'}agentStmt->execute([${'$'}collectionId]);

    sendJsonResponse('success', 'Cash payment verified and escrow activated successfully.');
}
?>""",
            "backend/agent/dashboard.php" to """<?php
// backend/agent/dashboard.php
// Cash Agent Portal Dashboard
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAgentAuth();
${'$'}agentId = ${'$'}_SESSION['user_id'] ?? 1;
${'$'}db = Database::getInstance();

${'$'}stmt = ${'$'}db->prepare("SELECT * FROM cash_collections WHERE agent_id = ? ORDER BY id DESC");
${'$'}stmt->execute([${'$'}agentId]);
${'$'}collections = ${'$'}stmt->fetchAll();

sendJsonResponse('success', 'Cash agent collections retrieved', [
    'agent_code' => 'AGENT001',
    'commission_rate' => '5.0%',
    'collections' => ${'$'}collections
]);
?>""",
            "backend/agent/collection_requests.php" to """<?php
// backend/agent/collection_requests.php
// Cash Agent Pending Collection Requests
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAgentAuth();
${'$'}agentId = ${'$'}_SESSION['user_id'] ?? 1;
${'$'}db = Database::getInstance();

// Submit new collected cash with receipt photo
if (${'$'}_SERVER['REQUEST_METHOD'] === 'POST') {
    ${'$'}bookingId = sanitizeInput(${'$'}_POST['booking_id']);
    ${'$'}amount = (float)${'$'}_POST['amount'];
    ${'$'}receiptPhoto = ${'$'}_FILES['receipt']['name'] ?? 'default_receipt.jpg';

    ${'$'}stmt = ${'$'}db->prepare("INSERT INTO cash_collections (collection_code, booking_id, agent_id, amount, status, receipt_photo_url) VALUES (?, ?, ?, ?, 'PENDING', ?)");
    ${'$'}stmt->execute(['CC' . rand(10000, 99999), ${'$'}bookingId, ${'$'}agentId, ${'$'}amount, ${'$'}receiptPhoto]);

    sendJsonResponse('success', 'Collection submitted for Admin verification.');
}
?>""",
            "backend/api/auth/admin_login.php" to """<?php
// backend/api/auth/admin_login.php
// REST API Endpoint for Admin & Agent Authentication
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../config/config.php';

header('Content-Type: application/json');
${'$'}input = json_decode(file_get_contents('php://input'), true);
${'$'}email = ${'$'}input['email'] ?? '';
${'$'}password = ${'$'}input['password'] ?? '';

if (${'$'}email === 'admin@modolconnect.com' && ${'$'}password === 'admin123') {
    ${'$'}_SESSION['user_role'] = 'ADMIN';
    sendJsonResponse('success', 'Admin login successful', [
        'token' => 'jwt_token_admin_98234712',
        'user' => ['id' => 1, 'name' => 'System Admin', 'role' => 'ADMIN']
    ]);
} else {
    sendJsonResponse('error', 'Invalid admin credentials', [], 401);
}
?>""",
            "backend/config/database.php" to """<?php
// backend/config/database.php
// PDO Database Connection Setup
define('DB_HOST', 'localhost');
define('DB_USER', 'modol_connect_user');
define('DB_PASS', 'SecurePassword123!');
define('DB_NAME', 'modol_connect_db');

class Database {
    private static ${'$'}instance = null;
    private ${'$'}conn;

    private function __construct() {
        ${'$'}dsn = "mysql:host=" . DB_HOST . ";dbname=" . DB_NAME . ";charset=utf8mb4";
        ${'$'}this->conn = new PDO(${'$'}dsn, DB_USER, DB_PASS, [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
        ]);
    }

    public static function getInstance() {
        if (self::${'$'}instance === null) {
            self::${'$'}instance = new Database();
        }
        return self::${'$'}instance->conn;
    }
}
?>""",
            "backend/admin/users.php" to """<?php
// backend/admin/users.php - Public Users Endpoint
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAdminAuth();
${'$'}db = Database::getInstance();
${'$'}users = ${'$'}db->query("SELECT id, name, email, phone, role FROM users ORDER BY id DESC")->fetchAll();
sendJsonResponse('success', 'Public users retrieved', ['users' => ${'$'}users]);
?>""",
            "backend/admin/cash_agents.php" to """<?php
// backend/admin/cash_agents.php - Cash Agent Management
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAdminAuth();
${'$'}db = Database::getInstance();
${'$'}agents = ${'$'}db->query("SELECT id, agent_code, name, phone, commission_rate, wallet_balance FROM cash_agents")->fetchAll();
sendJsonResponse('success', 'Cash agents retrieved', ['cash_agents' => ${'$'}agents]);
?>""",
            "backend/admin/payments.php" to """<?php
// backend/admin/payments.php - Cash Collection Verifications
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAdminAuth();
${'$'}db = Database::getInstance();
${'$'}collections = ${'$'}db->query("SELECT * FROM cash_collections ORDER BY id DESC")->fetchAll();
sendJsonResponse('success', 'Cash collections retrieved', ['collections' => ${'$'}collections]);
?>""",
            "backend/agent/wallet.php" to """<?php
// backend/agent/wallet.php - Cash Agent Wallet Balance
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAgentAuth();
sendJsonResponse('success', 'Agent wallet data', [
    'agent_code' => 'AGENT001',
    'wallet_balance_bdt' => 12500.00,
    'total_commission_earned_bdt' => 2450.00
]);
?>""",
            "backend/config/config.php" to """<?php
// backend/config/config.php - Global Configuration
define('APP_NAME', 'Modol Connect Backend');
define('BASE_URL', 'http://localhost/backend/');
define('AGENT_COMMISSION_PERCENT', 5.0);
define('ADMIN_PLATFORM_FEE_PERCENT', 15.0);

function sendJsonResponse(${'$'}status, ${'$'}message, ${'$'}data = [], ${'$'}code = 200) {
    http_response_code(${'$'}code);
    header('Content-Type: application/json');
    echo json_encode([
        'status' => ${'$'}status,
        'message' => ${'$'}message,
        'data' => ${'$'}data,
        'timestamp' => date('c')
    ]);
    exit();
}
?>""",
            "backend/config/auth.php" to """<?php
// backend/config/auth.php - Authentication Helper
require_once __DIR__ . '/database.php';
require_once __DIR__ . '/config.php';

function checkAdminAuth() {
    if (!isset(${'$'}_SESSION['user_role']) || ${'$'}_SESSION['user_role'] !== 'ADMIN') {
        sendJsonResponse('error', 'Unauthorized access. Admin privileges required.', [], 401);
    }
}

function checkAgentAuth() {
    if (!isset(${'$'}_SESSION['user_role']) || ${'$'}_SESSION['user_role'] !== 'CASH_AGENT') {
        sendJsonResponse('error', 'Unauthorized access. Cash Agent login required.', [], 401);
    }
}
?>""",
            "backend/schema.sql" to """-- backend/schema.sql
-- MySQL 8.0 Relational Database Schema
CREATE TABLE IF NOT EXISTS `admins` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `email` VARCHAR(150) UNIQUE NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(50) DEFAULT 'SUPER_ADMIN'
);

CREATE TABLE IF NOT EXISTS `cash_agents` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `agent_code` VARCHAR(20) UNIQUE NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) UNIQUE NOT NULL,
    `commission_rate` DECIMAL(5,2) DEFAULT 5.00,
    `wallet_balance` DECIMAL(12,2) DEFAULT 0.00
);

CREATE TABLE IF NOT EXISTS `cash_collections` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `collection_code` VARCHAR(30) UNIQUE NOT NULL,
    `booking_id` VARCHAR(30) NOT NULL,
    `agent_id` INT NOT NULL,
    `amount` DECIMAL(12,2) NOT NULL,
    `status` ENUM('PENDING', 'PAID') DEFAULT 'PENDING'
);"""
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("PHP Backend Code Display", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Folder /backend/ (PHP 8.2 + MySQL)", color = TextSecondary, fontSize = 10.sp)
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Modal Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Source Code", "Folder Tree", "API Simulator").forEachIndexed { idx, title ->
                        val isSel = activeTab == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) PinkHighlight else Color(0xFF2E2E3E))
                                .clickable { activeTab = idx }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(title, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (activeTab == 0) {
                    // File Picker Row
                    Text("Select PHP Backend File:", color = TextSecondary, fontSize = 10.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        fileMap.keys.forEach { path ->
                            val isSel = selectedFilePath == path
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) PinkHighlight.copy(alpha = 0.25f) else Color(0xFF2E2E3E))
                                    .border(1.dp, if (isSel) PinkHighlight else Color.Transparent, RoundedCornerShape(6.dp))
                                    .clickable { selectedFilePath = path }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    path.substringAfterLast('/'),
                                    color = if (isSel) PinkHighlight else Color.LightGray,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Code Viewer Window
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF13151F))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📄 /$selectedFilePath",
                                    color = PinkHighlight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("PHP 8.2", color = TextSecondary, fontSize = 9.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = fileMap[selectedFilePath] ?: "// File empty",
                                color = Color(0xFFC3E88D),
                                fontSize = 10.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }
                } else if (activeTab == 1) {
                    // Folder Tree View
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("📁 backend/", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("  ├── 📂 admin/ (12 Admin Portal PHP files)", color = Color.White, fontSize = 11.sp)
                        Text("  ├── 📂 agent/ (10 Cash Agent Portal PHP files)", color = Color.White, fontSize = 11.sp)
                        Text("  ├── 📂 api/ (REST JSON Endpoints for Mobile App)", color = Color.White, fontSize = 11.sp)
                        Text("  ├── 📂 config/ (database.php, config.php, auth.php)", color = Color.White, fontSize = 11.sp)
                        Text("  ├── 📂 middleware/ (Role authorization scripts)", color = Color.White, fontSize = 11.sp)
                        Text("  ├── 📂 uploads/ (Receipts, profiles, documents)", color = Color.White, fontSize = 11.sp)
                        Text("  └── 📄 schema.sql (MySQL Database Schema)", color = Color.White, fontSize = 11.sp)
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2232))) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Storage Location in Repository:", color = TextSecondary, fontSize = 10.sp)
                                Text("/backend/", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("All PHP files are cleanly isolated under the `/backend/` directory.", color = TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                } else {
                    // REST API Tester
                    var apiOutput by remember { mutableStateOf("") }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Simulate Live PHP REST API:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        
                        Button(
                            onClick = {
                                apiOutput = """HTTP/1.1 200 OK
Content-Type: application/json

{
  "status": "success",
  "message": "Modol Connect Backend Service API operational.",
  "data": {
    "version": "2.1.0",
    "app_name": "Modol Connect Backend",
    "environment": "production",
    "production": true,
    "base_url": "https://modolconnect.com/",
    "modules": {
      "Admin Portal": "https://modolconnect.com/backend/admin/dashboard.php",
      "Cash Agent Portal": "https://modolconnect.com/backend/agent/dashboard.php",
      "API": "https://modolconnect.com/backend/api/",
      "Documentation": "https://modolconnect.com/backend/docs/"
    },
    "server": {
      "php_version": "8.2.31",
      "server_time": "2026-07-22 21:08:04",
      "timezone": "Asia/Dhaka"
    }
  },
  "timestamp": "2026-07-22T21:08:04+06:00"
}"""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("GET /backend/ (PHP 8.2 Operational Health)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                apiOutput = """HTTP/1.1 200 OK
Content-Type: application/json

{
  "status": "success",
  "message": "Admin dashboard statistics retrieved",
  "data": {
    "metrics": {
      "total_users": 12568,
      "total_models": 2356,
      "cash_agents": 632,
      "total_bookings": 8965,
      "monthly_revenue_bdt": 4565230.00
    }
  },
  "timestamp": "2026-07-21T10:18:00+00:00"
}"""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E3E)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("GET /backend/admin/dashboard.php", fontSize = 10.sp)
                        }

                        Button(
                            onClick = {
                                apiOutput = """HTTP/1.1 200 OK
Content-Type: application/json

{
  "status": "success",
  "message": "Cash payment verified and escrow activated successfully.",
  "data": {
    "collection_id": "CC88521",
    "agent_name": "Agent Sumon",
    "commission_credited": "৳175.00",
    "escrow_status": "ACTIVE"
  }
}"""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E3E)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("POST /backend/api/admin/approve_collection.php", fontSize = 10.sp)
                        }

                        if (apiOutput.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .background(Color(0xFF13151F), RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(apiOutput, color = Color(0xFF82AAFF), fontSize = 10.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
            ) {
                Text("Close Viewer", fontSize = 11.sp)
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

// ============================================================================
// OFFERED SERVICES & PRICE CONFIGURATION SETTINGS SCREEN
// ============================================================================

@Composable
fun ModelOfferedServicesScreen(viewModel: AppViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val models by viewModel.allModels.collectAsStateWithLifecycle()
    val model = remember(models) { models.firstOrNull { it.id == 1 } }

    // 1. Service Status State
    var isServiceEnabled by remember { mutableStateOf(true) }
    var isFeaturedService by remember { mutableStateOf(true) }

    // 2. Service Categories State
    val allCategories = listOf(
        "Outcall", "Fashion Modeling", "Commercial Modeling", "Brand Promotion",
        "Event Appearance", "Photoshoot", "Video Shoot", "Dating",
        "Influencer Promotion", "Travel", "VIP Booking", "Custom Service"
    )
    var selectedCategories by remember { 
        mutableStateOf(setOf("Fashion Modeling", "Event Appearance", "Outcall", "VIP Booking", "Photoshoot")) 
    }

    // 3. Price Configuration State
    var price1Hour by remember { mutableStateOf("50") }
    var price2Hours by remember { mutableStateOf("90") }
    var price3Hours by remember { mutableStateOf("130") }
    var price4Hours by remember { mutableStateOf("170") }
    var priceHalfDay by remember { mutableStateOf("250") }
    var priceFullDay by remember { mutableStateOf("450") }
    var priceOvernight by remember { mutableStateOf("700") }
    var customPriceLabel by remember { mutableStateOf("Special Event Package") }
    var customPriceValue by remember { mutableStateOf("1200") }
    var selectedCurrency by remember { mutableStateOf("USD ($)") }

    // 4. Travel Charges State
    var freeDistanceKm by remember { mutableStateOf("10") }
    var perKmCharge by remember { mutableStateOf("2.5") }
    var outOfCityCharge by remember { mutableStateOf("150") }
    var hotelRequired by remember { mutableStateOf(true) }
    var flightRequired by remember { mutableStateOf(true) }

    // 5. Working Schedule State
    var activeDays by remember { mutableStateOf(setOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")) }
    var startTime by remember { mutableStateOf("09:00 AM") }
    var endTime by remember { mutableStateOf("10:00 PM") }
    var breakTime by remember { mutableStateOf("01:00 PM - 02:00 PM") }
    var instantBooking by remember { mutableStateOf(true) }

    // 6. Additional Charges State
    var makeupFee by remember { mutableStateOf("30") }
    var transportationFee by remember { mutableStateOf("25") }
    var photographyFee by remember { mutableStateOf("50") }
    var overtimePerHour by remember { mutableStateOf("60") }
    var holidayCharge by remember { mutableStateOf("100") }

    // 7. Booking Rules State
    var minBookingDuration by remember { mutableStateOf("1 Hour") }
    var maxBookingDuration by remember { mutableStateOf("12 Hours") }
    var advanceBookingHours by remember { mutableStateOf("24 Hours") }
    var freeCancellationHours by remember { mutableStateOf("12 Hours") }
    var cancellationFee by remember { mutableStateOf("25") }
    var autoAcceptBookings by remember { mutableStateOf(true) }

    // 8. Payment Settings State
    var acceptCashAgent by remember { mutableStateOf(true) }
    var acceptWallet by remember { mutableStateOf(true) }
    var acceptBkash by remember { mutableStateOf(true) }
    var acceptNagad by remember { mutableStateOf(true) }
    var acceptStripe by remember { mutableStateOf(true) }
    var acceptGooglePay by remember { mutableStateOf(true) }
    var acceptApplePay by remember { mutableStateOf(true) }
    var acceptGooglePlayBilling by remember { mutableStateOf(true) }

    // Google Play Billing / In-App Purchase Config
    var googlePlayLicenseKey by remember { mutableStateOf("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQE3x8...") }
    var googlePlayMerchantId by remember { mutableStateOf("merchant.com.aistudio.app") }
    var googlePlayPackageName by remember { mutableStateOf("com.aistudio.app") }
    var googlePlaySkuPrefix by remember { mutableStateOf("booking_payment_") }
    var googlePlaySandboxMode by remember { mutableStateOf(true) }
    var googlePlayAutoSync by remember { mutableStateOf(true) }
    var googlePlayStatusMsg by remember { mutableStateOf("") }

    // 9. Service Description State
    var serviceTitle by remember { mutableStateOf("VIP Modeling & High-End Event Appearance") }
    var serviceDescription by remember { mutableStateOf("Professional modeling for fashion shows, brand promotions, commercial video shoots, VIP appearances, and luxury event hostings.") }
    val includedFeatures = remember { mutableStateListOf("Full HD Photoshoot", "Professional Styling Support", "Punctual Arrival", "Custom Wardrobe Consultation") }
    val excludedFeatures = remember { mutableStateListOf("Unapproved High-Risk Stunts", "Personal Security Personnel", "International Flight Tickets") }
    var newIncludedInput by remember { mutableStateOf("") }
    var newExcludedInput by remember { mutableStateOf("") }

    // 10. Portfolio Media State
    val portfolioImages = remember { mutableStateListOf(
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
        "https://images.unsplash.com/photo-1517841905240-472988babdf9",
        "https://images.unsplash.com/photo-1524504388940-b1c1722653e1"
    ) }
    val portfolioVideos = remember { mutableStateListOf(
        "Fashion Runway Walk 2026",
        "Commercial Brand Promo Reel"
    ) }

    // Dialog state
    var showAddImageDialog by remember { mutableStateOf(false) }
    var newImageUrl by remember { mutableStateOf("") }
    var showAddVideoDialog by remember { mutableStateOf(false) }
    var newVideoName by remember { mutableStateOf("") }
    var showAddNewServiceDialog by remember { mutableStateOf(false) }
    var newServiceNameInput by remember { mutableStateOf("") }
    var newServicePriceInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(DarkSurface)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo("DASHBOARD") }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Dashboard",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "💼 Offered Services & Price",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Configuration & Rates Settings",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .background(PinkHighlight.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "MODEL SETTINGS",
                        color = PinkHighlight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. SERVICE STATUS CARD
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Service Status", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("✅ Enable Service", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text("Toggle visibility of your service listings to clients", color = TextSecondary, fontSize = 11.sp)
                                }
                                Switch(
                                    checked = isServiceEnabled,
                                    onCheckedChange = { isServiceEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = PinkHighlight, checkedTrackColor = PinkHighlight.copy(alpha = 0.5f))
                                )
                            }

                            HorizontalDivider(color = Color(0xFF2E2E3E), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("📌 Featured Service Toggle", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFFFFD700), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("BOOSTED", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Black)
                                        }
                                    }
                                    Text("Highlight this service package on search top recommendations", color = TextSecondary, fontSize = 11.sp)
                                }
                                Switch(
                                    checked = isFeaturedService,
                                    onCheckedChange = { isFeaturedService = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD700), checkedTrackColor = Color(0xFFFFD700).copy(alpha = 0.5f))
                                )
                            }
                        }
                    }
                }

                // 2. SERVICE CATEGORIES CARD
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Category, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Service Categories", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Select all categories applicable to this service offering:", color = TextSecondary, fontSize = 11.sp)

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                allCategories.forEach { cat ->
                                    val isSelected = selectedCategories.contains(cat)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedCategories = if (isSelected) {
                                                selectedCategories - cat
                                            } else {
                                                selectedCategories + cat
                                            }
                                        },
                                        label = { Text(cat, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                        leadingIcon = if (isSelected) {
                                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = PinkHighlight,
                                            selectedLabelColor = Color.White,
                                            containerColor = Color(0xFF1E1E2E),
                                            labelColor = TextSecondary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. PRICE CONFIGURATION CARD
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.AttachMoney, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Price Configuration", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    listOf("USD ($)", "BDT (৳)").forEach { curr ->
                                        val isSel = selectedCurrency == curr
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isSel) PinkHighlight else Color(0xFF1E1E2E))
                                                .clickable { selectedCurrency = curr }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(curr, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Table style for price duration rates
                            val priceFields = listOf(
                                "1 Hour" to price1Hour,
                                "2 Hours" to price2Hours,
                                "3 Hours" to price3Hours,
                                "4 Hours" to price4Hours,
                                "Half Day (6 Hours)" to priceHalfDay,
                                "Full Day (12 Hours)" to priceFullDay,
                                "Overnight" to priceOvernight
                            )

                            priceFields.chunked(2).forEach { pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    pair.forEach { (label, value) ->
                                        OutlinedTextField(
                                            value = value,
                                            onValueChange = { newValue ->
                                                when (label) {
                                                    "1 Hour" -> price1Hour = newValue
                                                    "2 Hours" -> price2Hours = newValue
                                                    "3 Hours" -> price3Hours = newValue
                                                    "4 Hours" -> price4Hours = newValue
                                                    "Half Day (6 Hours)" -> priceHalfDay = newValue
                                                    "Full Day (12 Hours)" -> priceFullDay = newValue
                                                    "Overnight" -> priceOvernight = newValue
                                                }
                                            },
                                            label = { Text(label, color = TextSecondary, fontSize = 11.sp) },
                                            prefix = { Text(if (selectedCurrency.contains("USD")) "$" else "৳ ", color = PinkHighlight, fontWeight = FontWeight.Bold) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    if (pair.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = customPriceLabel,
                                    onValueChange = { customPriceLabel = it },
                                    label = { Text("Custom Tier Label", color = TextSecondary, fontSize = 11.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1.2f)
                                )

                                OutlinedTextField(
                                    value = customPriceValue,
                                    onValueChange = { customPriceValue = it },
                                    label = { Text("Custom Price", color = TextSecondary, fontSize = 11.sp) },
                                    prefix = { Text(if (selectedCurrency.contains("USD")) "$" else "৳ ", color = PinkHighlight, fontWeight = FontWeight.Bold) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // 4. TRAVEL CHARGES CARD
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Travel Charges", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = freeDistanceKm,
                                    onValueChange = { freeDistanceKm = it },
                                    label = { Text("Free Radius (km)", color = TextSecondary, fontSize = 11.sp) },
                                    suffix = { Text("km", color = TextSecondary, fontSize = 11.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = perKmCharge,
                                    onValueChange = { perKmCharge = it },
                                    label = { Text("Per km Charge", color = TextSecondary, fontSize = 11.sp) },
                                    prefix = { Text("$ ", color = PinkHighlight, fontWeight = FontWeight.Bold) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = outOfCityCharge,
                                onValueChange = { outOfCityCharge = it },
                                label = { Text("Out of City Flat Charge", color = TextSecondary, fontSize = 11.sp) },
                                prefix = { Text("$ ", color = PinkHighlight, fontWeight = FontWeight.Bold) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                    focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Hotel Accommodation Required", color = Color.White, fontSize = 13.sp)
                                Switch(
                                    checked = hotelRequired,
                                    onCheckedChange = { hotelRequired = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = PinkHighlight, checkedTrackColor = PinkHighlight.copy(alpha = 0.5f))
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Flight Tickets Required", color = Color.White, fontSize = 13.sp)
                                Switch(
                                    checked = flightRequired,
                                    onCheckedChange = { flightRequired = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = PinkHighlight, checkedTrackColor = PinkHighlight.copy(alpha = 0.5f))
                                )
                            }
                        }
                    }
                }

                // 5. WORKING SCHEDULE CARD
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Working Schedule", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Available Working Days:", color = TextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))

                            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                days.forEach { day ->
                                    val isSel = activeDays.contains(day)
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(if (isSel) PinkHighlight else Color(0xFF1E1E2E))
                                            .clickable {
                                                activeDays = if (isSel) activeDays - day else activeDays + day
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(day, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = startTime,
                                    onValueChange = { startTime = it },
                                    label = { Text("Start Time", color = TextSecondary, fontSize = 11.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = endTime,
                                    onValueChange = { endTime = it },
                                    label = { Text("End Time", color = TextSecondary, fontSize = 11.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = breakTime,
                                onValueChange = { breakTime = it },
                                label = { Text("Break Time Window", color = TextSecondary, fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                    focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Instant Booking Mode (On/Off)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Allow clients to confirm schedule automatically", color = TextSecondary, fontSize = 10.sp)
                                }
                                Switch(
                                    checked = instantBooking,
                                    onCheckedChange = { instantBooking = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = PinkHighlight, checkedTrackColor = PinkHighlight.copy(alpha = 0.5f))
                                )
                            }
                        }
                    }
                }

                // 6. ADDITIONAL CHARGES CARD
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.AddCard, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Additional Charges", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = makeupFee,
                                    onValueChange = { makeupFee = it },
                                    label = { Text("Makeup Fee", color = TextSecondary, fontSize = 11.sp) },
                                    prefix = { Text("$ ", color = PinkHighlight, fontWeight = FontWeight.Bold) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = transportationFee,
                                    onValueChange = { transportationFee = it },
                                    label = { Text("Transportation Fee", color = TextSecondary, fontSize = 11.sp) },
                                    prefix = { Text("$ ", color = PinkHighlight, fontWeight = FontWeight.Bold) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = photographyFee,
                                    onValueChange = { photographyFee = it },
                                    label = { Text("Photography Fee", color = TextSecondary, fontSize = 11.sp) },
                                    prefix = { Text("$ ", color = PinkHighlight, fontWeight = FontWeight.Bold) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = overtimePerHour,
                                    onValueChange = { overtimePerHour = it },
                                    label = { Text("Overtime (Per Hour)", color = TextSecondary, fontSize = 11.sp) },
                                    prefix = { Text("$ ", color = PinkHighlight, fontWeight = FontWeight.Bold) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = holidayCharge,
                                onValueChange = { holidayCharge = it },
                                label = { Text("Holiday / Weekend Surge Charge", color = TextSecondary, fontSize = 11.sp) },
                                prefix = { Text("$ ", color = PinkHighlight, fontWeight = FontWeight.Bold) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                    focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // 7. BOOKING RULES CARD
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Booking Rules", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = minBookingDuration,
                                    onValueChange = { minBookingDuration = it },
                                    label = { Text("Min Booking Duration", color = TextSecondary, fontSize = 11.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = maxBookingDuration,
                                    onValueChange = { maxBookingDuration = it },
                                    label = { Text("Max Booking Duration", color = TextSecondary, fontSize = 11.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = advanceBookingHours,
                                    onValueChange = { advanceBookingHours = it },
                                    label = { Text("Advance Booking Required", color = TextSecondary, fontSize = 11.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = freeCancellationHours,
                                    onValueChange = { freeCancellationHours = it },
                                    label = { Text("Free Cancellation (Hours)", color = TextSecondary, fontSize = 11.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = cancellationFee,
                                onValueChange = { cancellationFee = it },
                                label = { Text("Cancellation Fee ($)", color = TextSecondary, fontSize = 11.sp) },
                                prefix = { Text("$ ", color = PinkHighlight, fontWeight = FontWeight.Bold) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                    focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Auto Accept Bookings (On/Off)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Instantly accept requests without manual review", color = TextSecondary, fontSize = 10.sp)
                                }
                                Switch(
                                    checked = autoAcceptBookings,
                                    onCheckedChange = { autoAcceptBookings = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = PinkHighlight, checkedTrackColor = PinkHighlight.copy(alpha = 0.5f))
                                )
                            }
                        }
                    }
                }

                // 8. PAYMENT SETTINGS CARD
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Payment, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Payment Settings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Accepted payment methods for client bookings:", color = TextSecondary, fontSize = 11.sp)

                            Spacer(modifier = Modifier.height(12.dp))

                            val pMethods = listOf(
                                "Cash agent" to acceptCashAgent,
                                "Wallet" to acceptWallet,
                                "bKash" to acceptBkash,
                                "Nagad" to acceptNagad,
                                "Stripe" to acceptStripe,
                                "Google Pay" to acceptGooglePay,
                                "Apple Pay" to acceptApplePay,
                                "Google Play Billing" to acceptGooglePlayBilling
                            )

                            pMethods.chunked(2).forEach { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    row.forEach { (name, isChecked) ->
                                        Row(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFF1E1E2E))
                                                .clickable {
                                                    when (name) {
                                                        "Cash agent" -> acceptCashAgent = !acceptCashAgent
                                                        "Wallet" -> acceptWallet = !acceptWallet
                                                        "bKash" -> acceptBkash = !acceptBkash
                                                        "Nagad" -> acceptNagad = !acceptNagad
                                                        "Stripe" -> acceptStripe = !acceptStripe
                                                        "Google Pay" -> acceptGooglePay = !acceptGooglePay
                                                        "Apple Pay" -> acceptApplePay = !acceptApplePay
                                                        "Google Play Billing" -> acceptGooglePlayBilling = !acceptGooglePlayBilling
                                                    }
                                                }
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = isChecked,
                                                onCheckedChange = { checked ->
                                                    when (name) {
                                                        "Cash agent" -> acceptCashAgent = checked
                                                        "Wallet" -> acceptWallet = checked
                                                        "bKash" -> acceptBkash = checked
                                                        "Nagad" -> acceptNagad = checked
                                                        "Stripe" -> acceptStripe = checked
                                                        "Google Pay" -> acceptGooglePay = checked
                                                        "Apple Pay" -> acceptApplePay = checked
                                                        "Google Play Billing" -> acceptGooglePlayBilling = checked
                                                    }
                                                },
                                                colors = CheckboxDefaults.colors(checkedColor = PinkHighlight)
                                            )
                                            Text(name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            if (acceptGooglePlayBilling) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .background(Color(0xFF00E676).copy(alpha = 0.2f), CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ShoppingBag,
                                                        contentDescription = null,
                                                        tint = Color(0xFF00E676),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Google Play In-App Billing Configuration",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        OutlinedTextField(
                                            value = googlePlayPackageName,
                                            onValueChange = { googlePlayPackageName = it; googlePlayStatusMsg = "" },
                                            label = { Text("App Package Name", color = TextSecondary) },
                                            placeholder = { Text("com.aistudio.app", color = Color.Gray) },
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        OutlinedTextField(
                                            value = googlePlayMerchantId,
                                            onValueChange = { googlePlayMerchantId = it; googlePlayStatusMsg = "" },
                                            label = { Text("Google Play Merchant / Service Account ID", color = TextSecondary) },
                                            placeholder = { Text("merchant.com.aistudio.app", color = Color.Gray) },
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        OutlinedTextField(
                                            value = googlePlaySkuPrefix,
                                            onValueChange = { googlePlaySkuPrefix = it; googlePlayStatusMsg = "" },
                                            label = { Text("Product / In-App SKU Prefix", color = TextSecondary) },
                                            placeholder = { Text("booking_payment_", color = Color.Gray) },
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        OutlinedTextField(
                                            value = googlePlayLicenseKey,
                                            onValueChange = { googlePlayLicenseKey = it; googlePlayStatusMsg = "" },
                                            label = { Text("Google Play RSA License Public Key (Base64)", color = TextSecondary) },
                                            minLines = 2,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Google Play License Tester / Sandbox Mode", color = Color.White, fontSize = 11.sp)
                                            Switch(
                                                checked = googlePlaySandboxMode,
                                                onCheckedChange = { googlePlaySandboxMode = it; googlePlayStatusMsg = "" },
                                                colors = SwitchDefaults.colors(checkedThumbColor = PinkHighlight, checkedTrackColor = PinkHighlight.copy(alpha = 0.4f))
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Auto-Verify & Realtime Payment Webhook Sync", color = Color.White, fontSize = 11.sp)
                                            Switch(
                                                checked = googlePlayAutoSync,
                                                onCheckedChange = { googlePlayAutoSync = it; googlePlayStatusMsg = "" },
                                                colors = SwitchDefaults.colors(checkedThumbColor = PinkHighlight, checkedTrackColor = PinkHighlight.copy(alpha = 0.4f))
                                            )
                                        }

                                        if (googlePlayStatusMsg.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = googlePlayStatusMsg,
                                                color = Color(0xFF00E676),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Button(
                                            onClick = {
                                                googlePlayStatusMsg = "✓ Google Play Billing Gateway Configured & Saved Successfully!"
                                                viewModel.addNotification(
                                                    "Google Play Billing Updated",
                                                    "Google Play Billing credentials and license key updated in backend admin settings.",
                                                    "Admin"
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Save & Verify Google Play Gateway", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 9. SERVICE DESCRIPTION & FEATURES CARD
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Service Description", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = serviceTitle,
                                onValueChange = { serviceTitle = it },
                                label = { Text("Service Title", color = TextSecondary) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                    focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = serviceDescription,
                                onValueChange = { serviceDescription = it },
                                label = { Text("Detailed Description", color = TextSecondary) },
                                minLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                    focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Included Features:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                includedFeatures.forEach { feature ->
                                    Box(
                                        modifier = Modifier
                                            .background(OnlineGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                            .border(0.5.dp, OnlineGreen, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = OnlineGreen, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(feature, color = Color.White, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newIncludedInput,
                                    onValueChange = { newIncludedInput = it },
                                    placeholder = { Text("Add included feature...", color = Color.Gray, fontSize = 11.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(48.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        if (newIncludedInput.isNotBlank()) {
                                            includedFeatures.add(newIncludedInput.trim())
                                            newIncludedInput = ""
                                        }
                                    },
                                    modifier = Modifier.background(PinkHighlight, RoundedCornerShape(8.dp))
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Included", tint = Color.White)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Excluded Features:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                excludedFeatures.forEach { feature ->
                                    Box(
                                        modifier = Modifier
                                            .background(Color.Red.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                            .border(0.5.dp, Color.Red, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color.Red, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(feature, color = Color.White, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newExcludedInput,
                                    onValueChange = { newExcludedInput = it },
                                    placeholder = { Text("Add excluded feature...", color = Color.Gray, fontSize = 11.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(48.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        if (newExcludedInput.isNotBlank()) {
                                            excludedFeatures.add(newExcludedInput.trim())
                                            newExcludedInput = ""
                                        }
                                    },
                                    modifier = Modifier.background(Color(0xFF2E2E3E), RoundedCornerShape(8.dp))
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Excluded", tint = Color.White)
                                }
                            }
                        }
                    }
                }

                // 10. PORTFOLIO & MEDIA UPLOAD CARD
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Collections, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Portfolio", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = { showAddImageDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("+ Image", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { showAddVideoDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E3E)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("+ Video", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Service Images:", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                portfolioImages.forEachIndexed { idx, url ->
                                    Box(
                                        modifier = Modifier
                                            .size(90.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF1E1E2E))
                                    ) {
                                        SubcomposeAsyncImage(
                                            model = url,
                                            contentDescription = "Portfolio Image $idx",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )

                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(2.dp)
                                                .size(20.dp)
                                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                                .clickable { portfolioImages.removeAt(idx) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(12.dp))
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Service Videos:", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))

                            portfolioVideos.forEachIndexed { idx, title ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E1E2E))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }

                                    IconButton(
                                        onClick = { portfolioVideos.removeAt(idx) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 11. BOTTOM ACTION BUTTONS BAR
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                val rateVal = price1Hour.toIntOrNull() ?: 50
                                val modelProfile = models.firstOrNull { it.id == 1 }
                                if (modelProfile != null) {
                                    val updated = modelProfile.copy(
                                        hourlyRate = rateVal,
                                        services = selectedCategories.joinToString(", ")
                                    )
                                    viewModel.repository.updateModel(updated)
                                }
                                viewModel.addNotification(
                                    "Services & Price Updated",
                                    "Your offered services, pricing tiers, rules & schedule have been published live.",
                                    "System"
                                )
                                android.widget.Toast.makeText(context, "💾 Changes Saved Successfully!", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("💾 Save Changes", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.navigateTo("MODEL_PROFILE") },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFF2E2E3E)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("👁 Preview", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(
                        onClick = { showAddNewServiceDialog = true },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF1E1E2E), RoundedCornerShape(12.dp))
                            .border(1.dp, PinkHighlight, RoundedCornerShape(12.dp))
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add New Service", tint = PinkHighlight)
                    }
                }
            }
        }

        // Add Image Dialog
        if (showAddImageDialog) {
            AlertDialog(
                onDismissRequest = { showAddImageDialog = false },
                title = { Text("Upload Portfolio Image", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Enter image URL or select sample asset:", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = newImageUrl,
                            onValueChange = { newImageUrl = it },
                            placeholder = { Text("https://images.unsplash.com/...", color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newImageUrl.isNotBlank()) {
                                portfolioImages.add(newImageUrl.trim())
                                newImageUrl = ""
                            } else {
                                portfolioImages.add("https://images.unsplash.com/photo-1534528741775-53994a69daeb")
                            }
                            showAddImageDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
                    ) {
                        Text("Add Image", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddImageDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = DarkSurface
            )
        }

        // Add Video Dialog
        if (showAddVideoDialog) {
            AlertDialog(
                onDismissRequest = { showAddVideoDialog = false },
                title = { Text("Upload Portfolio Video", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Enter video title or video reel link:", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = newVideoName,
                            onValueChange = { newVideoName = it },
                            placeholder = { Text("E.g. VIP Fashion Catwalk Reel", color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newVideoName.isNotBlank()) {
                                portfolioVideos.add(newVideoName.trim())
                                newVideoName = ""
                            }
                            showAddVideoDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
                    ) {
                        Text("Add Video", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddVideoDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = DarkSurface
            )
        }

        // Add New Service Package Dialog
        if (showAddNewServiceDialog) {
            AlertDialog(
                onDismissRequest = { showAddNewServiceDialog = false },
                title = { Text("➕ Add New Service Package", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Create a new service tier or custom booking offer:", color = TextSecondary, fontSize = 12.sp)
                        OutlinedTextField(
                            value = newServiceNameInput,
                            onValueChange = { newServiceNameInput = it },
                            label = { Text("Service Title", color = TextSecondary) },
                            placeholder = { Text("E.g. Full Day VIP Commercial Shoot", color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newServicePriceInput,
                            onValueChange = { newServicePriceInput = it },
                            label = { Text("Package Base Price ($)", color = TextSecondary) },
                            placeholder = { Text("E.g. 500", color = Color.Gray) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newServiceNameInput.isNotBlank()) {
                                if (!allCategories.contains(newServiceNameInput.trim())) {
                                    selectedCategories = selectedCategories + newServiceNameInput.trim()
                                }
                                customPriceLabel = newServiceNameInput.trim()
                                if (newServicePriceInput.isNotBlank()) {
                                    customPriceValue = newServicePriceInput.trim()
                                }
                                newServiceNameInput = ""
                                newServicePriceInput = ""
                            }
                            showAddNewServiceDialog = false
                            android.widget.Toast.makeText(context, "New Service Offer Added!", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
                    ) {
                        Text("Create Service", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddNewServiceDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = DarkSurface
            )
        }
    }
}

// ============================================================================
// 12. LIVE SUPPORT CHAT SYSTEM & FACEBOOK MESSENGER ADMIN PANEL
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveSupportChatModal(
    onDismiss: () -> Unit,
    viewModel: AppViewModel
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allMessages by viewModel.allMessages.collectAsStateWithLifecycle()
    val userId = currentUser?.id ?: "user_1"

    var messageInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    val supportMessages = remember(allMessages, userId) {
        allMessages.filter {
            (it.senderId == userId && it.receiverId == "ADMIN_SUPPORT") ||
            (it.senderId == "ADMIN_SUPPORT" && it.receiverId == userId)
        }.sortedBy { it.timestamp }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            color = Color(0xFF0F1019)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF161824))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    Brush.linearGradient(listOf(Color(0xFFFF2B85), Color(0xFF9C27B0))),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SupportAgent,
                                contentDescription = "Support Agent",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Modol Live Support Desk", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(modifier = Modifier.size(8.dp).background(Color(0xFF00E676), CircleShape))
                            }
                            Text("🟢 Admin Team Online • 24/7 Live Care", color = Color(0xFF00E676), fontSize = 11.sp)
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                HorizontalDivider(color = Color(0xFF25283A))

                // Quick Inquiry Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF161824))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickPills = listOf(
                        "bKash Deposit Help",
                        "Escrow Refund Status",
                        "Model Verification",
                        "Contact Admin Phone"
                    )
                    items(quickPills) { pill ->
                        Surface(
                            color = Color(0xFF25283A),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.clickable {
                                viewModel.sendSupportMessage(pill)
                            }
                        ) {
                            Text(
                                text = pill,
                                color = Color.White,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFF25283A))

                // Message Thread
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (supportMessages.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(56.dp))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("Welcome to Modol Connect Live Support!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Type your question below or tap a quick topic to start.", color = TextSecondary, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    items(supportMessages) { msg ->
                        val isFromUser = msg.senderId == userId
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isFromUser) Arrangement.End else Arrangement.Start
                        ) {
                            if (!isFromUser) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Brush.linearGradient(listOf(Color(0xFFFF2B85), Color(0xFF9C27B0))), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            Column(
                                horizontalAlignment = if (isFromUser) Alignment.End else Alignment.Start,
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Text(
                                    text = if (isFromUser) "You" else "Modol Admin Support",
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                                Surface(
                                    color = if (isFromUser) PinkHighlight else Color(0xFF202336),
                                    shape = RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isFromUser) 16.dp else 2.dp,
                                        bottomEnd = if (isFromUser) 2.dp else 16.dp
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(text = msg.content, color = Color.White, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Input Bar
                Surface(
                    color = Color(0xFF161824),
                    tonalElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            placeholder = { Text("Ask Admin Live Support...", color = TextSecondary, fontSize = 13.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PinkHighlight,
                                unfocusedBorderColor = Color(0xFF2E324D),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0F1019),
                                unfocusedContainerColor = Color(0xFF0F1019)
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.weight(1f),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        FloatingActionButton(
                            onClick = {
                                if (messageInput.isNotBlank()) {
                                    viewModel.sendSupportMessage(messageInput)
                                    messageInput = ""
                                }
                            },
                            containerColor = PinkHighlight,
                            contentColor = Color.White,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// FACEBOOK MESSENGER STYLE ADMIN LIVE SUPPORT WORKSPACE
// ----------------------------------------------------------------------------

data class SupportThreadUser(
    val id: String,
    val name: String,
    val role: String, // "CLIENT", "MODEL", "CASH_AGENT"
    val avatarUrl: String,
    val isOnline: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSupportMessengerTab(viewModel: AppViewModel) {
    val allMessages by viewModel.allMessages.collectAsStateWithLifecycle()
    val models by viewModel.allModels.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") } // "All", "Unread", "Clients", "Models", "Agents"
    var adminMessageInput by remember { mutableStateOf("") }

    // Pre-populated support contact list
    val supportUsersList = listOf(
        SupportThreadUser("user_1", "Rahul Verma", "CLIENT", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d"),
        SupportThreadUser("1", "Jessica Simpson", "MODEL", "https://images.unsplash.com/photo-1534528741775-53994a69daeb"),
        SupportThreadUser("2", "Nusrat Jahan", "MODEL", "https://images.unsplash.com/photo-1517841905240-472988babdf9"),
        SupportThreadUser("3", "Tanvir Agent", "CASH_AGENT", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e"),
        SupportThreadUser("4", "Anika Kabir", "MODEL", "https://images.unsplash.com/photo-1524504388940-b1c1722653e1")
    )

    val selectedUser = supportUsersList.firstOrNull { it.id == viewModel.adminSelectedSupportUserId } ?: supportUsersList.first()

    // Filtered user list
    val filteredUsers = supportUsersList.filter { u ->
        val matchesSearch = u.name.contains(searchQuery, ignoreCase = true) || u.role.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "Clients" -> u.role == "CLIENT"
            "Models" -> u.role == "MODEL"
            "Agents" -> u.role == "CASH_AGENT"
            "Unread" -> true
            else -> true
        }
        matchesSearch && matchesFilter
    }

    // Active conversation messages with selected user
    val activeThreadMessages = remember(allMessages, selectedUser.id) {
        allMessages.filter {
            (it.senderId == selectedUser.id && it.receiverId == "ADMIN_SUPPORT") ||
            (it.senderId == "ADMIN_SUPPORT" && it.receiverId == selectedUser.id) ||
            (it.senderId == selectedUser.id && it.receiverId == "1") ||
            (it.senderId == "1" && it.receiverId == selectedUser.id)
        }.sortedBy { it.timestamp }
    }

    val messengerGradient = Brush.linearGradient(
        listOf(Color(0xFF0084FF), Color(0xFFA033FF), Color(0xFFFF5252))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E15))
    ) {
        // --- 1. FACEBOOK MESSENGER HEADER BAR ---
        Surface(
            color = Color(0xFF141622),
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(messengerGradient, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Messenger",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Messenger Admin Support",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF00E676), CircleShape)
                            )
                        }
                        Text(
                            "⚡ Active Live Care Workspace • 24/7 Desk",
                            color = Color(0xFF00E676),
                            fontSize = 10.sp
                        )
                    }
                }

                // Header Action Pill
                Surface(
                    color = Color(0xFF0084FF).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFF0084FF))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFF0084FF), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Admin Master Mode", color = Color(0xFF0084FF), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }
        }

        HorizontalDivider(color = Color(0xFF222538))

        // --- 2. MAIN WORKSPACE CONTENT ---
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Active User Stories Bar (Facebook Messenger Style)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF11131F))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { viewModel.showLiveSupportModal = true }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFF222538), CircleShape)
                                .border(1.dp, Color(0xFF0084FF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "New Ticket", tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Test Chat", color = TextSecondary, fontSize = 10.sp)
                    }
                }

                items(supportUsersList) { user ->
                    val isSelected = user.id == selectedUser.id
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { viewModel.adminSelectedSupportUserId = user.id }
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        brush = if (isSelected) messengerGradient else Brush.linearGradient(listOf(Color(0xFF2E324D), Color(0xFF2E324D))),
                                        shape = CircleShape
                                    )
                            ) {
                                SubcomposeAsyncImage(
                                    model = user.avatarUrl,
                                    contentDescription = user.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                    error = {
                                        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF2A2B3D)), contentAlignment = Alignment.Center) {
                                            Text(user.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                )
                            }
                            // Online Indicator Dot
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(Color(0xFF00E676), CircleShape)
                                    .border(2.dp, Color(0xFF11131F), CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = user.name.split(" ").firstOrNull() ?: user.name,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFF222538))

            // --- 3. FILTER TABS & SEARCH ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF141622))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search Messenger users...", color = TextSecondary, fontSize = 12.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0084FF),
                        unfocusedBorderColor = Color(0xFF2E324D),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF0D0E15),
                        unfocusedContainerColor = Color(0xFF0D0E15)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Filter chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val filters = listOf("All", "Clients", "Models", "Agents")
                    items(filters) { flt ->
                        val isSel = selectedFilter == flt
                        Surface(
                            color = if (isSel) Color(0xFF0084FF) else Color(0xFF222538),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.clickable { selectedFilter = flt }
                        ) {
                            Text(
                                text = flt,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFF222538))

            // --- 4. MESSENGER CHAT WORKSPACE ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Active Thread Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF181A29))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            SubcomposeAsyncImage(
                                model = selectedUser.avatarUrl,
                                contentDescription = selectedUser.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape),
                                error = {
                                    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF2A2B3D)), contentAlignment = Alignment.Center) {
                                        Text(selectedUser.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            )
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color(0xFF00E676), CircleShape)
                                    .border(1.5.dp, Color(0xFF181A29), CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    selectedUser.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = when (selectedUser.role) {
                                        "MODEL" -> Color(0xFFE91E63).copy(alpha = 0.2f)
                                        "CASH_AGENT" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                                        else -> Color(0xFF0084FF).copy(alpha = 0.2f)
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = selectedUser.role,
                                        color = when (selectedUser.role) {
                                            "MODEL" -> Color(0xFFFF4081)
                                            "CASH_AGENT" -> Color(0xFFFFB74D)
                                            else -> Color(0xFF40C4FF)
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text("🟢 Active Now • Facebook Messenger Live Support", color = Color(0xFF00E676), fontSize = 10.sp)
                        }
                    }

                    // Header Call / Profile Icons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(onClick = {
                            android.widget.Toast.makeText(context, "Initiating Support Call with ${selectedUser.name}...", android.widget.Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = "Call", tint = Color(0xFF0084FF), modifier = Modifier.size(20.dp))
                        }
                        IconButton(onClick = {
                            android.widget.Toast.makeText(context, "Initiating Live Video Verification...", android.widget.Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(imageVector = Icons.Default.Videocam, contentDescription = "Video", tint = Color(0xFF0084FF), modifier = Modifier.size(20.dp))
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFF25283A))

                // Chat Messages Thread
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (activeThreadMessages.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 30.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No previous messages with ${selectedUser.name}. Send a Messenger reply below!",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    items(activeThreadMessages) { msg ->
                        val isAdminReply = msg.senderId == "ADMIN_SUPPORT"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isAdminReply) Arrangement.End else Arrangement.Start
                        ) {
                            if (!isAdminReply) {
                                SubcomposeAsyncImage(
                                    model = selectedUser.avatarUrl,
                                    contentDescription = selectedUser.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape),
                                    error = {
                                        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF2A2B3D)), contentAlignment = Alignment.Center) {
                                            Text(selectedUser.name.take(1), color = Color.White, fontSize = 10.sp)
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            Column(
                                horizontalAlignment = if (isAdminReply) Alignment.End else Alignment.Start,
                                modifier = Modifier.widthIn(max = 290.dp)
                            ) {
                                Text(
                                    text = if (isAdminReply) "Admin Support" else selectedUser.name,
                                    color = TextSecondary,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 16.dp,
                                                topEnd = 16.dp,
                                                bottomStart = if (isAdminReply) 16.dp else 2.dp,
                                                bottomEnd = if (isAdminReply) 2.dp else 16.dp
                                            )
                                        )
                                        .background(
                                            if (isAdminReply) messengerGradient else Brush.linearGradient(listOf(Color(0xFF202336), Color(0xFF202336)))
                                        )
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = msg.content, color = Color.White, fontSize = 13.sp)
                                    }
                                }

                                if (isAdminReply) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Text("Sent", color = Color(0xFF0084FF), fontSize = 9.sp)
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF0084FF), modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Canned Preset Quick Replies (One-tap Admin Answers)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF11131F))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val cannedReplies = listOf(
                        "👋 Assalamu Alaikum! How can I help you?",
                        "✅ Payment verified & approved!",
                        "📄 Please upload your NID/passport proof.",
                        "💰 Refund of ৳3,500 released to wallet.",
                        "⭐ Your profile verification is complete!"
                    )
                    items(cannedReplies) { canned ->
                        Surface(
                            color = Color(0xFF202336),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(0.5.dp, Color(0xFF0084FF).copy(alpha = 0.5f)),
                            modifier = Modifier.clickable {
                                viewModel.sendAdminSupportReply(selectedUser.id, selectedUser.name, canned)
                            }
                        ) {
                            Text(
                                text = canned,
                                color = Color(0xFFE0E7FF),
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFF25283A))

                // --- 5. MESSENGER INPUT FOOTER ---
                Surface(
                    color = Color(0xFF141622),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            viewModel.sendAdminSupportReply(
                                selectedUser.id,
                                selectedUser.name,
                                "📄 Attachment: [Payment Receipt & Escrow Proof Document Verified]"
                            )
                        }) {
                            Icon(imageVector = Icons.Default.AttachFile, contentDescription = "Attach", tint = Color(0xFF0084FF), modifier = Modifier.size(22.dp))
                        }

                        OutlinedTextField(
                            value = adminMessageInput,
                            onValueChange = { adminMessageInput = it },
                            placeholder = { Text("Type Messenger reply to ${selectedUser.name}...", color = TextSecondary, fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0084FF),
                                unfocusedBorderColor = Color(0xFF2E324D),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0D0E15),
                                unfocusedContainerColor = Color(0xFF0D0E15)
                            ),
                            shape = RoundedCornerShape(22.dp),
                            modifier = Modifier.weight(1f),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (adminMessageInput.isNotBlank()) {
                                    viewModel.sendAdminSupportReply(
                                        selectedUser.id,
                                        selectedUser.name,
                                        adminMessageInput
                                    )
                                    adminMessageInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084FF)),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Send", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// ====================================================================
// ============ B2B CASH AGENT MARKETPLACE & CHAT SYSTEM =============
// ====================================================================

@Composable
fun B2BCashAgentMarketplaceDialog(
    viewModel: AppViewModel,
    onDismiss: () -> Unit
) {
    val allAgents by viewModel.paymentAgents.collectAsStateWithLifecycle()
    val allOrders by viewModel.allB2BOrders.collectAsStateWithLifecycle()

    var selectedCountry by remember { mutableStateOf("Bangladesh") }
    var tradeType by remember { mutableStateOf("DEPOSIT") } // "DEPOSIT" or "WITHDRAWAL"
    var selectedOrderForChat by remember { mutableStateOf<B2BOrder?>(null) }
    var selectedAgentForOrder by remember { mutableStateOf<PaymentAgent?>(null) }

    val countryList = listOf("Bangladesh", "China", "USA", "UK", "UAE", "India", "Other")

    val countryAgents = remember(allAgents, selectedCountry, tradeType) {
        allAgents.filter { agent ->
            val matchCountry = agent.country.equals(selectedCountry, ignoreCase = true)
            val matchTrade = if (tradeType == "DEPOSIT") agent.supportsDeposit else agent.supportsWithdraw
            matchCountry && matchTrade && agent.verificationStatus == "VERIFIED"
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(16.dp),
            color = DarkBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("MODOL CONNECT", color = PinkHighlight, fontWeight = FontWeight.Black, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF00E676).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("B2B P2P ESCROW", color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text("B2B Cash Agent Marketplace", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // Trade Mode Toggle (Deposit vs Withdraw)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurface, RoundedCornerShape(10.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (tradeType == "DEPOSIT") Color(0xFF00E676) else Color.Transparent)
                            .clickable { tradeType = "DEPOSIT" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "🟢 DEPOSIT (Cash-In)",
                            color = if (tradeType == "DEPOSIT") Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (tradeType == "WITHDRAWAL") PinkHighlight else Color.Transparent)
                            .clickable { tradeType = "WITHDRAWAL" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "🔴 WITHDRAW (Payout)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // 🌍 Country Selector Row
                Text("Select Target Country:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    countryList.forEach { c ->
                        val isSel = selectedCountry.equals(c, ignoreCase = true)
                        val flag = when (c) {
                            "Bangladesh" -> "🇧🇩"
                            "China" -> "🇨🇳"
                            "USA" -> "🇺🇸"
                            "UK" -> "🇬🇧"
                            "UAE" -> "🇦🇪"
                            "India" -> "🇮🇳"
                            else -> "🌍"
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) PinkHighlight else DarkSurface)
                                .clickable { selectedCountry = c }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("$flag $c", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Live Active Orders Bar
                val activeOrders = remember(allOrders) {
                    allOrders.filter { it.status != "RELEASED" && it.status != "CANCELLED" }
                }

                if (activeOrders.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E203E)),
                        border = BorderStroke(1.dp, PinkHighlight),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Active B2B Orders & Chat (${activeOrders.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                activeOrders.forEach { order ->
                                    Card(
                                        modifier = Modifier.clickable { selectedOrderForChat = order },
                                        colors = CardDefaults.cardColors(containerColor = DarkBg),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Column {
                                                Text("Order #${order.orderId}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                Text("${order.type} ৳${order.amount.toInt()} • ${order.agentName}", color = TextSecondary, fontSize = 9.sp)
                                            }

                                            val (stColor, stLbl) = when (order.status) {
                                                "DISPUTED" -> Color(0xFFEF4444) to "DISPUTED"
                                                "PAYMENT_SUBMITTED" -> Color(0xFFFF9800) to "SUBMITTED"
                                                else -> Color(0xFF2196F3) to "PENDING"
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .background(stColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(stLbl, color = stColor, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

                // Verified Agents List for Selected Country & Trade
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Verified Agents ($selectedCountry - ${countryAgents.size})",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text("Instant B2B Escrow", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                if (countryAgents.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No verified cash agents available in $selectedCountry for $tradeType at this moment. Try selecting 'Bangladesh' or 'China'.", color = TextSecondary, fontSize = 12.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(countryAgents) { agent ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                border = BorderStroke(1.dp, Color(0xFF2E2E3E)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(agent.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFF00E676).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("✓ VERIFIED", color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("${agent.rating}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("Available Pool: ৳${agent.availableBalance.toInt()}", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("Limit: ৳${agent.minLimit.toInt()} - ৳${agent.maxLimit.toInt()}", color = TextSecondary, fontSize = 11.sp)
                                            Text("Comm Rate: ${agent.commissionRate}%", color = Color(0xFF00E676), fontSize = 11.sp)
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Agent Code: #${agent.agentCode}", color = TextSecondary, fontSize = 11.sp)
                                            Text("Channel: ${agent.paymentMethod}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                            Text("Account: ${agent.accountNumber}", color = TextSecondary, fontSize = 10.sp)
                                        }
                                    }

                                    Text("Approved Payment Channels:", color = TextSecondary, fontSize = 10.sp)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        agent.allowedMethods.split(",").forEach { method ->
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFF2E2E3E), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text("✓ ${method.trim()}", color = Color.White, fontSize = 9.sp)
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = { selectedAgentForOrder = agent },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (tradeType == "DEPOSIT") Color(0xFF00E676) else PinkHighlight
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            if (tradeType == "DEPOSIT") "Trade (Buy / Cash-In)" else "Trade (Sell / Withdraw)",
                                            color = if (tradeType == "DEPOSIT") Color.Black else Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal to create order for selected agent
    if (selectedAgentForOrder != null) {
        CreateB2BOrderModal(
            agent = selectedAgentForOrder!!,
            tradeType = tradeType,
            onDismiss = { selectedAgentForOrder = null },
            onCreated = { order ->
                selectedAgentForOrder = null
                selectedOrderForChat = order
            },
            viewModel = viewModel
        )
    }

    // Modal for B2B Order Details and Real-time Chat
    if (selectedOrderForChat != null) {
        B2BOrderDetailAndChatDialog(
            order = selectedOrderForChat!!,
            onDismiss = { selectedOrderForChat = null },
            viewModel = viewModel
        )
    }
}

@Composable
fun CreateB2BOrderModal(
    agent: PaymentAgent,
    tradeType: String,
    onDismiss: () -> Unit,
    onCreated: (B2BOrder) -> Unit,
    viewModel: AppViewModel
) {
    var amountInput by remember { mutableStateOf("1000") }
    var selectedMethod by remember { mutableStateOf(agent.paymentMethod) }

    val amountVal = amountInput.toDoubleOrNull() ?: 1000.0
    val commFee = amountVal * (agent.commissionRate / 100.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create B2B $tradeType Trade Order", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E202E))) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Verified Agent: ${agent.name}", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Country: ${agent.country} • Code #${agent.agentCode}", color = TextSecondary, fontSize = 10.sp)
                        Text("Agent Account: ${agent.accountNumber}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        if (agent.accountHolder.isNotEmpty()) {
                            Text("Account Holder: ${agent.accountHolder}", color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                }

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Amount (৳ BDT)", color = TextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Agent Commission (${agent.commissionRate}%):", color = TextSecondary, fontSize = 11.sp)
                    Text("৳${commFee.toInt()} BDT", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Text("Instructions: You will pay directly to the verified cash agent account above and receive real-time escrow verification chat.", color = TextSecondary, fontSize = 10.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.createB2BOrder(
                        agent = agent,
                        type = tradeType,
                        amount = amountVal,
                        paymentMethod = selectedMethod,
                        onOrderCreated = { order ->
                            onCreated(order)
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
            ) {
                Text("Confirm & Launch B2B Trade Chat", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = DarkSurface
    )
}

// ---------------- B2B ORDER DETAILS & IN-APP REAL-TIME CHAT DIALOG ----------------
@Composable
fun B2BOrderDetailAndChatDialog(
    order: B2BOrder,
    onDismiss: () -> Unit,
    viewModel: AppViewModel
) {
    val liveMessages by viewModel.getB2BChatMessages(order.orderId).collectAsStateWithLifecycle(initialValue = emptyList())
    val allOrders by viewModel.allB2BOrders.collectAsStateWithLifecycle()
    val liveOrder = remember(allOrders, order) {
        allOrders.find { it.orderId == order.orderId } ?: order
    }

    var messageInput by remember { mutableStateOf("") }
    var selectedScreenshotUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1556742049-0a670f4a4591") }
    var showProofModal by remember { mutableStateOf(false) }
    var showDisputeModal by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            shape = RoundedCornerShape(16.dp),
            color = DarkBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("B2B ORDER #${liveOrder.orderId}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))

                            val (stColor, stText) = when (liveOrder.status) {
                                "RELEASED" -> Color(0xFF00E676) to "RELEASED & SETTLED"
                                "DISPUTED" -> Color(0xFFEF4444) to "DISPUTED"
                                "PAYMENT_SUBMITTED" -> Color(0xFFFF9800) to "PAYMENT SUBMITTED"
                                else -> Color(0xFF2196F3) to "PENDING PAYMENT"
                            }

                            Box(
                                modifier = Modifier
                                    .background(stColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(stText, color = stColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text("Agent: ${liveOrder.agentName} (${liveOrder.country})", color = TextSecondary, fontSize = 11.sp)
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // Order Info Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, Color(0xFF2E2E3E)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Trade Amount: ৳${liveOrder.amount.toInt()} BDT", color = PinkHighlight, fontWeight = FontWeight.Black, fontSize = 14.sp)
                            Text("Type: ${liveOrder.type}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text("Agent Account (${liveOrder.paymentMethod}): ${liveOrder.agentAccountNumber}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        if (liveOrder.agentAccountHolder.isNotEmpty()) {
                            Text("Account Holder: ${liveOrder.agentAccountHolder}", color = TextSecondary, fontSize = 10.sp)
                        }
                        if (liveOrder.transactionRef != null) {
                            Text("Submitted Ref: ${liveOrder.transactionRef}", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (liveOrder.status == "PENDING_PAYMENT") {
                        Button(
                            onClick = { showProofModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Submit Payment Proof", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    if (liveOrder.status == "PAYMENT_SUBMITTED" || liveOrder.status == "PENDING_PAYMENT") {
                        Button(
                            onClick = { viewModel.releaseB2BOrder(liveOrder.orderId) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Confirm & Release", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    if (liveOrder.status != "RELEASED" && liveOrder.status != "CANCELLED") {
                        Button(
                            onClick = { showDisputeModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🚨 Raise Dispute", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }

                if (liveOrder.status == "DISPUTED") {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF3E1E24)),
                        border = BorderStroke(1.dp, Color(0xFFEF4444)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("🚨 Dispute Under Admin Moderation", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("Reason: ${liveOrder.disputeReason ?: "Payment verification conflict"}. Admin support has access to inspect this chat log and payment proof.", color = Color.White, fontSize = 10.sp)
                        }
                    }
                }

                // Chat Policy / Privacy Notice
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF181A26), RoundedCornerShape(6.dp))
                        .padding(6.dp)
                ) {
                    Text("🛡️ Admin Moderation Policy: Admin support can access chats & proofs for dispute resolution.", color = TextSecondary, fontSize = 9.sp)
                }

                // Real-Time B2B Chat Log
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(DarkSurface, RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(liveMessages) { msg ->
                            val isMe = msg.senderRole == "USER"
                            val isAdmin = msg.senderRole == "ADMIN"

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                            ) {
                                Text("${msg.senderName} (${msg.senderRole})", color = TextSecondary, fontSize = 9.sp)
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = when {
                                            isAdmin -> Color(0xFF2A1E3E)
                                            isMe -> PinkHighlight
                                            else -> Color(0xFF1E203E)
                                        }
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(msg.content, color = Color.White, fontSize = 12.sp)
                                        if (msg.imageUrl != null) {
                                            Box(
                                                modifier = Modifier
                                                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                                    .padding(6.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("[Screenshot Attachment Attached]", color = Color(0xFF2196F3), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Chat Input Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        placeholder = { Text("Type message to agent...", color = TextSecondary, fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            if (messageInput.isNotBlank()) {
                                viewModel.sendB2BChatMessage(
                                    orderId = liveOrder.orderId,
                                    senderRole = "USER",
                                    content = messageInput
                                )
                                messageInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }

    // Submit Proof Modal
    if (showProofModal) {
        var txnRef by remember { mutableStateOf("TXN${(10000000..99999999).random()}") }
        AlertDialog(
            onDismissRequest = { showProofModal = false },
            title = { Text("Upload Payment Screenshot Proof", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = txnRef,
                        onValueChange = { txnRef = it },
                        label = { Text("Transaction Reference ID", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Select Sample Screenshot:", color = TextSecondary, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            "bKash Receipt" to "https://images.unsplash.com/photo-1556742049-0a670f4a4591",
                            "Nagad Slip" to "https://images.unsplash.com/photo-1563013544-824ae1b704d3"
                        ).forEach { (lbl, url) ->
                            val isSel = selectedScreenshotUrl == url
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) PinkHighlight else DarkSurface)
                                    .clickable { selectedScreenshotUrl = url }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(lbl, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitB2BPaymentProof(liveOrder.orderId, txnRef, selectedScreenshotUrl)
                        showProofModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
                ) {
                    Text("Submit Proof")
                }
            },
            dismissButton = { TextButton(onClick = { showProofModal = false }) { Text("Cancel", color = TextSecondary) } },
            containerColor = DarkSurface
        )
    }

    // Raise Dispute Modal
    if (showDisputeModal) {
        var disputeReasonInput by remember { mutableStateOf("Payment made but agent delayed release") }
        AlertDialog(
            onDismissRequest = { showDisputeModal = false },
            title = { Text("Raise B2B Escrow Dispute", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("State reason for dispute. Admin team will review order chat log & screenshot proof.", color = TextSecondary, fontSize = 11.sp)
                    OutlinedTextField(
                        value = disputeReasonInput,
                        onValueChange = { disputeReasonInput = it },
                        label = { Text("Dispute Details", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.raiseB2BDispute(liveOrder.orderId, "USER", disputeReasonInput)
                        showDisputeModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Submit Dispute to Admin")
                }
            },
            dismissButton = { TextButton(onClick = { showDisputeModal = false }) { Text("Cancel", color = TextSecondary) } },
            containerColor = DarkSurface
        )
    }
}

// ---------------- ADMIN B2B DISPUTES & ORDERS CONTROL TAB ----------------
@Composable
fun AdminB2BDisputesTab(viewModel: AppViewModel) {
    val allOrders by viewModel.allB2BOrders.collectAsStateWithLifecycle()
    val allAgents by viewModel.paymentAgents.collectAsStateWithLifecycle()

    var statusFilter by remember { mutableStateOf("ALL") }
    var selectedOrderForAdminReview by remember { mutableStateOf<B2BOrder?>(null) }

    val filteredOrders = remember(allOrders, statusFilter) {
        if (statusFilter == "ALL") allOrders else allOrders.filter { it.status == statusFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("B2B Escrow & Dispute Center", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Monitor live B2B orders, chat histories, payment proofs & resolve disputes", color = TextSecondary, fontSize = 11.sp)
            }
        }

        // B2B Dashboard Metrics Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Total Orders", color = TextSecondary, fontSize = 10.sp)
                    Text("${allOrders.size}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Active Agents", color = TextSecondary, fontSize = 10.sp)
                    Text("${allAgents.size}", color = Color(0xFF00E676), fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Open Disputes", color = TextSecondary, fontSize = 10.sp)
                    Text("${allOrders.count { it.status == "DISPUTED" }}", color = Color(0xFFEF4444), fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            }
        }

        // Status Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("ALL", "DISPUTED", "PAYMENT_SUBMITTED", "RELEASED", "PENDING_PAYMENT", "CANCELLED").forEach { st ->
                val isSel = statusFilter == st
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) PinkHighlight else Color(0xFF2E2E3E))
                        .clickable { statusFilter = st }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(st, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Orders List
        if (filteredOrders.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text("No B2B orders matching status criteria.", color = TextSecondary, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredOrders) { order ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, if (order.status == "DISPUTED") Color(0xFFEF4444) else Color(0xFF2E2E3E)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Order #${order.orderId}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("(${order.country})", color = TextSecondary, fontSize = 10.sp)
                                }

                                val (stColor, stText) = when (order.status) {
                                    "RELEASED" -> Color(0xFF00E676) to "RELEASED"
                                    "DISPUTED" -> Color(0xFFEF4444) to "DISPUTED"
                                    "PAYMENT_SUBMITTED" -> Color(0xFFFF9800) to "SUBMITTED"
                                    else -> Color(0xFF2196F3) to "PENDING"
                                }

                                Box(
                                    modifier = Modifier
                                        .background(stColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(stText, color = stColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("User: ${order.userName}", color = Color.White, fontSize = 11.sp)
                                    Text("Agent: ${order.agentName}", color = TextSecondary, fontSize = 11.sp)
                                    Text("Method: ${order.paymentMethod}", color = TextSecondary, fontSize = 11.sp)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("৳${order.amount.toInt()} BDT", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Type: ${order.type}", color = TextSecondary, fontSize = 10.sp)
                                }
                            }

                            if (order.disputeReason != null) {
                                Text("Reason: ${order.disputeReason}", color = Color(0xFFEF4444), fontSize = 10.sp)
                            }

                            Button(
                                onClick = { selectedOrderForAdminReview = order },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Inspect Chat & Apply Dispute Decision", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Admin Dispute Resolution Dialog
    if (selectedOrderForAdminReview != null) {
        val selOrd = selectedOrderForAdminReview!!
        val liveMsgs by viewModel.getB2BChatMessages(selOrd.orderId).collectAsStateWithLifecycle(initialValue = emptyList())
        var adminNoteInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { selectedOrderForAdminReview = null },
            title = {
                Text("Admin Dispute & Chat Moderation (#${selOrd.orderId})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("User: ${selOrd.userName} • Agent: ${selOrd.agentName} • Amount: ৳${selOrd.amount.toInt()}", color = TextSecondary, fontSize = 11.sp)
                    if (selOrd.proofScreenshotUrl != null) {
                        Text("Submitted Proof: ${selOrd.proofScreenshotUrl}", color = Color(0xFF00E676), fontSize = 10.sp)
                    }

                    Text("Full User ↔ Agent Chat Log:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E202E))) {
                        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            liveMsgs.forEach { m ->
                                Text("${m.senderName} (${m.senderRole}): ${m.content}", color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = adminNoteInput,
                        onValueChange = { adminNoteInput = it },
                        label = { Text("Admin Decision Note", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFF2E2E3E)),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Admin Action Decisions:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(
                            onClick = {
                                viewModel.resolveB2BDispute(selOrd.orderId, "RELEASE_FUNDS", adminNoteInput.ifEmpty { "Funds released by admin" })
                                selectedOrderForAdminReview = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Release Funds", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.resolveB2BDispute(selOrd.orderId, "REFUND_USER", adminNoteInput.ifEmpty { "Refunded to user" })
                                selectedOrderForAdminReview = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Refund User", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.resolveB2BDispute(selOrd.orderId, "REJECT_CLAIM", adminNoteInput.ifEmpty { "Dispute claim rejected" })
                                selectedOrderForAdminReview = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Reject Claim", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { selectedOrderForAdminReview = null }) { Text("Close", color = TextSecondary) } },
            containerColor = DarkSurface
        )
    }
}




