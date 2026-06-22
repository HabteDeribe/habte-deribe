package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
}

@Dao
interface LandParcelDao {
    @Query("SELECT * FROM land_parcels")
    fun getAllParcels(): Flow<List<LandParcel>>

    @Query("SELECT * FROM land_parcels WHERE id = :parcelId LIMIT 1")
    suspend fun getParcelById(parcelId: String): LandParcel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParcel(parcel: LandParcel)

    @Update
    suspend fun updateParcel(parcel: LandParcel)

    @Query("UPDATE land_parcels SET status = :status WHERE id = :parcelId")
    suspend fun updateParcelStatus(parcelId: String, status: String)
}

@Dao
interface ConstructionApplicationDao {
    @Query("SELECT * FROM construction_applications ORDER BY submissionDate DESC")
    fun getAllApplications(): Flow<List<ConstructionApplication>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(app: ConstructionApplication)

    @Update
    suspend fun updateApplication(app: ConstructionApplication)
}

@Dao
interface ViolationDao {
    @Query("SELECT * FROM violations ORDER BY timestamp DESC")
    fun getAllViolations(): Flow<List<Violation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViolation(violation: Violation)

    @Update
    suspend fun updateViolation(violation: Violation)
}

@Dao
interface PropertyListingDao {
    @Query("SELECT * FROM property_listings ORDER BY listingDate DESC")
    fun getAllListings(): Flow<List<PropertyListing>>

    @Query("SELECT * FROM property_listings WHERE location = :location")
    fun getListingsByLocation(location: String): Flow<List<PropertyListing>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: PropertyListing)

    @Delete
    suspend fun deleteListing(listing: PropertyListing)
}
