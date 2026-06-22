package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AppRepository(
    private val userDao: UserDao,
    private val landParcelDao: LandParcelDao,
    private val constructionApplicationDao: ConstructionApplicationDao,
    private val violationDao: ViolationDao,
    private val propertyListingDao: PropertyListingDao
) {
    val allUsers: Flow<List<User>> = userDao.getAllUsers()
    val allParcels: Flow<List<LandParcel>> = landParcelDao.getAllParcels()
    val allApplications: Flow<List<ConstructionApplication>> = constructionApplicationDao.getAllApplications()
    val allViolations: Flow<List<Violation>> = violationDao.getAllViolations()
    val allListings: Flow<List<PropertyListing>> = propertyListingDao.getAllListings()

    suspend fun getUserById(userId: String) = userDao.getUserById(userId)
    suspend fun insertUser(user: User) = userDao.insertUser(user)

    suspend fun insertParcel(parcel: LandParcel) = landParcelDao.insertParcel(parcel)
    suspend fun updateParcel(parcel: LandParcel) = landParcelDao.updateParcel(parcel)
    suspend fun updateParcelStatus(parcelId: String, status: String) = landParcelDao.updateParcelStatus(parcelId, status)

    suspend fun insertApplication(app: ConstructionApplication) = constructionApplicationDao.insertApplication(app)
    suspend fun updateApplication(app: ConstructionApplication) = constructionApplicationDao.updateApplication(app)

    suspend fun insertViolation(violation: Violation) = violationDao.insertViolation(violation)
    suspend fun updateViolation(violation: Violation) = violationDao.updateViolation(violation)

    suspend fun insertListing(listing: PropertyListing) = propertyListingDao.insertListing(listing)
    suspend fun deleteListing(listing: PropertyListing) = propertyListingDao.deleteListing(listing)

    suspend fun seedDatabaseIfEmpty() {
        val currentUsers = allUsers.first()
        if (currentUsers.isEmpty()) {
            // Seed default users
            userDao.insertUser(User("u1", "h_deribe", "habte101101@gmail.com", UserRole.Inspector))
            userDao.insertUser(User("u2", "k_solomon", "officer.solomon@addis.gov", UserRole.Municipality_Officer))
            userDao.insertUser(User("u3", "almaz_k", "almaz.k@example.com", UserRole.Citizen))
            userDao.insertUser(User("u4", "admin_hdz", "admin.hdz@government.et", UserRole.Admin))

            // Seed land parcels
            landParcelDao.insertParcel(
                LandParcel(
                    id = "LP-BOLE-882",
                    ownerName = "Habte Deribe Zeleke",
                    approvedUse = "Commercial/Office",
                    areaSqMeter = 1250.0,
                    coordinatesJson = "[[9.0210, 38.7830], [9.0240, 38.7830], [9.0240, 38.7860], [9.0210, 38.7860]]",
                    centerLatitude = 9.0225,
                    centerLongitude = 38.7845,
                    status = "Approved"
                )
            )
            landParcelDao.insertParcel(
                LandParcel(
                    id = "LP-KAZ-334",
                    ownerName = "Bole Horizon Properties",
                    approvedUse = "High-Rise Residential",
                    areaSqMeter = 840.0,
                    coordinatesJson = "[[9.0175, 38.7612], [9.0198, 38.7612], [9.0198, 38.7635], [9.0175, 38.7635]]",
                    centerLatitude = 9.0186,
                    centerLongitude = 38.7623,
                    status = "Flagged"
                )
            )
            landParcelDao.insertParcel(
                LandParcel(
                    id = "LP-CMC-491",
                    ownerName = "Almaz Kenenisa",
                    approvedUse = "Residential Villa",
                    areaSqMeter = 2100.0,
                    coordinatesJson = "[[9.0278, 38.8115], [9.0298, 38.8115], [9.0298, 38.8142], [9.0278, 38.8142]]",
                    centerLatitude = 9.0288,
                    centerLongitude = 38.8128,
                    status = "Under Review"
                )
            )

            // Seed construction applications
            constructionApplicationDao.insertApplication(
                ConstructionApplication(
                    parcelId = "LP-BOLE-882",
                    applicantName = "Habte Deribe Zeleke",
                    projectTitle = "HDZ Executive Plaza & Business Tower",
                    blueprintUrl = "http://smartnation.et/blueprints/hdz-tower-v3.pdf",
                    statusStep = "Site Inspection Scheduled"
                )
            )
            constructionApplicationDao.insertApplication(
                ConstructionApplication(
                    parcelId = "LP-CMC-491",
                    applicantName = "Almaz Kenenisa",
                    projectTitle = "CMC Double-Story Courtyard Villa",
                    blueprintUrl = "http://smartnation.et/blueprints/cmc-villa-final.pdf",
                    statusStep = "In Review"
                )
            )

            // Seed violations (reporters or automated satellite scans)
            violationDao.insertViolation(
                Violation(
                    parcelId = "LP-KAZ-334",
                    title = "Unauthorized Multi-Storey Slabs Detected",
                    description = "In-situ inspection with AI radar scanning reveals LP-KAZ-334 has active reinforcement bar installation reaching 15th floor slabs, exceeding the municipal limit of 10 floors for this high-density Kazanchis corridor. Site work must freeze immediately.",
                    severity = "Severe",
                    reporterName = "AI Satellite Scanner",
                    gpsCoordinates = "9.0186, 38.7623",
                    status = "Reported"
                )
            )
            violationDao.insertViolation(
                Violation(
                    parcelId = "LP-BOLE-882",
                    title = "Minor Excavation Encroachment",
                    description = "Excavators observed storing surplus structural basalt blocks 2 meters into parallel public pedestrian pathways on the eastern plot frontage.",
                    severity = "Low",
                    reporterName = "Inspector Habte",
                    gpsCoordinates = "9.0225, 38.7845",
                    status = "Resolved"
                )
            )
            violationDao.insertViolation(
                Violation(
                    parcelId = "LP-CMC-491",
                    title = "Porous Foundation Integrity Check",
                    description = "Field scanner flagged concrete mix moisture quotient beyond acceptable safety indices for localized heavy monsoon mud conditions.",
                    severity = "High",
                    reporterName = "Field App Sensor Capture",
                    gpsCoordinates = "9.0288, 38.8128",
                    status = "Under Investigation"
                )
            )

            // Seed property listings
            // Images are beautifully loaded or illustrated elegantly. We use premium URLs or simple indicators.
            propertyListingDao.insertListing(
                PropertyListing(
                    title = "Bole Modern Premium Commercial Apartment",
                    description = "Exquisite 3-bedroom premium executive suite located near Bole International Road. High-tech integrated security systems, secure private underground automated parking, smart automation systems, and ultra-high speed backup electricity. Perfect option for high-profile diplomats or enterprise offices.",
                    priceEtb = 13800000.0,
                    subType = "Residential",
                    location = "Bole",
                    address = "Bole Ring-Road, Block 12, Addis Ababa",
                    areaSqMeter = 180.0,
                    contactPhone = "+251 91 100 2424",
                    imageUrl = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?q=80&w=600&auto=format",
                    isRental = false,
                    latitude = 9.0215,
                    longitude = 38.7845
                )
            )
            propertyListingDao.insertListing(
                PropertyListing(
                    title = "Kazanchis Executive Corporate Plaza Office",
                    description = "Full floor A-grade office space in Kazanchis Financial District. Premium panoramic windows providing continuous stunning views across Mount Entoto. Comes pre-wired for high-density servers, dedicated security, and advanced variable air-volume cooling systems.",
                    priceEtb = 48200000.0,
                    subType = "Commercial",
                    location = "Kazanchis",
                    address = "Kazanchis Corporate Blvd, Addis Ababa",
                    areaSqMeter = 450.0,
                    contactPhone = "+251 92 344 5566",
                    imageUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?q=80&w=600&auto=format",
                    isRental = false,
                    latitude = 9.0190,
                    longitude = 38.7615
                )
            )
            propertyListingDao.insertListing(
                PropertyListing(
                    title = "CMC Cozy Luxury Family Townhouse",
                    description = "Spacious modern villa styled with a beautiful Ethiopian natural stone fireplace, extensive high ceiling living room, dynamic open layout, and a private compound decorated with lush mature native floral gardens. Located in a secure gated CMC community cluster.",
                    priceEtb = 85000.0,
                    subType = "Residential",
                    location = "CMC",
                    address = "CMC Gated Estate Way, Sector 4, Addis Ababa",
                    areaSqMeter = 280.0,
                    contactPhone = "+251 91 000 7878",
                    imageUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?q=80&w=600&auto=format",
                    isRental = true,
                    latitude = 9.0298,
                    longitude = 38.8115
                )
            )
            propertyListingDao.insertListing(
                PropertyListing(
                    title = "Bole Roadside Commercial Plot",
                    description = "Superb flat development land with direct dual frontage configuration onto Bole Road. Full commercial zoning classification permit. Ideal fit for construction towers, banks, flagship retailer showrooms, or mixed-use operations.",
                    priceEtb = 32000000.0,
                    subType = "Land",
                    location = "Bole",
                    address = "Bole Main Highway, Block 41, Addis Ababa",
                    areaSqMeter = 600.0,
                    contactPhone = "+251 90 401 0203",
                    imageUrl = "https://images.unsplash.com/photo-1500382017468-9049fed747ef?q=80&w=600&auto=format",
                    isRental = false,
                    latitude = 9.0232,
                    longitude = 38.7831
                )
            )
            propertyListingDao.insertListing(
                PropertyListing(
                    title = "Kazanchis Luxury Penthouse Suite",
                    description = "Premium upper top floor double-height duplex penthouse overlooking Addis Ababa's financial city. Elegant custom marble kitchens, extensive floor-to-ceiling double-paned noise-canceling glass, private lift entrance, and dynamic ambient designer mood lighting.",
                    priceEtb = 145000.0,
                    subType = "Residential",
                    location = "Kazanchis",
                    address = "Kazanchis Park View, Tower B, Addis Ababa",
                    areaSqMeter = 320.0,
                    contactPhone = "+251 91 199 0088",
                    imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?q=80&w=600&auto=format",
                    isRental = true,
                    latitude = 9.0178,
                    longitude = 38.7628
                )
            )
        }
    }
}
