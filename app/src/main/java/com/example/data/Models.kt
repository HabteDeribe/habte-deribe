package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

enum class UserRole {
    Citizen, Property_Owner, Municipality_Officer, Inspector, Admin
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val username: String,
    val email: String,
    val role: UserRole
)

@Entity(tableName = "land_parcels")
data class LandParcel(
    @PrimaryKey val id: String, // Digital Land ID
    val ownerName: String,
    val approvedUse: String, // Residential, Commercial, Mixed Use, etc.
    val areaSqMeter: Double,
    val coordinatesJson: String, // Polygon boundary as serialized GPS coordinates e.g., "[[9.021,38.752],[9.024,38.755]...]"
    val centerLatitude: Double,
    val centerLongitude: Double,
    val documentUrl: String = "",
    val status: String = "Approved" // Approved, Under Review, Discrepancy, Flagged
)

@Entity(tableName = "construction_applications")
data class ConstructionApplication(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val parcelId: String,
    val applicantName: String,
    val projectTitle: String,
    val blueprintUrl: String,
    val statusStep: String, // Submitted, In Review, Site Inspection Scheduled, Approved, Rejected
    val submissionDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "violations")
data class Violation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val parcelId: String,
    val title: String,
    val description: String,
    val severity: String, // Low, Medium, High, Severe (AI Alert)
    val reporterName: String,
    val gpsCoordinates: String, // "Latitude, Longitude" e.g., "9.0212, 38.7512"
    val imageUrl: String = "",
    val status: String = "Reported", // Reported, Investigated, Fine Pending, Resolved
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "property_listings")
data class PropertyListing(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val priceEtb: Double,
    val subType: String, // Residential, Commercial, Industrial, Land
    val location: String, // Bole, Kazanchis, CMC
    val address: String,
    val areaSqMeter: Double,
    val contactPhone: String,
    val imageUrl: String,
    val isRental: Boolean, // Rent or Sale
    val latitude: Double,
    val longitude: Double,
    val listingDate: Long = System.currentTimeMillis()
)
