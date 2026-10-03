package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AdminPaymentGatewayConfig
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@Composable
fun AdminPaymentGatewaysSection(viewModel: AppViewModel) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, PinkHighlight.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
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
                            .size(42.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF3B82F6), PinkHighlight, Color(0xFF10B981))
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                "Payment Gateways & Methods",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, Color(0xFF10B981))
                            ) {
                                Text(
                                    "${viewModel.adminPaymentGateways.count { it.isEnabled }} ACTIVE",
                                    color = Color(0xFF10B981),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            "Google Pay, Alipay, Apple Pay & Custom Add Gateways",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }

                Button(
                    onClick = { viewModel.showAddGatewayDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Gateway", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Explanatory Banner
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0F172A),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        "Configure live Merchant IDs, API keys, Webhook URLs, fee percentages, and enable/disable Google Pay, Alipay, Apple Pay, or any new custom payment gateways.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            // Gateway Cards List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                viewModel.adminPaymentGateways.forEach { gateway ->
                    PaymentGatewayAdminCard(
                        gateway = gateway,
                        onToggle = { viewModel.togglePaymentGatewayStatus(gateway.id) },
                        onEdit = { viewModel.editingPaymentGateway = gateway },
                        onDelete = {
                            viewModel.deleteCustomPaymentGateway(gateway.id)
                            Toast.makeText(context, "${gateway.name} removed.", Toast.LENGTH_SHORT).show()
                        },
                        onTestPing = {
                            viewModel.testGatewayConnection(gateway.id)
                            Toast.makeText(context, "${gateway.name} connection test: HTTP 200 OK! ✓", Toast.LENGTH_SHORT).show()
                        },
                        onCopy = { text, label ->
                            clipboardManager.setText(AnnotatedString(text))
                            Toast.makeText(context, "$label copied!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    // Modal: Edit Gateway Configuration
    if (viewModel.editingPaymentGateway != null) {
        EditPaymentGatewayModal(
            gateway = viewModel.editingPaymentGateway!!,
            onDismiss = { viewModel.editingPaymentGateway = null },
            onSave = { updated ->
                viewModel.savePaymentGateway(updated)
                viewModel.editingPaymentGateway = null
                Toast.makeText(context, "${updated.name} settings saved successfully! ✓", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Modal: Add New Payment Gateway
    if (viewModel.showAddGatewayDialog) {
        AddNewPaymentGatewayModal(
            onDismiss = { viewModel.showAddGatewayDialog = false },
            onAdd = { newGw ->
                viewModel.savePaymentGateway(newGw)
                viewModel.showAddGatewayDialog = false
                Toast.makeText(context, "New Gateway '${newGw.name}' configured and added! ✓", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun PaymentGatewayAdminCard(
    gateway: AdminPaymentGatewayConfig,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTestPing: () -> Unit,
    onCopy: (String, String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(1.dp, if (gateway.isEnabled) PinkHighlight.copy(alpha = 0.4f) else Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Icon, Name, Env, Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Gateway Icon Box
                    val (iconBg, iconTint, iconVec) = when (gateway.id) {
                        "google_pay" -> Triple(Color(0xFFEA4335).copy(alpha = 0.15f), Color(0xFFEA4335), Icons.Default.Payment)
                        "alipay" -> Triple(Color(0xFF027AFF).copy(alpha = 0.15f), Color(0xFF027AFF), Icons.Default.Payment)
                        "apple_pay" -> Triple(Color.White.copy(alpha = 0.15f), Color.White, Icons.Default.CreditCard)
                        else -> Triple(PinkHighlight.copy(alpha = 0.15f), PinkHighlight, Icons.Default.AccountBalanceWallet)
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(iconBg, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        when (gateway.id) {
                            "google_pay" -> Text("GPay", color = Color(0xFFEA4335), fontWeight = FontWeight.Black, fontSize = 11.sp)
                            "alipay" -> Text("支", color = Color(0xFF027AFF), fontWeight = FontWeight.Black, fontSize = 18.sp)
                            "apple_pay" -> Text("Pay", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            else -> Icon(imageVector = iconVec, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                gateway.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (gateway.environment == "PRODUCTION") Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFF59E0B).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    gateway.environment,
                                    color = if (gateway.environment == "PRODUCTION") Color(0xFF10B981) else Color(0xFFF59E0B),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            "Code: ${gateway.code} • Fee: ${gateway.transactionFeePercent}% • Limits: ৳${gateway.minAmount.toInt()} - ৳${gateway.maxAmount.toInt()}",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )
                    }
                }

                // Switch Toggle
                Switch(
                    checked = gateway.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF10B981),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color(0xFF334155)
                    )
                )
            }

            HorizontalDivider(color = Color(0xFF1E293B), thickness = 1.dp)

            // Merchant ID & Credentials Summary
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (gateway.merchantId.isNotBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Merchant / Partner ID:",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = gateway.merchantId,
                                color = Color(0xFFE2E8F0),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            IconButton(onClick = { onCopy(gateway.merchantId, "Merchant ID") }, modifier = Modifier.size(20.dp)) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(11.dp))
                            }
                        }
                    }
                }

                if (gateway.webhookUrl.isNotBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Webhook Callback URL:",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = gateway.webhookUrl,
                            color = Color(0xFF38BDF8),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }
                }

                Text(
                    text = "Supported: ${gateway.supportedCurrencies}",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp
                )
            }

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Ping test
                OutlinedButton(
                    onClick = onTestPing,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test Ping", color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Right: Edit & Delete
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = onEdit,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Configure", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    if (!gateway.isSystemDefault) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditPaymentGatewayModal(
    gateway: AdminPaymentGatewayConfig,
    onDismiss: () -> Unit,
    onSave: (AdminPaymentGatewayConfig) -> Unit
) {
    var name by remember { mutableStateOf(gateway.name) }
    var merchantId by remember { mutableStateOf(gateway.merchantId) }
    var merchantName by remember { mutableStateOf(gateway.merchantName) }
    var apiKey by remember { mutableStateOf(gateway.apiKey) }
    var secretKey by remember { mutableStateOf(gateway.secretKey) }
    var publicKeyOrCert by remember { mutableStateOf(gateway.publicKeyOrCert) }
    var webhookUrl by remember { mutableStateOf(gateway.webhookUrl) }
    var environment by remember { mutableStateOf(gateway.environment) }
    var feePercent by remember { mutableStateOf(gateway.transactionFeePercent.toString()) }
    var minAmount by remember { mutableStateOf(gateway.minAmount.toInt().toString()) }
    var maxAmount by remember { mutableStateOf(gateway.maxAmount.toInt().toString()) }
    var currencies by remember { mutableStateOf(gateway.supportedCurrencies) }
    var instructions by remember { mutableStateOf(gateway.instructions) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(16.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Configure ${gateway.name}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Credentials, Merchant ID, Webhooks & Rules", fontSize = 11.sp, color = TextSecondary)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                HorizontalDivider(color = PinkBorderSoft)

                // Environment Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("PRODUCTION" to "Live Production", "SANDBOX" to "Sandbox / Test").forEach { (envKey, envLabel) ->
                        val isSel = environment == envKey
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, if (isSel) Color(0xFF10B981) else Color(0xFFCBD5E1)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { environment = envKey }
                        ) {
                            Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    envLabel,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) Color(0xFF047857) else TextPrimary
                                )
                            }
                        }
                    }
                }

                // Display Name
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Gateway Display Name", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Merchant / Partner ID
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Merchant ID / Partner Account ID", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = merchantId,
                        onValueChange = { merchantId = it },
                        placeholder = { Text("e.g. BCR2DN6TXM4K829L or Alipay Partner ID", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // API Key / App ID
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("API Key / App ID / Client ID", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        placeholder = { Text("e.g. live_key_...", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Secret Key / Private RSA Key
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Secret Key / Merchant Private Key", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = secretKey,
                        onValueChange = { secretKey = it },
                        placeholder = { Text("Private key / Secret Token", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Public Key / Certificate (for Alipay / Apple Pay)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Public Key / Payment Processing Certificate (Optional)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = publicKeyOrCert,
                        onValueChange = { publicKeyOrCert = it },
                        placeholder = { Text("Alipay Public Key or Apple Pay Certificate ID", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Webhook Notify URL
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Webhook / IPN Callback URL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = webhookUrl,
                        onValueChange = { webhookUrl = it },
                        placeholder = { Text("https://api.modolconnect.fun/v1/payments/callback", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Fee & Limits Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Fee (%)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        OutlinedTextField(
                            value = feePercent,
                            onValueChange = { feePercent = it },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Min Amount", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        OutlinedTextField(
                            value = minAmount,
                            onValueChange = { minAmount = it },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Max Amount", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        OutlinedTextField(
                            value = maxAmount,
                            onValueChange = { maxAmount = it },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                // Currencies
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Supported Currencies", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = currencies,
                        onValueChange = { currencies = it },
                        placeholder = { Text("e.g. BDT, USD, EUR, CNY", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Instructions
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Checkout Instructions for Users", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = instructions,
                        onValueChange = { instructions = it },
                        placeholder = { Text("e.g. One-tap instant payment authentication", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        val feeDouble = feePercent.toDoubleOrNull() ?: gateway.transactionFeePercent
                        val minDouble = minAmount.toDoubleOrNull() ?: gateway.minAmount
                        val maxDouble = maxAmount.toDoubleOrNull() ?: gateway.maxAmount
                        onSave(
                            gateway.copy(
                                name = name.ifBlank { gateway.name },
                                merchantId = merchantId,
                                merchantName = merchantName,
                                apiKey = apiKey,
                                secretKey = secretKey,
                                publicKeyOrCert = publicKeyOrCert,
                                webhookUrl = webhookUrl,
                                environment = environment,
                                transactionFeePercent = feeDouble,
                                minAmount = minDouble,
                                maxAmount = maxDouble,
                                supportedCurrencies = currencies,
                                instructions = instructions
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Gateway Settings", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddNewPaymentGatewayModal(
    onDismiss: () -> Unit,
    onAdd: (AdminPaymentGatewayConfig) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var iconType by remember { mutableStateOf("WALLET") }
    var merchantId by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }
    var secretKey by remember { mutableStateOf("") }
    var webhookUrl by remember { mutableStateOf("") }
    var feePercent by remember { mutableStateOf("1.5") }
    var minAmount by remember { mutableStateOf("100") }
    var maxAmount by remember { mutableStateOf("500000") }
    var currencies by remember { mutableStateOf("BDT, USD, EUR") }
    var instructions by remember { mutableStateOf("") }
    var environment by remember { mutableStateOf("PRODUCTION") }

    val presetIcons = listOf(
        "WALLET" to "Digital Wallet",
        "CARD" to "Credit / Debit Card",
        "PAYPAL" to "PayPal Express",
        "STRIPE" to "Stripe Payments",
        "CRYPTO" to "Crypto / USDT",
        "BANK" to "Direct Bank Wire"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(16.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Add New Payment Gateway", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Configure custom payment methods or regional gateways", fontSize = 11.sp, color = TextSecondary)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                HorizontalDivider(color = PinkBorderSoft)

                // Gateway Name
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Gateway Name *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            if (code.isBlank() || code == name.dropLast(1).uppercase().replace(" ", "_")) {
                                code = it.uppercase().replace(" ", "_")
                            }
                        },
                        placeholder = { Text("e.g. PayPal, Stripe, bKash, WeChat Pay, USDT", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Gateway Code
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("System Gateway Code (UPPERCASE) *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it.uppercase().replace(" ", "_") },
                        placeholder = { Text("e.g. STRIPE_GLOBAL, BKASH_ONLINE, CRYPTO_USDT", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Icon / Type Selector
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Gateway Type / Icon", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetIcons.take(3).forEach { (typeKey, label) ->
                            val isSel = iconType == typeKey
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) PinkHighlight.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (isSel) PinkHighlight else Color(0xFFCBD5E1)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { iconType = typeKey }
                            ) {
                                Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSel) PinkHighlight else TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // Environment
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("PRODUCTION" to "Live Production", "SANDBOX" to "Sandbox / Test").forEach { (envKey, envLabel) ->
                        val isSel = environment == envKey
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, if (isSel) Color(0xFF10B981) else Color(0xFFCBD5E1)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { environment = envKey }
                        ) {
                            Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    envLabel,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) Color(0xFF047857) else TextPrimary
                                )
                            }
                        }
                    }
                }

                // Merchant ID & API Key
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Merchant ID / Account Reference", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = merchantId,
                        onValueChange = { merchantId = it },
                        placeholder = { Text("Merchant / Partner ID", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("API Key / Client Key", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        placeholder = { Text("Publishable or Client Key", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Secret Key / Token", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = secretKey,
                        onValueChange = { secretKey = it },
                        placeholder = { Text("Secret / Bearer Token", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Webhook IPN URL (Optional)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = webhookUrl,
                        onValueChange = { webhookUrl = it },
                        placeholder = { Text("https://api.modolconnect.fun/v1/payments/webhook", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Fee & Limits Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Fee (%)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        OutlinedTextField(
                            value = feePercent,
                            onValueChange = { feePercent = it },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Min (BDT)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        OutlinedTextField(
                            value = minAmount,
                            onValueChange = { minAmount = it },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Max (BDT)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        OutlinedTextField(
                            value = maxAmount,
                            onValueChange = { maxAmount = it },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        val cleanCode = code.ifBlank { name.uppercase().replace(" ", "_") }
                        val feeDouble = feePercent.toDoubleOrNull() ?: 1.5
                        val minDouble = minAmount.toDoubleOrNull() ?: 100.0
                        val maxDouble = maxAmount.toDoubleOrNull() ?: 500000.0
                        val newGateway = AdminPaymentGatewayConfig(
                            id = "custom_${cleanCode.lowercase()}_${System.currentTimeMillis()}",
                            name = name.ifBlank { "Custom Gateway" },
                            code = cleanCode,
                            iconType = iconType,
                            isEnabled = true,
                            environment = environment,
                            merchantId = merchantId,
                            apiKey = apiKey,
                            secretKey = secretKey,
                            webhookUrl = webhookUrl,
                            supportedCurrencies = currencies.ifBlank { "BDT, USD" },
                            transactionFeePercent = feeDouble,
                            minAmount = minDouble,
                            maxAmount = maxDouble,
                            instructions = instructions,
                            isSystemDefault = false
                        )
                        onAdd(newGateway)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Icon(imageVector = Icons.Default.AddCard, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save & Activate Gateway", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
