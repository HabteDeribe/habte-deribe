package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class LandMonitoringSubTab {
    COMMAND_CENTER,
    FIELD_INSPECTOR
}

@Composable
fun PortalLandMonitoringScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var subTab by remember { mutableStateOf(LandMonitoringSubTab.COMMAND_CENTER) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalSurface)
    ) {
        // Consolidated Custom Tab Switcher styled with modern dark-forest branding
        Surface(
            color = CharcoalLight,
            tonalElevation = 3.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardSlate)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AI Urban Land Monitoring",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Emerald40,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "Adama Smart City Live Spatial Feeds",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Digital Pulse Indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(TealSoft)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Text(
                            text = "LIVE FEED",
                            color = Color(0xFF047857),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Sub-tabs Selection Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CharcoalSurface)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Command hub toggle
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (subTab == LandMonitoringSubTab.COMMAND_CENTER) Emerald40 else Color.Transparent)
                            .clickable { subTab = LandMonitoringSubTab.COMMAND_CENTER }
                            .padding(vertical = 10.dp)
                            .testTag("subtab_command_center"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Security,
                                contentDescription = null,
                                tint = if (subTab == LandMonitoringSubTab.COMMAND_CENTER) Color.White else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Command Center",
                                color = if (subTab == LandMonitoringSubTab.COMMAND_CENTER) Color.White else Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Field Inspector toggle
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (subTab == LandMonitoringSubTab.FIELD_INSPECTOR) Emerald40 else Color.Transparent)
                            .clickable { subTab = LandMonitoringSubTab.FIELD_INSPECTOR }
                            .padding(vertical = 10.dp)
                            .testTag("subtab_field_inspector"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Construction,
                                contentDescription = null,
                                tint = if (subTab == LandMonitoringSubTab.FIELD_INSPECTOR) Color.White else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Inspector Companion",
                                color = if (subTab == LandMonitoringSubTab.FIELD_INSPECTOR) Color.White else Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Inner viewport showing active tool utilizing smooth slide transitions
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AnimatedContent(
                targetState = subTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "Subtab transition"
            ) { targetSubTab ->
                when (targetSubTab) {
                    LandMonitoringSubTab.COMMAND_CENTER -> {
                        PortalCommandCenterScreen(
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    LandMonitoringSubTab.FIELD_INSPECTOR -> {
                        PortalInspectorScreen(
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
