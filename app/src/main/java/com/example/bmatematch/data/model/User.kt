package com.example.bmatematch.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val fullName: String = "",
    val facilityName: String = "",
    val artId: String = "",
    val email: String = "",
    val uniqueVersionNumber: String = "",
    val status: String = "Pending",
    val state: String = "",
    val lga: String = "",
    val address: String = "",
    val age: String = "",
    val dob: String = "",
    val sex: String = "",
    val religion: String = "",
    val registrationStep: Int = 1,
    val paymentReceiptUri: String? = null
)
