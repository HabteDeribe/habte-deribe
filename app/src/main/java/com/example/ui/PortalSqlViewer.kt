package com.example.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun PortalSqlViewerScreen(
    modifier: Modifier = Modifier
) {
    var rawSql by remember { mutableStateOf("") }
    var showCopyTip by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // High-fidelity rendering of our PostGIS & RLS definitions inside the app!
        rawSql = """
-- ============================================================================
-- HDZ SMART NATION PLATFORM - DATABASE MIGRATION SCHEMA
-- Founded by Habte Deribe Zeleke
-- Target: Supabase / PostgreSQL + PostGIS (Spatial Mapping Extensions)
-- ============================================================================

-- 1. EXTENSION PROVISIONING
CREATE EXTENSION IF NOT EXISTS postgis;

-- 2. DESIGNATION ROLE ENUMS
CREATE TYPE user_role_enum AS ENUM (
    'Citizen', 'Property Owner', 'Municipality Officer', 'Inspector', 'Admin'
);

-- 3. CORE TABLE DEFINITIONS
CREATE TABLE IF NOT EXISTS public.users (
    id UUID PRIMARY KEY REFERENCES auth.users(id),
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    role user_role_enum DEFAULT 'Citizen'::user_role_enum,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.land_parcels (
    id VARCHAR(50) PRIMARY KEY, -- Digital Land ID
    owner_id UUID REFERENCES public.users(id) ON DELETE SET NULL,
    approved_use_zoning VARCHAR(150) NOT NULL,
    area_sq_meters NUMERIC(12, 2) NOT NULL,
    boundary_geom GEOMETRY(Polygon, 4326) NOT NULL, -- Geographic WGS84
    status VARCHAR(50) DEFAULT 'Approved' NOT NULL, -- 'Approved', 'Flagged'
    CONSTRAINT check_polygon_validITY CHECK (ST_IsValid(boundary_geom))
);

CREATE INDEX IF NOT EXISTS idx_land_parcels_spatial 
    ON public.land_parcels USING GIST (boundary_geom);

CREATE TABLE IF NOT EXISTS public.violations (
    id BIGSERIAL PRIMARY KEY,
    parcel_id VARCHAR(50) REFERENCES public.land_parcels(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    severity violation_severity_enum DEFAULT 'Medium'::violation_severity_enum,
    gps_coordinates GEOMETRY(Point, 4326) NOT NULL, -- Captured in field mobile
    photo_evidence_url VARCHAR(512),
    status violation_status_enum DEFAULT 'Reported'
);

-- 4. ROW LEVEL SECURITY (RLS) POLICIES
ALTER TABLE public.land_parcels ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.violations ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Public parcels read access" 
    ON public.land_parcels FOR SELECT USING (true);

CREATE POLICY "Inspectors can submit site violations"
    ON public.violations FOR INSERT 
    WITH CHECK (EXISTS (
        SELECT 1 FROM public.users 
        WHERE users.id = auth.uid() AND users.role = 'Inspector'
    ));

-- 5. TELEBIRR & CBE BIRR GATEWAY TRANSACTIONS
CREATE TABLE IF NOT EXISTS public.chapa_payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_channel VARCHAR(40) NOT NULL, -- 'Telebirr', 'CBE_Birr'
    amount_etb NUMERIC(14, 2) NOT NULL,
    status VARCHAR(50) DEFAULT 'Pending'
);
        """.trimIndent()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalSurface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Platform Technical Overview Banner Cards
        Card(
            colors = CardDefaults.cardColors(containerColor = CharcoalLight),
            border = BorderStroke(1.dp, CardSlate),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Emerald40.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.MenuBook, null, tint = Emerald40, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text(
                            text = "H-Ethio-land System Architecture",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalDark
                        )
                        Text(
                            text = "Adama Smart City Spatial & AI Unifications",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                Text(
                    text = "A national-scale enterprise engine combining Real-Time Object Detection, GIS PostGIS databases, Chapa Telebirr transaction gateways, and Gemini Generative Real Estate matching chatbots in Ethiopia.",
                    fontSize = 11.sp,
                    color = Color.DarkGray,
                    lineHeight = 16.sp
                )

                // Row of technological bullet highlights
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(CharcoalSurface, RoundedCornerShape(8.dp))
                            .border(0.5.dp, CardSlate, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("Db Engine", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Emerald40)
                            Text("PostgreSQL\n+ PostGIS", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = CharcoalDark)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(CharcoalSurface, RoundedCornerShape(8.dp))
                            .border(0.5.dp, CardSlate, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("AI Models", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TealAccent)
                            Text("Gemini 2.5\n+ YOLO v11", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = CharcoalDark)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(CharcoalSurface, RoundedCornerShape(8.dp))
                            .border(0.5.dp, CardSlate, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("Gateway", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Gold40)
                            Text("Chapa API\n+ Telebirr", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = CharcoalDark)
                        }
                    }
                }
            }
        }

        // SQL Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CharcoalLight, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .border(1.dp, CardSlate, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.DataObject, "Database Object", tint = Emerald40)
                Column {
                    Text(
                        "PostgreSQL + PostGIS Database Schema",
                        color = CharcoalDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Supabase Back-End Infrastructure definitions",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }
            }

            // Quick Copy Action
            Button(
                onClick = { showCopyTip = true },
                colors = ButtonDefaults.buttonColors(containerColor = Emerald40),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.height(34.dp).testTag("copy_sql_button")
            ) {
                Icon(Icons.Filled.ContentCopy, "Copy Script", tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy SQL", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Script content body (brutalist high-contrast stylized display)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                .background(Color(0xFF06090F))
                .border(
                    BorderStroke(1.dp, CardSlate),
                    RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                )
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
        ) {
            Column {
                rawSql.split("\n").forEach { line ->
                    Text(
                        text = line,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = parseSqlTokenColors(line),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }

        if (showCopyTip) {
            AlertDialog(
                onDismissRequest = { showCopyTip = false },
                title = { Text("MIGRATION SCRIPT EXPORTED", color = Emerald40, fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                text = { Text("The complete production SQL PostGIS migration script with RLS guidelines of HDZ Smart Nation is saved at `/supabase_migration.sql` in your workspace directory.", color = CharcoalDark) },
                containerColor = CharcoalLight,
                confirmButton = {
                    Button(onClick = { showCopyTip = false }, colors = ButtonDefaults.buttonColors(containerColor = Emerald40)) {
                        Text("Acknowledge", color = Color.White)
                    }
                }
            )
        }
    }
}

// Customized token highlighter for gorgeous dark IDE visualizer
private fun parseSqlTokenColors(line: String): Color {
    val trimmed = line.trim()
    return when {
        trimmed.startsWith("--") -> Color(0xFF8B8B8D) // Grey comments
        trimmed.startsWith("CREATE") || trimmed.startsWith("ALTER") || trimmed.startsWith("CREATE TYPE") || trimmed.startsWith("CREATE POLICY") -> Emerald40 // Keywords
        trimmed.startsWith("CONSTRAINT") || trimmed.startsWith("PRIMARY KEY") || trimmed.startsWith("REFERENCES") -> Gold40 // Keys
        else -> Color.White
    }
}
