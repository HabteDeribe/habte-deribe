package com.example.api

import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiRetrofitClient {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    suspend fun getConversationalResponse(prompt: String, systemInstruction: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return generateMockAiResponse(prompt)
        }

        val fullPrompt = "$systemInstruction\n\nUser Question: $prompt"
        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = fullPrompt)))
            )
        )

        return try {
            val response = service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "I apologize, I searched but could not compile a response. Let me help you find listings in Bole, Kazanchis or CMC!"
        } catch (e: Exception) {
            // Fall back gracefully to mock system instead of crashing
            generateMockAiResponse(prompt)
        }
    }

    private fun generateMockAiResponse(prompt: String): String {
        val lowercase = prompt.lowercase()
        return when {
            lowercase.contains("bole") && (lowercase.contains("rent") || lowercase.contains("lease")) -> {
                "✨ [Local AI Simulation Mode] based on your interest, I found a 3-bedroom premium executive suite in Bole near Atlas Hotel. Rent is approximately 120,000 ETB/month with full backup solar generators, integrated security systems, and high-speed satellite internet."
            }
            lowercase.contains("bole") -> {
                "✨ [Local AI Simulation Mode] For Bole, I highly recommend our newly seeded Bole Roadside Commercial Plot (600 sqm @ 32,000,000 ETB) or our premium 3-bedroom Executive Commercial Apartment (180 sqm @ 13,800,000 ETB). Would you like to view contact details for these Addis Ababa properties?"
            }
            lowercase.contains("kazanchis") -> {
                "✨ [Local AI Simulation Mode] In Kazanchis (Addis Ababa financial core), we have an incredible A-grade Office Space at Kazanchis Corporate Boulevard (450 sqm @ 48,200,000 ETB) and a dual-storey luxury Penthouse Suite (320 sqm @ 145,000 ETB/month). It has floor-to-ceiling noise-canceling glass."
            }
            lowercase.contains("cmc") -> {
                "✨ [Local AI Simulation Mode] In CMC, I found a cozy luxury family townhouse with mature native gardens in a quiet gated estate sector. It is listed at a rental price of 85,000 ETB/month. An exceptional, family-safe option!"
            }
            lowercase.contains("land") || lowercase.contains("plot") -> {
                "✨ [Local AI Simulation Mode] For land seekers, we currently have a premium 600 sqm zoned commercial plot on Bole Main Highway listed for 32,000,000 ETB. Let me know if you would like me to retrieve the surveyor records of LP-BOLE-882!"
            }
            else -> {
                "👋 Hello! I am the HDZ Smart Nation Conversational Assistant. I can help search and filter prime properties in Addis Ababa (Bole, Kazanchis, CMC) in Ethiopian Birr (ETB). Try asking 'What properties are available in Bole?' or 'Show me rentals in CMC!'"
            }
        }
    }
}
