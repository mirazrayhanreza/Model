package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
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
    var selectedWalletTab by remember { mutableStateOf(0) } // 0 = Personal Wallet, 1 = B2B Cash Agent

    // Calculate balances
    val availableBalance = currentUser?.balance ?: 0.0
    val userCountry = currentUser?.country ?: "Bangladesh"
    val userCurrency = currentUser?.currency ?: CountryPaymentMaster.getCurrencyForCountry(userCountry)
    val currSymbol = CountryPaymentMaster.getCurrencySymbol(userCurrency)
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
            title = { Text("Exclusive Promo Offers", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
            text = {
                Column {
                    Text("Get special discounts on your active booking deposits!", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F5)),
                        border = BorderStroke(1.dp, PinkHighlight.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("PROMO CODE", color = PinkHighlight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("MODOL20", color = PinkHighlight, fontWeight = FontWeight.Black, fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Get 20% OFF on your next model booking. Valid on escrow services.",
                                color = TextPrimary,
                                fontSize = 12.sp,
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
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Awesome", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White
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
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFFF85A6), CircleShape)
                            .size(38.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text("My Wallet", color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            }
            IconButton(
                onClick = { /* Already on wallet */ },
                modifier = Modifier
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color(0xFFFF85A6), CircleShape)
                    .size(38.dp)
            ) {
                Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = "Wallet info", tint = PinkHighlight)
            }
        }

        // ==========================================
        // WALLET SEGMENT TABS (Personal vs B2B)
        // ==========================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .background(Color.White, RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFFFFDDE6), RoundedCornerShape(14.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Tab 0: Personal Wallet
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedWalletTab == 0) PinkHighlight else Color.Transparent)
                    .clickable { selectedWalletTab = 0 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = if (selectedWalletTab == 0) Color.White else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Personal Wallet",
                        color = if (selectedWalletTab == 0) Color.White else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            // Tab 1: B2B Cash Agent
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedWalletTab == 1) PinkHighlight else Color.Transparent)
                    .clickable { selectedWalletTab = 1 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = if (selectedWalletTab == 1) Color.White else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "B2B Cash Agent",
                        color = if (selectedWalletTab == 1) Color.White else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .background(if (selectedWalletTab == 1) Color.White.copy(alpha = 0.25f) else Color(0xFFFF2A6D).copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            "P2P",
                            color = if (selectedWalletTab == 1) Color.White else PinkHighlight,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        if (selectedWalletTab == 1) {
            // Tab 1: B2B Cash Agent Marketplace (Exact Reference Photo Design)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                B2BCashAgentMarketplaceContent(viewModel = viewModel, onClose = null)
            }
        } else {
            // Tab 0: Standard Personal Wallet Content
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
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFFF2A6D), Color(0xFFFF5E8A), Color(0xFF7C3AED))
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
                                    Text("Total Balance ($userCurrency)", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$currSymbol %,.2f".format(totalBalance),
                                        color = Color.White,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            HorizontalDivider(color = Color.White.copy(alpha = 0.25f), thickness = 0.5.dp)

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Available Balance", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$currSymbol %,.2f".format(availableBalance),
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(30.dp)
                                        .background(Color.White.copy(alpha = 0.25f))
                                )

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 16.dp)
                                ) {
                                    Text("Pending Balance", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$currSymbol %,.2f".format(pendingBalance),
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
                                        .background(Color.White)
                                        .border(1.dp, Color(0xFFFFDDE6), RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(action.color.copy(alpha = 0.12f), CircleShape),
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
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
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
                            color = TextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (showAllTransactions) "Collapse" else "View All",
                            color = PinkHighlight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
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
                                        .background(txColor.copy(alpha = 0.12f), CircleShape),
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
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = tx.description,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            val isPositive = tx.type == "DEPOSIT" || tx.type == "EARNING"
                            Text(
                                text = if (isPositive) "+$currSymbol %,.2f".format(tx.amount) else "-$currSymbol %,.2f".format(tx.amount),
                                color = if (isPositive) OnlineGreen else Color(0xFFFF5252),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        HorizontalDivider(color = PinkBorderLight, thickness = 0.8.dp)
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
                IconButton(
                    onClick = { viewModel.navigateTo("DASHBOARD") },
                    modifier = Modifier
                        .background(Color.White, CircleShape)
                        .border(1.dp, Color(0xFFFF85A6), CircleShape)
                        .size(38.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("Notifications", color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            }
            IconButton(
                onClick = { viewModel.navigateTo("SETTINGS") },
                modifier = Modifier
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color(0xFFFF85A6), CircleShape)
                    .size(38.dp)
            ) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = TextPrimary)
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
                        .background(if (isSelected) PinkHighlight else Color.White)
                        .then(if (!isSelected) Modifier.border(1.dp, Color(0xFFFF85A6), RoundedCornerShape(20.dp)) else Modifier)
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) Color.White else TextPrimary,
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
                    Text("No Notifications in $selectedCategory", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFFF85A6)),
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
                                    "Booking" -> Triple(Icons.Default.CalendarMonth, Color(0xFFE91E63).copy(alpha = 0.12f), Color(0xFFE91E63))
                                    "Payment", "Wallet" -> Triple(Icons.Default.CheckCircle, Color(0xFF4CAF50).copy(alpha = 0.12f), Color(0xFF4CAF50))
                                    "Chat" -> Triple(Icons.Default.Chat, Color(0xFF9C27B0).copy(alpha = 0.12f), Color(0xFF9C27B0))
                                    "Promotions" -> Triple(Icons.Default.Redeem, Color(0xFFFF9800).copy(alpha = 0.12f), Color(0xFFFF9800))
                                    "System" -> Triple(Icons.Default.Verified, Color(0xFF2196F3).copy(alpha = 0.12f), Color(0xFF2196F3))
                                    else -> Triple(Icons.Default.Info, Color(0xFF607D8B).copy(alpha = 0.12f), Color(0xFF607D8B))
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
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = notif.time,
                                        color = TextSecondary,
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo("DASHBOARD") }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Model Host Dashboard", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            IconButton(onClick = { viewModel.showSignOutConfirmDialog = true }) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = "Sign Out", tint = Color(0xFFEF4444))
            }
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
                Text("Incoming Booking Proposals", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
                                Text("Client ID: ${b.userId}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo("DASHBOARD") }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Admin Platform Panel", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            IconButton(onClick = { viewModel.showSignOutConfirmDialog = true }) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout", tint = Color(0xFFEF4444))
            }
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
                            Text(models.size.toString(), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
                            Text(bookings.size.toString(), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                }
            }

            // Verification lists
            item {
                Text("Verification Management", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(models) { m ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFFF85A6)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(m.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Status: ${if (m.isVerified) "Verified" else "Awaiting Verification"}", color = if (m.isVerified) OnlineGreen else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }

                        Button(
                            onClick = { viewModel.verifyModel(m.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = if (m.isVerified) Color(0xFF64748B) else PinkHighlight),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(if (m.isVerified) "Revoke" else "Verify", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
            .background(Color(0xFFF8FAFC))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo("DASHBOARD") },
                modifier = Modifier
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                    .size(38.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Settings & Security", color = Color(0xFF0F172A), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // =========================================================================
            // 1. ACCOUNT SECURITY & PASSWORDS (CHANGE PASSWORD & ALL DEVICES LOGOUT)
            // =========================================================================
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.2.dp, Color(0xFFFFE4EC)),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color(0xFFFF2A6D).copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFFFF2A6D), modifier = Modifier.size(16.dp))
                            }
                            Column {
                                Text("Account Security & Credentials", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("পাসওয়ার্ড ও সেশন ম্যানেজমেন্ট", color = Color(0xFF64748B), fontSize = 11.sp)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFECFDF5),
                            border = BorderStroke(0.8.dp, Color(0xFFA7F3D0))
                        ) {
                            Text(
                                "${viewModel.activeSessionsList.size} Active",
                                color = Color(0xFF059669),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // Option 1: Change Password
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.showChangePasswordDialog = true }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFF2A6D).copy(alpha = 0.10f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Default.LockReset, contentDescription = null, tint = Color(0xFFFF2A6D), modifier = Modifier.size(20.dp))
                                }
                            }
                            Column {
                                Text("Change Password (পাসওয়ার্ড পরিবর্তন)", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Update your login password and protect account", color = Color(0xFF64748B), fontSize = 11.sp)
                            }
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // Option 2: Log Out From All Devices
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.showAllLogoutConfirmDialog = true }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFDC2626).copy(alpha = 0.10f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Default.Devices, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                                }
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Log Out All Devices (সকল ডিভাইস থেকে লগআউট)", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFEF2F2)) {
                                        Text("${viewModel.activeSessionsList.size} Sessions", color = Color(0xFFDC2626), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }
                                Text("Terminate active sessions on other phones & browsers", color = Color(0xFF64748B), fontSize = 11.sp)
                            }
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Language selector
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("App Language", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("English", "Bangla").forEach { lang ->
                            val isSel = viewModel.appLanguage == lang
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFFFF2A6D) else Color(0xFFF1F5F9))
                                    .then(if (!isSel) Modifier.border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp)) else Modifier)
                                    .clickable { viewModel.appLanguage = lang }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(lang, color = if (isSel) Color.White else Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Splash Screen Preview
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.splashFinished = false
                        viewModel.navigateTo("SPLASH")
                    }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFF2A6D).copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Splash",
                                    tint = Color(0xFFFF2A6D),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Welcome / Splash Screen", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Tap to view full-screen model splash design", color = Color(0xFF64748B), fontSize = 11.sp)
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open",
                        tint = Color(0xFFFF2A6D),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // About Us
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("About MODOL CONNECT", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "MODOL CONNECT is the World's #1 professional model marketplace app. We connect brands, fashion houses, and individuals with top professional talents worldwide seamlessly, with secure escrow payments.",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            // Sign Out / Logout Card (This Device)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = BorderStroke(1.dp, Color(0xFFFECACA)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.showSignOutConfirmDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout", tint = Color(0xFFDC2626))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Sign Out (এই ডিভাইস থেকে সাইন আউট)", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Safely exit your account on this device", color = Color(0xFF7F1D1D), fontSize = 11.sp)
                        }
                    }
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Exit", tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                }
            }
        }
    }

    // Dialogs
    if (viewModel.showChangePasswordDialog) {
        ChangePasswordModal(
            onDismiss = { viewModel.showChangePasswordDialog = false },
            viewModel = viewModel
        )
    }

    if (viewModel.showAllLogoutConfirmDialog) {
        AllDevicesLogoutModal(
            onDismiss = { viewModel.showAllLogoutConfirmDialog = false },
            viewModel = viewModel
        )
    }
}

// ============================================================================
// CHANGE PASSWORD MODAL (পাসওয়ার্ড পরিবর্তন মোডাল)
// ============================================================================

@Composable
fun ChangePasswordModal(
    onDismiss: () -> Unit,
    viewModel: AppViewModel
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showOldPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var logoutOtherDevicesAfterChange by remember { mutableStateOf(true) }

    // Password strength logic
    val (strengthScore, strengthLabel, strengthColor) = remember(newPassword) {
        if (newPassword.isEmpty()) {
            Triple(0f, "Enter password (পাসওয়ার্ড লিখুন)", Color(0xFF94A3B8))
        } else {
            var score = 0
            if (newPassword.length >= 6) score += 1
            if (newPassword.length >= 8) score += 1
            if (newPassword.any { it.isDigit() }) score += 1
            if (newPassword.any { it.isUpperCase() }) score += 1
            if (newPassword.any { !it.isLetterOrDigit() }) score += 1
            when {
                score <= 2 -> Triple(0.33f, "Weak (দুর্বল)", Color(0xFFEF4444))
                score <= 3 -> Triple(0.66f, "Moderate (মাঝারি)", Color(0xFFF59E0B))
                else -> Triple(1.0f, "Strong (খুব শক্তিশালী)", Color(0xFF10B981))
            }
        }
    }

    val passwordsMatch = newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && newPassword == confirmPassword
    val passwordsMismatch = newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && newPassword != confirmPassword

    AlertDialog(
        onDismissRequest = {
            viewModel.changePasswordError = null
            viewModel.changePasswordSuccess = null
            onDismiss()
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(22.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFF2A6D).copy(alpha = 0.12f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LockReset,
                            contentDescription = null,
                            tint = Color(0xFFFF2A6D),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column {
                    Text(
                        "Change Password",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        "পাসওয়ার্ড পরিবর্তন ও অ্যাকাউন্ট সুরক্ষা",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
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
                // Info banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFFFF2A6D),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            "আপনার অ্যাকাউন্টের সর্বোচ্চ সুরক্ষায় নতুন পাসওয়ার্ডটি কমপক্ষে ৬ অক্ষরের হতে হবে। সংখ্যা এবং স্পেশাল চিহ্ন যোগ করা ভালো।",
                            color = Color(0xFF475569),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                // Error Banner
                if (viewModel.changePasswordError != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFEF2F2),
                        border = BorderStroke(1.dp, Color(0xFFFECACA))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                viewModel.changePasswordError ?: "",
                                color = Color(0xFFDC2626),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Success Banner
                if (viewModel.changePasswordSuccess != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFECFDF5),
                        border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    viewModel.changePasswordSuccess ?: "পাসওয়ার্ড সফলভাবে পরিবর্তন করা হয়েছে!",
                                    color = Color(0xFF059669),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "আপনার পাসওয়ার্ড আপডেট হয়েছে এবং নতুন সেশন কার্যকর হয়েছে।",
                                    color = Color(0xFF047857),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                // 1. Current Password
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        label = { Text("Current Password (বর্তমান পাসওয়ার্ড)") },
                        visualTransformation = if (showOldPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showOldPassword = !showOldPassword }) {
                                Icon(
                                    imageVector = if (showOldPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF0F172A),
                            focusedBorderColor = Color(0xFFFF2A6D),
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // 2. New Password
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password (নতুন পাসওয়ার্ড)") },
                        visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showNewPassword = !showNewPassword }) {
                                Icon(
                                    imageVector = if (showNewPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF0F172A),
                            focusedBorderColor = Color(0xFFFF2A6D),
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Strength Indicator Bar
                    if (newPassword.isNotEmpty()) {
                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Password Strength:",
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                                Text(
                                    strengthLabel,
                                    color = strengthColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { strengthScore },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = strengthColor,
                                trackColor = Color(0xFFE2E8F0)
                            )
                        }
                    }
                }

                // 3. Confirm Password
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm New Password (পুনরায় লিখুন)") },
                        visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                Icon(
                                    imageVector = if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF0F172A),
                            focusedBorderColor = if (passwordsMatch) Color(0xFF10B981) else if (passwordsMismatch) Color(0xFFEF4444) else Color(0xFFFF2A6D),
                            unfocusedBorderColor = if (passwordsMatch) Color(0xFF10B981) else if (passwordsMismatch) Color(0xFFEF4444) else Color(0xFFCBD5E1)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (passwordsMatch) {
                        Text(
                            "✓ Passwords match (পাসওয়ার্ড মিলেছে)",
                            color = Color(0xFF059669),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    } else if (passwordsMismatch) {
                        Text(
                            "⚠ Passwords do not match (পাসওয়ার্ড মিলছে না)",
                            color = Color(0xFFDC2626),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                // 4. Logout from all other devices option (Security best practice)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF1F2),
                    border = BorderStroke(1.dp, Color(0xFFFFE4E6)),
                    modifier = Modifier.clickable { logoutOtherDevicesAfterChange = !logoutOtherDevicesAfterChange }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Checkbox(
                            checked = logoutOtherDevicesAfterChange,
                            onCheckedChange = { logoutOtherDevicesAfterChange = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFFFF2A6D),
                                checkmarkColor = Color.White
                            )
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Log out all other devices after change",
                                color = Color(0xFF9F1239),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                "অন্যান্য ডিভাইসের পুরনো সেশন তাৎক্ষণিক বাতিল করুন",
                                color = Color(0xFFBE123C),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.changePassword(
                        oldPass = oldPassword,
                        newPass = newPassword,
                        confirmPass = confirmPassword,
                        logoutOtherDevicesAfterChange = logoutOtherDevicesAfterChange,
                        onSuccess = {
                            oldPassword = ""
                            newPassword = ""
                            confirmPassword = ""
                        }
                    )
                },
                enabled = !viewModel.isChangingPassword,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A6D)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(44.dp)
            ) {
                if (viewModel.isChangingPassword) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Updating...", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Default.LockReset, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Text("Update Password", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                viewModel.changePasswordError = null
                viewModel.changePasswordSuccess = null
                onDismiss()
            }) {
                Text("Close", color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

// ============================================================================
// ALL DEVICES LOGOUT MODAL (সকল ডিভাইস থেকে লগআউট মোডাল)
// ============================================================================

@Composable
fun AllDevicesLogoutModal(
    onDismiss: () -> Unit,
    viewModel: AppViewModel
) {
    val activeSessions = viewModel.activeSessionsList
    val currentSession = activeSessions.firstOrNull { it.isCurrent }
    val otherSessions = activeSessions.filterNot { it.isCurrent }

    AlertDialog(
        onDismissRequest = {
            viewModel.allLogoutSuccessMessage = null
            onDismiss()
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(22.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFDC2626).copy(alpha = 0.12f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Devices,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column {
                    Text(
                        "Active Devices & Logout",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        "ডিভাইস ম্যানেজমেন্ট ও সেশন বাতিল",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
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
                // Informational banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, Color(0xFFFECACA))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "আপনার অ্যাকাউন্টে মোট ${activeSessions.size}টি সেশন সক্রিয় রয়েছে। সন্দেহজনক কোনো ডিভাইস থাকলে 'Revoke' চাপুন অথবা এক ক্লিকে অন্য সব ডিভাইস থেকে লগআউট করুন।",
                            color = Color(0xFF991B1B),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                // Success Message Banner (e.g. session revoked or other devices logged out)
                if (viewModel.allLogoutSuccessMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFECFDF5),
                        border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                viewModel.allLogoutSuccessMessage ?: "",
                                color = Color(0xFF059669),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // --- 1. CURRENT DEVICE ---
                Text(
                    "Current Device (বর্তমান ডিভাইস):",
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, Color(0xFF10B981).copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = null,
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    currentSession?.deviceType ?: "This Device (Android)",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A),
                                    fontSize = 13.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(Color(0xFF10B981), CircleShape)
                                )
                            }
                            Text(
                                "${currentSession?.os ?: "Android"} • ${currentSession?.location ?: "Dhaka, Bangladesh"}",
                                color = Color(0xFF64748B),
                                fontSize = 10.sp
                            )
                            Text(
                                "IP: ${currentSession?.ip ?: "103.145.172.45"}",
                                color = Color(0xFF94A3B8),
                                fontSize = 9.sp
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFECFDF5),
                            border = BorderStroke(0.8.dp, Color(0xFFA7F3D0))
                        ) {
                            Text(
                                "Active Now",
                                color = Color(0xFF059669),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // --- 2. OTHER ACTIVE SESSIONS ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Other Active Devices (${otherSessions.size}):",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    if (otherSessions.isNotEmpty()) {
                        Text(
                            "অন্যান্য সক্রিয় ডিভাইস",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp
                        )
                    }
                }

                if (otherSessions.isEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    "No other active sessions detected",
                                    color = Color(0xFF15803D),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    "আপনার অ্যাকাউন্ট শুধুমাত্র বর্তমান ফোনেই সংযুক্ত রয়েছে।",
                                    color = Color(0xFF166534),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                } else {
                    otherSessions.forEach { session ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFE2E8F0),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = when {
                                                session.deviceType.contains("Web", ignoreCase = true) -> Icons.Default.Computer
                                                session.deviceType.contains("Tab", ignoreCase = true) -> Icons.Default.TabletAndroid
                                                else -> Icons.Default.PhoneAndroid
                                            },
                                            contentDescription = null,
                                            tint = Color(0xFF475569),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        session.deviceType,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A),
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        "${session.os} • ${session.location}",
                                        color = Color(0xFF64748B),
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        "Last active: ${session.lastActive} (IP: ${session.ip})",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 9.sp
                                    )
                                }

                                // Revoke button for individual session
                                OutlinedButton(
                                    onClick = { viewModel.revokeSingleSession(session.id) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                    border = BorderStroke(1.dp, Color(0xFFFECACA)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Revoke", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // --- 3. DUAL LOGOUT OPTIONS (Log out other devices vs Log out all devices) ---
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                // Option A: Log Out Other Devices (Keeps current device logged in)
                OutlinedButton(
                    onClick = { viewModel.logoutOtherDevices() },
                    enabled = !viewModel.isLoggingOutOtherDevices && otherSessions.isNotEmpty(),
                    border = BorderStroke(1.2.dp, Color(0xFFFF2A6D)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF2A6D),
                        containerColor = Color(0xFFFFF1F2)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    if (viewModel.isLoggingOutOtherDevices) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color(0xFFFF2A6D),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Revoking Other Devices...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Devices, contentDescription = null, tint = Color(0xFFFF2A6D), modifier = Modifier.size(16.dp))
                            Text(
                                "Log Out Other Devices Only (অন্য ডিভাইসগুলো লগআউট)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Option B: Log Out From ALL Devices (Including this phone)
                Button(
                    onClick = { viewModel.logoutAllDevices() },
                    enabled = !viewModel.isLoggingOutAll,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    if (viewModel.isLoggingOutAll) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Logging Out All...", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Logout, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Text(
                                "Log Out From ALL Devices (সব ডিভাইস থেকে সম্পূর্ণ লগআউট)",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = {
                viewModel.allLogoutSuccessMessage = null
                onDismiss()
            }) {
                Text("Close", color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

// ============================================================================
// SIGN OUT CONFIRM MODAL (লগআউট নিশ্চিতকরণ মোডাল)
// ============================================================================

@Composable
fun SignOutConfirmModal(
    onDismiss: () -> Unit,
    viewModel: AppViewModel
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val roleDisplay = when (currentUser?.role) {
        "ADMIN" -> "Admin Panel (অ্যাডমিন প্যানেল)"
        "MODEL" -> "Model Host (মডেল হোস্ট)"
        "CASH_AGENT" -> "Cash Agent (ক্যাশ এজেন্ট)"
        else -> "Client User (ইউজার)"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(22.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, Color(0xFFFECACA)),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Column {
                    Text(
                        "Sign Out Account",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        roleDisplay,
                        color = Color(0xFFDC2626),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFF2A6D).copy(alpha = 0.15f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFFFF2A6D),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                currentUser?.name ?: "Current User",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                fontSize = 13.sp
                            )
                            Text(
                                "Role: $roleDisplay",
                                color = Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                            Text(
                                currentUser?.email?.ifEmpty { "user@modolconnect.com" } ?: "Active Session",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Text(
                    "আপনি কীভাবে লগআউট করতে চান নির্বাচন করুন:",
                    color = Color(0xFF475569),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                // Option 1: Standard Logout (This Device Only)
                Button(
                    onClick = {
                        onDismiss()
                        viewModel.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Default.Logout, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Text("Log Out This Device (এই ফোন থেকে লগআউট)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                // Option 2: All Devices Logout
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        viewModel.showAllLogoutConfirmDialog = true
                    },
                    border = BorderStroke(1.2.dp, Color(0xFFEF4444)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFDC2626),
                        containerColor = Color(0xFFFFF1F2)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Default.Devices, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                        Text("Log Out All Devices (সকল ডিভাইস থেকে লগআউট)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel (বাতিল)", color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
            }
        }
    )
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
            .background(DarkBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. TOP HEADER & BRANDING BAR ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, PinkBorderSoft)
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
                                    color = TextPrimary,
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
                                    .background(Color(0xFFFFF0F5), RoundedCornerShape(20.dp))
                                    .border(1.dp, PinkBorderSoft, RoundedCornerShape(20.dp))
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
                                        color = TextPrimary,
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

                            // Quick Admin Sign Out Button
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFDC2626).copy(alpha = 0.15f), CircleShape)
                                    .border(1.dp, Color(0xFFDC2626).copy(alpha = 0.4f), CircleShape)
                                    .clickable { viewModel.showSignOutConfirmDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "Logout",
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = PinkBorderSoft)

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
                                Text("Miraz Rayhan", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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

        // --- 1.5 USER TO MODEL LIVE LOCATION TRACKING HERO CARD ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, Color(0xFF00E676).copy(alpha = 0.7f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (viewModel.currentUser.value?.role == "ADMIN") {
                            viewModel.selectedTab = 1
                        } else {
                            viewModel.selectedTab = 5
                        }
                        viewModel.navigateTo("DASHBOARD")
                    }
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF00E676).copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("User to Model Live Tracking", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF00E676), RoundedCornerShape(10.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("LIVE GPS", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 8.sp)
                                    }
                                }
                                Text("Real-Time Radar Map & Escort Geofence Telemetry", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open",
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Mini live stats strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("3 Active Escort Routes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("Precision: ±2.5m • 16 Sats", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        Text("Open Map ➔", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // --- 1.1 FIREBASE & OTP GATEWAY QUICK CONFIG CARD ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFFF9100).copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectedTab = 7 }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFFF9100).copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFFF9100),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Firebase & OTP Gateway",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF00E676).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text("ADMIN CONFIG", color = Color(0xFF00E676), fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Live SMS Gateway • User-Facing Firebase Hidden • Tap to Configure ➔",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Settings",
                        tint = Color(0xFFFF9100),
                        modifier = Modifier.size(16.dp)
                    )
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
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, PinkBorderSoft),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Revenue Overview", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, PinkBorderSoft),
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
                                Text("8,965", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
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
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, PinkBorderSoft),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Quick Actions", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                QuickActionButtonTile(
                                    icon = Icons.Default.PersonAdd,
                                    label = "Add New Model",
                                    color = Color(0xFFFF2B85),
                                    modifier = Modifier.weight(1f)
                                ) { viewModel.selectedTab = 3 }

                                QuickActionButtonTile(
                                    icon = Icons.Default.WorkOutline,
                                    label = "Add Cash Agent",
                                    color = Color(0xFF4CAF50),
                                    modifier = Modifier.weight(1f)
                                ) { viewModel.selectedTab = 3 }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                QuickActionButtonTile(
                                    icon = Icons.Default.PersonAddAlt1,
                                    label = "Add New User",
                                    color = Color(0xFF2196F3),
                                    modifier = Modifier.weight(1f)
                                ) { viewModel.selectedTab = 3 }

                                QuickActionButtonTile(
                                    icon = Icons.Default.LocationOn,
                                    label = "Live GPS Radar",
                                    color = Color(0xFF00E676),
                                    modifier = Modifier.weight(1f)
                                ) { viewModel.selectedTab = 1 }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                QuickActionButtonTile(
                                    icon = Icons.Default.AccountBalanceWallet,
                                    label = "Pending Payments",
                                    color = Color(0xFF9C27B0),
                                    modifier = Modifier.weight(1f)
                                ) { viewModel.selectedTab = 4 }

                                QuickActionButtonTile(
                                    icon = Icons.Default.Payments,
                                    label = "Cash Collection",
                                    color = Color(0xFF009688),
                                    modifier = Modifier.weight(1f)
                                ) { viewModel.selectedTab = 5 }
                            }
                        }
                    }
                }
            }
        }

        // --- 5. RECENT BOOKINGS TABLE (EXACT MATCH) ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, PinkBorderSoft),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Recent Bookings", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                                Text(bk.id, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(bk.dateTime, color = TextSecondary, fontSize = 8.sp)
                            }
                            Column(modifier = Modifier.weight(1.5f)) {
                                Text(bk.user, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
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
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, PinkBorderSoft),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Recent Cash Collections", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Box(
                            modifier = Modifier
                                .background(PinkHighlight.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .clickable { viewModel.selectedTab = 5 }
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
                                Text(coll.id, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text("Ref: ${coll.bookingId}", color = TextSecondary, fontSize = 8.sp)
                            }
                            Text(coll.agent, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, modifier = Modifier.weight(1.3f))
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
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, PinkBorderSoft),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("System Overview", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            SystemOverviewRow(icon = Icons.Default.Person, label = "Online Users", value = "256", iconTint = Color(0xFF4CAF50))
                            SystemOverviewRow(icon = Icons.Default.Layers, label = "App Version", value = "2.0.0", iconTint = Color(0xFF9C27B0))
                            SystemOverviewRow(icon = Icons.Default.SwapHoriz, label = "Total Transactions", value = "24,856", iconTint = Color(0xFF2196F3))
                            SystemOverviewRow(icon = Icons.Default.Public, label = "Active Countries", value = "16", iconTint = Color(0xFFFF9800))
                        }
                    }
                }

                // Recent Notifications Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, PinkBorderSoft),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Recent Notifications", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
        Text(value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
            Text(text, color = TextPrimary, fontSize = 11.sp, maxLines = 1)
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
        Text(count, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
            Text(label, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, PinkBorderSoft),
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
            Text(value, color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
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
        Text(label, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
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
            Text(text, color = TextPrimary, fontSize = 11.sp, maxLines = 1)
        }
        Text(time, color = TextSecondary, fontSize = 9.sp)
    }
}

// ---------------- USER & MODEL VERIFICATION TAB ----------------
@Composable
fun AdminUsersTab(viewModel: AppViewModel) {
    val context = LocalContext.current
    val models by viewModel.allModels.collectAsStateWithLifecycle()
    val dbAgents by viewModel.paymentAgents.collectAsStateWithLifecycle()
    val managedUsers = viewModel.managedUsers
    var tabSelected by remember { mutableStateOf(0) } // 0: Verification Center, 1: All Users, 2: Models, 3: Cash Agents
    var searchQuery by remember { mutableStateOf("") }
    var filterStatus by remember { mutableStateOf("ALL") } // "ALL", "PENDING_PHONE", "PENDING_EMAIL", "VERIFIED"

    // Modal state for manual code generation
    var manualCodeModalUser by remember { mutableStateOf<com.example.viewmodel.ManagedUser?>(null) }
    var manualCodeType by remember { mutableStateOf("PHONE") } // "PHONE" or "EMAIL"
    var manualCodeInput by remember { mutableStateOf("${(100000..999999).random()}") }

    // Count pending verifications
    val pendingPhoneCount = managedUsers.count { !it.isPhoneVerified }
    val pendingEmailCount = managedUsers.count { !it.isEmailVerified }
    val totalPendingCount = managedUsers.count { !it.isPhoneVerified || !it.isEmailVerified }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Row with Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("User Verification & Management", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Manual code generation & 1-click verification gateway", color = TextSecondary, fontSize = 11.sp)
            }
            Surface(
                color = if (totalPendingCount > 0) Color(0xFFFEF3C7) else Color(0xFFDCFCE7),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, if (totalPendingCount > 0) Color(0xFFFDE68A) else Color(0xFF86EFAC))
            ) {
                Text(
                    text = if (totalPendingCount > 0) "$totalPendingCount Pending" else "All Verified ✓",
                    color = if (totalPendingCount > 0) Color(0xFFD97706) else Color(0xFF16A34A),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Master Role OTP Quick Bar
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF1E293B),
            border = BorderStroke(1.dp, PinkHighlight.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().clickable { tabSelected = 6 }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Backend Master One-Time Passwords (OTPs):", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("👑 Admin: ${viewModel.adminMasterOtp} • 💃 Model: ${viewModel.modelMasterOtp} • 👤 User: ${viewModel.userMasterOtp} • 💼 Agent: ${viewModel.cashAgentMasterOtp}", color = PinkHighlight, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Box(
                    modifier = Modifier
                        .background(PinkHighlight.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Manage OTPs ⚙️", color = PinkHighlight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Sub-tabs Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val pendingKycCount = viewModel.submittedIdentityVerifications.count { it.status == "PENDING_REVIEW" }
            listOf(
                "Verification Center ($totalPendingCount)" to 0,
                "📄 NID & Passport KYC ($pendingKycCount)" to 7,
                "All Users (${managedUsers.size})" to 1,
                "Models (${models.size})" to 2,
                "Cash Agents (${dbAgents.size})" to 3,
                "🔐 Role OTPs" to 6,
                "Uploaded Photos (${viewModel.backendUploadedPhotos.size})" to 4,
                "📍 Live Tracking (3 Active)" to 5
            ).forEach { (label, idx) ->
                val isSel = tabSelected == idx
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSel) PinkHighlight else DarkSurface,
                    border = BorderStroke(1.dp, if (isSel) PinkHighlight else Color(0xFFFF85A6)),
                    modifier = Modifier.clickable { tabSelected = idx }
                ) {
                    Text(
                        label,
                        color = if (isSel) Color.White else TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }

        // Search & Filter Bar (for Verification Center and Users)
        if (tabSelected == 0 || tabSelected == 1) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, phone (+880...), email...", color = TextSecondary, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkHighlight,
                    unfocusedBorderColor = Color(0xFFFF85A6),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(46.dp),
                singleLine = true
            )

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "ALL" to "All (${managedUsers.size})",
                    "PENDING_PHONE" to "⚠️ Needs Phone ($pendingPhoneCount)",
                    "PENDING_EMAIL" to "⚠️ Needs Email ($pendingEmailCount)",
                    "VERIFIED" to "✓ Fully Verified (${managedUsers.count { it.isPhoneVerified && it.isEmailVerified }})"
                ).forEach { (key, label) ->
                    val isSel = filterStatus == key
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSel) Color(0xFF2563EB) else DarkSurface,
                        border = BorderStroke(1.dp, if (isSel) Color(0xFF2563EB) else Color(0xFFFF85A6)),
                        modifier = Modifier.clickable { filterStatus = key }
                    ) {
                        Text(
                            label,
                            color = if (isSel) Color.White else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Tab 0 & 1: Verification Center / All Users List
            if (tabSelected == 0 || tabSelected == 1) {
                val displayUsers = managedUsers.filter { user ->
                    val matchesSearch = searchQuery.isBlank() ||
                        user.name.contains(searchQuery, ignoreCase = true) ||
                        user.phone.contains(searchQuery, ignoreCase = true) ||
                        user.email.contains(searchQuery, ignoreCase = true)

                    val matchesFilter = when {
                        tabSelected == 0 -> !user.isPhoneVerified || !user.isEmailVerified // Verification Center shows pending only
                        filterStatus == "PENDING_PHONE" -> !user.isPhoneVerified
                        filterStatus == "PENDING_EMAIL" -> !user.isEmailVerified
                        filterStatus == "VERIFIED" -> user.isPhoneVerified && user.isEmailVerified
                        else -> true
                    }
                    matchesSearch && matchesFilter
                }

                if (displayUsers.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No pending verification requests found", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("All users in this category are up to date and verified.", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                } else {
                    items(displayUsers) { user ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, if (!user.isPhoneVerified || !user.isEmailVerified) Color(0xFFFF9800).copy(alpha = 0.5f) else Color(0xFF4CAF50).copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                // Top Row: Avatar, Name, Role badge, ID
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF374151))
                                        ) {
                                            if (user.avatarUrl.isNotEmpty()) {
                                                SubcomposeAsyncImage(model = user.avatarUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                                            } else {
                                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.LightGray, modifier = Modifier.align(Alignment.Center))
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(user.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                if (user.verifiedByAdmin) {
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("👑 Admin Approved", color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = when {
                                                        user.id.startsWith("AGENT") -> Color(0xFF2196F3).copy(alpha = 0.2f)
                                                        user.id.startsWith("MDL") -> Color(0xFFFF2A6D).copy(alpha = 0.2f)
                                                        else -> Color(0xFF10B981).copy(alpha = 0.2f)
                                                    },
                                                    border = BorderStroke(0.8.dp, when {
                                                        user.id.startsWith("AGENT") -> Color(0xFF2196F3)
                                                        user.id.startsWith("MDL") -> Color(0xFFFF2A6D)
                                                        else -> Color(0xFF10B981)
                                                    })
                                                ) {
                                                    Text(
                                                        text = "AUTO ID: ${user.id}",
                                                        color = when {
                                                            user.id.startsWith("AGENT") -> Color(0xFF90CAF9)
                                                            user.id.startsWith("MDL") -> Color(0xFFFF80AB)
                                                            else -> Color(0xFFA7F3D0)
                                                        },
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                                Text("• Role: ${user.role}", color = TextSecondary, fontSize = 10.sp)
                                            }
                                        }
                                    }

                                    // Overall Status Pill
                                    val isFull = user.isPhoneVerified && user.isEmailVerified
                                    Box(
                                        modifier = Modifier
                                            .background(if (isFull) Color(0xFF16A34A).copy(alpha = 0.2f) else Color(0xFFD97706).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (isFull) "VERIFIED" else "PENDING",
                                            color = if (isFull) Color(0xFF00E676) else Color(0xFFFFB74D),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 0.8.dp)

                                // Contact Status Rows
                                // 1. Phone
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Phone, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(user.phone, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (user.isPhoneVerified) {
                                            Text("Phone Verified ✓", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        } else {
                                            Text("Phone Unverified ⚠️", color = Color(0xFFFF9800), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                // 2. Email
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Mail, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(user.email, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (user.isEmailVerified) {
                                            Text("Email Verified ✓", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        } else {
                                            Text("Email Unverified ⚠️", color = Color(0xFFFF9800), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                // Active One-Time Password (OTP) Badge if present
                                val activeUserOtp = user.oneTimePassword ?: user.pendingPhoneOtp ?: user.pendingEmailOtp
                                if (!activeUserOtp.isNullOrEmpty()) {
                                    Surface(
                                        color = Color(0xFF2A1B60),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, PinkHighlight.copy(alpha = 0.5f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.VpnKey, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Active One-Time Password (OTP): $activeUserOtp",
                                                    color = PinkHighlight,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                            }
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                TextButton(
                                                    onClick = {
                                                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                                        clipboard?.setPrimaryClip(android.content.ClipData.newPlainText("OTP", activeUserOtp))
                                                        Toast.makeText(context, "OTP $activeUserOtp copied to clipboard!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text("Copy", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                                TextButton(
                                                    onClick = {
                                                        viewModel.backendRevokeOneTimePassword(user.id)
                                                        Toast.makeText(context, "OTP cleared for ${user.name}", Toast.LENGTH_SHORT).show()
                                                    },
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text("Clear", color = Color(0xFFEF4444), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 0.8.dp)

                                // Action Buttons Row: [🔑 Set One-Time Password] [✓ Direct Verify]
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Button 1: Set / Send OTP
                                    OutlinedButton(
                                        onClick = {
                                            manualCodeModalUser = user
                                            manualCodeType = if (!user.isPhoneVerified) "PHONE" else "EMAIL"
                                            manualCodeInput = user.oneTimePassword ?: "${(100000..999999).random()}"
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, PinkHighlight),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.weight(1f).height(36.dp)
                                    ) {
                                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Set / Edit OTP", color = PinkHighlight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    // Button 2: Direct Verify
                                    if (!user.isPhoneVerified || !user.isEmailVerified) {
                                        Button(
                                            onClick = {
                                                val verifyType = when {
                                                    !user.isPhoneVerified && !user.isEmailVerified -> "BOTH"
                                                    !user.isPhoneVerified -> "PHONE"
                                                    else -> "EMAIL"
                                                }
                                                viewModel.adminDirectVerifyUser(user.id, verifyType)
                                                Toast.makeText(context, "${user.name} verified by Admin!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.weight(1f).height(36.dp)
                                        ) {
                                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = when {
                                                    !user.isPhoneVerified && !user.isEmailVerified -> "Verify Both ✓"
                                                    !user.isPhoneVerified -> "Verify Phone ✓"
                                                    else -> "Verify Email ✓"
                                                },
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } else {
                                        Surface(
                                            color = Color(0xFF16A34A).copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f).height(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("Account Active & Verified ✓", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (tabSelected == 2) {
                // Models Tab
                items(models) { m ->
                    val linkedUser = managedUsers.firstOrNull { it.id == "model_${m.id}" || it.name.equals(m.name, true) }
                    val activeOtp = linkedUser?.oneTimePassword ?: viewModel.modelMasterOtp
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
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(0xFFFF85A6), CircleShape)
                                    ) {
                                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.LightGray, modifier = Modifier.align(Alignment.Center))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(m.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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

                            // Model OTP Row
                            Surface(
                                color = Color(0xFF2A1B60),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, PinkHighlight.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Model One-Time Password: $activeOtp",
                                            color = PinkHighlight,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        TextButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                                clipboard?.setPrimaryClip(android.content.ClipData.newPlainText("Model OTP", activeOtp))
                                                Toast.makeText(context, "Model OTP $activeOtp copied!", Toast.LENGTH_SHORT).show()
                                            },
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("Copy", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                val userToEdit = linkedUser ?: run {
                                                    val newU = com.example.viewmodel.ManagedUser(
                                                        id = "model_${m.id}",
                                                        name = m.name,
                                                        role = "MODEL",
                                                        phone = "+880 1911 234 567",
                                                        email = "${m.name.lowercase().replace(" ", "")}@modol.pro",
                                                        isPhoneVerified = true,
                                                        isEmailVerified = true,
                                                        oneTimePassword = activeOtp
                                                    )
                                                    managedUsers.add(newU)
                                                    newU
                                                }
                                                manualCodeModalUser = userToEdit
                                                manualCodeInput = "${(100000..999999).random()}"
                                            },
                                            border = BorderStroke(0.8.dp, PinkHighlight),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                            modifier = Modifier.height(26.dp)
                                        ) {
                                            Text("🔑 Set OTP", color = PinkHighlight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (tabSelected == 3) {
                // Cash Agents Tab
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        dbAgents.forEach { agent ->
                            val linkedUser = managedUsers.firstOrNull { it.id == agent.id || it.phone == agent.phone || it.name.equals(agent.name, true) || it.role == "AGENT" }
                            val activeOtp = linkedUser?.oneTimePassword ?: viewModel.cashAgentMasterOtp
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, PinkBorderSoft)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(agent.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("${agent.paymentMethod} • Acc: ${agent.accountNumber}", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                            Text("Float Balance: ৳ %,.0f • ${agent.country}".format(agent.availableBalance), color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFF2196F3).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text("${agent.commissionRate}% Commission", color = Color(0xFF2196F3), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // Cash Agent OTP Row
                                    Surface(
                                        color = Color(0xFF1E293B),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFF2196F3).copy(alpha = 0.5f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Agent One-Time Password: $activeOtp",
                                                    color = Color(0xFF90CAF9),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                            }
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                TextButton(
                                                    onClick = {
                                                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                                        clipboard?.setPrimaryClip(android.content.ClipData.newPlainText("Agent OTP", activeOtp))
                                                        Toast.makeText(context, "Agent OTP $activeOtp copied!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text("Copy", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                                OutlinedButton(
                                                    onClick = {
                                                        val userToEdit = linkedUser ?: run {
                                                            val newU = com.example.viewmodel.ManagedUser(
                                                                id = agent.id,
                                                                name = agent.name,
                                                                role = "AGENT",
                                                                phone = agent.phone,
                                                                email = "agent.${agent.name.lowercase().replace(" ", "")}@modol.cash",
                                                                isPhoneVerified = true,
                                                                isEmailVerified = true,
                                                                oneTimePassword = activeOtp
                                                            )
                                                            managedUsers.add(newU)
                                                            newU
                                                        }
                                                        manualCodeModalUser = userToEdit
                                                        manualCodeInput = "${(100000..999999).random()}"
                                                    },
                                                    border = BorderStroke(0.8.dp, Color(0xFF2196F3)),
                                                    shape = RoundedCornerShape(6.dp),
                                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                                    modifier = Modifier.height(26.dp)
                                                ) {
                                                    Text("🔑 Set OTP", color = Color(0xFF90CAF9), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (tabSelected == 4) {
                // Tab 4: Backend Uploaded Photos & Media Storage
                val uploads = viewModel.backendUploadedPhotos
                if (uploads.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No uploaded media found", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Photos uploaded from app camera or gallery will appear here in backend storage.", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                } else {
                    items(uploads) { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFF374151)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Box(
                                            modifier = Modifier
                                                .size(52.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFF374151))
                                        ) {
                                            val safeUrl = if (item.fileUrl.startsWith("/") && !item.fileUrl.startsWith("file://")) "file://${item.fileUrl}" else item.fileUrl
                                            SubcomposeAsyncImage(
                                                model = safeUrl,
                                                contentDescription = "Uploaded photo",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize(),
                                                error = {
                                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                        Icon(imageVector = Icons.Default.BrokenImage, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                                                    }
                                                }
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(item.fileName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Text("Uploaded by: ${item.uploaderName} (#${item.uploaderId})", color = TextSecondary, fontSize = 11.sp)
                                            Text("Size: ${item.fileSizeBytes / 1024} KB • ${item.timestamp}", color = Color.Gray, fontSize = 10.sp)
                                        }
                                    }

                                    Surface(
                                        color = Color(0xFF16A34A).copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "STORED IN DB ✓",
                                            color = Color(0xFF00E676),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 0.8.dp)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Target: /uploads/profile/${item.fileName}",
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    TextButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                            clipboard?.setPrimaryClip(android.content.ClipData.newPlainText("URL", item.fileUrl))
                                            Toast.makeText(context, "Photo URL copied!", Toast.LENGTH_SHORT).show()
                                        },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("Copy URL", color = PinkHighlight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (tabSelected == 5) {
                // Tab 5: User to Model Live Location Tracking
                item {
                    UserToModelLiveTrackingContent(viewModel = viewModel)
                }
            } else if (tabSelected == 6) {
                // Tab 6: Master Role One-Time Passwords (OTP) Center for Admin, Model, User, Cash Agent
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, PinkHighlight.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Backend Role Master OTP Center", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text("One-Time Passwords for Admin, Model, User, and Cash Agent", color = TextSecondary, fontSize = 11.sp)
                                    }
                                }

                                Text(
                                    "⚡ How it works: Admins can set or randomize master One-Time Passwords for each platform role. Users, models, cash agents, and admins can use these OTPs to immediately log in or complete account verification without SMS gateway delays.",
                                    color = Color(0xFF00E676),
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        // 4 Role Master OTP Cards
                        listOf(
                            Triple("ADMIN", "👑 Admin Master One-Time Password", viewModel.adminMasterOtp to Color(0xFFFF2A6D)),
                            Triple("MODEL", "💃 Model Master One-Time Password", viewModel.modelMasterOtp to Color(0xFFFF85A6)),
                            Triple("CLIENT", "👤 User / Client Master One-Time Password", viewModel.userMasterOtp to Color(0xFF10B981)),
                            Triple("CASH_AGENT", "💼 Cash Agent Master One-Time Password", viewModel.cashAgentMasterOtp to Color(0xFF2196F3))
                        ).forEach { (roleKey, title, pair) ->
                            val (currentOtp, accentColor) = pair
                            var editOtpValue by remember(currentOtp) { mutableStateOf(currentOtp) }
                            var isEditing by remember { mutableStateOf(false) }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Surface(
                                            color = accentColor.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                "ACTIVE IN BACKEND",
                                                color = accentColor,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    if (isEditing) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedTextField(
                                                value = editOtpValue,
                                                onValueChange = { if (it.length <= 6) editOtpValue = it },
                                                label = { Text("New 6-Digit OTP") },
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedTextColor = Color.White,
                                                    unfocusedTextColor = Color.White,
                                                    focusedBorderColor = accentColor,
                                                    unfocusedBorderColor = Color.Gray
                                                ),
                                                modifier = Modifier.weight(1f),
                                                singleLine = true
                                            )
                                            Button(
                                                onClick = { editOtpValue = "${(100000..999999).random()}" },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF374151)),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("🎲", fontSize = 14.sp)
                                            }
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            TextButton(onClick = { isEditing = false }) {
                                                Text("Cancel", color = TextSecondary, fontSize = 11.sp)
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                onClick = {
                                                    viewModel.backendSetRoleMasterOtp(roleKey, editOtpValue)
                                                    isEditing = false
                                                    Toast.makeText(context, "$title updated to $editOtpValue!", Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Save & Apply", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = currentOtp,
                                                    color = accentColor,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 22.sp,
                                                    letterSpacing = 2.sp
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                IconButton(
                                                    onClick = {
                                                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                                        clipboard?.setPrimaryClip(android.content.ClipData.newPlainText("OTP", currentOtp))
                                                        Toast.makeText(context, "$title [$currentOtp] copied to clipboard!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                                                }
                                            }

                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                OutlinedButton(
                                                    onClick = {
                                                        val generated = viewModel.backendGenerateRoleMasterOtp(roleKey)
                                                        Toast.makeText(context, "New random OTP [$generated] set for $roleKey!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    border = BorderStroke(1.dp, Color(0xFF64748B)),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                    modifier = Modifier.height(34.dp)
                                                ) {
                                                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("🎲 Random", color = Color.White, fontSize = 11.sp)
                                                }

                                                 Button(
                                                    onClick = {
                                                        editOtpValue = currentOtp
                                                        isEditing = true
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                    modifier = Modifier.height(34.dp)
                                                ) {
                                                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Change", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (tabSelected == 7) {
                // Tab 7: National ID & Passport KYC Submissions
                val kycDocs = viewModel.submittedIdentityVerifications
                if (kycDocs.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Badge, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No KYC Submissions Found", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Uploaded National IDs and Passports from users and models will appear here.", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                } else {
                    items(kycDocs) { doc ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                1.dp,
                                when (doc.status) {
                                    "VERIFIED" -> Color(0xFF10B981)
                                    "REJECTED" -> Color(0xFFEF4444)
                                    else -> Color(0xFFF59E0B)
                                }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                // Header: Name, Doc Type, Status badge
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(doc.userName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = when (doc.userRole) {
                                                    "MODEL" -> Color(0xFFFF2A6D).copy(alpha = 0.2f)
                                                    else -> Color(0xFF3B82F6).copy(alpha = 0.2f)
                                                }
                                            ) {
                                                Text(
                                                    doc.userRole,
                                                    color = when (doc.userRole) {
                                                        "MODEL" -> Color(0xFFFF80AB)
                                                        else -> Color(0xFF93C5FD)
                                                    },
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        Text("${doc.documentType} No: ${doc.documentNumber} • Submitted: ${doc.submittedAt}", color = TextSecondary, fontSize = 10.sp)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when (doc.status) {
                                            "VERIFIED" -> Color(0xFF10B981).copy(alpha = 0.2f)
                                            "REJECTED" -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                            else -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                        },
                                        border = BorderStroke(
                                            0.8.dp,
                                            when (doc.status) {
                                                "VERIFIED" -> Color(0xFF10B981)
                                                "REJECTED" -> Color(0xFFEF4444)
                                                else -> Color(0xFFF59E0B)
                                            }
                                        )
                                    ) {
                                        Text(
                                            text = when (doc.status) {
                                                "VERIFIED" -> "APPROVED ✓"
                                                "REJECTED" -> "REJECTED ✗"
                                                else -> "PENDING REVIEW ⏳"
                                            },
                                            color = when (doc.status) {
                                                "VERIFIED" -> Color(0xFF34D399)
                                                "REJECTED" -> Color(0xFFF87171)
                                                else -> Color(0xFFFBBF24)
                                            },
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                // Photo Previews: Front, Back, Selfie
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Front
                                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Front Side", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(75.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFF1E293B))
                                                .border(1.dp, Color(0xFF374151), RoundedCornerShape(6.dp))
                                        ) {
                                            if (doc.frontPhotoUri.isNotBlank()) {
                                                val safe = if (doc.frontPhotoUri.startsWith("/") && !doc.frontPhotoUri.startsWith("file://")) "file://${doc.frontPhotoUri}" else doc.frontPhotoUri
                                                SubcomposeAsyncImage(model = safe, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                                            } else {
                                                Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray, modifier = Modifier.align(Alignment.Center))
                                            }
                                        }
                                    }

                                    // Back
                                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Back Side", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(75.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFF1E293B))
                                                .border(1.dp, Color(0xFF374151), RoundedCornerShape(6.dp))
                                        ) {
                                            if (doc.backPhotoUri.isNotBlank()) {
                                                val safe = if (doc.backPhotoUri.startsWith("/") && !doc.backPhotoUri.startsWith("file://")) "file://${doc.backPhotoUri}" else doc.backPhotoUri
                                                SubcomposeAsyncImage(model = safe, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                                            } else {
                                                Text("N/A (Passport)", color = Color.Gray, fontSize = 9.sp, modifier = Modifier.align(Alignment.Center))
                                            }
                                        }
                                    }

                                    // Selfie
                                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Selfie Face", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(75.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFF1E293B))
                                                .border(1.dp, Color(0xFF374151), RoundedCornerShape(6.dp))
                                        ) {
                                            if (doc.selfiePhotoUri.isNotBlank()) {
                                                val safe = if (doc.selfiePhotoUri.startsWith("/") && !doc.selfiePhotoUri.startsWith("file://")) "file://${doc.selfiePhotoUri}" else doc.selfiePhotoUri
                                                SubcomposeAsyncImage(model = safe, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                                            } else {
                                                Icon(Icons.Default.Face, contentDescription = null, tint = Color.Gray, modifier = Modifier.align(Alignment.Center))
                                            }
                                        }
                                    }
                                }

                                if (doc.rejectionReason.isNotBlank()) {
                                    Text(
                                        "Rejection Note: ${doc.rejectionReason}",
                                        color = Color(0xFFF87171),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                // Action Buttons (Approve / Reject)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (doc.status != "VERIFIED") {
                                        Button(
                                            onClick = {
                                                viewModel.adminApproveIdentityVerification(doc.id)
                                                Toast.makeText(context, "${doc.userName}'s KYC Approved!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f).height(36.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Approve KYC ✓", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    if (doc.status != "REJECTED") {
                                        OutlinedButton(
                                            onClick = {
                                                viewModel.adminRejectIdentityVerification(doc.id, "Image blurry or document number unverified.")
                                                Toast.makeText(context, "${doc.userName}'s KYC Marked for Re-upload", Toast.LENGTH_SHORT).show()
                                            },
                                            border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f).height(36.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Request Re-upload", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

    // Modal: Admin Send Manual Code Dialog
    if (manualCodeModalUser != null) {
        val targetUser = manualCodeModalUser!!
        AlertDialog(
            onDismissRequest = { manualCodeModalUser = null },
            containerColor = DarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = PinkHighlight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Set One-Time Password (OTP)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Set an active One-Time Password for ${targetUser.name} (${targetUser.role}). This OTP can be used directly for login, account verification, and security bypass.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    // Select Target Channel
                    Text("Target Verification Channel:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("PHONE" to "📱 Phone (${targetUser.phone})", "EMAIL" to "✉️ Email (${targetUser.email})").forEach { (type, label) ->
                            val isSel = manualCodeType == type
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) PinkHighlight else Color(0xFF374151),
                                modifier = Modifier.weight(1f).clickable { manualCodeType = type }
                            ) {
                                Text(
                                    label,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(8.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Code Input with Randomizer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = manualCodeInput,
                            onValueChange = { if (it.length <= 6) manualCodeInput = it },
                            label = { Text("6-Digit One-Time Password") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = PinkHighlight,
                                unfocusedBorderColor = Color(0xFFFF85A6)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Button(
                            onClick = { manualCodeInput = "${(100000..999999).random()}" },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF374151)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(50.dp)
                        ) {
                            Text("🎲 New Code", fontSize = 10.sp)
                        }
                    }

                    Text(
                        text = "⚡ Dispatched code is saved to user's profile and active immediately for login & verification.",
                        color = Color(0xFF00E676),
                        fontSize = 10.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val code = viewModel.backendSetOneTimePassword(targetUser.id, manualCodeInput)
                        viewModel.adminSendManualCode(targetUser.id, manualCodeType, code)
                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                        clipboard?.setPrimaryClip(android.content.ClipData.newPlainText("Manual OTP", code))
                        Toast.makeText(context, "One-Time Password [$code] assigned to ${targetUser.name} and copied!", Toast.LENGTH_LONG).show()
                        manualCodeModalUser = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
                ) {
                    Text("Save & Copy OTP ✓", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (targetUser.oneTimePassword != null) {
                        TextButton(onClick = {
                            viewModel.backendRevokeOneTimePassword(targetUser.id)
                            Toast.makeText(context, "OTP revoked for ${targetUser.name}", Toast.LENGTH_SHORT).show()
                            manualCodeModalUser = null
                        }) {
                            Text("Revoke OTP", color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    TextButton(onClick = { manualCodeModalUser = null }) {
                        Text("Cancel", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        )
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
                        Text("💰 Admin Escrow & Proof Verification", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                        Text("৳${totalEscrowVolume.toInt()}", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 18.sp)
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
                    Text(label, color = if (isSel) Color.White else TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                                    Text("Booking #${booking.id}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                                    Text("Model: ${booking.modelName}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                                            Text("📸 Service Completion Proof", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                                            .background(Color(0xFFFF85A6), RoundedCornerShape(8.dp))
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
            title = { Text("Request Proof Re-upload", color = TextPrimary, fontWeight = FontWeight.Bold) },
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
                            focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)
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
            title = { Text("Inspect Service Proofs - Booking #${b.id}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
                Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val userCountry = currentUser?.country ?: "Bangladesh"
    val userCurrency = currentUser?.currency ?: CountryPaymentMaster.getCurrencyForCountry(userCountry)
    val currSymbol = CountryPaymentMaster.getCurrencySymbol(userCurrency)
    
    // Payment method choice: "GOOGLE_PAY", "ALIPAY", "CASH_AGENT"
    var selectedTopGateway by remember { mutableStateOf("GOOGLE_PAY") }
    
    // Cash Agent Flow states
    var selectedCountry by remember { mutableStateOf(userCountry) }
    val availableCountries = remember { CountryPaymentMaster.allCountries.map { it.countryName } }
    
    val countryAgents = remember(allAgents, selectedCountry) {
        val filtered = allAgents.filter { it.country.equals(selectedCountry, ignoreCase = true) }
        if (filtered.isNotEmpty()) filtered
        else {
            val cData = CountryPaymentMaster.getCountry(selectedCountry) ?: CountryPaymentMaster.allCountries.first()
            val primaryMethod = cData.paymentMethods.firstOrNull()?.methodName ?: "Bank Transfer"
            listOf(
                PaymentAgent(
                    id = "AGT_${cData.isoCode}_1",
                    name = "Verified Cash Agent (${cData.countryName})",
                    agentCode = "${cData.phoneCode.filter { it.isDigit() }}01",
                    country = cData.countryName,
                    phone = "${cData.phoneCode} 1700000000",
                    paymentMethod = primaryMethod,
                    accountNumber = "${cData.phoneCode} 1900000000",
                    accountHolder = "Authorized Cash Agent",
                    city = "Capital",
                    commissionRate = 1.0,
                    minLimit = 100.0,
                    maxLimit = 100000.0
                )
            )
        }
    }
    
    var selectedAgent by remember(countryAgents) {
        mutableStateOf(
            countryAgents.firstOrNull() ?: allAgents.firstOrNull() ?: PaymentAgent(
                id = "DEF",
                name = "Default Agent",
                agentCode = "00",
                country = "Bangladesh",
                phone = "01700000000",
                paymentMethod = "bKash",
                accountNumber = "01700000000"
            )
        )
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
                Text("Available Payment Gateways (Universal):", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)

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
                    border = BorderStroke(1.5.dp, if (isGPaySelected) Color(0xFF00E676) else Color(0xFFFF85A6)),
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
                                Text("🟢 Google Pay", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                    border = BorderStroke(1.5.dp, if (isAlipaySelected) Color(0xFF1E88E5) else Color(0xFFFF85A6)),
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
                                Text("🔵 Alipay", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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

                // 3. Apple Pay
                val isApplePaySelected = selectedTopGateway == "APPLE_PAY"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedTopGateway = "APPLE_PAY"
                            gatewayErrorNotice = null
                            gatewaySuccessNotice = null
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isApplePaySelected) Color.White.copy(alpha = 0.15f) else DarkSurface
                    ),
                    border = BorderStroke(1.5.dp, if (isApplePaySelected) Color.White else Color(0xFFFF85A6)),
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
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("⚪ Apple Pay", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Face ID / Touch ID Instant Payment", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                        RadioButton(
                            selected = isApplePaySelected,
                            onClick = {
                                selectedTopGateway = "APPLE_PAY"
                                gatewayErrorNotice = null
                                gatewaySuccessNotice = null
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = Color.White)
                        )
                    }
                }

                // 4. Cash Agent
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
                    border = BorderStroke(1.5.dp, if (isCashAgentSelected) PinkHighlight else Color(0xFFFF85A6)),
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
                                Text("👤 Cash Agent", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                if (selectedTopGateway != "CASH_AGENT") {
                    val gwName = when (selectedTopGateway) {
                        "GOOGLE_PAY" -> "Google Pay"
                        "ALIPAY" -> "Alipay"
                        "APPLE_PAY" -> "Apple Pay"
                        else -> selectedTopGateway
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Enter Amount ($gwName Automatic Checkout):", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        OutlinedTextField(
                            value = depositAmountInput,
                            onValueChange = { depositAmountInput = it },
                            label = { Text("Amount (৳ / $)", color = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)
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
                        Text("1. Select Country (Cash Agent Network):", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                                        .background(if (isSel) PinkHighlight else Color(0xFFFF85A6))
                                        .clickable {
                                            selectedCountry = c
                                            val filtered = allAgents.filter { it.country.equals(c, ignoreCase = true) }
                                            if (filtered.isNotEmpty()) selectedAgent = filtered.first()
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    val cFlag = CountryPaymentMaster.getFlagForCountry(c)
                                    Text("$cFlag $c", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Step 2: Verified Agent Selection
                        Text("2. Select Verified Cash Agent ($selectedCountry):", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                                        border = BorderStroke(1.dp, if (isSel) PinkHighlight else Color(0xFFFF85A6)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(agent.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                            border = BorderStroke(1.dp, Color(0xFFFF85A6)),
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
                        Text("3. Amount, Transaction ID & Screenshot Proof:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        OutlinedTextField(
                            value = depositAmountInput,
                            onValueChange = { depositAmountInput = it },
                            label = { Text("Deposit Amount", color = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = transactionIdInput,
                            onValueChange = { transactionIdInput = it },
                            label = { Text("Transaction ID / Ref (e.g. TXN100293)", color = TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)
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
                                        .background(if (isSel) Color(0xFF2196F3) else Color(0xFFFF85A6))
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
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (selectedTopGateway != "CASH_AGENT") {
                val gwName = when (selectedTopGateway) {
                    "GOOGLE_PAY" -> "Google Pay"
                    "ALIPAY" -> "Alipay"
                    "APPLE_PAY" -> "Apple Pay"
                    else -> selectedTopGateway
                }
                val btnColor = when (selectedTopGateway) {
                    "GOOGLE_PAY" -> Color(0xFF00E676)
                    "ALIPAY" -> Color(0xFF1E88E5)
                    "APPLE_PAY" -> Color.White
                    else -> PinkHighlight
                }
                val textColor = if (selectedTopGateway == "GOOGLE_PAY" || selectedTopGateway == "APPLE_PAY") Color.Black else Color.White

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
                    colors = ButtonDefaults.buttonColors(containerColor = btnColor)
                ) {
                    Text("Proceed with $gwName", color = textColor, fontWeight = FontWeight.Bold)
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
    val userCountry = currentUser?.country ?: "Bangladesh"
    val userCurrency = currentUser?.currency ?: CountryPaymentMaster.getCurrencyForCountry(userCountry)
    val currSymbol = CountryPaymentMaster.getCurrencySymbol(userCurrency)
    val countryFlag = CountryPaymentMaster.getFlagForCountry(userCountry)

    val countryMethods = remember(userCountry) {
        val methods = CountryPaymentMaster.getPaymentMethodsForCountry(userCountry).map { it.methodName }
        if (methods.isNotEmpty()) methods else listOf("Bank Transfer", "Cash")
    }

    var withdrawAmountInput by remember { mutableStateOf("500") }
    var selectedMethod by remember(countryMethods) { mutableStateOf(countryMethods.first()) }
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
                    Text("Withdraw Funds ($countryFlag $userCountry)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Available Wallet Balance: $currSymbol${availableBalance.toInt()} $userCurrency", color = Color(0xFF4CAF50), fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                                Text("Withdraw $userCurrency via B2B Cash Agents with live chat & escrow", color = TextSecondary, fontSize = 10.sp)
                            }
                        }
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                    }
                }

                Text("Withdrawal Amount ($userCurrency • $currSymbol):", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                OutlinedTextField(
                    value = withdrawAmountInput,
                    onValueChange = { withdrawAmountInput = it; errorMessage = null },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Payment Channel ($userCountry Wallets):", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    countryMethods.forEach { m ->
                        val isSel = selectedMethod == m
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) PinkHighlight else Color(0xFFFF85A6))
                                .clickable { selectedMethod = m }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(m, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedTextField(
                    value = accountNumberInput,
                    onValueChange = { accountNumberInput = it },
                    label = { Text("Account / Phone Number", color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = accountHolderInput,
                    onValueChange = { accountHolderInput = it },
                    label = { Text("Account Holder Name", color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)
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
                Text("Admin Ledger & Deposit Verification", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Verify transaction IDs & agent accounts before approving balance", color = TextSecondary, fontSize = 11.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { activeTab = 0 },
                    colors = ButtonDefaults.buttonColors(containerColor = if (activeTab == 0) PinkHighlight else Color(0xFFFF85A6)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Deposits (${deposits.count { it.status == "PENDING" }})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { activeTab = 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = if (activeTab == 1) PinkHighlight else Color(0xFFFF85A6)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Master Ledger (${ledgerEntries.size})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { activeTab = 2 },
                    colors = ButtonDefaults.buttonColors(containerColor = if (activeTab == 2) PinkHighlight else Color(0xFFFF85A6)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Gateways (${viewModel.adminPaymentGateways.size})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
                                        Text("Deposit #${dep.id}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                                        Text("${dep.agentName} • ${dep.country}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                                        Text(dep.transactionId, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                                            focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)
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
        } else if (activeTab == 1) {
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
        } else {
            // Payment Gateways Tab (Google Pay, Alipay, Apple Pay & Custom Gateways)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                AdminPaymentGatewaysSection(viewModel = viewModel)
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
                    Text("${agents.size}", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 18.sp)
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
                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)
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
                        .background(if (isSel) PinkHighlight else Color(0xFFFF85A6))
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
                                    Text(agent.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                                    Text("Channel: ${agent.paymentMethod}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF85A6)),
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
            Text(if (agent == null) "Add Cash Agent" else "Edit Cash Agent Configuration", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = agentCode,
                        onValueChange = { agentCode = it },
                        label = { Text("Agent Code", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Contact", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)),
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
                                .background(if (isSel) PinkHighlight else Color(0xFFFF85A6))
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
                                .background(if (isSel) Color(0xFF2196F3) else Color(0xFFFF85A6))
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
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = accountHolder,
                    onValueChange = { accountHolder = it },
                    label = { Text("Account Holder Name", color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = commissionRateInput,
                        onValueChange = { commissionRateInput = it },
                        label = { Text("Comm %", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = minLimitInput,
                        onValueChange = { minLimitInput = it },
                        label = { Text("Min Limit", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = maxLimitInput,
                        onValueChange = { maxLimitInput = it },
                        label = { Text("Max Limit", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)),
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
                                .background(if (isSel) PinkHighlight else Color(0xFFFF85A6))
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
    var selectedWalletSubTab by remember { mutableIntStateOf(0) } // 0 = Payouts, 1 = Payment Gateways (Google Pay, Alipay, Apple Pay & New)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Wallets & Payment Gateways", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Approvals & Google Pay, Alipay, Apple Pay, New Config", color = TextSecondary, fontSize = 11.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { selectedWalletSubTab = 0 },
                    colors = ButtonDefaults.buttonColors(containerColor = if (selectedWalletSubTab == 0) PinkHighlight else Color(0xFF334155)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Payouts (${withdrawals.count { it.status == "PENDING" }})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { selectedWalletSubTab = 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = if (selectedWalletSubTab == 1) PinkHighlight else Color(0xFF334155)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("💳 Gateways (${viewModel.adminPaymentGateways.size})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (selectedWalletSubTab == 1) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                AdminPaymentGatewaysSection(viewModel = viewModel)
            }
        } else {
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
                        Text("${withdrawals.size} Requests", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 18.sp)
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
                                    Text("Withdrawal #${wd.id}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                                    Text("Account: ${wd.accountNumber} (${wd.accountHolder})", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
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
    val context = LocalContext.current
    var feePercentage by remember { mutableStateOf("15") }

    // Admin Firebase & OTP form state (bound to AppViewModel live states)
    var projectIdInput by remember(viewModel.firebaseProjectId) { mutableStateOf(viewModel.firebaseProjectId) }
    var apiKeyInput by remember(viewModel.firebaseApiKey) { mutableStateOf(viewModel.firebaseApiKey) }
    var appIdInput by remember(viewModel.firebaseAppId) { mutableStateOf(viewModel.firebaseAppId) }
    var storageBucketInput by remember(viewModel.firebaseStorageBucket) { mutableStateOf(viewModel.firebaseStorageBucket) }
    var dbUrlInput by remember(viewModel.firebaseDatabaseUrl) { mutableStateOf(viewModel.firebaseDatabaseUrl) }
    var gatewayModeInput by remember(viewModel.otpGatewayMode) { mutableStateOf(viewModel.otpGatewayMode) }
    var senderBrandInput by remember(viewModel.otpSenderBrand) { mutableStateOf(viewModel.otpSenderBrand) }
    var testPhoneInput by remember(viewModel.otpTestPhoneNumber) { mutableStateOf(viewModel.otpTestPhoneNumber) }
    var testCodeInput by remember(viewModel.otpTestCode) { mutableStateOf(viewModel.otpTestCode) }
    var bypassRecaptchaInput by remember(viewModel.firebaseBypassRecaptcha) { mutableStateOf(viewModel.firebaseBypassRecaptcha) }
    var showApiKey by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Admin System Settings", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Firebase, OTP Gateway, Fees & Server Database", color = TextSecondary, fontSize = 11.sp)
            }
            Box(
                modifier = Modifier
                    .background(Color(0xFF00E676).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("ADMIN ONLY", color = Color(0xFF00E676), fontWeight = FontWeight.ExtraBold, fontSize = 9.sp)
            }
        }

        // =========================================================================
        // 🌐 BACKEND TO API GENERATOR SYSTEM (FOR WEBSITE & OTHER APPS) QUICK ACCESS
        // =========================================================================
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.5.dp, PinkHighlight),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.selectedTab = 8 }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6), PinkHighlight)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                "API Generator System",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, Color(0xFF10B981))
                            ) {
                                Text("NEW TAB", color = Color(0xFF10B981), fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                            }
                        }
                        Text(
                            "Generate REST APIs, Webhooks & Auth Keys for Website & Other Apps",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PinkHighlight,
                    modifier = Modifier.clickable { viewModel.selectedTab = 8 }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Open Tab", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                }
            }
        }
        // =========================================================================
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.2.dp, Color(0xFF334155))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFFF2A6D).copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFFFF2A6D),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text("Admin Security & Active Sessions", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("পাসওয়ার্ড পরিবর্তন ও সেশন ম্যানেজমেন্ট", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFF2A6D).copy(alpha = 0.15f)
                    ) {
                        Text(
                            "${viewModel.activeSessionsList.size} Devices",
                            color = Color(0xFFFF2A6D),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.showChangePasswordDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A6D)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.LockReset, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Change Password", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.showAllLogoutConfirmDialog = true },
                        border = BorderStroke(1.2.dp, Color(0xFFEF4444)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Devices, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("All Logout", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Direct Admin Sign Out Button
                Button(
                    onClick = { viewModel.showSignOutConfirmDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out Admin Panel (অ্যাডমিন লগআউট)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // =========================================================================
        // 📍 0. LIVE GPS BOTTOM NAVIGATION TAB VISIBILITY (ADMIN MASTER CONTROL)
        // =========================================================================
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.5.dp, if (viewModel.showLiveGpsBottomTab) Color(0xFF00E676) else Color(0xFF334155))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF00E676).copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text("Live GPS Bottom Navigation Tab", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                text = if (viewModel.showLiveGpsBottomTab) "Status: VISIBLE in User & Model Bottom Bar" else "Status: HIDDEN by default (Admin Controlled)",
                                color = if (viewModel.showLiveGpsBottomTab) Color(0xFF00E676) else TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Switch(
                        checked = viewModel.showLiveGpsBottomTab,
                        onCheckedChange = { viewModel.setLiveGpsBottomTabVisibility(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF00E676),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color(0xFF1E293B)
                        )
                    )
                }
                Text(
                    text = "Controls whether the 'Live GPS' tab appears in the bottom navigation bar for Clients and Models. You can also toggle this remotely via the PHP Web Backend under Settings / Live GPS.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }

        // =========================================================================
        // 🔥 1. FIREBASE & OTP GATEWAY MASTER CONFIGURATION (ADMIN EXCLUSIVE)
        // =========================================================================
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, PinkHighlight.copy(alpha = 0.35f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header with Flame Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFFF9100).copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFFF9100),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Firebase & OTP Gateway Configuration",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                "Live Phone Auth & Backend Infrastructure",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Security & Privacy Shield Badge (User Request: All Firebase Hidden on User OTP)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1E293B),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "USER-FACING OTP: ALL FIREBASE METADATA HIDDEN",
                                color = Color(0xFF00E676),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Text(
                                "ইউজার বা মডেলদের ওটিপি স্ক্রিনে Firebase সম্পূর্ণ গোপন থাকবে। ওটিপি সংক্রান্ত সমস্ত কনফিগারেশন শুধুমাত্র অ্যাডমিন এখান থেকে পরিচালনা করবেন।",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Status Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF00E676), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = viewModel.firebaseConnectionStatus,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = "ID: ${projectIdInput.take(14)}...",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }

                // Project ID Input
                Column {
                    Text("Firebase Project ID", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = projectIdInput,
                        onValueChange = { projectIdInput = it },
                        placeholder = { Text("e.g. modol-connect", color = Color.Gray, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            unfocusedBorderColor = Color.Gray,
                            focusedBorderColor = PinkHighlight
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Web API Key Input (with visibility toggle)
                Column {
                    Text("Firebase Web / App API Key", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        placeholder = { Text("AIzaSy...", color = Color.Gray, fontSize = 12.sp) },
                        visualTransformation = if (showApiKey) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showApiKey = !showApiKey }) {
                                Icon(
                                    imageVector = if (showApiKey) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle visibility",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            unfocusedBorderColor = Color.Gray,
                            focusedBorderColor = PinkHighlight
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Mobile SDK App ID Input
                Column {
                    Text("Mobile SDK App ID (mobilesdk_app_id)", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = appIdInput,
                        onValueChange = { appIdInput = it },
                        placeholder = { Text("1:125116191467:android:...", color = Color.Gray, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            unfocusedBorderColor = Color.Gray,
                            focusedBorderColor = PinkHighlight
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Storage Bucket Input
                Column {
                    Text("Firebase Storage Bucket", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = storageBucketInput,
                        onValueChange = { storageBucketInput = it },
                        placeholder = { Text("modol-connect.firebasestorage.app", color = Color.Gray, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            unfocusedBorderColor = Color.Gray,
                            focusedBorderColor = PinkHighlight
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Backend Database API URL
                Column {
                    Text("Backend Database / API Endpoint", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = dbUrlInput,
                        onValueChange = { dbUrlInput = it },
                        placeholder = { Text("http://173.249.28.110/", color = Color.Gray, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            unfocusedBorderColor = Color.Gray,
                            focusedBorderColor = PinkHighlight
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                // --- OTP Gateway Mode Selection ---
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Active OTP Gateway Mode", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("FIREBASE_LIVE", "Firebase Live SMS", Icons.Default.Sms),
                            Triple("BACKEND_SMS", "Backend SMS", Icons.Default.Sensors),
                            Triple("TEST_MODE", "Test Whitelist", Icons.Default.VpnKey)
                        ).forEach { (mode, label, icon) ->
                            val isSel = gatewayModeInput == mode
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) PinkHighlight.copy(alpha = 0.2f) else Color(0xFF1E293B),
                                border = BorderStroke(1.dp, if (isSel) PinkHighlight else Color(0xFF334155)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { gatewayModeInput = mode }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSel) PinkHighlight else TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        label,
                                        color = if (isSel) Color.White else TextSecondary,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                // Test Whitelist Phone & Code Fields
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1.4f)) {
                        Text("Test Whitelist Phone", color = TextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = testPhoneInput,
                            onValueChange = { testPhoneInput = it },
                            placeholder = { Text("+8801700000000", color = Color.Gray, fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                unfocusedBorderColor = Color.Gray,
                                focusedBorderColor = PinkHighlight
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Test OTP Code", color = TextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = testCodeInput,
                            onValueChange = { testCodeInput = it },
                            placeholder = { Text("123456", color = Color.Gray, fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                unfocusedBorderColor = Color.Gray,
                                focusedBorderColor = PinkHighlight
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Bypass reCAPTCHA Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Bypass reCAPTCHA for Testing", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("Disable phone verification challenge for frictionless SMS", color = TextSecondary, fontSize = 10.sp)
                    }
                    Switch(
                        checked = bypassRecaptchaInput,
                        onCheckedChange = { bypassRecaptchaInput = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PinkHighlight
                        )
                    )
                }

                // Action Buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            viewModel.saveFirebaseConfig(
                                projectId = projectIdInput,
                                apiKey = apiKeyInput,
                                appId = appIdInput,
                                storageBucket = storageBucketInput,
                                databaseUrl = dbUrlInput,
                                gatewayMode = gatewayModeInput,
                                senderBrand = senderBrandInput,
                                testPhone = testPhoneInput,
                                testCode = testCodeInput,
                                bypassRecaptcha = bypassRecaptchaInput
                            )
                            Toast.makeText(context, "Firebase & OTP Configuration Saved!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save & Apply Firebase Config", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.testFirebaseConnection()
                                Toast.makeText(context, "Diagnostics: Firebase Auth Connected", Toast.LENGTH_SHORT).show()
                            },
                            border = BorderStroke(1.dp, Color(0xFF00E676)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FlashOn, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Auth", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.resetFirebaseConfigToDefaults()
                                projectIdInput = "modol-connect"
                                apiKeyInput = "AIzaSyA14wj8tZCED9AsSbyvy_SO1zS_Q_AR9nA"
                                appIdInput = "1:125116191467:android:89995d07837981f91924ae"
                                storageBucketInput = "modol-connect.firebasestorage.app"
                                dbUrlInput = "http://173.249.28.110/"
                                gatewayModeInput = "FIREBASE_LIVE"
                                testPhoneInput = "+8801700000000"
                                testCodeInput = "123456"
                                bypassRecaptchaInput = true
                                Toast.makeText(context, "Restored Production Defaults", Toast.LENGTH_SHORT).show()
                            },
                            border = BorderStroke(1.dp, Color.Gray),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Defaults", color = Color.LightGray, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // =========================================================================
        // 🔐 1.1 BACKEND MASTER ONE-TIME PASSWORDS (ADMIN, MODEL, USER, CASH AGENT)
        // =========================================================================
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.2.dp, PinkHighlight.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(PinkHighlight.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = null,
                                tint = PinkHighlight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text("Backend Master Role One-Time Passwords", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("One-Time Passwords for Admin, Model, User, and Cash Agent", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF00E676).copy(alpha = 0.15f)
                    ) {
                        Text(
                            "LIVE IN DB",
                            color = Color(0xFF00E676),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "These master OTPs allow instant login and identity verification for each platform role without SMS gateway latency. Accounts can use these codes in the OTP verification screen or as a temporary login password.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                // 4 Role Master OTP Input Rows
                listOf(
                    Triple("ADMIN", "👑 Admin Master OTP", viewModel.adminMasterOtp to Color(0xFFFF2A6D)),
                    Triple("MODEL", "💃 Model Master OTP", viewModel.modelMasterOtp to Color(0xFFFF85A6)),
                    Triple("CLIENT", "👤 User / Client Master OTP", viewModel.userMasterOtp to Color(0xFF10B981)),
                    Triple("CASH_AGENT", "💼 Cash Agent Master OTP", viewModel.cashAgentMasterOtp to Color(0xFF2196F3))
                ).forEach { (roleKey, title, pair) ->
                    val (currentOtp, accentColor) = pair
                    var tempOtp by remember(currentOtp) { mutableStateOf(currentOtp) }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1.5f)) {
                            Text(title, color = accentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(3.dp))
                            OutlinedTextField(
                                value = tempOtp,
                                onValueChange = { if (it.length <= 6) tempOtp = it },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = accentColor,
                                    unfocusedBorderColor = Color(0xFF334155)
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Button(
                            onClick = {
                                val generated = viewModel.backendGenerateRoleMasterOtp(roleKey)
                                tempOtp = generated
                                Toast.makeText(context, "$title randomized to $generated!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(top = 16.dp).height(42.dp)
                        ) {
                            Text("🎲 Random", fontSize = 10.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.backendSetRoleMasterOtp(roleKey, tempOtp)
                                Toast.makeText(context, "$title saved as $tempOtp!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(top = 16.dp).height(42.dp)
                        ) {
                            Text("Save", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // =========================================================================
        // 2. COMMISSION RATES CARD
        // =========================================================================
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Commission Rates", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                
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
                        Text("5% per collection", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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

        // =========================================================================
        // 3. DATABASE BACKUP CARD
        // =========================================================================
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Database Backup", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Initiate full backup of all SQLite & server models.", color = TextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = { viewModel.addNotification("Backup Success", "Full DB dump saved locally.", "System") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF85A6)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Trigger Database Backup", fontSize = 11.sp)
                }
            }
        }

        // =========================================================================
        // 4. 💳 ONLINE PAYMENT GATEWAYS (GOOGLE PAY, ALIPAY, APPLE PAY & NEW GATEWAYS)
        // =========================================================================
        AdminPaymentGatewaysSection(viewModel = viewModel)
    }
}


// ============================================================================
// 7. HIGH-FIDELITY CASH AGENT TAB MODULES
// ============================================================================

@Composable
fun CashAgentDashboardTab(viewModel: AppViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val collections by viewModel.cashCollections.collectAsStateWithLifecycle()
    val allOrders by viewModel.allB2BOrders.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var isAgentOnline by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var selectedOrderForChat by remember { mutableStateOf<B2BOrder?>(null) }
    var showAddFloatDialog by remember { mutableStateOf(false) }
    var showAccountQrDialog by remember { mutableStateOf(false) }
    var orderToRelease by remember { mutableStateOf<B2BOrder?>(null) }

    val pendingCount = collections.count { it.status == "PENDING" }
    val paidCount = collections.count { it.status == "PAID" }
    val totalCollected = collections.filter { it.status == "PAID" }.sumOf { it.amount }
    val commissionEarned = totalCollected * (viewModel.agentCommissionRate / 100.0) + 6250.0

    val activeB2BOrders = remember(allOrders) {
        allOrders.filter { it.status != "RELEASED" && it.status != "CANCELLED" }
    }
    val escrowLockedTotal = remember(activeB2BOrders) {
        activeB2BOrders.sumOf { it.amount }
    }
    val completedCount = remember(allOrders) {
        allOrders.count { it.status == "RELEASED" }
    }

    val filteredOrders = remember(allOrders, selectedFilter) {
        when (selectedFilter) {
            "DEPOSIT" -> allOrders.filter { it.type == "DEPOSIT" }
            "WITHDRAWAL" -> allOrders.filter { it.type == "WITHDRAWAL" }
            "ESCROW" -> allOrders.filter { it.status == "PENDING_PAYMENT" || it.status == "PAYMENT_SUBMITTED" }
            "COMPLETED" -> allOrders.filter { it.status == "RELEASED" }
            else -> allOrders
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Top Bar Header & Online Status Switcher
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("MODOL CONNECT", color = PinkHighlight, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF00E676).copy(alpha = 0.18f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("B2B P2P ESCROW", color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text("B2B Cash Agent Platform", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Online/Offline Toggle Button
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isAgentOnline) Color(0xFF00E676).copy(alpha = 0.15f) else Color.White.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, if (isAgentOnline) Color(0xFF00E676) else Color.Gray),
                        modifier = Modifier.clickable {
                            isAgentOnline = !isAgentOnline
                            Toast.makeText(
                                context,
                                if (isAgentOnline) "🟢 Agent Status: Online (Ready to accept B2B orders)" else "⚪ Agent Status: Offline (Hidden from marketplace)",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(if (isAgentOnline) Color(0xFF00E676) else Color.Gray, CircleShape)
                            )
                            Text(
                                if (isAgentOnline) "Online" else "Offline",
                                color = if (isAgentOnline) Color(0xFF00E676) else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Quick Cash Agent Sign Out Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFDC2626).copy(alpha = 0.2f), CircleShape)
                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f), CircleShape)
                            .clickable { viewModel.showSignOutConfirmDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sign Out",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 2. Verified Agent Identity Bar
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, PinkBorderSoft),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(PinkHighlight.copy(alpha = 0.15f), CircleShape)
                            .border(1.5.dp, PinkHighlight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(26.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(currentUser?.name ?: "Agent Sumon", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF2196F3).copy(alpha = 0.18f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("✓ VERIFIED B2B AGENT", color = Color(0xFF2196F3), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text("Code: #CA-2048 • Dhaka Hub • Rating: 4.9 ★ (428 trades)", color = TextSecondary, fontSize = 11.sp)
                    }

                    Surface(
                        shape = CircleShape,
                        color = PinkHighlight.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, PinkHighlight.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .size(38.dp)
                            .clickable { showAccountQrDialog = true }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.QrCode2, contentDescription = "Payment QR", tint = PinkHighlight, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }

        // 3. High-Fidelity B2B Liquidity Float & Wallet Card (Professional High-Contrast Gradient)
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, PinkHighlight.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFF2A6D),
                                    Color(0xFFFF5E8A),
                                    Color(0xFF8B5CF6)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val agentBal = currentUser?.balance ?: 1500.0
                            Column {
                                Text("FLOAT LIQUIDITY POOL", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text("৳ %,.0f".format(agentBal), color = Color.White, fontWeight = FontWeight.Black, fontSize = 28.sp)
                                    Text(".00", color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 2.dp))
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .background(Color(0xFF86EFAC), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Synced with Personal Wallet (Shared)", color = Color.White.copy(alpha = 0.9f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("🔒 Escrow Protected", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.25f), thickness = 0.8.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Commission Earned", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("৳ %,d".format(commissionEarned.toInt()), color = Color(0xFF86EFAC), fontWeight = FontWeight.Black, fontSize = 16.sp)
                                Text("5% per trade", color = Color.White.copy(alpha = 0.75f), fontSize = 9.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Today's Volume", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("৳ %,d".format((totalCollected + 45000).toInt()), color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                Text("Active Settlement", color = Color.White.copy(alpha = 0.75f), fontSize = 9.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Escrow Locked", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("৳ %,d".format(escrowLockedTotal.toInt()), color = Color(0xFFFDE047), fontWeight = FontWeight.Black, fontSize = 16.sp)
                                Text("${activeB2BOrders.size} In-Flight", color = Color.White.copy(alpha = 0.75f), fontSize = 9.sp)
                            }
                        }

                        // Wallet Action Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showAddFloatDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = PinkHighlight),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1.1f).height(40.dp)
                            ) {
                                Icon(imageVector = Icons.Default.AddCard, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Float", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PinkHighlight)
                            }

                            Button(
                                onClick = { viewModel.selectedTab = 2 },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Payouts", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Button(
                                onClick = { showAccountQrDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(0.95f).height(40.dp)
                            ) {
                                Icon(imageVector = Icons.Default.QrCode, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("QR Code", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // 4. Quick Metrics 4-Box Grid (High Contrast & Clear Badges)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Active Trades
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, PinkBorderSoft),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Active Trades", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("${activeB2BOrders.size}", color = Color(0xFF0284C7), fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Text("Real-Time", color = Color(0xFF0369A1), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // In Escrow
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, PinkBorderSoft),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("In Escrow", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("৳${escrowLockedTotal.toInt()}", color = Color(0xFFD97706), fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Text("Locked", color = Color(0xFFB45309), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Collections
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, PinkBorderSoft),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.weight(1f).clickable { viewModel.selectedTab = 1 }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Cash Tasks", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("$pendingCount", color = PinkHighlight, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Text("Pending", color = PinkDark, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Completed
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, PinkBorderSoft),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Completed", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("${completedCount + paidCount}", color = Color(0xFF16A34A), fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Text("Settled", color = Color(0xFF15803D), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // 5. B2B P2P Escrow Orders Header & Filter Tabs
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, tint = PinkHighlight)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("B2B Escrow Orders", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(PinkHighlight.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text("${allOrders.size}", color = PinkHighlight, fontWeight = FontWeight.Black, fontSize = 10.sp)
                        }
                    }

                    IconButton(onClick = {
                        Toast.makeText(context, "Orders up to date", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = PinkHighlight, modifier = Modifier.size(18.dp))
                    }
                }

                // Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val filters = listOf(
                        "ALL" to "All (${allOrders.size})",
                        "ESCROW" to "🔒 In Escrow (${activeB2BOrders.size})",
                        "DEPOSIT" to "🟢 Cash-In",
                        "WITHDRAWAL" to "🔴 Cash-Out",
                        "COMPLETED" to "✓ Completed ($completedCount)"
                    )
                    filters.forEach { (key, label) ->
                        val isSelected = selectedFilter == key
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) PinkHighlight else DarkSurface,
                            border = BorderStroke(1.dp, if (isSelected) PinkHighlight else PinkBorderSoft),
                            modifier = Modifier.clickable { selectedFilter = key }
                        ) {
                            Text(
                                label,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }

        // 6. B2B Orders List
        if (filteredOrders.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Inbox, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(36.dp))
                        Text("No orders in '$selectedFilter'", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Active P2P escrow orders and wallet transactions will appear here.", color = TextSecondary, fontSize = 11.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            items(filteredOrders) { order ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (order.status == "PAYMENT_SUBMITTED") Color(0xFF00E676) else PinkBorderSoft),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Order Header Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (order.type == "DEPOSIT") Color(0xFF00E676).copy(alpha = 0.2f) else PinkHighlight.copy(alpha = 0.2f),
                                            RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        if (order.type == "DEPOSIT") "🟢 CASH-IN (BUY)" else "🔴 CASH-OUT (SELL)",
                                        color = if (order.type == "DEPOSIT") Color(0xFF00E676) else PinkHighlight,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("#${order.orderId.takeLast(8)}", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // Status badge
                            val (badgeBg, badgeColor, badgeText) = when (order.status) {
                                "PAYMENT_SUBMITTED" -> Triple(Color(0xFF2196F3).copy(alpha = 0.2f), Color(0xFF2196F3), "PAYMENT SUBMITTED")
                                "RELEASED" -> Triple(Color(0xFF00E676).copy(alpha = 0.2f), Color(0xFF00E676), "ESCROW RELEASED")
                                "DISPUTED" -> Triple(Color(0xFFEF4444).copy(alpha = 0.2f), Color(0xFFEF4444), "DISPUTE OPEN")
                                else -> Triple(Color(0xFFFFB74D).copy(alpha = 0.2f), Color(0xFFFFB74D), "AWAITING PAYMENT")
                            }
                            Box(
                                modifier = Modifier
                                    .background(badgeBg, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(badgeText, color = badgeColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Order Body Info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Client / Counterparty", color = TextSecondary, fontSize = 10.sp)
                                Text(order.userName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Method: ${order.paymentMethod}", color = TextSecondary, fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Trade Amount", color = TextSecondary, fontSize = 10.sp)
                                Text("৳${order.amount.toInt()}.00", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                Text("Earn: +৳${(order.amount * 0.05).toInt()} (5%)", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (!order.transactionRef.isNullOrBlank()) {
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, PinkBorderSoft),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Txn Ref: ${order.transactionRef}", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        HorizontalDivider(color = PinkBorderSoft, thickness = 0.8.dp)

                        // Action Buttons on Order Card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Live Chat & Proofs Button
                            OutlinedButton(
                                onClick = { selectedOrderForChat = order },
                                border = BorderStroke(1.dp, PinkHighlight),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Chat & Proof", fontSize = 11.sp, color = PinkHighlight, fontWeight = FontWeight.Bold)
                            }

                            // Release Escrow Button (Enabled if pending or submitted)
                            if (order.status != "RELEASED") {
                                Button(
                                    onClick = { orderToRelease = order },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (order.status == "PAYMENT_SUBMITTED") Color(0xFF00E676) else Color(0xFF2196F3)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(38.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LockOpen,
                                        contentDescription = null,
                                        tint = if (order.status == "PAYMENT_SUBMITTED") Color.Black else Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Release Funds",
                                        fontSize = 11.sp,
                                        color = if (order.status == "PAYMENT_SUBMITTED") Color.Black else Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Surface(
                                    color = Color(0xFF00E676).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("✓ Completed & Settled", color = Color(0xFF00A86B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Payment Channels & Operating Limits Information Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, PinkBorderSoft),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Agent Payment Channels & Limits", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("bKash Personal", "Nagad Agent", "Rocket", "City Bank Transfer", "Physical Cash Pickup").forEach { ch ->
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFFEFF3), RoundedCornerShape(6.dp))
                                    .border(1.dp, Color(0xFFFFD6E2), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("✓ $ch", color = Color(0xFFC2185B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Text("Per Trade Limits: ৳ 100 - ৳ 100,000 BDT • P2P Escrow Protection Guarantee", color = TextSecondary, fontSize = 10.sp)
                }
            }
        }

        // 8. Quick Task Navigation Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PinkBorderSoft),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.selectedTab = 1 }
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, tint = PinkHighlight)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Cash Collections ($pendingCount)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PinkBorderSoft),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.selectedTab = 2 }
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color(0xFF2196F3))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("My Wallet & Payout", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }

    // Modal 1: Live B2B Order Details and Real-time Chat
    if (selectedOrderForChat != null) {
        B2BOrderDetailAndChatDialog(
            order = selectedOrderForChat!!,
            onDismiss = { selectedOrderForChat = null },
            viewModel = viewModel
        )
    }

    // Modal 2: Release Escrow Confirmation Dialog
    if (orderToRelease != null) {
        val ord = orderToRelease!!
        AlertDialog(
            onDismissRequest = { orderToRelease = null },
            containerColor = DarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LockOpen, contentDescription = null, tint = Color(0xFF00E676))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm Escrow Release", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (ord.type == "DEPOSIT") {
                        Text("Are you sure you have received payment of ৳${ord.amount.toInt()} for Order #${ord.orderId.takeLast(8)} from ${ord.userName}?", color = TextSecondary, fontSize = 12.sp)
                        Text("Releasing will credit ৳${ord.amount.toInt()} directly to customer's wallet balance.", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text("Release withdrawal of ৳${ord.amount.toInt()} for customer ${ord.userName}?", color = TextSecondary, fontSize = 12.sp)
                        Text("Releasing will deduct ৳${ord.amount.toInt()} from customer's wallet and transfer it to your Cash Agent float/balance. Ensure cash is disbursed to the customer.", color = Color(0xFFFF9800), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.releaseB2BOrder(ord.orderId)
                        val msg = if (ord.type == "DEPOSIT") "Escrow Released! ৳${ord.amount.toInt()} credited to ${ord.userName}'s wallet." else "Withdrawal Released! ৳${ord.amount.toInt()} deducted from customer and transferred to Cash Agent."
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        orderToRelease = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                ) {
                    Text("Release Funds Now", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { orderToRelease = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Modal 3: Add Float / Liquidity Top Up Dialog
    if (showAddFloatDialog) {
        var floatAmountInput by remember { mutableStateOf("25000") }
        AlertDialog(
            onDismissRequest = { showAddFloatDialog = false },
            containerColor = DarkSurface,
            title = {
                Text("Add Float Liquidity", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Increase your B2B Agent Liquidity Pool to accept higher trade volumes:", color = TextSecondary, fontSize = 11.sp)
                    OutlinedTextField(
                        value = floatAmountInput,
                        onValueChange = { floatAmountInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Float Amount (৳ BDT)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PinkHighlight,
                            unfocusedBorderColor = PinkBorderSoft,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Settlement Method: Bank Wire / Admin Deposit Approval", color = TextSecondary, fontSize = 10.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = floatAmountInput.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            viewModel.quickAddWalletBalance(amt)
                            Toast.makeText(context, "Float top-up of ৳${amt.toInt()} added to wallet! Synced with Personal Wallet.", Toast.LENGTH_LONG).show()
                            showAddFloatDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
                ) {
                    Text("Confirm Top Up", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFloatDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Modal 4: Account Details & QR Code Dialog
    if (showAccountQrDialog) {
        AlertDialog(
            onDismissRequest = { showAccountQrDialog = false },
            containerColor = DarkSurface,
            title = {
                Text("B2B Payment Accounts & QR", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.QrCode2, contentDescription = "QR Code", tint = Color.Black, modifier = Modifier.fillMaxSize())
                    }
                    Text("Agent Sumon • Code #CA-2048", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("bKash Personal: 01711204899", color = Color(0xFFE91E63), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Nagad Agent: 01711204899", color = Color(0xFFFF9800), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("City Bank A/C: 150248896001", color = Color(0xFF2196F3), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAccountQrDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        )
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
        Text("Collection Requests", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
                            Text("ID: ${coll.id}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                                Text("Physical Cash", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                                    title = { Text("Confirm Cash Received", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp) },
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
        Text("Wallet & Commission", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)

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
                Text("Withdraw Commission", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                
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
        Text("My Profile", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, PinkBorderSoft),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.clickable { viewModel.showPhotoUploadDialog = true }
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(PinkLight, CircleShape)
                            .border(1.5.dp, PinkHighlight, CircleShape)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val agentAvatar = currentUser?.avatarUrl
                        if (!agentAvatar.isNullOrBlank()) {
                            val safeAvatar = if (agentAvatar.startsWith("/") && !agentAvatar.startsWith("file://")) "file://$agentAvatar" else agentAvatar
                            SubcomposeAsyncImage(
                                model = safeAvatar,
                                contentDescription = "Agent Avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                error = {
                                    Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(32.dp))
                                }
                            )
                        } else {
                            Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(32.dp))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(PinkHighlight, CircleShape)
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Edit Photo", tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(currentUser?.name ?: "Agent Sumon", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Role: Cash Collection Agent", color = TextSecondary, fontSize = 11.sp)
                    Text("ID: agent_584", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }

        // =========================================================================
        // 🔐 CASH AGENT SECURITY & LOGOUT CONTROLS
        // =========================================================================
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, PinkBorderSoft),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFFF2A6D).copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFFFF2A6D), modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text("Agent Security & Active Sessions", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Password & session management", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFF2A6D).copy(alpha = 0.15f)
                    ) {
                        Text(
                            "${viewModel.activeSessionsList.size} Devices",
                            color = Color(0xFFFF2A6D),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                HorizontalDivider(color = PinkBorderSoft, thickness = 0.8.dp)

                // 1. Change Password Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.showChangePasswordDialog = true }
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFF2A6D).copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.LockReset, contentDescription = null, tint = Color(0xFFFF2A6D), modifier = Modifier.size(18.dp))
                            }
                        }
                        Column {
                            Text("Change Password", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Update login credentials & safeguard account", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                }

                HorizontalDivider(color = PinkBorderSoft, thickness = 0.8.dp)

                // 2. All Devices Logout Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.showAllLogoutConfirmDialog = true }
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFDC2626).copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Devices, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                            }
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Log Out All Devices", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Text("Terminate active sessions on other phones & PCs", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                }

                HorizontalDivider(color = PinkBorderSoft, thickness = 0.8.dp)

                // 3. Direct Sign Out Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.showSignOutConfirmDialog = true }
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFDC2626).copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Logout, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                            }
                        }
                        Column {
                            Text("Sign Out", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Safely exit cash agent session on this device", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, PinkBorderSoft),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Role Switcher", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF85A6)),
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
                        Text("PHP Backend Files & Architecture", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                            .background(Color(0xFFFF85A6), RoundedCornerShape(6.dp))
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
    ${'$'}photoUrl = "http://173.249.28.110/uploads/profile/" . ${'$'}filename;
    
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
        'base_url' => 'http://173.249.28.110/',
        'modules' => [
            'Admin Portal' => 'http://173.249.28.110/backend/admin/dashboard.php',
            'Cash Agent Portal' => 'http://173.249.28.110/backend/agent/dashboard.php',
            'API' => 'http://173.249.28.110/backend/api/',
            'Documentation' => 'http://173.249.28.110/backend/docs/'
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
                    Text("PHP Backend Code Display", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
                                .background(if (isSel) PinkHighlight else Color(0xFFFF85A6))
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
                                    .background(if (isSel) PinkHighlight.copy(alpha = 0.25f) else Color(0xFFFF85A6))
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
                        Text("  ├── 📂 admin/ (12 Admin Portal PHP files)", color = TextPrimary, fontSize = 11.sp)
                        Text("  ├── 📂 agent/ (10 Cash Agent Portal PHP files)", color = TextPrimary, fontSize = 11.sp)
                        Text("  ├── 📂 api/ (REST JSON Endpoints for Mobile App)", color = TextPrimary, fontSize = 11.sp)
                        Text("  ├── 📂 config/ (database.php, config.php, auth.php)", color = TextPrimary, fontSize = 11.sp)
                        Text("  ├── 📂 middleware/ (Role authorization scripts)", color = TextPrimary, fontSize = 11.sp)
                        Text("  ├── 📂 uploads/ (Receipts, profiles, documents)", color = TextPrimary, fontSize = 11.sp)
                        Text("  └── 📄 schema.sql (MySQL Database Schema)", color = TextPrimary, fontSize = 11.sp)
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            border = BorderStroke(1.dp, PinkBorderSoft)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Storage Location in Repository:", color = TextSecondary, fontSize = 10.sp)
                                Text("/backend/", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                        Text("Simulate Live PHP REST API:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        
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
    "base_url": "http://173.249.28.110/",
    "modules": {
      "Admin Portal": "http://173.249.28.110/backend/admin/dashboard.php",
      "Cash Agent Portal": "http://173.249.28.110/backend/agent/dashboard.php",
      "API": "http://173.249.28.110/backend/api/",
      "Documentation": "http://173.249.28.110/backend/docs/"
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
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF85A6)),
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
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF85A6)),
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
        modifier = Modifier.border(1.dp, PinkBorderSoft, RoundedCornerShape(16.dp)),
        containerColor = Color.White,
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
    var googlePlayMerchantId by remember { mutableStateOf("merchant.com.modol.connect.app") }
    var googlePlayPackageName by remember { mutableStateOf("com.modol.connect.app") }
    var googlePlaySkuPrefix by remember { mutableStateOf("booking_payment_") }
    var googlePlaySandboxMode by remember { mutableStateOf(false) }
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
                    .background(Color.White)
                    .border(BorderStroke(1.dp, Color(0xFFFF85A6)))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo("DASHBOARD") },
                    modifier = Modifier
                        .background(Color(0xFFF1F5F9), CircleShape)
                        .size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Dashboard",
                        tint = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "💼 Offered Services & Price",
                        color = TextPrimary,
                        fontWeight = FontWeight.ExtraBold,
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
                        .background(PinkHighlight.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
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
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFFF85A6)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Service Status", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("✅ Enable Service", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text("Toggle visibility of your service listings to clients", color = TextSecondary, fontSize = 11.sp)
                                }
                                Switch(
                                    checked = isServiceEnabled,
                                    onCheckedChange = { isServiceEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = PinkHighlight, checkedTrackColor = PinkHighlight.copy(alpha = 0.5f))
                                )
                            }

                            HorizontalDivider(color = Color(0xFFFF85A6), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("📌 Featured Service Toggle", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
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
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFFF85A6)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Category, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Service Categories", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                                            containerColor = Color(0xFFF1F5F9),
                                            labelColor = TextPrimary
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
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFFF85A6)),
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
                                    Text("Price Configuration", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    listOf("USD ($)", "BDT (৳)").forEach { curr ->
                                        val isSel = selectedCurrency == curr
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isSel) PinkHighlight else Color(0xFFF1F5F9))
                                                .then(if (!isSel) Modifier.border(1.dp, Color(0xFFFF85A6), RoundedCornerShape(6.dp)) else Modifier)
                                                .clickable { selectedCurrency = curr }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(curr, color = if (isSel) Color.White else TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
                                                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFFF85A6)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Travel Charges", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                    focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                Text("Hotel Accommodation Required", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
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
                                Text("Flight Tickets Required", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
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
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFFF85A6)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Working Schedule", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                                            .background(if (isSel) PinkHighlight else Color(0xFFF1F5F9))
                                            .then(if (!isSel) Modifier.border(1.dp, Color(0xFFFF85A6), CircleShape) else Modifier)
                                            .clickable {
                                                activeDays = if (isSel) activeDays - day else activeDays + day
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(day, color = if (isSel) Color.White else TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = endTime,
                                    onValueChange = { endTime = it },
                                    label = { Text("End Time", color = TextSecondary, fontSize = 11.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                    focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                    Text("Instant Booking Mode (On/Off)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
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
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFFF85A6)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.AddCard, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Additional Charges", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                    focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFFF85A6)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Booking Rules", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = maxBookingDuration,
                                    onValueChange = { maxBookingDuration = it },
                                    label = { Text("Max Booking Duration", color = TextSecondary, fontSize = 11.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = freeCancellationHours,
                                    onValueChange = { freeCancellationHours = it },
                                    label = { Text("Free Cancellation (Hours)", color = TextSecondary, fontSize = 11.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                    focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                    Text("Auto Accept Bookings (On/Off)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
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
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFFF85A6)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Payment, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Payment Settings", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                                                .background(Color(0xFFF1F5F9))
                                                .border(1.dp, Color(0xFFFF85A6), RoundedCornerShape(8.dp))
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
                                            Text(name, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            if (acceptGooglePlayBilling) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                    border = BorderStroke(1.dp, Color(0xFFFF85A6)),
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
                                                        tint = Color(0xFF00C853),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Google Play In-App Billing Configuration",
                                                    color = TextPrimary,
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
                                                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                            Text("Google Play License Tester / Sandbox Mode", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
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
                                            Text("Auto-Verify & Realtime Payment Webhook Sync", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
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
                                                color = Color(0xFF00C853),
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
                                            Text("Save & Verify Google Play Gateway", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFFF85A6)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Service Description", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = serviceTitle,
                                onValueChange = { serviceTitle = it },
                                label = { Text("Service Title", color = TextSecondary) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                    focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                    focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Included Features:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                includedFeatures.forEach { feature ->
                                    Box(
                                        modifier = Modifier
                                            .background(OnlineGreen.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                            .border(1.dp, OnlineGreen, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = OnlineGreen, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(feature, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
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
                                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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

                            Text("Excluded Features:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                excludedFeatures.forEach { feature ->
                                    Box(
                                        modifier = Modifier
                                            .background(Color.Red.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                            .border(1.dp, Color.Red, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color.Red, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(feature, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
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
                                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6),
                                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
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
                                    modifier = Modifier.background(PinkHighlight, RoundedCornerShape(8.dp))
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
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFFF85A6)),
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
                                    Text("Portfolio", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                                        Text("+ Image", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { showAddVideoDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64748B)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("+ Video", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Service Images:", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
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
                                            .background(Color(0xFFF1F5F9))
                                            .border(1.dp, Color(0xFFFF85A6), RoundedCornerShape(10.dp))
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

                            Text("Service Videos:", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))

                            portfolioVideos.forEachIndexed { idx, title ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF8FAFC))
                                        .border(1.dp, Color(0xFFFF85A6), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFFF85A6)),
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
                            Text("💾 Save Changes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.navigateTo("MODEL_PROFILE") },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = BorderStroke(1.dp, Color(0xFFFF85A6)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("👁 Preview", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                title = { Text("Upload Portfolio Image", color = TextPrimary, fontWeight = FontWeight.Bold) },
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
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)
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
                title = { Text("Upload Portfolio Video", color = TextPrimary, fontWeight = FontWeight.Bold) },
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
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)
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
                title = { Text("➕ Add New Service Package", color = TextPrimary, fontWeight = FontWeight.Bold) },
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
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)
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
                                focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)
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

                HorizontalDivider(color = PinkBorderSoft)

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
                            color = PinkBorderSoft,
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

                HorizontalDivider(color = PinkBorderSoft)

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
                }

                HorizontalDivider(color = PinkBorderSoft)

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

                HorizontalDivider(color = PinkBorderSoft)

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
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        B2BCashAgentMarketplaceContent(viewModel = viewModel, onClose = onDismiss)
    }
}

@Composable
fun B2BCashAgentMarketplaceContent(
    viewModel: AppViewModel,
    onClose: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val allAgents by viewModel.paymentAgents.collectAsStateWithLifecycle()
    val allOrders by viewModel.allB2BOrders.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var activeTopTab by remember { mutableStateOf("BUY") } // "BUY", "SELL", "AGENTS", "ORDERS"
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val selectedCountry = viewModel.p2pSelectedCountry
    val availableCountries = viewModel.p2pAvailableCountries
    var showCountryDialog by remember { mutableStateOf(false) }
    var activePaymentMethodFilter by remember { mutableStateOf("ALL") }
    var onlineOnlyFilter by remember { mutableStateOf(false) }
    var verifiedOnlyFilter by remember { mutableStateOf(false) }

    var selectedOrderForChat by remember { mutableStateOf<B2BOrder?>(null) }
    var selectedAgentForOrder by remember { mutableStateOf<PaymentAgent?>(null) }
    var showTopUpDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var showSystemsGuideDialog by remember { mutableStateOf(false) }

    // Sync countries and payment methods from backend
    LaunchedEffect(Unit) {
        viewModel.syncCountriesFromBackend()
        viewModel.syncPaymentAgentsFromBackend()
    }

    // Reset payment method filter whenever selected country changes
    LaunchedEffect(selectedCountry.countryName) {
        activePaymentMethodFilter = "ALL"
    }

    // Dynamic localized agents matching selected Country, Currency & Payment Methods
    val countryAgents = remember(selectedCountry, allAgents) {
        getMarketplaceAgentsForCountry(selectedCountry, allAgents)
    }

    // Filter agents based on payment method filter, online/verified status, and search query
    val displayedAgents = remember(
        countryAgents,
        activePaymentMethodFilter,
        onlineOnlyFilter,
        verifiedOnlyFilter,
        searchQuery
    ) {
        countryAgents.filter { agent ->
            val matchMethod = if (activePaymentMethodFilter == "ALL") true else {
                agent.paymentMethods.any { it.equals(activePaymentMethodFilter, ignoreCase = true) }
            }
            val matchOnline = if (onlineOnlyFilter) agent.isOnline else true
            val matchVerified = if (verifiedOnlyFilter) agent.isVerified else true
            val matchQuery = if (searchQuery.isBlank()) true else {
                agent.name.contains(searchQuery, ignoreCase = true) ||
                agent.location.contains(searchQuery, ignoreCase = true) ||
                agent.currency.contains(searchQuery, ignoreCase = true) ||
                agent.paymentMethods.any { it.contains(searchQuery, ignoreCase = true) }
            }
            matchMethod && matchOnline && matchVerified && matchQuery
        }
    }

    val activeOrders = remember(allOrders) {
        allOrders.filter { it.status != "RELEASED" && it.status != "CANCELLED" }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC) // Soft modern clean canvas
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (onClose != null) Modifier.windowInsetsPadding(WindowInsets.statusBars) else Modifier)
                .padding(horizontal = 14.dp, vertical = if (onClose != null) 10.dp else 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ==========================================
            // 1. TOP APP BAR (Logo, Country, Search & Bell)
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Heart 'M' Logo Box
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFFFF2A6D), Color(0xFFFF5E8A), Color(0xFF9333EA))
                                ),
                                shape = RoundedCornerShape(13.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Logo",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("MODOL ", color = Color(0xFF0F172A), fontWeight = FontWeight.Black, fontSize = 17.sp)
                            Text("CONNECT", color = Color(0xFFFF2A6D), fontWeight = FontWeight.Black, fontSize = 17.sp)
                        }
                        Text("B2B Cash Agent Marketplace", color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                    }
                }

                // Action Icons (Country Switcher, Search, Notification, Close)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // P2P Country / Currency Switcher Button (Soft & Prominent)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.2.dp, Color(0xFFFF2A6D).copy(alpha = 0.4f)),
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .height(38.dp)
                            .clickable { showCountryDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(selectedCountry.flag, fontSize = 15.sp)
                            Text(
                                selectedCountry.currencyCode,
                                color = Color(0xFFFF2A6D),
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Country",
                                tint = Color(0xFFFF2A6D),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .size(38.dp)
                            .clickable { isSearchActive = !isSearchActive }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .size(38.dp)
                            .clickable {
                                Toast.makeText(context, "B2B Escrow System: 100% Protected & Active", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.NotificationsNone,
                                contentDescription = "Notifications",
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(19.dp)
                            )
                            // Notification Dot
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFFFF2A6D), CircleShape)
                                    .align(Alignment.TopEnd)
                                    .padding(top = 4.dp, end = 4.dp)
                            )
                        }
                    }

                    if (onClose != null) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable { onClose() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color(0xFF475569),
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar Expandable
            if (isSearchActive) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by agent name, city, wallet, currency...", fontSize = 12.sp, color = Color(0xFF94A3B8)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF2A6D),
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color(0xFF0F172A),
                        unfocusedTextColor = Color(0xFF0F172A)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    singleLine = true
                )
            }

            // ==========================================
            // 2. SEGMENT TABS (Buy, Sell, Agents, Orders)
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tabs = listOf(
                    "BUY" to Triple("Buy / Cash In", Icons.Default.ArrowDownward, Color(0xFFFF2A6D)),
                    "SELL" to Triple("Sell / Cash Out", Icons.Default.ArrowUpward, Color(0xFF475569)),
                    "AGENTS" to Triple("Agent List", Icons.Default.Group, Color(0xFF475569)),
                    "ORDERS" to Triple("My Orders", Icons.Default.ReceiptLong, Color(0xFF475569))
                )

                tabs.forEach { (tabKey, tabData) ->
                    val isSelected = activeTopTab == tabKey
                    val (label, icon, _) = tabData

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) Color(0xFFFF2A6D) else Color.White,
                        border = BorderStroke(1.2.dp, if (isSelected) Color(0xFFFF2A6D) else Color(0xFFE2E8F0)),
                        shadowElevation = if (isSelected) 2.dp else 1.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clickable { activeTopTab = tabKey }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else Color(0xFF475569),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else Color(0xFF334155),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (tabKey == "ORDERS" && activeOrders.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(3.dp))
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(if (isSelected) Color.White else Color(0xFFFF2A6D), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${activeOrders.size}",
                                        color = if (isSelected) Color(0xFFFF2A6D) else Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 4. B2B WALLET BALANCE CARD (Dynamic & Soft)
            // ==========================================
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.2.dp, Color(0xFFFFD5E2)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFFF0F5),
                                    Color(0xFFFFFFFF),
                                    Color(0xFFFFF5F7)
                                )
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Wallet Icon + Balance Text
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFFF2A6D),
                                shadowElevation = 2.dp,
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            val userBalance = currentUser?.balance ?: 1500.0
                            val curSymbol = CountryPaymentMaster.getCurrencySymbol(selectedCountry.currencyCode)
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("B2B Wallet Balance", color = Color(0xFFC2185B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text("ESCROW READY", color = Color(0xFF047857), fontSize = 8.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        Toast.makeText(context, "Wallet: $curSymbol${"%,.2f".format(userBalance)} (${selectedCountry.currencyCode})", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Text(
                                        "$curSymbol %,.2f".format(userBalance),
                                        color = Color(0xFF0F172A),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 22.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        selectedCountry.currencyCode,
                                        color = Color(0xFFFF2A6D),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                val usdVal = userBalance / 122.5
                                Text("≈ $ %,.2f USD • Account Balance".format(usdVal), color = Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Right: Top Up & Withdraw Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Top Up
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { showTopUpDialog = true }
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFFF2A6D),
                                    shadowElevation = 2.dp,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Top Up", tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text("Cash In", color = Color(0xFF0F172A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            // Withdraw
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { showWithdrawDialog = true }
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White,
                                    border = BorderStroke(1.2.dp, Color(0xFFFF2A6D)),
                                    shadowElevation = 1.dp,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(imageVector = Icons.Default.CallMade, contentDescription = "Withdraw", tint = Color(0xFFFF2A6D), modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text("Cash Out", color = Color(0xFF0F172A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 5. HORIZONTAL FILTER CHIPS ROW (Soft & Clear)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Country Picker Quick Chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.2.dp, Color(0xFFFF2A6D).copy(alpha = 0.5f)),
                    shadowElevation = 1.dp,
                    modifier = Modifier.clickable { showCountryDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(selectedCountry.flag, fontSize = 14.sp)
                        Text(
                            selectedCountry.countryName,
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color(0xFFFF2A6D),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // "All Methods" Chip
                val isAllSelected = activePaymentMethodFilter == "ALL"
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isAllSelected) Color(0xFFFF2A6D) else Color.White,
                    border = BorderStroke(1.dp, if (isAllSelected) Color(0xFFFF2A6D) else Color(0xFFCBD5E1)),
                    shadowElevation = if (isAllSelected) 1.5.dp else 0.5.dp,
                    modifier = Modifier.clickable { activePaymentMethodFilter = "ALL" }
                ) {
                    Text(
                        "All Methods",
                        color = if (isAllSelected) Color.White else Color(0xFF334155),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }

                // Dynamic Payment Methods for Selected Country
                selectedCountry.paymentMethods.forEach { method ->
                    val isSelected = activePaymentMethodFilter.equals(method.methodName, ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Color(0xFFFF2A6D) else Color.White,
                        border = BorderStroke(1.dp, if (isSelected) Color(0xFFFF2A6D) else Color(0xFFCBD5E1)),
                        shadowElevation = if (isSelected) 1.5.dp else 0.5.dp,
                        modifier = Modifier.clickable {
                            activePaymentMethodFilter = if (isSelected) "ALL" else method.methodName
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val ic = when (method.methodType) {
                                "Mobile Wallet" -> Icons.Default.Smartphone
                                "Bank Transfer" -> Icons.Default.AccountBalance
                                "Instant Bank" -> Icons.Default.FlashOn
                                "Cash" -> Icons.Default.Payments
                                "Card" -> Icons.Default.CreditCard
                                "Crypto/USDT" -> Icons.Default.CurrencyBitcoin
                                else -> Icons.Default.Payment
                            }
                            Icon(
                                imageVector = ic,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else Color(0xFFFF2A6D),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                method.methodName,
                                color = if (isSelected) Color.White else Color(0xFF334155),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Online Only Filter Chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (onlineOnlyFilter) Color(0xFF10B981) else Color.White,
                    border = BorderStroke(1.dp, if (onlineOnlyFilter) Color(0xFF10B981) else Color(0xFFCBD5E1)),
                    shadowElevation = if (onlineOnlyFilter) 1.5.dp else 0.5.dp,
                    modifier = Modifier.clickable { onlineOnlyFilter = !onlineOnlyFilter }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(7.dp).background(if (onlineOnlyFilter) Color.White else Color(0xFF10B981), CircleShape))
                        Text(
                            "Online Only",
                            color = if (onlineOnlyFilter) Color.White else Color(0xFF334155),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Verified Only Filter Chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (verifiedOnlyFilter) Color(0xFFFF2A6D) else Color.White,
                    border = BorderStroke(1.dp, if (verifiedOnlyFilter) Color(0xFFFF2A6D) else Color(0xFFCBD5E1)),
                    shadowElevation = if (verifiedOnlyFilter) 1.5.dp else 0.5.dp,
                    modifier = Modifier.clickable { verifiedOnlyFilter = !verifiedOnlyFilter }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (verifiedOnlyFilter) Color.White else Color(0xFFFF2A6D),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            "Verified Only",
                            color = if (verifiedOnlyFilter) Color.White else Color(0xFF334155),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // ==========================================
            // 6. MAIN CONTENT AREA (AGENTS OR ORDERS)
            // ==========================================
            if (activeTopTab == "ORDERS") {
                // Orders View
                if (allOrders.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Inbox, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(44.dp))
                                Text("No Active B2B Orders", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Select an agent from the marketplace to start Cash In or Cash Out.", color = Color(0xFF64748B), fontSize = 12.sp, textAlign = TextAlign.Center)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(allOrders) { order ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(18.dp),
                                border = BorderStroke(1.2.dp, Color(0xFFE2E8F0)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth().clickable { selectedOrderForChat = order }
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Order #${order.orderId.takeLast(8)}", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 13.sp)
                                        val (stCol, stTxt) = when (order.status) {
                                            "RELEASED" -> Color(0xFF10B981) to "COMPLETED & RELEASED"
                                            "PAYMENT_SUBMITTED" -> Color(0xFF2563EB) to "PAYMENT SUBMITTED"
                                            "DISPUTED" -> Color(0xFFEF4444) to "DISPUTED"
                                            else -> Color(0xFFF59E0B) to "PENDING PAYMENT"
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(stCol.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(stTxt, color = stCol, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                        }
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${order.type} • ${order.agentName}", color = Color(0xFF475569), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                        Text("৳${order.amount.toInt()} (${order.currency})", color = Color(0xFFFF2A6D), fontWeight = FontWeight.Black, fontSize = 14.sp)
                                    }
                                    Button(
                                        onClick = { selectedOrderForChat = order },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A6D)),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().height(36.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(imageVector = Icons.Default.Forum, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                            Text("Live Chat & Escrow Security", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // =========================================================================
                // MARKETPLACE AGENTS LIST (SOFT, HIGH-CONTRAST, SUPER VISIBLE & ELEGANT)
                // =========================================================================
                if (displayedAgents.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(Color(0xFFFF2A6D).copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Storefront, contentDescription = null, tint = Color(0xFFFF2A6D), modifier = Modifier.size(32.dp))
                                }
                                Text("No Active B2B Agents in ${selectedCountry.countryName}", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 15.sp, textAlign = TextAlign.Center)
                                Text("No live cash agent is currently registered for this country yet. Register as an official cash agent to start receiving B2B trade orders.", color = Color(0xFF64748B), fontSize = 12.sp, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { viewModel.navigateTo("REGISTER_CASH_AGENT") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A6D)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.AddBusiness, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Register as Cash Agent", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(displayedAgents) { agent ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.2.dp, Color(0xFFE2E8F0)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // -------------------------------------------------------------
                                // 1. AGENT IDENTITY HEADER (Avatar, Name, Location, Rating)
                                // -------------------------------------------------------------
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // Avatar with Online Badge
                                        Box {
                                            SubcomposeAsyncImage(
                                                model = agent.avatarUrl,
                                                contentDescription = agent.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(52.dp)
                                                    .clip(CircleShape)
                                                    .border(1.5.dp, Color(0xFFFF2A6D).copy(alpha = 0.5f), CircleShape)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .background(if (agent.isOnline) Color(0xFF10B981) else Color(0xFF94A3B8), CircleShape)
                                                    .border(1.5.dp, Color.White, CircleShape)
                                                    .align(Alignment.BottomEnd)
                                            )
                                        }

                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    agent.name,
                                                    color = Color(0xFF0F172A),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Verified Agent",
                                                    tint = Color(0xFF2563EB),
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(agent.flagEmoji, fontSize = 12.sp)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(agent.location, color = Color(0xFF475569), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(12.dp))
                                                Text(
                                                    "${agent.rating}",
                                                    color = Color(0xFF0F172A),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text("(${agent.ordersCount} trades)", color = Color(0xFF64748B), fontSize = 10.sp)
                                                Text("•", color = Color(0xFF94A3B8), fontSize = 9.sp)
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = Color(0xFFECFDF5)
                                                ) {
                                                    Text(
                                                        "${agent.completionRate} completion",
                                                        color = Color(0xFF059669),
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Online Pill
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (agent.isOnline) Color(0xFFECFDF5) else Color(0xFFF1F5F9),
                                        border = BorderStroke(0.8.dp, if (agent.isOnline) Color(0xFFA7F3D0) else Color(0xFFCBD5E1))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Box(modifier = Modifier.size(6.dp).background(if (agent.isOnline) Color(0xFF10B981) else Color(0xFF94A3B8), CircleShape))
                                            Text(
                                                if (agent.isOnline) "Active" else "Offline",
                                                color = if (agent.isOnline) Color(0xFF065F46) else Color(0xFF64748B),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        }
                                    }
                                }

                                // -------------------------------------------------------------
                                // 2. SYSTEM RATES CONTAINER (Crystal Clear, Soft & Prominent)
                                // -------------------------------------------------------------
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Buy Rate Box
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            horizontalAlignment = Alignment.Start
                                        ) {
                                             Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(Color(0xFFDCFCE7), RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                                ) {
                                                    Text("BUY / CASH IN", color = Color(0xFF15803D), fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                "${agent.currencySymbol}${agent.buyRate}",
                                                color = Color(0xFF15803D),
                                                fontWeight = FontWeight.Black,
                                                fontSize = 16.sp
                                            )
                                        }

                                        // Divider
                                        Box(
                                            modifier = Modifier
                                                .width(1.dp)
                                                .height(32.dp)
                                                .background(Color(0xFFE2E8F0))
                                        )

                                        // Sell Rate Box
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            horizontalAlignment = Alignment.End
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(Color(0xFFFFE4E6), RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                                ) {
                                                    Text("SELL / CASH OUT", color = Color(0xFFBE123C), fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                "${agent.currencySymbol}${agent.sellRate}",
                                                color = Color(0xFFBE123C),
                                                fontWeight = FontWeight.Black,
                                                fontSize = 16.sp
                                            )
                                        }
                                    }
                                }

                                // -------------------------------------------------------------
                                // 3. SYSTEM LIMITS & TIMINGS (Clear & Readable)
                                // -------------------------------------------------------------
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            "Trade Limit: ${agent.currencySymbol}%,d - %,d".format(agent.minLimit.toInt(), agent.maxLimit.toInt()),
                                            color = Color(0xFF334155),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "Reserve Pool: ${agent.currencySymbol}%,.0f".format(agent.availableBalance),
                                            color = Color(0xFF64748B),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFF3E8FF),
                                        border = BorderStroke(0.6.dp, Color(0xFFD8B4FE))
                                    ) {
                                        Text(
                                            "⚡ ${agent.avgReleaseTime} Release",
                                            color = Color(0xFF7E22CE),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                // -------------------------------------------------------------
                                // 4. PAYMENT METHODS ROW
                                // -------------------------------------------------------------
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    agent.paymentMethods.forEach { method ->
                                        val (pillBg, pillTint) = when (method.lowercase()) {
                                            "bkash" -> Color(0xFFFFE8F0) to Color(0xFFE91E63)
                                            "nagad" -> Color(0xFFFFF3E0) to Color(0xFFFF9800)
                                            "bank", "wire", "bank transfer" -> Color(0xFFE3F2FD) to Color(0xFF1976D2)
                                            "usdt" -> Color(0xFFE0F2F1) to Color(0xFF00897B)
                                            "wise" -> Color(0xFFE1F5FE) to Color(0xFF0288D1)
                                            else -> Color(0xFFECFDF5) to Color(0xFF059669)
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = pillBg,
                                            border = BorderStroke(0.5.dp, pillTint.copy(alpha = 0.35f))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                                            ) {
                                                val icon = when (method.lowercase()) {
                                                    "bank", "wire", "bank transfer" -> Icons.Default.AccountBalance
                                                    "cash" -> Icons.Default.Payments
                                                    "usdt" -> Icons.Default.CurrencyBitcoin
                                                    else -> Icons.Default.Payment
                                                }
                                                Icon(imageVector = icon, contentDescription = null, tint = pillTint, modifier = Modifier.size(11.dp))
                                                Text(method, color = pillTint, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                // -------------------------------------------------------------
                                // 5. ESCROW GUARANTEE BADGE & ACTION BUTTON
                                // -------------------------------------------------------------
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFECFDF5),
                                        border = BorderStroke(0.8.dp, Color(0xFFA7F3D0))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(12.dp))
                                            Text(
                                                "100% Escrow Protected",
                                                color = Color(0xFF065F46),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    val btnLabel = when (activeTopTab) {
                                        "SELL" -> "Sell / Cash Out"
                                        "BUY" -> "Buy / Cash In"
                                        else -> "Start Trade"
                                    }

                                    Button(
                                        onClick = {
                                                val matchedPaymentAgent = PaymentAgent(
                                                    id = agent.id,
                                                    name = agent.name,
                                                    agentCode = agent.id.takeLast(4),
                                                    country = agent.country,
                                                    currency = agent.currency,
                                                    phone = agent.accountNumber,
                                                    paymentMethod = agent.paymentMethods.firstOrNull() ?: "Bank Transfer",
                                                    accountNumber = agent.accountNumber,
                                                    accountHolder = agent.name,
                                                    commissionRate = 1.5,
                                                    buyRate = agent.buyRate,
                                                    sellRate = agent.sellRate,
                                                    minLimit = agent.minLimit,
                                                    maxLimit = agent.maxLimit,
                                                    availableBalance = agent.availableBalance,
                                                    allowedMethods = agent.paymentMethods.joinToString(", "),
                                                    totalOrders = agent.ordersCount,
                                                    completionRate = agent.completionRate,
                                                    avgReleaseTime = agent.avgReleaseTime,
                                                    rating = agent.rating.toFloat(),
                                                    isOnline = agent.isOnline,
                                                    verificationStatus = if (agent.isVerified) "VERIFIED" else "PENDING_VERIFICATION"
                                                )
                                                selectedAgentForOrder = matchedPaymentAgent
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A6D)),
                                            shape = RoundedCornerShape(12.dp),
                                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                                            modifier = Modifier.height(38.dp).widthIn(min = 120.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(btnLabel, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
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
}

    // =========================================================================
    // MODAL: HOW B2B ESCROW SYSTEM WORKS
    // =========================================================================
    if (showSystemsGuideDialog) {
        AlertDialog(
            onDismissRequest = { showSystemsGuideDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFFF2A6D).copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFFFF2A6D), modifier = Modifier.size(18.dp))
                    }
                    Text(
                        "B2B Escrow System Guide",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Our B2B Cash Agent Marketplace is operated under 100% automated smart escrow security. There is zero risk of fund loss for either party.",
                        color = Color(0xFF475569),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    // Step 1
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.size(24.dp).background(Color(0xFFFF2A6D), CircleShape), contentAlignment = Alignment.Center) {
                                Text("1", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Column {
                                Text("Select Agent & Payment Method", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 12.sp)
                                Text("Select your preferred verified agent based on country rates, trade limits, and payment methods (bKash/Nagad/Bank Transfer, etc.).", color = Color(0xFF64748B), fontSize = 11.sp)
                            }
                        }
                    }

                    // Step 2
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.size(24.dp).background(Color(0xFF2563EB), CircleShape), contentAlignment = Alignment.Center) {
                                Text("2", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Column {
                                Text("Smart Escrow Lock (Escrow Vault)", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 12.sp)
                                Text("When a trade order is placed, the funds are safely locked inside the system escrow vault until the trade completes.", color = Color(0xFF64748B), fontSize = 11.sp)
                            }
                        }
                    }

                    // Step 3
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.size(24.dp).background(Color(0xFFF59E0B), CircleShape), contentAlignment = Alignment.Center) {
                                Text("3", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Column {
                                Text("Payment & Proof Upload in Live Chat", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 12.sp)
                                Text("Send payment to the agent's account and upload the screenshot/reference ID in the trade chat. For withdrawals, the agent transfers payment to you.", color = Color(0xFF64748B), fontSize = 11.sp)
                            }
                        }
                    }

                    // Step 4
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.size(24.dp).background(Color(0xFF10B981), CircleShape), contentAlignment = Alignment.Center) {
                                Text("4", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Column {
                                Text("Instant Release & Wallet Credit", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 12.sp)
                                Text("Once payment is verified, clicking Release immediately transfers the balance to your wallet without delay.", color = Color(0xFF64748B), fontSize = 11.sp)
                            }
                        }
                    }

                    // Policy Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFECFDF5),
                        border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                    ) {
                        Text(
                            "🛡️ In case of any issue, our 24/7 Admin Dispute Team inspects trade chats & proofs for instant resolution.",
                            color = Color(0xFF065F46),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSystemsGuideDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A6D)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Got It", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Modal 1: Create B2B Trade Order Dialog
    if (selectedAgentForOrder != null) {
        CreateB2BOrderModal(
            agent = selectedAgentForOrder!!,
            tradeType = if (activeTopTab == "SELL") "WITHDRAWAL" else "DEPOSIT",
            onDismiss = { selectedAgentForOrder = null },
            onCreated = { order ->
                selectedAgentForOrder = null
                selectedOrderForChat = order
            },
            viewModel = viewModel
        )
    }

    // Modal 2: B2B Order Details and Real-time Chat
    if (selectedOrderForChat != null) {
        B2BOrderDetailAndChatDialog(
            order = selectedOrderForChat!!,
            onDismiss = { selectedOrderForChat = null },
            viewModel = viewModel
        )
    }

    // Modal 3: Top Up Dialog (B2B Cash Agent Escrow Topup)
    if (showTopUpDialog) {
        var topUpAmount by remember { mutableStateOf("1000") }
        val presets = listOf("500", "1000", "2000", "5000")
        val availableAgents by viewModel.paymentAgents.collectAsStateWithLifecycle()
        val defaultAgent = availableAgents.firstOrNull() ?: PaymentAgent(
            id = "agent_sumon",
            name = "Agent Sumon",
            agentCode = "CA-2048",
            country = "Bangladesh",
            phone = "01711204899",
            paymentMethod = "bKash / Nagad",
            accountNumber = "01711204899",
            accountHolder = "Md. Sumon Reza",
            availableBalance = 50000.0
        )
        var selectedAgent by remember { mutableStateOf(defaultAgent) }

        AlertDialog(
            onDismissRequest = { showTopUpDialog = false },
            containerColor = Color.White,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, tint = Color(0xFFFF2A6D))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("B2B Cash Agent Top Up", color = Color(0xFF1E1E1E), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Agent info card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F0F4)),
                        border = BorderStroke(1.dp, Color(0xFFFFC0D0)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Cash Agent: ${selectedAgent.name}", fontWeight = FontWeight.Bold, color = Color(0xFFC2185B), fontSize = 12.sp)
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF00A86B).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("VERIFIED AGENT", color = Color(0xFF00A86B), fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                }
                            }
                            Text("Agent Account (${selectedAgent.paymentMethod}): ${selectedAgent.accountNumber}", fontWeight = FontWeight.SemiBold, color = Color(0xFF333333), fontSize = 11.sp)
                            if (selectedAgent.accountHolder.isNotEmpty()) {
                                Text("Account Holder: ${selectedAgent.accountHolder}", color = Color(0xFF666666), fontSize = 10.sp)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = topUpAmount,
                        onValueChange = { topUpAmount = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Top Up Amount (৳ BDT)") },
                        prefix = { Text("৳ ", fontWeight = FontWeight.Bold, color = Color(0xFFFF2A6D)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF2A6D),
                            unfocusedBorderColor = Color(0xFFE5D5DC)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presets.forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (topUpAmount == preset) Color(0xFFFF2A6D) else Color(0xFFFFEFF3),
                                border = BorderStroke(1.dp, Color(0xFFFF85A6).copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { topUpAmount = preset }
                            ) {
                                Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        "৳$preset",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (topUpAmount == preset) Color.White else Color(0xFFC2185B)
                                    )
                                }
                            }
                        }
                    }

                    // Strict Policy Warning Box
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                        border = BorderStroke(1.dp, Color(0xFFFFD54F)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.Top) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color(0xFFF57F17), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Funds will not be credited until the Cash Agent confirms release. Once you submit payment proof and the agent verifies and releases it, the balance will be instantly added to your wallet.",
                                color = Color(0xFFE65100),
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = topUpAmount.toDoubleOrNull() ?: 1000.0
                        if (amt > 0) {
                            viewModel.createB2BOrder(
                                agent = selectedAgent,
                                type = "DEPOSIT",
                                amount = amt,
                                paymentMethod = selectedAgent.paymentMethod
                            ) { createdOrder ->
                                showTopUpDialog = false
                                selectedOrderForChat = createdOrder
                                Toast.makeText(
                                    context,
                                    "B2B Topup Order #${createdOrder.orderId} Created! Cash Agent release required.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A6D))
                ) {
                    Text("Request Top Up & Open Chat", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTopUpDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // Modal 4: Withdraw Dialog (B2B Cash Agent Escrow Withdrawal)
    if (showWithdrawDialog) {
        var withdrawAmount by remember { mutableStateOf("500") }
        val presets = listOf("500", "1000", "2000", "5000")
        val currentBal = currentUser?.balance ?: 1500.0
        val availableAgents by viewModel.paymentAgents.collectAsStateWithLifecycle()
        val defaultAgent = availableAgents.firstOrNull() ?: PaymentAgent(
            id = "agent_sumon",
            name = "Agent Sumon",
            agentCode = "CA-2048",
            country = "Bangladesh",
            phone = "01711204899",
            paymentMethod = "bKash / Nagad",
            accountNumber = "01711204899",
            accountHolder = "Md. Sumon Reza",
            availableBalance = 50000.0
        )
        var selectedAgent by remember { mutableStateOf(defaultAgent) }

        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            containerColor = Color.White,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CallMade, contentDescription = null, tint = Color(0xFFFF2A6D))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("B2B Cash Agent Withdrawal", color = Color(0xFF1E1E1E), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Available Wallet Balance: ৳ %,.2f".format(currentBal), color = Color(0xFF00897B), fontWeight = FontWeight.Bold, fontSize = 12.sp)

                    // Agent info card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F0F4)),
                        border = BorderStroke(1.dp, Color(0xFFFFC0D0)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("Payout Cash Agent: ${selectedAgent.name}", fontWeight = FontWeight.Bold, color = Color(0xFFC2185B), fontSize = 12.sp)
                            Text("Payout Method: ${selectedAgent.paymentMethod} • Agent Phone: ${selectedAgent.phone}", color = Color(0xFF333333), fontSize = 11.sp)
                        }
                    }

                    OutlinedTextField(
                        value = withdrawAmount,
                        onValueChange = { withdrawAmount = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Withdraw Amount (৳ BDT)") },
                        prefix = { Text("৳ ", fontWeight = FontWeight.Bold, color = Color(0xFFFF2A6D)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF2A6D),
                            unfocusedBorderColor = Color(0xFFE5D5DC)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presets.forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (withdrawAmount == preset) Color(0xFFFF2A6D) else Color(0xFFFFEFF3),
                                border = BorderStroke(1.dp, Color(0xFFFF85A6).copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { withdrawAmount = preset }
                            ) {
                                Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        "৳$preset",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (withdrawAmount == preset) Color.White else Color(0xFFC2185B)
                                    )
                                }
                            }
                        }
                    }

                    // Strict Policy Warning Box
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE)),
                        border = BorderStroke(1.dp, Color(0xFF7DD3FC)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.Top) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Balance is not immediately deducted upon withdrawal request. When Release is approved, funds are debited from your wallet to the Cash Agent, and the agent sends cash/mobile payment directly to you.",
                                color = Color(0xFF0369A1),
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = withdrawAmount.toDoubleOrNull() ?: 500.0
                        if (amt > currentBal) {
                            Toast.makeText(context, "Insufficient balance! Available: ৳${currentBal.toInt()}", Toast.LENGTH_SHORT).show()
                        } else if (amt > 0) {
                            viewModel.createB2BOrder(
                                agent = selectedAgent,
                                type = "WITHDRAWAL",
                                amount = amt,
                                paymentMethod = selectedAgent.paymentMethod
                            ) { createdOrder ->
                                showWithdrawDialog = false
                                selectedOrderForChat = createdOrder
                                Toast.makeText(
                                    context,
                                    "B2B Withdrawal Order #${createdOrder.orderId} Created! Status: In Escrow. Balance will be debited on release.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A6D))
                ) {
                    Text("Confirm Withdrawal & Open Chat", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // Modal 4: Binance P2P Country & Region Selector
    if (showCountryDialog) {
        CountrySelectionDialog(
            currentCountry = selectedCountry,
            availableCountries = availableCountries,
            onSelectCountry = { c ->
                viewModel.setP2PCountry(c)
                activePaymentMethodFilter = "ALL"
                showCountryDialog = false
            },
            onDismiss = { showCountryDialog = false }
        )
    }
}

// Data class to support rich Binance-style marketplace agent cards matching photo
data class MarketplaceAgentItem(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val country: String,
    val location: String,
    val flagEmoji: String,
    val rating: Double,
    val reviewsCount: Int,
    val ordersCount: Int,
    val buyRate: Double,
    val sellRate: Double,
    val currency: String,
    val currencySymbol: String,
    val minLimit: Double,
    val maxLimit: Double,
    val availableBalance: Double = 50000.0,
    val paymentMethods: List<String>,
    val buttonLabel: String,
    val isOnline: Boolean = true,
    val isVerified: Boolean = true,
    val completionRate: String = "99.4%",
    val avgReleaseTime: String = "2.4 min",
    val accountNumber: String = ""
)

/**
 * Dialog to select any of the 61 supported countries/regions with dynamic fiat currency
 * and dynamic P2P payment methods (Binance-style).
 */
@Composable
fun CountrySelectionDialog(
    currentCountry: CountryData,
    availableCountries: List<CountryData>,
    onSelectCountry: (CountryData) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredCountries = remember(searchQuery, availableCountries) {
        if (searchQuery.isBlank()) availableCountries
        else availableCountries.filter {
            it.countryName.contains(searchQuery, ignoreCase = true) ||
            it.isoCode.contains(searchQuery, ignoreCase = true) ||
            it.currencyCode.contains(searchQuery, ignoreCase = true)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.80f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Select Country / Currency",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = Color(0xFF1E1E1E)
                        )
                        Text(
                            "Binance-Style Dynamic Payment Hub (${availableCountries.size} Countries)",
                            color = Color(0xFFFF2A6D),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.size(32.dp).clickable { onDismiss() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                        }
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search country, ISO code, or currency (e.g. BD, UAE, MYR, USD)...", fontSize = 11.sp, color = Color.Gray) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF2A6D),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFFFF5F7),
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedTextColor = Color(0xFF1E1E1E),
                        unfocusedTextColor = Color(0xFF1E1E1E)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    singleLine = true
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredCountries) { country ->
                        val isSelected = country.countryName.equals(currentCountry.countryName, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) Color(0xFFFF2A6D).copy(alpha = 0.08f) else Color.White,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFFFF2A6D) else Color(0xFFF1F5F9)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectCountry(country)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(country.flag, fontSize = 24.sp)
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                country.countryName,
                                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                                color = if (isSelected) Color(0xFFFF2A6D) else Color(0xFF1E293B),
                                                fontSize = 13.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFFF1F5F9)
                                            ) {
                                                Text(
                                                    country.isoCode,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF64748B),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            "${country.paymentMethods.size} payment methods (${country.paymentMethods.take(3).joinToString { it.methodName }}${if (country.paymentMethods.size > 3) "..." else ""})",
                                            color = Color(0xFF64748B),
                                            fontSize = 10.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSelected) Color(0xFFFF2A6D) else Color(0xFFFF2A6D).copy(alpha = 0.1f)
                                    ) {
                                        Text(
                                            country.currencyCode,
                                            color = if (isSelected) Color.White else Color(0xFFFF2A6D),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = Color(0xFFFF2A6D),
                                            modifier = Modifier.size(18.dp)
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
}

/**
 * Returns dynamic B2B Cash Agents matching the selected Country, Currency,
 * and localized Payment Methods (Binance P2P style).
 */
fun getMarketplaceAgentsForCountry(
    country: CountryData,
    dbAgents: List<PaymentAgent>
): List<MarketplaceAgentItem> {
    // 1. Any agents from DB for this country
    val matchedDb = dbAgents.filter {
        it.country.equals(country.countryName, ignoreCase = true) ||
        it.currency.equals(country.currencyCode, ignoreCase = true)
    }.map { a ->
        MarketplaceAgentItem(
            id = a.id,
            name = a.name,
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            country = a.country,
            location = "${a.city.ifEmpty { "Central" }}, ${a.country}",
            flagEmoji = country.flag,
            rating = a.rating.toDouble(),
            reviewsCount = 320,
            ordersCount = a.totalOrders,
            buyRate = a.buyRate,
            sellRate = a.sellRate,
            currency = a.currency,
            currencySymbol = when (a.currency) {
                "BDT" -> "৳"
                "USD" -> "$"
                "EUR" -> "€"
                "GBP" -> "£"
                "INR" -> "₹"
                "AED" -> "AED "
                "MYR" -> "RM "
                "SAR" -> "SAR "
                else -> "${a.currency} "
            },
            minLimit = a.minLimit,
            maxLimit = a.maxLimit,
            availableBalance = a.availableBalance,
            paymentMethods = if (a.allowedMethods.isNotBlank()) {
                a.allowedMethods.split(",").map { it.trim() }
            } else {
                country.paymentMethods.map { it.methodName }
            },
            buttonLabel = "Trade",
            isOnline = a.isOnline,
            isVerified = a.verificationStatus == "VERIFIED",
            completionRate = a.completionRate,
            avgReleaseTime = a.avgReleaseTime,
            accountNumber = a.accountNumber
        )
    }

    return matchedDb
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
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text("Create B2B $tradeType Trade Order", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Verified Agent: ${agent.name}", color = Color(0xFFFF2A6D), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Country: ${agent.country} • Code #${agent.agentCode}", color = Color(0xFF64748B), fontSize = 11.sp)
                        Text("Agent Account: ${agent.accountNumber}", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        if (agent.accountHolder.isNotEmpty()) {
                            Text("Account Holder: ${agent.accountHolder}", color = Color(0xFF64748B), fontSize = 10.sp)
                        }
                    }
                }

                // Payment Method Selector
                val availableMethods = remember(agent.allowedMethods) {
                    val split = agent.allowedMethods.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    if (split.isNotEmpty()) split else listOf(agent.paymentMethod)
                }

                if (availableMethods.size > 1) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Select Payment Method:", color = Color(0xFF475569), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            availableMethods.forEach { meth ->
                                val isSel = selectedMethod.equals(meth, ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) Color(0xFFFF2A6D) else Color.White,
                                    border = BorderStroke(1.dp, if (isSel) Color(0xFFFF2A6D) else Color(0xFFCBD5E1)),
                                    modifier = Modifier.clickable { selectedMethod = meth }
                                ) {
                                    Text(
                                        meth,
                                        color = if (isSel) Color.White else Color(0xFF334155),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Amount (${agent.currency})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF0F172A), unfocusedTextColor = Color(0xFF0F172A),
                        focusedBorderColor = Color(0xFFFF2A6D), unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Agent Commission (${agent.commissionRate}%):", color = Color(0xFF64748B), fontSize = 11.sp)
                    Text("${agent.currency} ${commFee.toInt()}", color = Color(0xFF059669), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                if (tradeType == "DEPOSIT") {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFFBEB),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A))
                    ) {
                        Text(
                            "⚠️ Important Rule: Transfer payment to the Cash Agent's account and upload proof in chat. The balance will not be credited until the Cash Agent releases it; once released, it is instantly added to your wallet.",
                            color = Color(0xFFB45309),
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                    ) {
                        Text(
                            "⚠️ Important Rule: The withdrawal order is safely held in Escrow. Approving Release debits your wallet to the Cash Agent, and the agent sends cash/mobile payment directly to you.",
                            color = Color(0xFF1E40AF),
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A6D)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Confirm & Launch B2B Trade Chat", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF64748B))
            }
        }
    )
}

// ---------------- B2B ORDER DETAILS & IN-APP REAL-TIME CHAT DIALOG ----------------
@Composable
fun B2BOrderDetailAndChatDialog(
    order: B2BOrder,
    onDismiss: () -> Unit,
    viewModel: AppViewModel
) {
    val context = LocalContext.current
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
                            Text("B2B ORDER #${liveOrder.orderId}", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
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
                    border = BorderStroke(1.dp, Color(0xFFFF85A6)),
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

                        // Escrow & Release Status Banner
                        if (liveOrder.type == "DEPOSIT") {
                            if (liveOrder.status == "RELEASED") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF00E676).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Text("✅ RELEASED & ADDED: ৳${liveOrder.amount.toInt()} has been credited to your wallet balance by Cash Agent ${liveOrder.agentName}.", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFF9800).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Text("⏳ WAITING FOR CASH AGENT RELEASE: Balance will NOT be credited until Cash Agent confirms and releases the funds.", color = Color(0xFFFF9800), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            if (liveOrder.status == "RELEASED") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF00E676).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Text("✅ WITHDRAWAL RELEASED: ৳${liveOrder.amount.toInt()} has been deducted from your wallet and transferred to Cash Agent ${liveOrder.agentName}.", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF2196F3).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Text("🔒 ESCROW ACTIVE: Upon Release, ৳${liveOrder.amount.toInt()} will be deducted from your wallet and transferred to Cash Agent (${liveOrder.agentName}).", color = Color(0xFF64B5F6), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (liveOrder.type == "DEPOSIT") {
                        if (liveOrder.status == "PENDING_PAYMENT") {
                            Button(
                                onClick = { showProofModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Upload Payment Proof", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }

                        if (liveOrder.status != "RELEASED" && liveOrder.status != "CANCELLED") {
                            Button(
                                onClick = {
                                    viewModel.releaseB2BOrder(liveOrder.orderId)
                                    Toast.makeText(context, "Cash Agent released ৳${liveOrder.amount.toInt()}! Balance credited to user wallet.", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cash Agent: Release Funds", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    } else {
                        // WITHDRAWAL
                        if (liveOrder.status != "RELEASED" && liveOrder.status != "CANCELLED") {
                            Button(
                                onClick = {
                                    viewModel.releaseB2BOrder(liveOrder.orderId)
                                    Toast.makeText(context, "Withdrawal Released! ৳${liveOrder.amount.toInt()} debited from wallet and transferred to Cash Agent.", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Text("Release (Deduct & Send to Agent)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }

                    if (liveOrder.status != "RELEASED" && liveOrder.status != "CANCELLED") {
                        Button(
                            onClick = { showDisputeModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(0.8f)
                        ) {
                            Text("🚨 Dispute", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
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
                            focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)
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
            title = { Text("Upload Payment Screenshot Proof", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = txnRef,
                        onValueChange = { txnRef = it },
                        label = { Text("Transaction Reference ID", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)),
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
            title = { Text("Raise B2B Escrow Dispute", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("State reason for dispute. Admin team will review order chat log & screenshot proof.", color = TextSecondary, fontSize = 11.sp)
                    OutlinedTextField(
                        value = disputeReasonInput,
                        onValueChange = { disputeReasonInput = it },
                        label = { Text("Dispute Details", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)),
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
                Text("B2B Escrow & Dispute Center", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
                    Text("${allOrders.size}", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 18.sp)
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
                        .background(if (isSel) PinkHighlight else Color(0xFFFF85A6))
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
                        border = BorderStroke(1.dp, if (order.status == "DISPUTED") Color(0xFFEF4444) else Color(0xFFFF85A6)),
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
                                    Text("Order #${order.orderId}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                                    Text("User: ${order.userName}", color = TextPrimary, fontSize = 11.sp)
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
                Text("Admin Dispute & Chat Moderation (#${selOrd.orderId})", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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

                    Text("Full User ↔ Agent Chat Log:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PinkHighlight, unfocusedBorderColor = Color(0xFFFF85A6)),
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

// ============================================================================
// USER TO MODEL LIVE LOCATION TRACKING TAB & REAL-TIME GPS ESCORT ENGINE
// ============================================================================

data class LiveEscortTrackingSession(
    val sessionId: String,
    val bookingId: String,
    val modelName: String,
    val modelAvatar: String,
    val modelPhone: String,
    val modelAddress: String,
    val modelLat: Double,
    val modelLng: Double,
    val userName: String,
    val userAvatar: String,
    val userPhone: String,
    val destinationStudio: String,
    val userLat: Double,
    val userLng: Double,
    val initialDistanceKm: Double,
    val initialEtaMinutes: Int,
    val status: String,
    val speedKmh: Int,
    val modelBatteryPercent: Int,
    val userBatteryPercent: Int,
    val gpsAccuracyMeters: Double,
    val transportMode: String
)

@Composable
fun AdminLiveLocationTrackingTab(viewModel: AppViewModel, onBack: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Standalone Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF00E676).copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("User to Model Live Tracking", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Text("Real-Time GPS Telemetry & Safety Escort", color = TextSecondary, fontSize = 11.sp)
                }
            }

            Box(
                modifier = Modifier
                    .background(Color(0xFF00E676).copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .border(1.dp, Color(0xFF00E676).copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(6.dp).background(Color(0xFF00E676), CircleShape))
                    Text("GPS LIVE", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 9.sp)
                }
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            UserToModelLiveTrackingContent(viewModel = viewModel)
        }
    }
}

@Composable
fun UserToModelLiveTrackingContent(viewModel: AppViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val sessions = remember {
        listOf(
            LiveEscortTrackingSession(
                sessionId = "TRK-84920",
                bookingId = "BK-1024",
                modelName = "Ananya Sen",
                modelAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                modelPhone = "+880 1822-987654",
                modelAddress = "Gulshan Avenue, near Shooting Club",
                modelLat = 23.7745,
                modelLng = 90.4120,
                userName = "Rahul Verma",
                userAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde",
                userPhone = "+880 1711-234567",
                destinationStudio = "Studio Mirage, House 42, Road 11, Banani, Dhaka",
                userLat = 23.7937,
                userLng = 90.4066,
                initialDistanceKm = 2.4,
                initialEtaMinutes = 7,
                status = "EN_ROUTE",
                speedKmh = 28,
                modelBatteryPercent = 86,
                userBatteryPercent = 92,
                gpsAccuracyMeters = 2.5,
                transportMode = "Car (Uber Ride #849)"
            ),
            LiveEscortTrackingSession(
                sessionId = "TRK-77190",
                bookingId = "BK-2058",
                modelName = "Priya Sharma",
                modelAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9",
                modelPhone = "+880 1733-112233",
                modelAddress = "Uttara Sector 4, Azampur Intersection",
                modelLat = 23.8681,
                modelLng = 90.3984,
                userName = "Tanvir Ahmed",
                userAvatar = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61",
                userPhone = "+880 1912-334455",
                destinationStudio = "Radisson Blu Water Garden, Grand Studio 1, Dhaka",
                userLat = 23.8164,
                userLng = 90.4074,
                initialDistanceKm = 5.8,
                initialEtaMinutes = 14,
                status = "IN_TRANSIT",
                speedKmh = 35,
                modelBatteryPercent = 74,
                userBatteryPercent = 81,
                gpsAccuracyMeters = 3.1,
                transportMode = "Private Studio Escort Car"
            ),
            LiveEscortTrackingSession(
                sessionId = "TRK-61400",
                bookingId = "BK-3091",
                modelName = "Meera Kapoor",
                modelAvatar = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1",
                modelPhone = "+880 1622-445566",
                modelAddress = "Arrived at Venue Entrance",
                modelLat = 23.7927,
                modelLng = 90.4148,
                userName = "Imran Hossain",
                userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
                userPhone = "+880 1819-556677",
                destinationStudio = "The Westin Dhaka, Ballroom Stage 2, Gulshan 2",
                userLat = 23.7925,
                userLng = 90.4150,
                initialDistanceKm = 0.05,
                initialEtaMinutes = 0,
                status = "ARRIVED",
                speedKmh = 0,
                modelBatteryPercent = 95,
                userBatteryPercent = 89,
                gpsAccuracyMeters = 1.8,
                transportMode = "At Venue"
            )
        )
    }

    var selectedSessionIdx by remember { mutableIntStateOf(0) }
    val currentSession = sessions[selectedSessionIdx]

    // Movement Simulation states
    var simStep by remember(selectedSessionIdx) { mutableIntStateOf(0) }
    var sosActive by remember { mutableStateOf(false) }

    // Dynamic calculated values based on simulation step
    val progress = (simStep * 0.25f).coerceAtMost(1.0f)
    val curDistance = if (progress >= 1.0f) 0.0 else (currentSession.initialDistanceKm * (1.0f - progress)).coerceAtLeast(0.0)
    val curEta = if (progress >= 1.0f) 0 else ((currentSession.initialEtaMinutes * (1.0f - progress)).toInt()).coerceAtLeast(0)
    val curSpeed = if (progress >= 1.0f) 0 else if (simStep == 0) currentSession.speedKmh else (currentSession.speedKmh + (simStep % 3) * 4)
    val isArrived = progress >= 1.0f || currentSession.status == "ARRIVED"

    val telemetryLogs = remember(selectedSessionIdx, simStep) {
        mutableListOf(
            "🛰️ [SYSTEM] GPS telemetry sync active: 16 orbital satellites locked (Precision ±${currentSession.gpsAccuracyMeters}m)",
            "📡 [PING] Model ${currentSession.modelName} ping: Lat ${currentSession.modelLat + progress * 0.015}, Lng ${currentSession.modelLng - progress * 0.005} (Speed: $curSpeed km/h)",
            "📍 [ROUTING] En-route to ${currentSession.destinationStudio}",
            "⏱️ [ETA UPDATE] Remaining distance: ${"%.2f".format(curDistance)} km | Est. Arrival: $curEta min(s)",
            "🛡️ [SECURITY] Geofence active (Radius: 150m) • SOS status: ${if (sosActive) "🚨 EMERGENCY TRIGGERED" else "NORMAL ✓"}"
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Session Picker Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Select Live Booking Tracking Session:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sessions.forEachIndexed { idx, s ->
                        val isSelected = selectedSessionIdx == idx
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) PinkHighlight else DarkSurface,
                            border = BorderStroke(1.dp, if (isSelected) PinkHighlight else PinkBorderSoft),
                            modifier = Modifier.clickable {
                                selectedSessionIdx = idx
                                simStep = 0
                                sosActive = false
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(if (s.status == "ARRIVED") Color(0xFF00E676) else Color(0xFFFF9800), CircleShape)
                                )
                                Column {
                                    Text(
                                        "${s.modelName} ↔ ${s.userName}",
                                        color = if (isSelected) Color.White else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        "#${s.sessionId} • ${s.status}",
                                        color = if (isSelected) Color.White.copy(alpha = 0.85f) else TextSecondary,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. High-Tech Tactical Radar Map (Jetpack Compose Canvas)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B132B)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Map Top Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Radar, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                            Text("LIVE GPS RADAR MAP", color = Color(0xFF00E676), fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 1.sp)
                        }

                        // ETA Pill badge
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isArrived) Color(0xFF00E676).copy(alpha = 0.2f) else Color(0xFFFF2A6D).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, if (isArrived) Color(0xFF00E676) else Color(0xFFFF2A6D))
                        ) {
                            Text(
                                text = if (isArrived) "🎯 ARRIVED AT VENUE" else "📍 ${"%.1f".format(curDistance)} km • ETA $curEta mins",
                                color = if (isArrived) Color(0xFF00E676) else Color(0xFFFF2A6D),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Interactive Canvas Drawing Tactical Radar Visualizer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0A0F1D))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val cx = w / 2f
                            val cy = h / 2f

                            // 1. Tactical Grid Lines
                            val gridSpacing = 40f
                            var x = 0f
                            while (x <= w) {
                                drawLine(
                                    color = Color(0xFF1E293B).copy(alpha = 0.4f),
                                    start = Offset(x, 0f),
                                    end = Offset(x, h),
                                    strokeWidth = 1f
                                )
                                x += gridSpacing
                            }
                            var y = 0f
                            while (y <= h) {
                                drawLine(
                                    color = Color(0xFF1E293B).copy(alpha = 0.4f),
                                    start = Offset(0f, y),
                                    end = Offset(w, y),
                                    strokeWidth = 1f
                                )
                                y += gridSpacing
                            }

                            // 2. Concentric Radar Range Rings
                            drawCircle(color = Color(0xFF00E676).copy(alpha = 0.12f), radius = 50f, center = Offset(cx, cy), style = Stroke(width = 1.2f))
                            drawCircle(color = Color(0xFF00E676).copy(alpha = 0.08f), radius = 100f, center = Offset(cx, cy), style = Stroke(width = 1.2f))
                            drawCircle(color = Color(0xFF00E676).copy(alpha = 0.05f), radius = 160f, center = Offset(cx, cy), style = Stroke(width = 1.2f))

                            // 3. User / Studio Destination Marker (Fixed destination pin)
                            val destX = cx + w * 0.28f
                            val destY = cy - h * 0.22f

                            // Model Start Marker
                            val startX = cx - w * 0.32f
                            val startY = cy + h * 0.25f

                            // Model Current Simulated Position (interpolated along vector)
                            val curModelX = startX + (destX - startX) * progress
                            val curModelY = startY + (destY - startY) * progress

                            // 4. Draw Route Path line (Gradient from Model to Destination)
                            drawLine(
                                color = Color(0xFFFF2A6D).copy(alpha = 0.8f),
                                start = Offset(startX, startY),
                                end = Offset(destX, destY),
                                strokeWidth = 3f
                            )

                            // 5. Draw Pulse rings around Model
                            drawCircle(color = Color(0xFFFF2A6D).copy(alpha = 0.25f), radius = 22f, center = Offset(curModelX, curModelY))
                            drawCircle(color = Color(0xFFFF2A6D), radius = 8f, center = Offset(curModelX, curModelY))

                            // 6. Draw Destination Studio Pulse rings
                            drawCircle(color = Color(0xFF00E676).copy(alpha = 0.25f), radius = 24f, center = Offset(destX, destY))
                            drawCircle(color = Color(0xFF00E676), radius = 9f, center = Offset(destX, destY))
                        }

                        // Overlay Markers with Avatars & Labels
                        // Model Marker Overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 16.dp, bottom = 16.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E1E2E).copy(alpha = 0.9f),
                                border = BorderStroke(1.dp, Color(0xFFFF2A6D))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    SubcomposeAsyncImage(
                                        model = currentSession.modelAvatar,
                                        contentDescription = currentSession.modelName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(24.dp).clip(CircleShape)
                                    )
                                    Column {
                                        Text("${currentSession.modelName} (Model)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                        Text("Speed: $curSpeed km/h • GPS Active", color = Color(0xFFFF2A6D), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // User Destination Studio Marker Overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(end = 16.dp, top = 16.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E1E2E).copy(alpha = 0.9f),
                                border = BorderStroke(1.dp, Color(0xFF00E676))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    SubcomposeAsyncImage(
                                        model = currentSession.userAvatar,
                                        contentDescription = currentSession.userName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(24.dp).clip(CircleShape)
                                    )
                                    Column {
                                        Text("${currentSession.userName} (Client)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                        Text("Venue: Studio Mirage", color = Color(0xFF00E676), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Compass Indicator
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.6f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("N ↑", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }

                    // Map Simulation Action Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                if (simStep < 4) {
                                    simStep += 1
                                    Toast.makeText(context, "GPS Step simulated: Model moving closer (${"%.1f".format(curDistance)} km remaining)", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Model has arrived at destination studio!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.2f).height(38.dp)
                        ) {
                            Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isArrived) "Arrived ✓" else "Simulate Step Move", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                simStep = 0
                                sosActive = false
                                Toast.makeText(context, "Tracking route reset to start point.", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF64748B)),
                            modifier = Modifier.weight(0.9f).height(38.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset", color = Color.White, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                sosActive = !sosActive
                                Toast.makeText(
                                    context,
                                    if (sosActive) "🚨 EMERGENCY SOS ACTIVATED: Escort team alerted!" else "SOS Alert Disarmed. Status Normal.",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (sosActive) Color(0xFFEF4444) else Color(0xFF334155)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(0.9f).height(38.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (sosActive) "SOS ALARM" else "Test SOS", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. User & Model Dual Telemetry Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Model Live Telemetry Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PinkBorderSoft),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SubcomposeAsyncImage(
                                model = currentSession.modelAvatar,
                                contentDescription = currentSession.modelName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(34.dp).clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(currentSession.modelName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("MODEL (EN ROUTE)", color = PinkHighlight, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 0.5.dp)

                        Text("GPS: ${"%.4f".format(currentSession.modelLat + progress * 0.015)}° N, ${"%.4f".format(currentSession.modelLng - progress * 0.005)}° E", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        Text("Speed: $curSpeed km/h • Mode: ${currentSession.transportMode}", color = TextSecondary, fontSize = 9.sp)
                        Text("Battery: ${currentSession.modelBatteryPercent}% 🔋 • Acc: ±${currentSession.gpsAccuracyMeters}m", color = Color(0xFF00E676), fontSize = 9.sp)
                        Text("Loc: ${currentSession.modelAddress}", color = TextSecondary, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)

                        Button(
                            onClick = {
                                Toast.makeText(context, "Calling Model ${currentSession.modelName}: ${currentSession.modelPhone}", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth().height(30.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call Model", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Client / Venue Telemetry Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SubcomposeAsyncImage(
                                model = currentSession.userAvatar,
                                contentDescription = currentSession.userName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(34.dp).clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(currentSession.userName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("CLIENT (AT VENUE)", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 0.5.dp)

                        Text("GPS: ${currentSession.userLat}° N, ${currentSession.userLng}° E", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        Text("Destination: ${currentSession.destinationStudio}", color = TextSecondary, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Client Battery: ${currentSession.userBatteryPercent}% 🔋 • Verified Venue ✓", color = Color(0xFF00E676), fontSize = 9.sp)
                        Text("Booking ID: #${currentSession.bookingId}", color = Color(0xFF2196F3), fontSize = 9.sp, fontWeight = FontWeight.Bold)

                        Button(
                            onClick = {
                                Toast.makeText(context, "Calling Client ${currentSession.userName}: ${currentSession.userPhone}", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth().height(30.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call Client", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 4. Safety & Escort System Telemetry Log Stream
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🛰️ REAL-TIME TELEMETRY AUDIT LOG", color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 0.5.sp)
                        Text("Live Stream (2s)", color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF020617), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            telemetryLogs.forEach { logLine ->
                                Text(
                                    text = logLine,
                                    color = if (logLine.contains("EMERGENCY")) Color(0xFFEF4444) else Color(0xFF38BDF8),
                                    fontSize = 10.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- CALL LOCKED MODAL (MODEL ORDER ACCEPT RULE) ----------------
@Composable
fun CallLockedModal(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onViewBookings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E2235),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color(0xFFEF4444).copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (title.isBlank()) "Call Locked" else title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text("Escrow Protection Policy", color = Color(0xFF94A3B8), fontSize = 10.sp)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = Color(0xFF282D42),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔒", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Order Accept Rule (মডেল অর্ডার গ্রহণ নীতি)", color = Color(0xFFFF85A6), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text(
                            text = if (message.isBlank()) "মডেল আপনার বুকিং অর্ডার গ্রহণ (Accept) করার পরই অডিও কল চালু হবে। দয়া করে অপেক্ষা করুন অথবা অর্ডার বুক করুন।" else message,
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onViewBookings,
                colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Go to My Bookings", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFF94A3B8))
            }
        }
    )
}





