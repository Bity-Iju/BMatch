package com.example.bmatematch.data

import com.example.bmatematch.ui.viewmodel.RegistrationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object SupabaseConfig {
    const val url = "https://xrhplalnbphkryiwkkxt.supabase.co"
    const val anonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhyaHBsYWxuYnBoa3J5aXdra3h0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODg5Njk2NDAsImV4cCI6MjEwNDU0NTY0MH0.7RWtosTFdUuukSmVHNMIiBkfOKvA2FKasA6X_0E9obA"
}

data class SupabaseAccess(val userId: String, val paymentExempt: Boolean)

class SupabaseClient {
    suspend fun login(email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        requireConfigured()
        request(
            "${SupabaseConfig.url}/auth/v1/token?grant_type=password",
            "POST",
            JSONObject().put("email", email).put("password", password).toString()
        )
        true
    }

    suspend fun register(state: RegistrationState): SupabaseAccess? = withContext(Dispatchers.IO) {
        requireConfigured()
        require(state.password.isNotBlank()) { "Password is required" }
        val auth = request(
            "${SupabaseConfig.url}/auth/v1/signup",
            "POST",
            JSONObject().put("email", state.email).put("password", state.password).toString()
        )
        val authJson = JSONObject(auth)
        val userId = authJson.getJSONObject("user").getString("id")
        val accessToken = authJson.optString("access_token")
            .ifBlank { error("Registration requires email confirmation before profile setup") }
        request(
            "${SupabaseConfig.url}/rest/v1/profiles",
            "POST",
            JSONObject()
                .put("id", userId)
                .put("full_name", state.name)
                .put("email", state.email)
                .put("art_id", state.artId)
                .put("facility_name", state.facilityName)
                .put("state", state.selectedState)
                .put("lga", state.selectedLga)
                .put("address", state.address)
                .put("age", state.age)
                .put("age_range", state.ageGroup.ifBlank { state.age })
                .put("sex", state.sex)
                .put("religion", state.religion)
                .put("marital_status", state.maritalStatus)
                .put("professional_category", state.professionalCategory)
                .put("terms_accepted", state.consentAccepted)
                .put("unique_version_number", state.uniqueVersionNumber)
                .put("dob", state.dob)
                .toString(),
            accessToken
        )
        val profile = request(
            "${SupabaseConfig.url}/rest/v1/profile_access?id=eq.$userId&select=payment_exempt",
            "GET",
            null,
            accessToken
        )
        val rows = org.json.JSONArray(profile)
        SupabaseAccess(userId, rows.length() > 0 && rows.getJSONObject(0).optBoolean("payment_exempt"))
    }

    private fun requireConfigured() {
        require(
            !SupabaseConfig.url.contains("YOUR_PROJECT") &&
                SupabaseConfig.anonKey.isNotBlank() &&
                SupabaseConfig.anonKey != "******"
        ) {
            "Supabase is not configured"
        }
    }

    private fun request(url: String, method: String, body: String?, token: String = SupabaseConfig.anonKey): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = method
        connection.setRequestProperty("apikey", SupabaseConfig.anonKey)
        connection.setRequestProperty("Authorization", "Bearer $token")
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Prefer", "return=representation")
        if (body != null) {
            connection.doOutput = true
            connection.outputStream.use { it.write(body.toByteArray()) }
        }
        val response = (if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream)
            .bufferedReader().use { it.readText() }
        if (connection.responseCode !in 200..299) error("Supabase request failed (${connection.responseCode}): $response")
        return response
    }
}
