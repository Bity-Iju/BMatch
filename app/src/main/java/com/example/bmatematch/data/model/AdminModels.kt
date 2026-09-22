package com.example.bmatematch.data.model

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class AppSettings(
    val paymentInitiationThreshold: Double = 5000.0,
    val currency: String = "NGN",
    val autoApproveUsers: Boolean = false
)

@Serializable
data class PaymentLog(
    val id: String,
    val userId: String,
    val userEmail: String,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Success"
)

@Serializable
data class MatchRecord(
    val id: String,
    val firstUserId: String,
    val secondUserId: String,
    val status: String = "Pending review",
    val createdAt: Long = System.currentTimeMillis()
)
