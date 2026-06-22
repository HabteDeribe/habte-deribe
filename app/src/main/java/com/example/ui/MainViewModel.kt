package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.api.GeminiRetrofitClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = AppRepository(
        database.userDao(),
        database.landParcelDao(),
        database.constructionApplicationDao(),
        database.violationDao(),
        database.propertyListingDao()
    )

    // Current logged-in role for testing/demonstration
    private val _currentUserRole = MutableStateFlow(UserRole.Municipality_Officer)
    val currentUserRole: StateFlow<UserRole> = _currentUserRole.asStateFlow()

    // User Registration / Verification status matching Lovable design spec
    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    private val _userIdCard = MutableStateFlow<String?>(null)
    val userIdCard: StateFlow<String?> = _userIdCard.asStateFlow()

    private val _isUserVerified = MutableStateFlow(false)
    val isUserVerified: StateFlow<Boolean> = _isUserVerified.asStateFlow()

    // Database reactive sources
    val allParcels: StateFlow<List<LandParcel>> = repository.allParcels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allApplications: StateFlow<List<ConstructionApplication>> = repository.allApplications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allViolations: StateFlow<List<Violation>> = repository.allViolations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allListings: StateFlow<List<PropertyListing>> = repository.allListings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Marketplace Search & Search Filters state
    private val _filterLocation = MutableStateFlow<String?>("All") // "All", "Bole", "Kazanchis", "CMC"
    val filterLocation: StateFlow<String?> = _filterLocation.asStateFlow()

    private val _filterPriceMax = MutableStateFlow<Double?>(null)
    val filterPriceMax: StateFlow<Double?> = _filterPriceMax.asStateFlow()

    private val _filterIsRental = MutableStateFlow<Boolean?>(null) // null = Both, true = Rent, false = Buy
    val filterIsRental: StateFlow<Boolean?> = _filterIsRental.asStateFlow()

    private val _filterSubType = MutableStateFlow<String?>("All") // "All", "Residential", "Commercial", "Land"
    val filterSubType: StateFlow<String?> = _filterSubType.asStateFlow()

    // Filtered listings derived reactive state
    val filteredListings: StateFlow<List<PropertyListing>> = combine(
        allListings, _filterLocation, _filterPriceMax, _filterIsRental, _filterSubType
    ) { listings, loc, maxP, rent, subT ->
        listings.filter { item ->
            val matchLoc = loc == "All" || item.location.equals(loc, ignoreCase = true)
            val matchPrice = maxP == null || item.priceEtb <= maxP
            val matchRent = rent == null || item.isRental == rent
            val matchSubT = subT == "All" || item.subType.equals(subT, ignoreCase = true)
            matchLoc && matchPrice && matchRent && matchSubT
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Map selection details (Left pane on Command Center)
    private val _selectedParcel = MutableStateFlow<LandParcel?>(null)
    val selectedParcel: StateFlow<LandParcel?> = _selectedParcel.asStateFlow()

    // AI Chat Conversational Finder State
    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                "Ameseginalehu! Welcome to the AI Conversational Property Finder. I can locate residential, commercial, or land listings in Addis Ababa (Bole, Kazanchis, CMC) and provide current legal status checks. What are you looking for today?",
                isUser = false
            )
        )
    )
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
            // Set initial selected parcel automatically
            val parcels = repository.allParcels.first()
            if (parcels.isNotEmpty()) {
                _selectedParcel.value = parcels.first()
            }
        }
    }

    fun changeUserRole(role: UserRole) {
        _currentUserRole.value = role
    }

    fun verifyUser(email: String, idCard: String) {
        _userEmail.value = email
        _userIdCard.value = idCard
        _isUserVerified.value = true
    }

    fun logoutUser() {
        _userEmail.value = null
        _userIdCard.value = null
        _isUserVerified.value = false
    }

    fun selectParcel(parcel: LandParcel) {
        _selectedParcel.value = parcel
    }

    // Set filters
    fun setLocationFilter(location: String) {
        _filterLocation.value = location
    }

    fun setIsRentalFilter(isRental: Boolean?) {
        _filterIsRental.value = isRental
    }

    fun setSubTypeFilter(subType: String) {
        _filterSubType.value = subType
    }

    fun setPriceMaxFilter(max: Double?) {
        _filterPriceMax.value = max
    }

    // Action: Inspector submits discrepancy report
    fun submitViolation(parcelId: String, title: String, description: String, severity: String, gps: String) {
        viewModelScope.launch {
            val report = Violation(
                parcelId = parcelId,
                title = title,
                description = description,
                severity = severity,
                reporterName = "Inspector Habte",
                gpsCoordinates = gps,
                status = "Reported"
            )
            repository.insertViolation(report)
            // Flag the land parcel state automatically
            repository.updateParcelStatus(parcelId, "Flagged")
        }
    }

    fun updateParcelStatus(parcelId: String, status: String) {
        viewModelScope.launch {
            repository.updateParcelStatus(parcelId, status)
        }
    }

    // Action: Submit Citizen housing construction permit blueprint
    fun submitConstructionApplication(parcelId: String, projectTitle: String) {
        viewModelScope.launch {
            val app = ConstructionApplication(
                parcelId = parcelId,
                applicantName = "Almaz Kenenisa",
                projectTitle = projectTitle,
                blueprintUrl = "http://smartnation.et/blueprints/manual-upload-${System.currentTimeMillis()}.pdf",
                statusStep = "Submitted"
            )
            repository.insertApplication(app)
            repository.updateParcelStatus(parcelId, "Under Review")
        }
    }

    // Action: Post New Property listing
    fun createPropertyListing(
        title: String,
        description: String,
        price: Double,
        subType: String,
        location: String,
        address: String,
        isRental: Boolean,
        contact: String
    ) {
        viewModelScope.launch {
            val img = when (location.lowercase()) {
                "bole" -> "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?q=80&w=600&auto=format"
                "kazanchis" -> "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?q=80&w=600&auto=format"
                else -> "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?q=80&w=600&auto=format"
            }
            val listing = PropertyListing(
                title = title,
                description = description,
                priceEtb = price,
                subType = subType,
                location = location,
                address = address,
                areaSqMeter = 200.0,
                contactPhone = contact,
                imageUrl = img,
                isRental = isRental,
                latitude = 9.02,
                longitude = 38.75
            )
            repository.insertListing(listing)
        }
    }

    // Action: AI Chat Property query
    fun sendChatMessage(query: String) {
        if (query.isBlank()) return
        val userMessage = ChatMessage(query, isUser = true)
        _chatHistory.value = _chatHistory.value + userMessage
        _isChatLoading.value = true

        viewModelScope.launch {
            val systemPrompt = """
                You are the AI Real Estate & Land Monitor agent for the HDZ Smart Nation Platform founded by Habte Deribe Zeleke.
                You are helpful, precise, and polite. Always formulate pricing in Ethiopian Birr (ETB).
                You can recommend properties in Bole, Kazanchis, and CMC from our official database.
                Available Listings in your memory:
                1. Bole Commercial Apartment: 13,800,000 ETB, Residential sale
                2. Kazanchis Corporate Blvd Office: 48,200,000 ETB, Commercial sale
                3. CMC Gated Townhouse: 85,000 ETB/month, Residential Rent
                4. Bole Main Highway Land Plot: 32,000,000 ETB, land sale
                5. Kazanchis Duplex Penthouse: 145,000 ETB/month, Residential Rent

                Provide intelligent real-time answers based on these. If user asks about land plots or files, refer to our spatial data records.
            """.trimIndent()

            val botResponseText = GeminiRetrofitClient.getConversationalResponse(query, systemPrompt)
            val botMessage = ChatMessage(botResponseText, isUser = false)
            _chatHistory.value = _chatHistory.value + botMessage
            _isChatLoading.value = false
        }
    }

    fun clearChat() {
        _chatHistory.value = listOf(
            ChatMessage(
                "Chat history wiped. How can I assist with your Addis Ababa land property search today?",
                isUser = false
            )
        )
    }
}
