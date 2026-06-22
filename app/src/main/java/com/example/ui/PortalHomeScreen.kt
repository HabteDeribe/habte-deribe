package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserRole
import com.example.ActivePortalTab
import com.example.ui.theme.*

@Composable
fun PortalHomeScreen(
    viewModel: MainViewModel,
    onNavigateToTab: (ActivePortalTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val isVerified by viewModel.isUserVerified.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val userIdCard by viewModel.userIdCard.collectAsState()

    var registerEmail by remember { mutableStateOf("") }
    var registerIdCard by remember { mutableStateOf("") }
    var registrationError by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalSurface)
            .verticalScroll(rememberScrollState())
    ) {
        // Glowing Background Gradients for Premium Visual Feel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Emerald40.copy(alpha = 0.08f),
                            CharcoalSurface
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Spacer for TopBar
            Spacer(modifier = Modifier.height(8.dp))

            // 1. HERO SECTION
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Emerald40.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "HOME • LAND MONITORING • BUY • SELL • RENT",
                        style = MaterialTheme.typography.labelSmall,
                        color = Emerald40,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "H-Ethio-land",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Emerald40,
                    textAlign = TextAlign.Center,
                    lineHeight = 44.sp,
                    letterSpacing = (-1).sp
                )

                Text(
                    text = "Ethiopia's national platform unifying AI urban land monitoring, property sales and rentals — built on real GPS data from the Adama smart city.",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    lineHeight = 20.sp
                )
            }

            // 2. INTERACTIVE CIRCULAR MENU CENTERPIECE
            Text(
                text = "INTERACTIVE CENTRAL NAVIGATION HUB",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.5.sp
            )

            Box(
                modifier = Modifier
                    .size(310.dp)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                // Background Circular Rings for Tech Aesthetic
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .border(1.5.dp, Emerald40.copy(alpha = 0.15f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .border(1.dp, CardSlate, CircleShape)
                )

                // 4 Interactive Navigation Nodes / Buttons in Circle
                // Node 1: Top (Home)
                CircularHubNode(
                    label = "LAND HUB",
                    icon = Icons.Filled.Public,
                    color = Emerald40,
                    modifier = Modifier.align(Alignment.TopCenter),
                    onClick = { onNavigateToTab(ActivePortalTab.HOME) }
                )

                // Node 2: Right (Land Monitoring)
                CircularHubNode(
                    label = "MONITORING",
                    icon = Icons.Filled.MyLocation,
                    color = Emerald40,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = { onNavigateToTab(ActivePortalTab.LAND_MONITORING) }
                )

                // Node 3: Bottom (Rent)
                CircularHubNode(
                    label = "RENT PROPERTIES",
                    icon = Icons.Filled.Key,
                    color = TealAccent,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onClick = { onNavigateToTab(ActivePortalTab.RENT) }
                )

                // Node 4: Left (Buy)
                CircularHubNode(
                    label = "BUY HOMES",
                    icon = Icons.Filled.HomeWork,
                    color = Gold40,
                    modifier = Modifier.align(Alignment.CenterStart),
                    onClick = { onNavigateToTab(ActivePortalTab.BUY) }
                )

                // Decorative Center Core Block
                Card(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .border(2.dp, Emerald40, CircleShape),
                    colors = CardDefaults.cardColors(containerColor = CharcoalDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "H-Ethio-land",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.2).sp
                        )
                        Box(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .height(1.dp)
                                .width(40.dp)
                                .background(Gold40)
                        )
                        Text(
                            text = "ADAMA",
                            color = TealAccent,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "ETHIOPIA",
                            color = Color.LightGray,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Quick Shortcut Floating Badges Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = { onNavigateToTab(ActivePortalTab.LAND_MONITORING) },
                    label = { Text("Open Municipality Map", fontSize = 11.sp, color = CharcoalDark) },
                    leadingIcon = { Icon(Icons.Filled.Map, null, tint = Emerald40, modifier = Modifier.size(14.dp)) }
                )
                AssistChip(
                    onClick = { onNavigateToTab(ActivePortalTab.RENT) },
                    label = { Text("AI Property Search", fontSize = 11.sp, color = CharcoalDark) },
                    leadingIcon = { Icon(Icons.Filled.Chat, null, tint = TealAccent, modifier = Modifier.size(14.dp)) }
                )
            }

            // 3. STATISTICS PANEL (4 High Fidelity Cards)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "MUNICIPAL SCADA METRICS & DATASETS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Emerald40,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        value = "184K+",
                        title = "Land Parcels",
                        desc = "Registered & mapped",
                        icon = Icons.Filled.Grid4x4,
                        color = Emerald40,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        value = "12.4K",
                        title = "Active Permits",
                        desc = "Building applications",
                        icon = Icons.Filled.Approval,
                        color = TealAccent,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        value = "98%",
                        title = "AI Accuracy",
                        desc = "Object segmentation",
                        icon = Icons.Filled.AreaChart,
                        color = Gold40,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        value = "24/7",
                        title = "Monitoring",
                        desc = "Live sentinel feed",
                        icon = Icons.Filled.SatelliteAlt,
                        color = Color(0xFF6366F1),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 4. ETHIOPIAN IDENTITY VERIFICATION BLOCK
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CharcoalLight),
                border = BorderStroke(1.dp, CardSlate),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Emerald40.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.VerifiedUser, null, tint = Emerald40, modifier = Modifier.size(24.dp))
                        }

                        Column {
                            Text(
                                "National Identity & Fida Verification",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalDark
                            )
                            Text(
                                "Addis Ababa Municipal Integration",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(CardSlate)
                    )

                    AnimatedContent(
                        targetState = isVerified,
                        label = "Verify Transition"
                    ) { verified ->
                        if (verified) {
                            // Verified State view
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(TealSoft, RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Icon(Icons.Filled.CheckCircle, "Verified", tint = Color(0xFF10B981), modifier = Modifier.size(36.dp))
                                Text(
                                    "CLEARANCE CERTIFICATE ACTIVE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF047857)
                                )
                                Text(
                                    "Email: $userEmail\nNational ID: $userIdCard",
                                    fontSize = 12.sp,
                                    color = CharcoalDark,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 16.sp
                                )
                                Text(
                                    "Your identity has been cross-checked live with the Ethiopian National ID (Fida) Database Registry. You now have full clearance to transact land and register architectural permits.",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                Button(
                                    onClick = { viewModel.logoutUser() },
                                    colors = ButtonDefaults.buttonColors(containerColor = WarningRed),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Unlink Profile / Sign Out", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            // Unverified Registration Input Form
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Register with your email and verify with your Fida (Kebele) ID or Ethiopian National ID card to access secure land, sales, and rental portals.",
                                    fontSize = 12.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 18.sp
                                )

                                if (registrationError.isNotBlank()) {
                                    Text(
                                        text = registrationError,
                                        color = WarningRed,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                val textColors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = CharcoalDark,
                                    unfocusedTextColor = CharcoalDark,
                                    focusedContainerColor = CharcoalSurface,
                                    unfocusedContainerColor = CharcoalSurface,
                                    focusedBorderColor = Emerald40,
                                    unfocusedBorderColor = CardSlate
                                )

                                OutlinedTextField(
                                    value = registerEmail,
                                    onValueChange = { registerEmail = it },
                                    label = { Text("Email Address", color = Color.Gray, fontSize = 12.sp) },
                                    singleLine = true,
                                    colors = textColors,
                                    modifier = Modifier.fillMaxWidth().testTag("reg_email_input")
                                )

                                OutlinedTextField(
                                    value = registerIdCard,
                                    onValueChange = { registerIdCard = it },
                                    label = { Text("National Fida ID / Kebele Card Number", color = Color.Gray, fontSize = 12.sp) },
                                    singleLine = true,
                                    colors = textColors,
                                    modifier = Modifier.fillMaxWidth().testTag("reg_fida_input")
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            if (registerEmail.isBlank() || registerIdCard.isBlank()) {
                                                registrationError = "All verification fields are required."
                                            } else {
                                                registrationError = ""
                                                viewModel.verifyUser(registerEmail, registerIdCard)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Emerald40),
                                        modifier = Modifier.weight(1f).testTag("reg_register_button")
                                    ) {
                                        Text("Register & Verify", color = Color.White, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            viewModel.verifyUser("demo.officer@habte.et", "FIDA-983271-AA")
                                        },
                                        border = BorderStroke(1.dp, Emerald40),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Emerald40),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Guest Bypass", fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. CORPORATE CEO AND PLATFORM FOOTER CREDITS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CharcoalDark, RoundedCornerShape(14.dp))
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "H-Ethio-land",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Ethiopia's national platform combining AI urban land monitoring, property sales, and rentals — powered by real GPS data from the Adama smart city.",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .width(100.dp)
                        .background(Color.DarkGray)
                )
                Text(
                    text = "Founder & CEO — Habte Deribe Zeleke",
                    color = Gold40,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Ministry of Innovation & Technology (MInT) • © 2026",
                    color = Color.Gray,
                    fontSize = 9.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CircularHubNode(
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(10.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.5.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = CharcoalDark,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MetricCard(
    value: String,
    title: String,
    desc: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CharcoalLight),
        border = BorderStroke(1.dp, CardSlate)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(14.dp))
                }
            }
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CharcoalDark)
            Text(desc, fontSize = 9.sp, color = Color.Gray)
        }
    }
}
