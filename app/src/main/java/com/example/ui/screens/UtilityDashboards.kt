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
import androidx.compose.material.icons.filled.*
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
        AlertDialog(
            onDismissRequest = { showAddFundsDialog = false },
            title = { Text("Add Funds to Wallet", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = viewModel.depositAmount,
                        onValueChange = { viewModel.depositAmount = it },
                        label = { Text("Amount in Taka (৳)", color = TextSecondary) },
                        placeholder = { Text("Enter amount, e.g. 1000", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PinkHighlight,
                            unfocusedBorderColor = Color(0xFF2E2E3E)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Select Funding Channel", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("bKash", "Nagad", "Rocket", "Stripe").forEach { m ->
                            val isSel = viewModel.walletMethod == m
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) PinkHighlight else Color(0xFF2E2E3E))
                                    .clickable { viewModel.walletMethod = m }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(m, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (viewModel.depositAmount.isNotBlank()) {
                            viewModel.depositWallet()
                            showAddFundsDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
                ) {
                    Text("Confirm", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFundsDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }

    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = { Text("Request Income Withdrawal", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = viewModel.withdrawAmount,
                        onValueChange = { viewModel.withdrawAmount = it },
                        label = { Text("Amount in Taka (৳)", color = TextSecondary) },
                        placeholder = { Text("Enter amount, e.g. 2000", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PinkHighlight,
                            unfocusedBorderColor = Color(0xFF2E2E3E)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Select Withdrawal Channel", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("bKash", "Nagad", "Rocket", "Stripe").forEach { m ->
                            val isSel = viewModel.walletMethod == m
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) PinkHighlight else Color(0xFF2E2E3E))
                                    .clickable { viewModel.walletMethod = m }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(m, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (viewModel.withdrawAmount.isNotBlank()) {
                            viewModel.withdrawWallet()
                            showWithdrawDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
                ) {
                    Text("Confirm", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
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
@Composable
fun AdminCollectionsTab(viewModel: AppViewModel) {
    val collections by viewModel.cashCollections.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Payment & Cash collections", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Review cash payments submitted by Cash Agents. Approving them will mark bookings as PAID and release escrow.", color = TextSecondary, fontSize = 11.sp)

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(collections) { coll ->
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
                            Column {
                                Text("ID: ${coll.id}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Booking: ${coll.bookingId}", color = TextSecondary, fontSize = 11.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (coll.status == "PAID") Color(0xFF4CAF50).copy(alpha = 0.15f) else Color(0xFFFF9800).copy(alpha = 0.15f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    coll.status,
                                    color = if (coll.status == "PAID") Color(0xFF4CAF50) else Color(0xFFFF9800),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Collected By", color = TextSecondary, fontSize = 10.sp)
                                Text(coll.agentName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Amount Collected", color = TextSecondary, fontSize = 10.sp)
                                Text("৳${coll.amount.toInt()}", color = PinkHighlight, fontWeight = FontWeight.Black, fontSize = 14.sp)
                            }
                        }

                        if (coll.status == "PENDING") {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.approveCashCollectionByAdmin(coll.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(36.dp)
                            ) {
                                Text("Approve & Verify Cash Payment", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- ADMIN WALLET APPROVALS TAB ----------------
@Composable
fun AdminWalletsTab(viewModel: AppViewModel) {
    var payoutRequests by remember {
        mutableStateOf(
            listOf(
                Pair("Jessica (Model)", 1250.0),
                Pair("Agent Sumon", 675.0),
                Pair("Agent Rafiq", 820.0)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Wallet & Withdrawal Requests", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Escrow Volume", color = TextSecondary, fontSize = 11.sp)
                    Text("৳1,250,300", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Pending Withdrawals", color = TextSecondary, fontSize = 11.sp)
                    Text("৳${payoutRequests.sumOf { it.second }.toInt()}", color = PinkHighlight, fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            }
        }

        Text("Pending Payout Approvals", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)

        if (payoutRequests.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text("All withdrawal requests cleared! ✓", color = TextSecondary, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(payoutRequests.size) { idx ->
                    val req = payoutRequests[idx]
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(req.first, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Method: bKash Agent", color = TextSecondary, fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("৳${req.second.toInt()}", color = PinkHighlight, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF4CAF50), RoundedCornerShape(6.dp))
                                        .clickable {
                                            viewModel.addNotification("Withdrawal Approved", "Approved payment withdrawal of ৳${req.second} to ${req.first}", "Wallet")
                                            payoutRequests = payoutRequests.toMutableList().apply { removeAt(idx) }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Approve Payout", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
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
                                "Apple Pay" to acceptApplePay
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



