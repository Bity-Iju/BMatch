package com.example.bmatematch.data

import com.example.bmatematch.data.model.AppSettings
import com.example.bmatematch.data.model.PaymentLog
import com.example.bmatematch.data.model.MatchRecord
import com.example.bmatematch.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AdminRepository {
    private val _users = MutableStateFlow(
        listOf(
            User(facilityName = "General Hospital Lagos", artId = "ART-001", email = "admin@ghlagos.com", status = "Active", state = "Lagos", lga = "Ikeja"),
            User(facilityName = "St. Nicholas Hospital", artId = "ART-002", email = "info@stnicholas.com", status = "Pending", state = "Lagos", lga = "Lagos Island"),
            User(facilityName = "Maitama District Hospital", artId = "ART-003", email = "contact@maitama.gov.ng", status = "Active", state = "FCT", lga = "Abuja Municipal")
        )
    )
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private val _paymentLogs = MutableStateFlow(
        listOf(
            PaymentLog(id = UUID.randomUUID().toString(), userId = "ART-001", userEmail = "admin@ghlagos.com", amount = 15000.0),
            PaymentLog(id = UUID.randomUUID().toString(), userId = "ART-003", userEmail = "contact@maitama.gov.ng", amount = 25000.0)
        )
    )
    val paymentLogs: StateFlow<List<PaymentLog>> = _paymentLogs.asStateFlow()

    private val _matches = MutableStateFlow(
        listOf(
            MatchRecord("match-001", "ART-001", "ART-003", "Active")
        )
    )
    val matches: StateFlow<List<MatchRecord>> = _matches.asStateFlow()

    fun updateSettings(newSettings: AppSettings) {
        _settings.value = newSettings
    }

    fun approveUser(artId: String) {
        _users.value = _users.value.map {
            if (it.artId == artId) it.copy(status = "Active") else it
        }
    }
}
