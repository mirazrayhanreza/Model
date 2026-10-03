package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.data.ApiEndpointDoc
import com.example.data.GeneratedApiKey
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@Composable
fun AdminApiGeneratorTab(viewModel: AppViewModel) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedSubTab by remember { mutableStateOf(0) } // 0: Keys, 1: Endpoints, 2: Code Snippets, 3: Sandbox, 4: Webhooks
    var showCreateKeyModal by remember { mutableStateOf(false) }

    // Dialog form states
    var newKeyName by remember { mutableStateOf("") }
    var newKeyType by remember { mutableStateOf("WEBSITE") }
    var newKeyEnv by remember { mutableStateOf("PRODUCTION") }
    var newKeyWebhook by remember { mutableStateOf("") }
    var newKeyRateLimit by remember { mutableStateOf("120") }
    var newKeyIpWhitelist by remember { mutableStateOf("0.0.0.0/0") }
    val availableScopes = listOf(
        "auth.otp" to "OTP Dispatch & Verification",
        "users.read" to "Read User Profiles & Status",
        "models.read" to "Fetch Models Directory & Rates",
        "bookings.create" to "Create Escrow Bookings",
        "agent.cash" to "B2B Cash Agent Deposit / P2P",
        "escrow.release" to "Release Escrow Funds",
        "webhooks.listen" to "Receive Real-time Event Callbacks"
    )
    val selectedScopes = remember { mutableStateListOf("auth.otp", "users.read", "models.read", "agent.cash") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Top Header
        Surface(
            color = Color(0xFF0F172A),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
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
                                .size(40.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6), PinkHighlight)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = "API Generator",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Backend API Generator",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF10B981).copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, Color(0xFF10B981))
                                ) {
                                    Text(
                                        text = "LIVE v1.0",
                                        color = Color(0xFF10B981),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "REST APIs for Website, Partner Apps & Cash Agents",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            newKeyName = ""
                            newKeyWebhook = ""
                            showCreateKeyModal = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New API Key", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Base URL Quick Banner
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "BASE URL:",
                                color = Color(0xFF38BDF8),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = viewModel.apiBaseUrl,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF334155),
                            modifier = Modifier.clickable {
                                clipboardManager.setText(AnnotatedString(viewModel.apiBaseUrl))
                                Toast.makeText(context, "API Base URL copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text("Copy", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Sub-tabs navigation
        val subTabs = listOf(
            Triple(0, "🔑 API Keys (${viewModel.generatedApiKeys.size})", Icons.Default.VpnKey),
            Triple(1, "🌐 Endpoints (10)", Icons.Default.Api),
            Triple(2, "💻 Code Snippets", Icons.Default.IntegrationInstructions),
            Triple(3, "⚡ Test Sandbox", Icons.Default.PlayArrow),
            Triple(4, "🔔 Webhooks", Icons.Default.NotificationsActive)
        )

        ScrollableTabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = Color.White,
            contentColor = PinkHighlight,
            edgePadding = 12.dp,
            divider = { HorizontalDivider(color = PinkBorderSoft, thickness = 1.dp) }
        ) {
            subTabs.forEach { (idx, label, icon) ->
                Tab(
                    selected = selectedSubTab == idx,
                    onClick = { selectedSubTab = idx },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(14.dp))
                            Text(label, fontSize = 12.sp, fontWeight = if (selectedSubTab == idx) FontWeight.Bold else FontWeight.Medium)
                        }
                    }
                )
            }
        }

        // Main Tab Content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedSubTab) {
                0 -> ApiKeysManagerSubTab(viewModel = viewModel, onOpenCreateModal = { showCreateKeyModal = true })
                1 -> EndpointsDocsSubTab(viewModel = viewModel, onOpenSandbox = { selectedSubTab = 3 })
                2 -> CodeSnippetsSubTab(viewModel = viewModel)
                3 -> SandboxTesterSubTab(viewModel = viewModel)
                4 -> WebhooksManagerSubTab(viewModel = viewModel)
            }
        }
    }

    // Modal: Generate New API Key
    if (showCreateKeyModal) {
        Dialog(
            onDismissRequest = { showCreateKeyModal = false },
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Generate New API Key",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Create credentials for Website or External Apps",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        IconButton(onClick = { showCreateKeyModal = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                        }
                    }

                    HorizontalDivider(color = PinkBorderSoft)

                    // Target Application Name
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Application / Website Name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        OutlinedTextField(
                            value = newKeyName,
                            onValueChange = { newKeyName = it },
                            placeholder = { Text("e.g. Official WordPress Portal, Cash Agent App", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    // Platform / System Type
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Platform / App Type", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "WEBSITE" to "🌐 Website",
                                "MOBILE_APP" to "📱 Mobile App",
                                "AGENT_APP" to "💼 Cash Agent"
                            ).forEach { (typeVal, label) ->
                                val isSelected = newKeyType == typeVal
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) PinkHighlight.copy(alpha = 0.12f) else Color(0xFFF1F5F9),
                                    border = BorderStroke(1.dp, if (isSelected) PinkHighlight else Color(0xFFCBD5E1)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { newKeyType = typeVal }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) PinkHighlight else TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Environment
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Environment", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("PRODUCTION" to "Live Production", "SANDBOX" to "Sandbox / Test").forEach { (envKey, envLabel) ->
                                val isSelected = newKeyEnv == envKey
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF10B981) else Color(0xFFCBD5E1)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { newKeyEnv = envKey }
                                ) {
                                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                        Text(
                                            text = envLabel,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color(0xFF047857) else TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Webhook URL
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Webhook Event URL (Optional)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        OutlinedTextField(
                            value = newKeyWebhook,
                            onValueChange = { newKeyWebhook = it },
                            placeholder = { Text("https://yourwebsite.com/api/webhook", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                        Text("Modol Connect will push real-time OTP, Escrow & Payment events here.", fontSize = 10.sp, color = TextSecondary)
                    }

                    // Scopes / Permissions
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Granted API Scopes & Permissions", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        availableScopes.forEach { (scopeId, scopeDesc) ->
                            val isChecked = selectedScopes.contains(scopeId)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isChecked) selectedScopes.remove(scopeId)
                                        else selectedScopes.add(scopeId)
                                    }
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        if (checked) selectedScopes.add(scopeId)
                                        else selectedScopes.remove(scopeId)
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = PinkHighlight)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(scopeId, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                                    Text(scopeDesc, fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                        }
                    }

                    // Rate Limit & IP Whitelist
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Rate Limit (req/min)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            OutlinedTextField(
                                value = newKeyRateLimit,
                                onValueChange = { newKeyRateLimit = it },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("IP / Domain Whitelist", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            OutlinedTextField(
                                value = newKeyIpWhitelist,
                                onValueChange = { newKeyIpWhitelist = it },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            val rateInt = newKeyRateLimit.toIntOrNull() ?: 120
                            val created = viewModel.createNewApiKey(
                                name = newKeyName.ifBlank { "Website Integration" },
                                appType = newKeyType,
                                scopes = selectedScopes.toList(),
                                environment = newKeyEnv,
                                webhookUrl = newKeyWebhook,
                                rateLimit = rateInt,
                                ipWhitelist = newKeyIpWhitelist
                            )
                            showCreateKeyModal = false
                            Toast.makeText(context, "API Key generated: ${created.apiKey}", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Key, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Credentials Now", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ============================================================================
// SUB-TAB 0: API KEYS & INTEGRATIONS
// ============================================================================

@Composable
fun ApiKeysManagerSubTab(viewModel: AppViewModel, onOpenCreateModal: () -> Unit) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Explanatory Banner
            Surface(
                color = Color(0xFFEFF6FF),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Use these generated API Keys in your Website (React, Next.js, WordPress, PHP) or other Mobile/Desktop Apps to perform authentication, send OTP, query models, and manage cash agent payments.",
                        fontSize = 11.sp,
                        color = Color(0xFF1E3A8A),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        items(viewModel.generatedApiKeys, key = { it.id }) { keyItem ->
            ApiKeyCard(
                keyItem = keyItem,
                onCopy = { text, label ->
                    clipboardManager.setText(AnnotatedString(text))
                    Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
                },
                onRotate = {
                    viewModel.regenerateApiKeySecret(keyItem.id)
                    Toast.makeText(context, "New API secret key generated!", Toast.LENGTH_SHORT).show()
                },
                onToggleStatus = {
                    viewModel.toggleApiKeyStatus(keyItem.id)
                },
                onDelete = {
                    viewModel.deleteApiKey(keyItem.id)
                    Toast.makeText(context, "API Key revoked.", Toast.LENGTH_SHORT).show()
                },
                onPingWebhook = {
                    viewModel.triggerWebhookPing(keyItem.id, keyItem.webhookUrl)
                    Toast.makeText(context, "Ping sent to ${keyItem.webhookUrl}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        item {
            OutlinedButton(
                onClick = onOpenCreateModal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, PinkHighlight),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PinkHighlight)
            ) {
                Icon(imageVector = Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Another API Key", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ApiKeyCard(
    keyItem: GeneratedApiKey,
    onCopy: (String, String) -> Unit,
    onRotate: () -> Unit,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit,
    onPingWebhook: () -> Unit
) {
    var showSecret by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header row with Name, Platform & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val iconVector = when (keyItem.appType) {
                        "WEBSITE" -> Icons.Default.Language
                        "MOBILE_APP" -> Icons.Default.PhoneAndroid
                        "AGENT_APP" -> Icons.Default.Payments
                        else -> Icons.Default.Cloud
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = iconVector, contentDescription = null, tint = PinkHighlight, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(keyItem.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(
                            text = "${keyItem.appType} • ${keyItem.environment} • Rate: ${keyItem.rateLimitPerMin}/min",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (keyItem.status == "ACTIVE") Color(0xFFECFDF5) else Color(0xFFFFFBEB),
                    border = BorderStroke(1.dp, if (keyItem.status == "ACTIVE") Color(0xFF10B981) else Color(0xFFF59E0B))
                ) {
                    Text(
                        text = keyItem.status,
                        color = if (keyItem.status == "ACTIVE") Color(0xFF047857) else Color(0xFFB45309),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            // Public API Key (Client ID)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Public Key / Client ID (X-API-KEY):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = keyItem.apiKey,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0F172A)
                        )
                        IconButton(
                            onClick = { onCopy(keyItem.apiKey, "Public API Key") },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // Secret API Key
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("API Secret (Bearer Token):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Text(
                        text = if (showSecret) "Hide Secret" else "Reveal Secret",
                        fontSize = 10.sp,
                        color = PinkHighlight,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showSecret = !showSecret }
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (showSecret) keyItem.apiSecret else "••••••••••••••••••••••••••••••••",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = if (showSecret) Color(0xFFBE123C) else Color(0xFF64748B)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { onCopy(keyItem.apiSecret, "Secret Key") },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(14.dp))
                            }
                            IconButton(
                                onClick = onRotate,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Rotate", tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            // Webhook row if present
            if (keyItem.webhookUrl.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF0FDF4),
                    border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(14.dp))
                            Text(
                                text = "Webhook: ${keyItem.webhookUrl}",
                                fontSize = 10.sp,
                                color = Color(0xFF14532D),
                                maxLines = 1
                            )
                        }
                        Text(
                            text = "Ping",
                            color = Color(0xFF16A34A),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { onPingWebhook() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Scopes list
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                keyItem.scopes.forEach { sc ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(0.5.dp, Color(0xFFCBD5E1))
                    ) {
                        Text(
                            text = sc,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155),
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Action footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Requests: ${keyItem.requestCount}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextButton(
                        onClick = {
                            val headerVal = "Bearer ${keyItem.apiSecret}"
                            onCopy(headerVal, "Authorization Header")
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Copy Auth Header", fontSize = 11.sp, color = PinkHighlight, fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = onToggleStatus,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (keyItem.status == "ACTIVE") "Pause" else "Resume",
                            fontSize = 11.sp,
                            color = if (keyItem.status == "ACTIVE") Color(0xFFF59E0B) else Color(0xFF10B981),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// ============================================================================
// SUB-TAB 1: ENDPOINTS DOCUMENTATION EXPLORER
// ============================================================================

@Composable
fun EndpointsDocsSubTab(viewModel: AppViewModel, onOpenSandbox: () -> Unit) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedCategory by remember { mutableStateOf("ALL") }

    val endpoints = remember {
        listOf(
            ApiEndpointDoc(
                method = "POST",
                path = "/api/v1/auth/otp/send",
                category = "Auth & OTP",
                description = "Dispatch dynamic OTP to Phone or Email for Login/Verification from Website or App",
                requiresAuth = true,
                sampleRequest = """{ "target": "+8801700000000", "type": "PHONE", "role": "CLIENT" }""",
                sampleResponse = """{ "status": "success", "message": "OTP sent via Live Gateway", "expiresIn": 600 }"""
            ),
            ApiEndpointDoc(
                method = "POST",
                path = "/api/v1/auth/otp/verify",
                category = "Auth & OTP",
                description = "Verify user submitted One-Time Password against backend whitelist & role master codes",
                requiresAuth = true,
                sampleRequest = """{ "target": "+8801700000000", "otpCode": "123456" }""",
                sampleResponse = """{ "verified": true, "userId": "usr_9912", "role": "CLIENT", "token": "mc_token_abc" }"""
            ),
            ApiEndpointDoc(
                method = "POST",
                path = "/api/v1/auth/login",
                category = "Auth & OTP",
                description = "Authenticate user via Email/Password or Backend OTP for Admin, Model, or Agent",
                requiresAuth = true,
                sampleRequest = """{ "email": "client@example.com", "passwordOrOtp": "777333" }""",
                sampleResponse = """{ "status": "success", "userId": "usr_9912", "dashboard": "CLIENT_PORTAL" }"""
            ),
            ApiEndpointDoc(
                method = "GET",
                path = "/api/v1/models/catalog",
                category = "Models & Bookings",
                description = "List all registered models with real-time online status, city, rating & hourly rates",
                requiresAuth = true,
                sampleRequest = "",
                sampleResponse = """{ "total": 12, "models": [ { "id": 1, "name": "Ayesha", "hourlyRate": 3500, "city": "Dhaka" } ] }"""
            ),
            ApiEndpointDoc(
                method = "GET",
                path = "/api/v1/models/{id}",
                category = "Models & Bookings",
                description = "Fetch comprehensive model portfolio, bio, photos, verified badges, and reviews",
                requiresAuth = true,
                sampleRequest = "",
                sampleResponse = """{ "id": 1, "name": "Ayesha", "heightCm": 168, "skills": "VIP, Fashion, Dinner" }"""
            ),
            ApiEndpointDoc(
                method = "POST",
                path = "/api/v1/bookings/create",
                category = "Models & Bookings",
                description = "Create new escort booking with automated escrow lock on funds",
                requiresAuth = true,
                sampleRequest = """{ "modelId": 1, "serviceType": "Dinner Escort", "durationHours": 3, "location": "Gulshan 2, Dhaka" }""",
                sampleResponse = """{ "bookingId": "BK_1092", "totalPrice": 10500, "escrowStatus": "HELD" }"""
            ),
            ApiEndpointDoc(
                method = "POST",
                path = "/api/v1/agent/cash/deposit",
                category = "Cash Agent & Wallet",
                description = "B2B Cash Agent cash intake and balance credit confirmation",
                requiresAuth = true,
                sampleRequest = """{ "agentId": "agent_cash_live", "userId": "usr_9912", "amount": 5000, "currency": "BDT" }""",
                sampleResponse = """{ "txnId": "TXN_7719", "newBalance": 6500.0, "status": "COMPLETED" }"""
            ),
            ApiEndpointDoc(
                method = "POST",
                path = "/api/v1/agent/cash/withdraw",
                category = "Cash Agent & Wallet",
                description = "B2B Cash Agent user payout execution API",
                requiresAuth = true,
                sampleRequest = """{ "agentId": "agent_cash_live", "userId": "usr_9912", "amount": 3000 }""",
                sampleResponse = """{ "txnId": "WDR_3312", "status": "SETTLED" }"""
            ),
            ApiEndpointDoc(
                method = "GET",
                path = "/api/v1/user/wallet/balance",
                category = "Cash Agent & Wallet",
                description = "Get real-time balance for user, model, or cash agent wallet",
                requiresAuth = true,
                sampleRequest = """{ "userId": "user_1" }""",
                sampleResponse = """{ "balance": 15000.0, "currency": "BDT", "escrowLocked": 3500.0 }"""
            ),
            ApiEndpointDoc(
                method = "POST",
                path = "/api/v1/webhooks/subscribe",
                category = "Webhooks",
                description = "Register external website webhook listener for real-time payment and OTP events",
                requiresAuth = true,
                sampleRequest = """{ "targetUrl": "https://mywebsite.com/events", "events": ["otp.verified", "escrow.paid"] }""",
                sampleResponse = """{ "status": "subscribed", "secretKey": "whsec_..." }"""
            )
        )
    }

    val categories = listOf("ALL", "Auth & OTP", "Models & Bookings", "Cash Agent & Wallet", "Webhooks")
    val filteredEndpoints = if (selectedCategory == "ALL") endpoints else endpoints.filter { it.category == selectedCategory }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Category Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSel = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSel) PinkHighlight else Color.White,
                        border = BorderStroke(1.dp, if (isSel) PinkHighlight else Color(0xFFCBD5E1)),
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) Color.White else TextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        items(filteredEndpoints) { ep ->
            EndpointCard(
                endpoint = ep,
                baseUrl = viewModel.apiBaseUrl,
                onCopy = { txt ->
                    clipboardManager.setText(AnnotatedString(txt))
                    Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                },
                onTestInSandbox = onOpenSandbox
            )
        }
    }
}

@Composable
fun EndpointCard(
    endpoint: ApiEndpointDoc,
    baseUrl: String,
    onCopy: (String) -> Unit,
    onTestInSandbox: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    val methodColor = if (endpoint.method == "GET") Color(0xFF10B981) else Color(0xFF3B82F6)
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = methodColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, methodColor)
                    ) {
                        Text(
                            text = endpoint.method,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = methodColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = endpoint.path,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                IconButton(onClick = { expanded = !expanded }, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = TextSecondary
                    )
                }
            }

            Text(endpoint.description, fontSize = 11.sp, color = TextSecondary)

            if (expanded) {
                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Full URL
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("FULL ENDPOINT URL:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$baseUrl${endpoint.path}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF0F172A),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { onCopy("$baseUrl${endpoint.path}") }, modifier = Modifier.size(24.dp)) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp), tint = PinkHighlight)
                        }
                    }
                }

                // Sample Request
                if (endpoint.sampleRequest.isNotBlank()) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("SAMPLE REQUEST BODY (JSON):", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF0F172A),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = endpoint.sampleRequest,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                // Sample Response
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("SAMPLE 200 OK RESPONSE:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF0F172A),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = endpoint.sampleResponse,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF4ADE80),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                // Quick test button
                Button(
                    onClick = onTestInSandbox,
                    colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Test This Endpoint in Sandbox", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ============================================================================
// SUB-TAB 2: CODE SNIPPETS GENERATOR
// ============================================================================

@Composable
fun CodeSnippetsSubTab(viewModel: AppViewModel) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedLang by remember { mutableStateOf("JavaScript") }
    val activeKey = viewModel.generatedApiKeys.firstOrNull { it.status == "ACTIVE" } ?: viewModel.generatedApiKeys.firstOrNull()
    val activeSecret = activeKey?.apiSecret ?: "mc_live_sk_example_98f413a0e"
    val activePk = activeKey?.apiKey ?: "mc_live_pk_example_8912384a"

    val languages = listOf("JavaScript", "cURL", "PHP", "Kotlin", "Python")

    val codeText = when (selectedLang) {
        "JavaScript" -> """
        // -------------------------------------------------------------
        // Modol Connect REST API Client (React / Next.js / Node.js)
        // -------------------------------------------------------------
        const BASE_URL = "${viewModel.apiBaseUrl}";
        const API_KEY = "$activePk";
        const API_SECRET = "$activeSecret";

        // 1. Send OTP to Phone or Email
        async function sendOneTimePassword(target, role = "CLIENT") {
          const res = await fetch(`${'$'}{BASE_URL}/auth/otp/send`, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              "X-API-KEY": API_KEY,
              "Authorization": `Bearer ${'$'}{API_SECRET}`
            },
            body: JSON.stringify({ target, type: "PHONE", role })
          });
          return await res.json();
        }

        // 2. Verify OTP
        async function verifyOneTimePassword(target, otpCode) {
          const res = await fetch(`${'$'}{BASE_URL}/auth/otp/verify`, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              "X-API-KEY": API_KEY,
              "Authorization": `Bearer ${'$'}{API_SECRET}`
            },
            body: JSON.stringify({ target, otpCode })
          });
          return await res.json();
        }

        // 3. Fetch Registered Models Catalog
        async function getModelsCatalog() {
          const res = await fetch(`${'$'}{BASE_URL}/models/catalog`, {
            headers: { "X-API-KEY": API_KEY }
          });
          return await res.json();
        }
        """.trimIndent()

        "cURL" -> """
        # 1. Send OTP Request
        curl -X POST "${viewModel.apiBaseUrl}/auth/otp/send" \
          -H "Content-Type: application/json" \
          -H "X-API-KEY: $activePk" \
          -H "Authorization: Bearer $activeSecret" \
          -d '{
            "target": "+8801700000000",
            "type": "PHONE",
            "role": "CLIENT"
          }'

        # 2. Verify OTP Request
        curl -X POST "${viewModel.apiBaseUrl}/auth/otp/verify" \
          -H "Content-Type: application/json" \
          -H "X-API-KEY: $activePk" \
          -H "Authorization: Bearer $activeSecret" \
          -d '{
            "target": "+8801700000000",
            "otpCode": "123456"
          }'

        # 3. Get Models Catalog
        curl -X GET "${viewModel.apiBaseUrl}/models/catalog" \
          -H "X-API-KEY: $activePk"
        """.trimIndent()

        "PHP" -> """
        <?php
        // -------------------------------------------------------------
        // Modol Connect PHP / WordPress Integration SDK
        // -------------------------------------------------------------
        ${'$'}baseUrl = "${viewModel.apiBaseUrl}";
        ${'$'}apiKey = "$activePk";
        ${'$'}apiSecret = "$activeSecret";

        function sendModolOtp(${'$'}target, ${'$'}role = "CLIENT") {
            global ${'$'}baseUrl, ${'$'}apiKey, ${'$'}apiSecret;
            ${'$'}ch = curl_init("${'$'}baseUrl/auth/otp/send");
            curl_setopt(${'$'}ch, CURLOPT_RETURNTRANSFER, true);
            curl_setopt(${'$'}ch, CURLOPT_POST, true);
            curl_setopt(${'$'}ch, CURLOPT_HTTPHEADER, [
                "Content-Type: application/json",
                "X-API-KEY: ${'$'}apiKey",
                "Authorization: Bearer ${'$'}apiSecret"
            ]);
            curl_setopt(${'$'}ch, CURLOPT_POSTFIELDS, json_encode([
                "target" => ${'$'}target,
                "type" => "PHONE",
                "role" => ${'$'}role
            ]));
            ${'$'}response = curl_exec(${'$'}ch);
            curl_close(${'$'}ch);
            return json_decode(${'$'}response, true);
        }
        ?>
        """.trimIndent()

        "Kotlin" -> """
        // -------------------------------------------------------------
        // Android / Kotlin Retrofit / Ktor Client Integration
        // -------------------------------------------------------------
        object ModolConnectApiClient {
            private const val BASE_URL = "${viewModel.apiBaseUrl}/"
            private const val API_KEY = "$activePk"
            private const val API_SECRET = "$activeSecret"

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val req = chain.request().newBuilder()
                        .addHeader("X-API-KEY", API_KEY)
                        .addHeader("Authorization", "Bearer ${'$'}API_SECRET")
                        .build()
                    chain.proceed(req)
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        """.trimIndent()

        else -> """
        # Python Requests Integration
        import requests

        BASE_URL = "${viewModel.apiBaseUrl}"
        HEADERS = {
            "Content-Type": "application/json",
            "X-API-KEY": "$activePk",
            "Authorization": "Bearer $activeSecret"
        }

        def send_otp(phone_number):
            res = requests.post(f"{BASE_URL}/auth/otp/send", json={
                "target": phone_number,
                "type": "PHONE",
                "role": "CLIENT"
            }, headers=HEADERS)
            return res.json()

        def verify_otp(phone_number, code):
            res = requests.post(f"{BASE_URL}/auth/otp/verify", json={
                "target": phone_number,
                "otpCode": code
            }, headers=HEADERS)
            return res.json()
        """.trimIndent()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Copy & Paste Ready Integration Code",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Code automatically uses your active API credentials: ${activeKey?.name ?: "Default"}",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }

        item {
            // Language selector tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                languages.forEach { lang ->
                    val isSel = selectedLang == lang
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) PinkHighlight else Color.White,
                        border = BorderStroke(1.dp, if (isSel) PinkHighlight else Color(0xFFCBD5E1)),
                        modifier = Modifier.clickable { selectedLang = lang }
                    ) {
                        Text(
                            text = lang,
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) Color.White else TextPrimary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        item {
            // Code box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).background(Color(0xFFEF4444), CircleShape))
                            Box(modifier = Modifier.size(10.dp).background(Color(0xFFF59E0B), CircleShape))
                            Box(modifier = Modifier.size(10.dp).background(Color(0xFF10B981), CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(selectedLang, color = Color(0xFF94A3B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }

                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(codeText))
                                Toast.makeText(context, "$selectedLang snippet copied!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy Snippet", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(color = Color(0xFF1E293B))

                    Text(
                        text = codeText,
                        color = Color(0xFFE2E8F0),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// ============================================================================
// SUB-TAB 3: TEST SANDBOX & LIVE API CONSOLE
// ============================================================================

@Composable
fun SandboxTesterSubTab(viewModel: AppViewModel) {
    val context = LocalContext.current
    var selectedMethod by remember { mutableStateOf("POST") }
    var selectedEndpoint by remember { mutableStateOf("/api/v1/auth/otp/send") }
    var requestBodyInput by remember { mutableStateOf("""{ "target": "+8801700000000", "type": "PHONE", "role": "CLIENT" }""") }
    val activeKey = viewModel.generatedApiKeys.firstOrNull { it.status == "ACTIVE" } ?: viewModel.generatedApiKeys.firstOrNull()

    val availableEndpoints = listOf(
        "POST" to "/api/v1/auth/otp/send",
        "POST" to "/api/v1/auth/otp/verify",
        "POST" to "/api/v1/auth/login",
        "GET" to "/api/v1/models/catalog",
        "POST" to "/api/v1/agent/cash/deposit",
        "GET" to "/api/v1/user/wallet/balance"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Interactive API Test Console", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Simulate live external HTTP requests to verify backend payload structure and response speed.", fontSize = 11.sp, color = TextSecondary)
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Endpoint picker
                    Text("Select Target Endpoint:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        availableEndpoints.forEach { (mth, path) ->
                            val isSel = selectedEndpoint == path && selectedMethod == mth
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) PinkHighlight.copy(alpha = 0.1f) else Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, if (isSel) PinkHighlight else Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedMethod = mth
                                        selectedEndpoint = path
                                        requestBodyInput = when {
                                            path.contains("otp/send") -> """{ "target": "+8801700000000", "type": "PHONE", "role": "CLIENT" }"""
                                            path.contains("otp/verify") -> """{ "target": "+8801700000000", "otpCode": "123456" }"""
                                            path.contains("login") -> """{ "email": "client@example.com", "passwordOrOtp": "777333" }"""
                                            path.contains("agent/cash") -> """{ "agentId": "agent_cash_live", "userId": "user_1", "amount": 5000 }"""
                                            else -> """{ "userId": "user_1" }"""
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val mthColor = if (mth == "GET") Color(0xFF10B981) else Color(0xFF3B82F6)
                                    Text(mth, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = mthColor)
                                    Text(path, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextPrimary)
                                }
                            }
                        }
                    }

                    // Request Body
                    if (selectedMethod == "POST") {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Request Payload (JSON):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            OutlinedTextField(
                                value = requestBodyInput,
                                onValueChange = { requestBodyInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                                minLines = 3
                            )
                        }
                    }

                    // Send Request Button
                    Button(
                        onClick = {
                            viewModel.testSandboxApiCall(
                                method = selectedMethod,
                                endpoint = selectedEndpoint,
                                apiKeyStr = activeKey?.apiKey ?: "mc_live_pk_test",
                                reqBody = requestBodyInput
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Live Test Request", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Response box
        item {
            val lastResp = viewModel.apiSandboxLastResponse
            if (!lastResp.isNullOrEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF10B981).copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, Color(0xFF10B981))
                                ) {
                                    Text(
                                        text = "HTTP ${viewModel.apiSandboxStatusCode} OK",
                                        color = Color(0xFF10B981),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = "Latency: ${viewModel.apiSandboxLatencyMs} ms",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFF1E293B))

                        Text(
                            text = lastResp,
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// SUB-TAB 4: WEBHOOKS MANAGER
// ============================================================================

@Composable
fun WebhooksManagerSubTab(viewModel: AppViewModel) {
    val context = LocalContext.current
    var webhookUrlInput by remember { mutableStateOf("https://modolconnect.fun/api/webhook") }
    var selectedEvent by remember { mutableStateOf("otp.verified") }

    val webhookEvents = listOf(
        "otp.verified" to "Fired immediately when user verifies OTP on phone or web",
        "booking.escrow_locked" to "Fired when client confirms booking & funds are held",
        "agent.cash_collected" to "Fired when Cash Agent records cash deposit",
        "escrow.funds_released" to "Fired when Admin releases payment to model",
        "model.status_changed" to "Fired when model goes Online or Offline"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Automated Webhooks Dispatcher", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Send automated HTTP POST notifications to your website or partner CRM whenever important transactions occur.", fontSize = 11.sp, color = TextSecondary)
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Destination Webhook Endpoint URL:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        OutlinedTextField(
                            value = webhookUrlInput,
                            onValueChange = { webhookUrlInput = it },
                            placeholder = { Text("https://mywebsite.com/api/webhook", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Select Event to Test Ping:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        webhookEvents.forEach { (evKey, evDesc) ->
                            val isSel = selectedEvent == evKey
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) PinkHighlight.copy(alpha = 0.1f) else Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, if (isSel) PinkHighlight else Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedEvent = evKey }
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(evKey, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isSel) PinkHighlight else TextPrimary, fontFamily = FontFamily.Monospace)
                                    Text(evDesc, fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.triggerWebhookPing("manual_ping", webhookUrlInput)
                            Toast.makeText(context, "Webhook ping for $selectedEvent sent!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Test Webhook Ping ($selectedEvent)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
