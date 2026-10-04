package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.viewmodel.AppViewModel

@Composable
fun AudioCallScreen(viewModel: AppViewModel) {
    BackHandler {
        viewModel.endAudioCall()
    }

    val callState = viewModel.activeCallState
    val partnerName = viewModel.activeCallPartnerName
    val partnerRole = viewModel.activeCallPartnerRole
    val durationText = viewModel.activeCallDurationFormatted
    val isMuted = viewModel.isCallMicMuted
    val isSpeakerOn = viewModel.isCallSpeakerOn

    // Pulse animation for ringing
    val infiniteTransition = rememberInfiniteTransition(label = "RingingPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    // Audio Visualizer wave animations
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 8f, targetValue = 28f,
        animationSpec = infiniteRepeatable(tween(450, easing = LinearEasing), RepeatMode.Reverse),
        label = "Wave1"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 24f, targetValue = 12f,
        animationSpec = infiniteRepeatable(tween(380, easing = LinearEasing), RepeatMode.Reverse),
        label = "Wave2"
    )
    val wave3 by infiniteTransition.animateFloat(
        initialValue = 14f, targetValue = 36f,
        animationSpec = infiniteRepeatable(tween(520, easing = LinearEasing), RepeatMode.Reverse),
        label = "Wave3"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0E1A),
                        Color(0xFF131B2E),
                        Color(0xFF0F172A)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("audio_call_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Telemetry Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0x22FFFFFF),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Encrypted",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "End-to-End Encrypted (WebRTC P2P)",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    text = viewModel.callQualityLabel,
                    color = Color(0xFF64748B),
                    fontSize = 10.sp
                )
            }

            // Center: Avatar, Partner Details & Call Status
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                // Ringing pulse rings
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(200.dp)
                ) {
                    if (callState == "RINGING") {
                        Box(
                            modifier = Modifier
                                .size(170.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(Color(0xFFE91E63).copy(alpha = pulseAlpha))
                        )
                    }

                    // Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color(0xFFE91E63), CircleShape)
                            .background(Color(0xFF1E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (viewModel.activeCallPartnerAvatar.isNotBlank()) {
                            AsyncImage(
                                model = viewModel.activeCallPartnerAvatar,
                                contentDescription = partnerName,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = partnerName,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Partner Name & Verified Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = partnerName,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = partnerRole,
                    color = Color(0xFF94A3B8),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Call State / Live Duration
                when (callState) {
                    "RINGING" -> {
                        Text(
                            text = "Ringing...",
                            color = Color(0xFFE91E63),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    "CONNECTED" -> {
                        Text(
                            text = durationText,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Live Audio Waveform Bars
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(40.dp)
                        ) {
                            val waves = listOf(wave1, wave2, wave3, wave2, wave1, wave3, wave2)
                            waves.forEach { height ->
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(height.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color(0xFFE91E63).copy(alpha = 0.85f))
                                )
                            }
                        }
                    }
                    else -> {
                        Text(
                            text = "Call Ended",
                            color = Color(0xFFEF4444),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Bottom Call Action Controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                // Secondary Controls: Mute, Speaker, Chat
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute / Unmute Button
                    CallActionButton(
                        icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        label = if (isMuted) "Unmute" else "Mute",
                        isActive = isMuted,
                        activeColor = Color(0xFFEF4444),
                        onClick = { viewModel.toggleCallMute() },
                        testTag = "call_mute_button"
                    )

                    // Speaker Button
                    CallActionButton(
                        icon = if (isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                        label = if (isSpeakerOn) "Speaker On" else "Speaker",
                        isActive = isSpeakerOn,
                        activeColor = Color(0xFF38BDF8),
                        onClick = { viewModel.toggleCallSpeaker() },
                        testTag = "call_speaker_button"
                    )

                    // Switch to Chat Button
                    CallActionButton(
                        icon = Icons.Default.Chat,
                        label = "Chat",
                        isActive = false,
                        onClick = {
                            viewModel.selectChatPartner("user_1")
                            viewModel.selectedTab = 3
                            viewModel.navigateTo("DASHBOARD")
                        },
                        testTag = "call_chat_button"
                    )
                }

                // Primary End Call Button
                IconButton(
                    onClick = { viewModel.endAudioCall() },
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444))
                        .testTag("end_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CallActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color = Color.White,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) activeColor else Color(0x22FFFFFF)
                )
                .testTag(testTag)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.White else Color(0xFFE2E8F0),
                modifier = Modifier.size(26.dp)
            )
        }
        Text(
            text = label,
            color = Color(0xFF94A3B8),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
