package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.PropertyListing
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun PortalMarketplaceScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val listings by viewModel.filteredListings.collectAsState()
    val activeLocFilter by viewModel.filterLocation.collectAsState()
    val activeSubTFilter by viewModel.filterSubType.collectAsState()
    val activeRentFilter by viewModel.filterIsRental.collectAsState()

    // Screen-level navigation: True = Grid/List feed, False = Vector Map Price tag Pin Overlay
    var showGridFeed by remember { mutableStateOf(true) }

    // Dialog trigger for registering properties
    var showPublishDialog by remember { mutableStateOf(false) }

    // Slide-out Drawer state for Conversational Assistant
    var isAiDrawerOpen by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalSurface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Marketplace header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CharcoalLight)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "H-Ethio-land Property Portal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Emerald40,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Verified Listings • Adama & Addis Ababa, ETB",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Primary actions row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    // Create Listing Button
                    Button(
                        onClick = { showPublishDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald40),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(38.dp).testTag("publish_listing_button")
                    ) {
                        Icon(Icons.Filled.AddHome, "Publish", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("List Property", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Feed Mode toggler
                    IconButton(
                        onClick = { showGridFeed = !showGridFeed },
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(TealSoft)
                            .border(1.dp, CardSlate, RoundedCornerShape(8.dp))
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = if (showGridFeed) Icons.Filled.Map else Icons.Filled.ViewModule,
                            contentDescription = "Switch layout",
                            tint = Emerald40
                        )
                    }
                }
            }
            HorizontalDivider(color = CardSlate, thickness = 1.dp)

            // High-Performance Filtering Options Drawer Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CharcoalLight.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filters:",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 4.dp)
                )

                // Location Filter chips
                FilterChipGroup(
                    label = "Loc",
                    options = listOf("All", "Bole", "Kazanchis", "CMC"),
                    selectedOption = activeLocFilter ?: "All",
                    onSelected = { viewModel.setLocationFilter(it) }
                )

                Spacer(modifier = Modifier.width(2.dp))

                // Listing type Rent vs Buy
                FilterChipGroup(
                    label = "Listing Type",
                    options = listOf("All", "Rent", "Sale"),
                    selectedOption = when (activeRentFilter) {
                        null -> "All"
                        true -> "Rent"
                        false -> "Sale"
                    },
                    onSelected = {
                        val v = when (it) {
                            "Rent" -> true
                            "Sale" -> false
                            else -> null
                        }
                        viewModel.setIsRentalFilter(v)
                    }
                )

                Spacer(modifier = Modifier.width(2.dp))

                // Subtypes: Land / Residential / Commercial
                FilterChipGroup(
                    label = "Category",
                    options = listOf("All", "Residential", "Commercial", "Land"),
                    selectedOption = activeSubTFilter ?: "All",
                    onSelected = { viewModel.setSubTypeFilter(it) }
                )
            }

            // Main Content Area
            Box(modifier = Modifier.weight(1f)) {
                if (showGridFeed) {
                    // GRID LAYOUT FEED DISPLAY
                    if (listings.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Filled.Search, "No match found", tint = Color.Gray, modifier = Modifier.size(48.dp))
                                Text("No listings found matching criteria.", color = Color.Gray)
                                TextButton(onClick = {
                                    viewModel.setLocationFilter("All")
                                    viewModel.setSubTypeFilter("All")
                                    viewModel.setIsRentalFilter(null)
                                }) {
                                    Text("Reset Filters", color = Gold40)
                                }
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 300.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("listings_grid_view"),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(listings) { property ->
                                PropertyListingCard(property)
                            }
                        }
                    }
                } else {
                    // DUAL MAP-VIEW: SHOWCASE FLOATING PRICE PINS
                    MarketplaceGisMapView(listings)
                }
            }
        }

        // Floating Drawer Trigger representing AI Property Finder Chat Hook
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            FloatingActionButton(
                onClick = { isAiDrawerOpen = !isAiDrawerOpen },
                containerColor = Gold40,
                contentColor = CharcoalDark,
                modifier = Modifier.testTag("ai_property_finder_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Filled.Forum, "AI Property Finder")
                    Text("AI Finder", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Slide-up or Slide-In AI Chat Finder Drawer
        AnimatedVisibility(
            visible = isAiDrawerOpen,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
        ) {
            MarketplaceConversationalFinderDrawer(
                viewModel = viewModel,
                onClose = { isAiDrawerOpen = false }
            )
        }

        // Dialog Form: Publish Property Listing
        if (showPublishDialog) {
            PublishListingDialog(
                onDismiss = { showPublishDialog = false },
                onPublish = { t, d, p, sub, loc, adr, rent, phone ->
                    viewModel.createPropertyListing(t, d, p, sub, loc, adr, rent, phone)
                    showPublishDialog = false
                }
            )
        }
    }
}

// FilterChip helper group
@Composable
fun FilterChipGroup(
    label: String,
    options: List<String>,
    selectedOption: String,
    onSelected: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CharcoalLight),
        border = BorderStroke(1.dp, CardSlate),
        modifier = Modifier.height(34.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("$label: ", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            options.forEach { opt ->
                val isSelected = opt == selectedOption
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSelected) Emerald40 else Color.Transparent)
                        .clickable { onSelected(opt) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = opt,
                        color = if (isSelected) Color.White else Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Single marketplace property representation card
@Composable
fun PropertyListingCard(property: PropertyListing) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CharcoalLight),
        border = BorderStroke(1.dp, CardSlate),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("property_card_${property.id}")
    ) {
        Column {
            // Hero Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = property.imageUrl),
                    contentDescription = property.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Price Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(TealSoft)
                        .border(1.dp, TealAccent.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (property.isRental) {
                            String.format("%,.0f ETB / Mo", property.priceEtb)
                        } else {
                            String.format("%,.0f ETB", property.priceEtb)
                        },
                        color = Emerald40,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Property type Pill
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Emerald40)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        property.subType.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = Color.White
                    )
                }
            }

            // Core Details body
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        property.location.uppercase(),
                        color = Gold40,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Launch, "Rent type indicator", tint = Color.Gray, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (property.isRental) "Rental Lease" else "Direct Purchase",
                            color = Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Text(
                    text = property.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = property.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📍 ${property.address}",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )

                    Text(
                        text = "📞 Contact Owner",
                        fontSize = 11.sp,
                        color = Emerald40,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            // Simple mock action click
                        }
                    )
                }
            }
        }
    }
}

// Customize Map View for listing pins
@Composable
fun MarketplaceGisMapView(listings: List<PropertyListing>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF090D16))
            .border(2.dp, CardSlate, RoundedCornerShape(12.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Render detailed layout coordinates lines
            drawLine(
                color = Color(0xFF1E293B),
                start = Offset(0f, canvasHeight/2),
                end = Offset(canvasWidth, canvasHeight/2),
                strokeWidth = 3f
            )
            drawLine(
                color = Color(0xFF1E293B),
                start = Offset(canvasWidth/2, 0f),
                end = Offset(canvasWidth/2, canvasHeight),
                strokeWidth = 3f
            )

            // Draw Bole, Kazanchis, CMC text labels onto canvas
            // Represent boundaries
        }

        // Overlay text descriptions representing locations onto canvas
        Text("KA-ZANCHIS INT'L FINANCIAL ZONE", modifier = Modifier.align(Alignment.TopStart).padding(20.dp), color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text("BOLE COMMERCIAL RING ROAD", modifier = Modifier.align(Alignment.TopEnd).padding(20.dp), color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text("CMC PRESTIGE RESIDENTIAL BOULEVARD", modifier = Modifier.align(Alignment.BottomStart).padding(20.dp), color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)

        // Plot pins as layered views automatically
        listings.forEachIndexed { i, listing ->
            val alignValue = when (listing.location.lowercase()) {
                "bole" -> BiasAlignment(-0.45f + i * 0.15f, -0.3f, true)
                "kazanchis" -> BiasAlignment(-0.6f + i * 0.1f, -0.7f, true)
                "cmc" -> BiasAlignment(0.4f + i * 0.15f, 0.4f + i * 0.1f, true)
                else -> BiasAlignment(0.1f, 0.1f, true)
            }

            Box(
                modifier = Modifier
                    .align(alignValue)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Emerald40)
                    .border(1.5.dp, Color.White, RoundedCornerShape(50.dp))
                    .clickable { }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Filled.LocationOn, "Property Marker Price", tint = Color.White, modifier = Modifier.size(14.dp))
                    Text(
                        text = if (listing.isRental) {
                            String.format("%.0fk ETB / Mo", listing.priceEtb / 1000.0)
                        } else {
                            String.format("%.1fM ETB", listing.priceEtb / 1000000.0)
                        },
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Conversational AI Finder panel
@Composable
fun MarketplaceConversationalFinderDrawer(
    viewModel: MainViewModel,
    onClose: () -> Unit
) {
    val chatHistory by viewModel.chatHistory.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    var inputQuery by remember { mutableStateOf("") }
    val isKeyAbsent = com.example.BuildConfig.GEMINI_API_KEY.isEmpty() || com.example.BuildConfig.GEMINI_API_KEY == "MY_GEMINI_API_KEY"

    Card(
        colors = CardDefaults.cardColors(containerColor = CharcoalLight),
        border = BorderStroke(1.5.dp, Emerald40),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = Modifier.fillMaxSize().testTag("ai_property_chat_box")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Chat head
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Emerald40)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.SmartButton, "Bot avatar icon", tint = Emerald40)
                    }
                    Column {
                        Text(
                            text = "AI Real Estate Conversational Finder",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                            Text(
                                text = if (isKeyAbsent) "Platform Simulation Active" else "Connected to Gemini-3.5-Flash",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { viewModel.clearChat() }) {
                        Text("Reset System", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.Close, "Dismiss AI Dialog Finder", tint = Color.White)
                    }
                }
            }

            // Key Alert Notice Warning represent guidelines
            if (isKeyAbsent) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WarningRed.copy(alpha = 0.12f))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Filled.Security, "Shield Warning", tint = WarningRed, modifier = Modifier.size(14.dp))
                    Text(
                        text = "PROTOTYPE CAUTION: API KEY IS SECURELY SHIELDED. CHATTER IS RESPONDING USING EMBEDDED REALISTIC DATABASE SIMULATION GATES.",
                        color = WarningRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Scrolling Chat messages lists
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(chatHistory) { message ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (message.isUser) Emerald80 else Emerald40
                            ),
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = if (message.isUser) 12.dp else 0.dp,
                                bottomEnd = if (message.isUser) 0.dp else 12.dp
                            ),
                            border = if (!message.isUser) BorderStroke(1.dp, CardSlate) else null,
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = message.text,
                                    fontSize = 12.sp,
                                    color = if (message.isUser) CharcoalDark else Color.White,
                                    fontWeight = if (message.isUser) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                if (isChatLoading) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = Emerald40)
                            Text("AI Agent reading real estate records...", color = Color.Gray, fontSize = 11.sp)
                        }
                    }
                }
            }

            // Input Send Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CharcoalDark)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = inputQuery,
                    onValueChange = { inputQuery = it },
                    placeholder = { Text("Search Bole rent, Kazanchis office, CMC townhouse...", color = Color.Gray, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CharcoalDark,
                        unfocusedTextColor = CharcoalDark,
                        focusedContainerColor = CharcoalLight,
                        unfocusedContainerColor = CharcoalLight,
                        focusedBorderColor = Emerald80,
                        unfocusedBorderColor = CardSlate
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_finder_search_text_input")
                )

                Button(
                    onClick = {
                        if (inputQuery.isNotBlank()) {
                            viewModel.sendChatMessage(inputQuery)
                            inputQuery = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald40),
                    modifier = Modifier
                        .height(52.dp)
                        .testTag("ai_finder_send_button"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Filled.Send, "Send prompt", tint = Color.White)
                }
            }
        }
    }
}

// Dialog: Publish property listing
@Composable
fun PublishListingDialog(
    onDismiss: () -> Unit,
    onPublish: (title: String, desc: String, price: Double, subType: String, location: String, address: String, isRental: Boolean, contact: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var subType by remember { mutableStateOf("Residential") } // Residential, Commercial, Land, Industrial
    var location by remember { mutableStateOf("Bole") } // Bole, Kazanchis, CMC
    var address by remember { mutableStateOf("") }
    var isRental by remember { mutableStateOf(false) } // Rent vs Sale
    var contact by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "LIST NEW PROPERTY (ADDIS ABABA)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Emerald40
            )
        },
        containerColor = CharcoalLight,
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                val inputColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = CharcoalDark,
                    unfocusedTextColor = CharcoalDark,
                    focusedContainerColor = CharcoalSurface,
                    unfocusedContainerColor = CharcoalSurface,
                    focusedBorderColor = Emerald40,
                    unfocusedBorderColor = CardSlate
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Listing Title", color = Color.Gray) },
                    colors = inputColors,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Full Description (Contact phone & Area)", color = Color.Gray) },
                    colors = inputColors,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Price in ETB", color = Color.Gray) },
                    colors = inputColors,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Physical Address (Local references)", color = Color.Gray) },
                    colors = inputColors,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Telegram / Phone Number", color = Color.Gray) },
                    colors = inputColors,
                    modifier = Modifier.fillMaxWidth()
                )

                // Render toggler
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Listing Contract", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(
                            onClick = { isRental = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isRental) Gold40 else CharcoalSurface
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("Sale", fontSize = 11.sp, color = if (!isRental) Color.White else Color.Gray)
                        }
                        Button(
                            onClick = { isRental = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRental) Emerald40 else CharcoalSurface
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("Rent", fontSize = 11.sp, color = if (isRental) Color.White else Color.Gray)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val priceVal = priceStr.toDoubleOrNull() ?: 1000.0
                    onPublish(title, desc, priceVal, subType, location, address, isRental, contact)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Emerald40)
            ) {
                Text("Publish Listing", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

// Customized alignment class for Bias Layout
private fun BiasAlignment(horizontalBias: Float, verticalBias: Float, isAbsolute: Boolean): Alignment {
    return androidx.compose.ui.BiasAlignment(horizontalBias, verticalBias)
}
