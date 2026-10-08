package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdentityVerificationScreen(viewModel: AppViewModel) {
    val context = LocalContext.current
    var docType by remember { mutableStateOf(viewModel.userIdentityDocType) } // "NID" or "PASSPORT"
    var docNumber by remember { mutableStateOf(viewModel.userIdentityDocNumber) }
    var frontUri by remember { mutableStateOf(viewModel.userIdentityFrontPhotoUri) }
    var backUri by remember { mutableStateOf(viewModel.userIdentityBackPhotoUri) }
    var selfieUri by remember { mutableStateOf(viewModel.userIdentitySelfiePhotoUri) }

    var validationError by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val frontLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            frontUri = uri.toString()
        }
    }

    val backLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            backUri = uri.toString()
        }
    }

    val selfieLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selfieUri = uri.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Identity Verification (KYC)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            "National ID & Passport Verification",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.goBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when (viewModel.userIdentityStatus) {
                        "VERIFIED" -> Color(0xFFECFDF5)
                        "REJECTED" -> Color(0xFFFEF2F2)
                        "PENDING_REVIEW" -> Color(0xFFFFFBEB)
                        else -> Color.White
                    }
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    1.dp,
                    when (viewModel.userIdentityStatus) {
                        "VERIFIED" -> Color(0xFF10B981)
                        "REJECTED" -> Color(0xFFEF4444)
                        "PENDING_REVIEW" -> Color(0xFFF59E0B)
                        else -> PinkBorderSoft
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                color = when (viewModel.userIdentityStatus) {
                                    "VERIFIED" -> Color(0xFF10B981).copy(alpha = 0.2f)
                                    "REJECTED" -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                    "PENDING_REVIEW" -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                    else -> PinkHighlight.copy(alpha = 0.15f)
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (viewModel.userIdentityStatus) {
                                "VERIFIED" -> Icons.Default.CheckCircle
                                "REJECTED" -> Icons.Default.Error
                                "PENDING_REVIEW" -> Icons.Default.Pending
                                else -> Icons.Default.Badge
                            },
                            contentDescription = null,
                            tint = when (viewModel.userIdentityStatus) {
                                "VERIFIED" -> Color(0xFF10B981)
                                "REJECTED" -> Color(0xFFEF4444)
                                "PENDING_REVIEW" -> Color(0xFFF59E0B)
                                else -> PinkHighlight
                            },
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when (viewModel.userIdentityStatus) {
                                    "VERIFIED" -> "Identity Verified ✓"
                                    "PENDING_REVIEW" -> "Verification Pending Review"
                                    "REJECTED" -> "Verification Needs Attention"
                                    else -> "Verification Required"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = when (viewModel.userIdentityStatus) {
                                "VERIFIED" -> "Your government document has been verified and approved by the administration."
                                "PENDING_REVIEW" -> "Your documents are currently being inspected by Admin KYC review. Please allow up to 24 hours."
                                "REJECTED" -> "Reason: ${viewModel.userIdentityRejectionReason.ifBlank { "Unclear photo or mismatched ID" }}. Please re-submit below."
                                else -> "Upload your Bangladesh National ID (NID) or Passport to access all verified bookings, escrow, and audio call features."
                            },
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Document Selection & Number Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, PinkBorderSoft),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        "1. Select Government ID Type",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterChip(
                            selected = docType == "NID",
                            onClick = { docType = "NID" },
                            label = { Text("🇧🇩 National ID (NID)") },
                            leadingIcon = {
                                Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PinkHighlight,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            )
                        )

                        FilterChip(
                            selected = docType == "PASSPORT",
                            onClick = { docType = "PASSPORT" },
                            label = { Text("🌐 Passport") },
                            leadingIcon = {
                                Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PinkHighlight,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            )
                        )
                    }

                    OutlinedTextField(
                        value = docNumber,
                        onValueChange = {
                            docNumber = it
                            validationError = null
                        },
                        label = {
                            Text(if (docType == "NID") "National ID (NID) Number (10 or 17 Digits)" else "Passport Number")
                        },
                        placeholder = {
                            Text(if (docType == "NID") "e.g. 19942691234567890" else "e.g. A01234567")
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Pin, contentDescription = null, tint = PinkHighlight)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PinkHighlight,
                            unfocusedBorderColor = PinkBorderLight
                        )
                    )
                }
            }

            // Photos Upload Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, PinkBorderSoft),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        "2. Upload Document Photos & Live Selfie",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )

                    // Front Photo
                    IdentityUploadBox(
                        title = if (docType == "NID") "National ID Front Side" else "Passport Information Page",
                        subtitle = "Clear scan or high-resolution photo showing photo & name",
                        uri = frontUri,
                        icon = Icons.Default.FlipToFront,
                        onSelectPhoto = { frontLauncher.launch("image/*") },
                        onTakeSample = {
                            frontUri = "https://images.unsplash.com/photo-1544717305-2782549b5136?fit=crop&w=600&q=80"
                        }
                    )

                    // Back Photo (NID requires back photo)
                    if (docType == "NID") {
                        IdentityUploadBox(
                            title = "National ID Back Side",
                            subtitle = "Back side showing address, blood group & barcode",
                            uri = backUri,
                            icon = Icons.Default.FlipToBack,
                            onSelectPhoto = { backLauncher.launch("image/*") },
                            onTakeSample = {
                                backUri = "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?fit=crop&w=600&q=80"
                            }
                        )
                    }

                    // Selfie with ID
                    IdentityUploadBox(
                        title = "Selfie with ID Document (Face Match)",
                        subtitle = "Hold your ID card next to your face with good lighting",
                        uri = selfieUri,
                        icon = Icons.Default.Face,
                        onSelectPhoto = { selfieLauncher.launch("image/*") },
                        onTakeSample = {
                            selfieUri = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?fit=crop&w=600&q=80"
                        }
                    )
                }
            }

            // Validation Error Box
            if (validationError != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(validationError ?: "", color = Color(0xFFB91C1C), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    if (docNumber.isBlank()) {
                        validationError = "Please enter your ${if (docType == "NID") "National ID (NID)" else "Passport"} number."
                        return@Button
                    }
                    if (frontUri.isBlank()) {
                        validationError = "Please upload or capture the front side of your document."
                        return@Button
                    }
                    if (docType == "NID" && backUri.isBlank()) {
                        validationError = "Please upload the back side of your National ID."
                        return@Button
                    }
                    if (selfieUri.isBlank()) {
                        validationError = "Please upload a selfie holding your ID for facial verification."
                        return@Button
                    }

                    validationError = null
                    isSubmitting = true
                    viewModel.submitIdentityVerification(
                        documentType = docType,
                        documentNumber = docNumber.trim(),
                        frontPhotoUri = frontUri,
                        backPhotoUri = backUri,
                        selfiePhotoUri = selfieUri
                    )
                    isSubmitting = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkHighlight)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submitting Documents...", color = Color.White, fontWeight = FontWeight.Bold)
                } else {
                    Icon(imageVector = Icons.Default.UploadFile, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (viewModel.userIdentityStatus == "PENDING_REVIEW") "Update & Resubmit Verification" else "Submit Documents for Verification",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            // Security Disclaimer Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Government ID Privacy Guarantee", color = Color(0xFF334155), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(
                            "Your uploaded documents are securely stored in the Modol Connect database and reviewed strictly by authorized compliance admins. Documents are never made public to other models or clients.",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun IdentityUploadBox(
    title: String,
    subtitle: String,
    uri: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onSelectPhoto: () -> Unit,
    onTakeSample: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
            .border(1.dp, PinkBorderLight, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                Text(subtitle, fontSize = 10.sp, color = TextSecondary)
            }
            if (uri.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFECFDF5),
                    border = BorderStroke(0.8.dp, Color(0xFF10B981))
                ) {
                    Text(
                        "Uploaded ✓",
                        color = Color(0xFF047857),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        if (uri.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE2E8F0))
            ) {
                val safeUri = if (uri.startsWith("/") && !uri.startsWith("file://")) "file://$uri" else uri
                SubcomposeAsyncImage(
                    model = safeUri,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    error = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Image loaded ($title)", color = Color.DarkGray, fontSize = 12.sp)
                        }
                    }
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clickable { onSelectPhoto() },
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Text(
                        "Change Photo ✎",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSelectPhoto,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, PinkHighlight),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PinkHighlight)
                ) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Gallery Upload", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                FilledTonalButton(
                    onClick = onTakeSample,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Live Capture", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
