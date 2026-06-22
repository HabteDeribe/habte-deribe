package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LandParcel
import com.example.data.UserRole
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PortalInspectorScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val parcels by viewModel.allParcels.collectAsState()
    val scope = rememberCoroutineScope()

    var selectedParcelId by remember { mutableStateOf("") }
    var violationTitle by remember { mutableStateOf("") }
    var violationDesc by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("High") } // Low, Medium, High, Severe

    // Simulated states
    var isCapturingGps by remember { mutableStateOf(false) }
    var capturedGps by remember { mutableStateOf("") }
    var ratingImageOption by remember { mutableStateOf<String?>(null) } // Attached standard or thermal structure scan
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    // Dropdown expanded states
    var parcelExp by remember { mutableStateOf(false) }
    var severityExp by remember { mutableStateOf(false) }

    LaunchedEffect(parcels) {
        if (parcels.isNotEmpty() && selectedParcelId.isEmpty()) {
            selectedParcelId = parcels.first().id
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalSurface)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Rugged Inspector Header
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalLight),
                border = BorderStroke(1.5.dp, Emerald40),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Emerald40.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Build, "Rugged tools", tint = Emerald40, modifier = Modifier.size(24.dp))
                    }
                    Column {
                        Text(
                            text = "Field Auditing Companion",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Emerald40
                        )
                        Text(
                            text = "Officer: Inspector Habte Zeleke • Online Feed",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Parcel Target Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "TARGET LAND PLOT ASSIGNMENT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald40
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { parcelExp = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("inspector_parcel_dropdown"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = CharcoalLight,
                            contentColor = CharcoalDark
                        ),
                        border = BorderStroke(1.dp, CardSlate),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedParcelId.isEmpty()) "Select Assigned Parcel" else "Active Target: $selectedParcelId",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Icon(Icons.Filled.ArrowDropDown, "Open details")
                        }
                    }

                    DropdownMenu(
                        expanded = parcelExp,
                        onDismissRequest = { parcelExp = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(CharcoalLight)
                            .border(1.dp, CardSlate)
                    ) {
                        parcels.forEach { p ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(p.id, color = CharcoalDark, fontWeight = FontWeight.Bold)
                                        Text("Owner: ${p.ownerName} • ${p.approvedUse}", color = Color.Gray, fontSize = 11.sp)
                                    }
                                },
                                onClick = {
                                    selectedParcelId = p.id
                                    parcelExp = false
                                }
                            )
                        }
                    }
                }
            }

            // Real-Time GPS Coordinate Capture Gate
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalLight),
                border = BorderStroke(1.dp, CardSlate),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "REAL-TIME GPS TELEMETRY INGESTION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalDark
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = capturedGps,
                            onValueChange = { capturedGps = it },
                            placeholder = { Text("Capture GPS or enter manually", color = Color.Gray, fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CharcoalDark,
                                unfocusedTextColor = CharcoalDark,
                                focusedContainerColor = CharcoalSurface,
                                unfocusedContainerColor = CharcoalSurface,
                                focusedBorderColor = Emerald40,
                                unfocusedBorderColor = CardSlate
                            ),
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("gps_input_field"),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )

                        Button(
                            onClick = {
                                scope.launch {
                                    isCapturingGps = true
                                    capturedGps = "Acquiring satellites..."
                                    delay(1500)
                                    // Simulate localized GIS coordinates around selected parcel
                                    val par = parcels.find { it.id == selectedParcelId }
                                    capturedGps = if (par != null) {
                                        val latErr = (Math.random() - 0.5) * 0.001
                                        val lngErr = (Math.random() - 0.5) * 0.001
                                        String.format("%.6f, %.6f", par.centerLatitude + latErr, par.centerLongitude + lngErr)
                                    } else {
                                        "9.021245, 38.745621"
                                    }
                                    isCapturingGps = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald40),
                            modifier = Modifier
                                .weight(0.7f)
                                .height(52.dp)
                                .testTag("capture_gps_button"),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            if (isCapturingGps) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Filled.LocationOn, "Acquire Satellites", tint = Color.White)
                                    Text("Capture", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // High-Contrast Asset File Upload Drop-Zone
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "VISUAL AUDITING BLUEPRINTS & EVIDENCE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald40
                )

                // Drag/Drop simulated container representation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(CharcoalLight, RoundedCornerShape(12.dp))
                        .dashedBorder(1.dp, CardSlate, 12.dp)
                        .clickable {
                            // Cycle simple high-quality standard thermal architectural/reinforcement images
                            ratingImageOption = when (ratingImageOption) {
                                null -> "standard_evidence"
                                "standard_evidence" -> "thermal_scan"
                                else -> null
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (ratingImageOption == null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.AddAPhoto, "Asset Drop Upload", tint = Emerald40, modifier = Modifier.size(36.dp))
                            Text(
                                text = "UPLOAD RADAR / THERMAL CAMERA BLOB",
                                color = CharcoalDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Tap to upload site reinforcement photo (.png, .tiff)",
                                color = Color.Gray,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (ratingImageOption == "thermal_scan") Icons.Filled.Face else Icons.Filled.CheckCircle,
                                    contentDescription = "Success Upload",
                                    tint = Emerald40
                                )
                                Text(
                                    text = if (ratingImageOption == "thermal_scan") "THM_RADAR_SCAN_491.TIFF (Thermal)" else "ST_EVIDENCE_PHOTO_882.PNG",
                                    color = Emerald40,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Discrepancy attachment ready for automated municipal dashboard update. Tap again to clear.",
                                color = Color.Gray,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Discrepancy Details & Automated Alerts parameters
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "DISCREPANCY CHARACTERISTICS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald40
                )

                OutlinedTextField(
                    value = violationTitle,
                    onValueChange = { violationTitle = it },
                    label = { Text("Violation / Discrepancy Title", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CharcoalDark,
                        unfocusedTextColor = CharcoalDark,
                        focusedContainerColor = CharcoalLight,
                        unfocusedContainerColor = CharcoalLight,
                        focusedBorderColor = WarningRed,
                        unfocusedBorderColor = CardSlate
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("discrepancy_title_input")
                )

                OutlinedTextField(
                    value = violationDesc,
                    onValueChange = { violationDesc = it },
                    label = { Text("Evidence Description & Architectural Deviation", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CharcoalDark,
                        unfocusedTextColor = CharcoalDark,
                        focusedContainerColor = CharcoalLight,
                        unfocusedContainerColor = CharcoalLight,
                        focusedBorderColor = WarningRed,
                        unfocusedBorderColor = CardSlate
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .testTag("discrepancy_desc_input")
                )

                // Severity Selection Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Severity Classification", color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    Box {
                        OutlinedButton(
                            onClick = { severityExp = true },
                            border = BorderStroke(1.dp, CardSlate),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CharcoalDark)
                        ) {
                            Text(severity, fontWeight = FontWeight.Bold)
                        }

                        DropdownMenu(
                            expanded = severityExp,
                            onDismissRequest = { severityExp = false },
                            modifier = Modifier.background(CharcoalLight).border(1.dp, CardSlate)
                        ) {
                            listOf("Low", "Medium", "High", "Severe").forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s, color = CharcoalDark, fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        severity = s
                                        severityExp = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Trigger: Submit Discrepancy
            Button(
                onClick = {
                    if (selectedParcelId.isEmpty() || violationTitle.isBlank() || violationDesc.isBlank()) {
                        snackbarMessage = "Please complete the Active target, Title and Description!"
                        showSnackbar = true
                        return@Button
                    }
                    val finalGps = if (capturedGps.startsWith("Acquiring") || capturedGps.isEmpty()) "9.0212, 38.7456" else capturedGps
                    viewModel.submitViolation(
                        parcelId = selectedParcelId,
                        title = violationTitle,
                        description = violationDesc,
                        severity = severity,
                        gps = finalGps
                    )
                    snackbarMessage = "🎉 Discrepancy Dispatched! Land parcel $selectedParcelId status modified to FLAG in municipal dashboard."
                    showSnackbar = true

                    // Reset values
                    violationTitle = ""
                    violationDesc = ""
                    capturedGps = ""
                    ratingImageOption = null
                },
                colors = ButtonDefaults.buttonColors(containerColor = WarningRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_discrepancy_button"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.Gavel, "Dispatch Discrepancy", tint = Color.White)
                    Text("SUBMIT DISCREPANCY & NOTIFY COMMAND", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                }
            }
        }

        // Rugged Floating Snackbar for immediate dispatch verification!
        AnimatedVisibility(
            visible = showSnackbar,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalDark),
                border = BorderStroke(1.dp, Emerald40),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = snackbarMessage,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showSnackbar = false }) {
                        Icon(Icons.Filled.Close, "Dismiss feedback", tint = Color.White)
                    }
                }
            }
        }
    }
}

// Custom extension function to easily model dashed border on drop-zones
fun Modifier.dashedBorder(width: Dp = 1.dp, color: Color, radius: Dp = 0.dp) = drawBehind {
    val stroke = Stroke(
        width = width.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )
    drawRoundRect(
        color = color,
        style = stroke,
        cornerRadius = CornerRadius(radius.toPx(), radius.toPx())
    )
}
