package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LandParcel
import com.example.data.Violation
import com.example.data.ConstructionApplication
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun PortalCommandCenterScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val parcels by viewModel.allParcels.collectAsState()
    val violations by viewModel.allViolations.collectAsState()
    val applications by viewModel.allApplications.collectAsState()
    val selectedParcel by viewModel.selectedParcel.collectAsState()
    val scope = rememberCoroutineScope()

    // Map configuration toggles
    var showBorders by remember { mutableStateOf(true) }
    var showInfringements by remember { mutableStateOf(true) }
    var useSatelliteStyle by remember { mutableStateOf(true) }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalSurface)
    ) {
        // LEFT SIDE: GIS Map Container (60% weight on large layouts)
        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            // Header Stats Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AI Urban Land Monitoring",
                        style = MaterialTheme.typography.titleLarge,
                        color = Emerald40,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Adama Municipal Operations Center • Real-time GPS Scan",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Quick statistics indicator
                Card(
                    colors = CardDefaults.cardColors(containerColor = CharcoalLight),
                    border = BorderStroke(1.dp, CardSlate)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(WarningRed)
                        )
                        Text(
                            text = "${parcels.count { it.status == "Flagged" }} Anomalies Detected",
                            color = WarningRed,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Interactive GIS Map Mock Graphics Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (useSatelliteStyle) Color(0xFF031411) else Color(0xFFF8FAFC))
                    .border(2.dp, CardSlate, RoundedCornerShape(16.dp))
                    .testTag("gis_map_canvas")
            ) {
                // Customized Grid lines or background drawing
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            // Cycle selected parcels on tap
                            if (parcels.isNotEmpty()) {
                                val currentIndex = parcels.indexOf(selectedParcel)
                                val nextIndex = (currentIndex + 1) % parcels.size
                                viewModel.selectParcel(parcels[nextIndex])
                            }
                        }
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    // 1. Draw Radar scanning grids
                    val gridSpacing = 60.dp.toPx()
                    val gridColors = if (useSatelliteStyle) Color(0x1A10B981) else Color(0x1E0C625F)
                    for (x in 0 until (canvasWidth / gridSpacing).toInt()) {
                        drawLine(
                            color = gridColors,
                            start = Offset(x * gridSpacing, 0f),
                            end = Offset(x * gridSpacing, canvasHeight),
                            strokeWidth = 1f
                        )
                    }
                    for (y in 0 until (canvasHeight / gridSpacing).toInt()) {
                        drawLine(
                            color = gridColors,
                            start = Offset(0f, y * gridSpacing),
                            end = Offset(canvasWidth, y * gridSpacing),
                            strokeWidth = 1f
                        )
                    }

                    // 2. Plot Land Parcels as geometric shapes on the mock canvas representation
                    parcels.forEachIndexed { idx, parcel ->
                        // Dynamically scale/offset each mock map region
                        val (offsetX, offsetY, width, height) = when (parcel.id) {
                            "LP-BOLE-882" -> QuadValues(0.15f * canvasWidth, 0.25f * canvasHeight, 0.35f * canvasWidth, 0.25f * canvasHeight)
                            "LP-KAZ-334" -> QuadValues(0.55f * canvasWidth, 0.40f * canvasHeight, 0.35f * canvasWidth, 0.22f * canvasHeight)
                            "LP-CMC-491" -> QuadValues(0.25f * canvasWidth, 0.60f * canvasHeight, 0.45f * canvasWidth, 0.25f * canvasHeight)
                            else -> QuadValues(0.12f * canvasWidth, 0.12f * canvasHeight, 0.12f * canvasWidth, 0.12f * canvasHeight)
                        }

                        val fillAlpha = if (selectedParcel?.id == parcel.id) 0.50f else 0.25f
                        val areaColor = when (parcel.status) {
                            "Approved" -> Color(0xFF10B981) // Green
                            "Flagged" -> WarningRed // Red
                            "Under Review" -> Color(0xFFF59E0B) // Amber
                            else -> Color.DarkGray
                        }

                        // Fill Region
                        drawRoundRect(
                            color = areaColor.copy(alpha = fillAlpha),
                            topLeft = Offset(offsetX, offsetY),
                            size = Size(width, height),
                            cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                        )

                        // Draw borders if Layer toggle checked
                        if (showBorders) {
                            drawRoundRect(
                                color = if (selectedParcel?.id == parcel.id) Color.White else areaColor,
                                topLeft = Offset(offsetX, offsetY),
                                size = Size(width, height),
                                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                                style = Stroke(
                                    width = if (selectedParcel?.id == parcel.id) 4f else 2f,
                                    pathEffect = if (parcel.status == "Flagged") PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f) else null
                                )
                            )
                        }

                        // Interactively display violation alert hotspots
                        if (showInfringements && parcel.status == "Flagged") {
                            drawCircle(
                                color = WarningRed,
                                center = Offset(offsetX + width/2, offsetY + height/2),
                                radius = 16.dp.toPx() + (5 * Math.sin(System.currentTimeMillis() / 200.0).toFloat()).toDp().toPx(),
                                style = Stroke(width = 2f)
                            )
                            drawCircle(
                                color = WarningRed.copy(alpha = 0.4f),
                                center = Offset(offsetX + width/2, offsetY + height/2),
                                radius = 8.dp.toPx()
                            )
                        }

                        // Text labels inside plots
                        // Draw label is simplified
                    }
                }

                // Map HUD Layers Overlay
                val hudBg = if (useSatelliteStyle) CharcoalDark.copy(alpha = 0.85f) else CharcoalLight.copy(alpha = 0.95f)
                val hudText = if (useSatelliteStyle) Color.White else CharcoalDark
                val hudSubText = if (useSatelliteStyle) Color.LightGray else Color.DarkGray

                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(14.dp)
                        .background(hudBg, RoundedCornerShape(12.dp))
                        .border(1.dp, CardSlate, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "MAP CONTROLS",
                        color = hudText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )

                    ToggleRow(
                        label = "Show Plot Outlines",
                        checked = showBorders,
                        textColor = hudSubText,
                        onCheckedChange = { showBorders = it }
                    )
                    ToggleRow(
                        label = "Show AI Infringements",
                        checked = showInfringements,
                        textColor = hudSubText,
                        onCheckedChange = { showInfringements = it }
                    )
                    ToggleRow(
                        label = "Satellite Overlay",
                        checked = useSatelliteStyle,
                        textColor = hudSubText,
                        onCheckedChange = { useSatelliteStyle = it }
                    )
                }

                // Legend HUD at bottom
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp)
                        .background(hudBg, RoundedCornerShape(8.dp))
                        .border(1.dp, CardSlate, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LegendItem("Approved", Color(0xFF10B981), hudText)
                    LegendItem("Under Review", Color(0xFFF59E0B), hudText)
                    LegendItem("AI Violation Flag", WarningRed, hudText)
                }

                // Interactive Instructions Tip
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .background(Emerald40.copy(alpha = 0.95f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Tap areas to query",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Official GIS Parcel Details Desk (Reactive on selections)
            selectedParcel?.let { parcel ->
                val borderCol by animateColorAsState(
                    targetValue = when (parcel.status) {
                        "Approved" -> Emerald40
                        "Flagged" -> WarningRed
                        else -> Gold40
                    }, label = "Status Border"
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = CharcoalLight),
                    border = BorderStroke(1.dp, borderCol),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = parcel.id,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalDark
                                )
                                Text(
                                    text = "Zoning Authority Permit • Addis Ababa",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            StatusPill(parcel.status)
                        }

                        Divider(color = CardSlate, modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DetailItem("Registered Owner", parcel.ownerName, Modifier.weight(1f))
                            DetailItem("Approved Use", parcel.approvedUse, Modifier.weight(1f))
                            DetailItem("Parcel Area", "${parcel.areaSqMeter} Sq.M", Modifier.weight(0.8f))
                        }

                        if (parcel.status == "Flagged") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(WarningRed.copy(alpha = 0.15f))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Filled.Warning, "Danger Alert", tint = WarningRed)
                                Text(
                                    text = "AI ALERT: Site discrepancy logged. Multi-storey height violation flagged or pending fine action.",
                                    color = WarningRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // RIGHT SIDE: Live Scrolling Alert Center & Administrative Actions (40% weight)
        Column(
            modifier = Modifier
                .weight(0.8f)
                .fillMaxHeight()
                .background(CharcoalLight)
                .drawBehind {
                    drawLine(
                        color = CardSlate,
                        start = Offset(0f, 0f),
                        end = Offset(0f, this.size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                .padding(16.dp)
        ) {
            Text(
                "AI ALERTS & AUDITING GATE",
                style = MaterialTheme.typography.titleMedium,
                color = Emerald40,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                "Real-time Urban Spatial Anomalies",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Alert List Wrapper
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (violations.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No environmental anomalies detected", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(violations) { alert ->
                            ViolationAlertRow(alert = alert, onAlertClick = {
                                // Select corresponding parcel automatically if available in DB
                                parcels.find { it.id == alert.parcelId }?.let { p ->
                                    viewModel.selectParcel(p)
                                }
                            })
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lower Pane: Citizen Blueprint Verification Gate
            Text(
                "BUILDING APPLICATIONS",
                style = MaterialTheme.typography.titleSmall,
                color = CharcoalDark,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                border = BorderStroke(1.dp, CardSlate),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (applications.isEmpty()) {
                        Text(
                            text = "No pending engineering designs in review.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    } else {
                        val firstApp = applications.first()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = firstApp.projectTitle,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CharcoalDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Gold40.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    firstApp.statusStep,
                                    fontSize = 10.sp,
                                    color = Gold40,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = "Submitted by ${firstApp.applicantName} for Parcel ${firstApp.parcelId}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        viewModel.updateParcelStatus(firstApp.parcelId, "Approved")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Emerald40),
                                modifier = Modifier.weight(1f).height(36.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Approve Layout", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        viewModel.updateParcelStatus(firstApp.parcelId, "Flagged")
                                    }
                                },
                                border = BorderStroke(1.dp, WarningRed),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningRed),
                                modifier = Modifier.weight(1f).height(36.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Flag Layout", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Map layer toggle helper row
@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    textColor: Color = Color.Gray,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = textColor
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Emerald40,
                checkedTrackColor = Emerald40.copy(alpha = 0.3f),
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color(0xFFE2E8F0)
            ),
            modifier = Modifier.height(20.dp)
        )
    }
}

@Composable
private fun LegendItem(label: String, color: Color, textColor: Color = Color.White) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Text(text = label, color = textColor, fontSize = 11.sp)
    }
}

@Composable
private fun ViolationAlertRow(
    alert: Violation,
    onAlertClick: () -> Unit
) {
    val isSevere = alert.severity == "Severe"
    val borderCol = if (isSevere) WarningRed else CardSlate

    Card(
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        border = BorderStroke(1.dp, borderCol),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAlertClick)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSevere) WarningRed else Gold40)
                    )
                    Text(
                        text = alert.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSevere) WarningRed.copy(alpha = 0.15f) else Gold40.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = alert.severity,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSevere) WarningRed else Gold40
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = alert.description,
                fontSize = 11.sp,
                color = Color.Gray,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: ${alert.parcelId} • GPS: ${alert.gpsCoordinates}",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Reported by: ${alert.reporterName}",
                    fontSize = 10.sp,
                    color = Emerald40,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun StatusPill(status: String) {
    val (backCol, txtCol) = when (status) {
        "Approved" -> Pair(Color(0xFFE6F4EA), Color(0xFF137333))
        "Flagged" -> Pair(WarningRedSoft, WarningRed)
        else -> Pair(Gold80.copy(alpha = 0.3f), Gold40)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backCol)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = txtCol
        )
    }
}

@Composable
private fun DetailItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Text(text = value, fontSize = 13.sp, color = CharcoalDark, fontWeight = FontWeight.SemiBold)
    }
}

data class QuadValues(
    val x: Float,
    val y: Float,
    val w: Float,
    val h: Float
)
