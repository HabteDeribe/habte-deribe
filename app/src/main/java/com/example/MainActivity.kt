package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserRole
import com.example.ui.*
import com.example.ui.theme.*

enum class ActivePortalTab {
    HOME,
    LAND_MONITORING,
    BUY,
    RENT,
    ABOUT
}

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = false) { // Clean Pristine Light H-Ethio-land Aesthetic Mode
                val activeRole by viewModel.currentUserRole.collectAsState()
                var currentTab by remember { mutableStateOf(ActivePortalTab.HOME) }
                var showRoleMenu by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("main_scaffold"),
                    topBar = {
                        // Clean, professional light top app bar branding
                        Surface(
                            color = CharcoalLight,
                            tonalElevation = 2.dp,
                            border = BorderStroke(1.dp, CardSlate)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .statusBarsPadding()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Corporate branding logo "H-Ethio-land"
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Emerald40),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "HE",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "H-Ethio-land",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Emerald40,
                                            letterSpacing = (-0.5).sp
                                        )
                                        Text(
                                            text = "Founded by Habte Deribe Zeleke",
                                            fontSize = 10.sp,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                // Compact 3-line menu icon button for role navigation & simulation controls
                                Box {
                                    IconButton(
                                        onClick = { showRoleMenu = true },
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(CharcoalDark)
                                            .border(1.dp, CardSlate, CircleShape)
                                            .testTag("role_menu_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Menu,
                                            contentDescription = "Select Portal user role",
                                            tint = Gold40,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = showRoleMenu,
                                        onDismissRequest = { showRoleMenu = false },
                                        modifier = Modifier
                                            .background(CharcoalLight)
                                            .border(1.dp, CardSlate)
                                    ) {
                                        Text(
                                            text = "PORTAL USER ROLE",
                                            color = Emerald40,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                        )
                                        Text(
                                            text = "Active: ${activeRole.name.replace('_', ' ')}",
                                            color = Color.Gray,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Normal,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                                        )
                                        Divider(color = CardSlate, modifier = Modifier.padding(vertical = 4.dp))

                                        UserRole.values().forEach { role ->
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = when (role) {
                                                                UserRole.Admin -> Icons.Filled.Security
                                                                UserRole.Municipality_Officer -> Icons.Filled.AccountBalance
                                                                UserRole.Inspector -> Icons.Filled.Construction
                                                                UserRole.Property_Owner -> Icons.Filled.Storefront
                                                                UserRole.Citizen -> Icons.Filled.Person
                                                            },
                                                            contentDescription = null,
                                                            tint = if (activeRole == role) Gold40 else Color.Gray,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Text(
                                                            text = role.name.replace('_', ' '),
                                                            color = if (activeRole == role) Gold40 else CharcoalDark,
                                                            fontWeight = if (activeRole == role) FontWeight.Bold else FontWeight.Normal
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    viewModel.changeUserRole(role)
                                                    showRoleMenu = false

                                                    // Auto-navigate user to their primary portal for best demo flow
                                                    currentTab = when (role) {
                                                        UserRole.Municipality_Officer, UserRole.Admin, UserRole.Inspector -> ActivePortalTab.LAND_MONITORING
                                                        UserRole.Citizen, UserRole.Property_Owner -> ActivePortalTab.BUY
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
                    bottomBar = {
                        // High-fidelity responsive tab navigation bar
                        val surfaceColor by animateColorAsState(targetValue = CharcoalLight, label = "Bar Color")
                        Surface(
                            color = surfaceColor,
                            tonalElevation = 8.dp,
                            border = BorderStroke(1.dp, CardSlate)
                        ) {
                            NavigationBar(
                                containerColor = Color.Transparent,
                                modifier = Modifier
                                    .navigationBarsPadding()
                                    .height(72.dp)
                            ) {
                                NavigationBarItem(
                                    selected = currentTab == ActivePortalTab.HOME,
                                    onClick = { currentTab = ActivePortalTab.HOME },
                                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home Hub") },
                                    label = { Text("Home", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = Emerald40,
                                        indicatorColor = Emerald40,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    ),
                                    modifier = Modifier.testTag("tab_home")
                                )

                                NavigationBarItem(
                                    selected = currentTab == ActivePortalTab.LAND_MONITORING,
                                    onClick = { currentTab = ActivePortalTab.LAND_MONITORING },
                                    icon = { Icon(Icons.Filled.MyLocation, contentDescription = "Spatial Monitoring") },
                                    label = { Text("Monitoring", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = Emerald40,
                                        indicatorColor = Emerald40,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    ),
                                    modifier = Modifier.testTag("tab_monitoring")
                                )

                                NavigationBarItem(
                                    selected = currentTab == ActivePortalTab.BUY,
                                    onClick = { currentTab = ActivePortalTab.BUY },
                                    icon = { Icon(Icons.Filled.ShoppingBag, contentDescription = "Buy properties") },
                                    label = { Text("Buy", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = Emerald40,
                                        indicatorColor = Emerald40,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    ),
                                    modifier = Modifier.testTag("tab_buy")
                                )

                                NavigationBarItem(
                                    selected = currentTab == ActivePortalTab.RENT,
                                    onClick = { currentTab = ActivePortalTab.RENT },
                                    icon = { Icon(Icons.Filled.Key, contentDescription = "Rent properties") },
                                    label = { Text("Rent", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = Emerald40,
                                        indicatorColor = Emerald40,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    ),
                                    modifier = Modifier.testTag("tab_rent")
                                )

                                NavigationBarItem(
                                    selected = currentTab == ActivePortalTab.ABOUT,
                                    onClick = { currentTab = ActivePortalTab.ABOUT },
                                    icon = { Icon(Icons.Filled.MenuBook, contentDescription = "About Architecture") },
                                    label = { Text("About", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = Emerald40,
                                        indicatorColor = Emerald40,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    ),
                                    modifier = Modifier.testTag("tab_about")
                                )
                            }
                        }
                    }
                ) { padding ->
                    // Immersive view screen-flipper utilizing crossfades
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "Portal Transition"
                        ) { targetTab ->
                            when (targetTab) {
                                ActivePortalTab.HOME -> {
                                    PortalHomeScreen(
                                        viewModel = viewModel,
                                        onNavigateToTab = { currentTab = it },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                ActivePortalTab.LAND_MONITORING -> {
                                    PortalLandMonitoringScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                ActivePortalTab.BUY -> {
                                    LaunchedEffect(Unit) {
                                        viewModel.setIsRentalFilter(false)
                                    }
                                    PortalMarketplaceScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                ActivePortalTab.RENT -> {
                                    LaunchedEffect(Unit) {
                                        viewModel.setIsRentalFilter(true)
                                    }
                                    PortalMarketplaceScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                ActivePortalTab.ABOUT -> {
                                    PortalSqlViewerScreen(
                                        modifier = Modifier.fillMaxSize()
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
