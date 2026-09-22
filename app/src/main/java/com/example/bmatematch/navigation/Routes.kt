package com.example.bmatematch.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface BMatchRoute : NavKey

@Serializable
data object RegistrationStep1 : BMatchRoute

@Serializable
data object RegistrationStep2 : BMatchRoute

@Serializable
data object RegistrationStep3 : BMatchRoute

@Serializable
data object RegistrationStep4 : BMatchRoute

@Serializable
data object RegistrationStep5 : BMatchRoute

@Serializable
data object RegistrationStep6 : BMatchRoute

@Serializable
data object PaymentInitiation : BMatchRoute

@Serializable
data object MainDashboard : BMatchRoute

@Serializable
data object AdminDashboard : BMatchRoute

@Serializable
data object AdminUserRegistry : BMatchRoute

@Serializable
data object AdminSettings : BMatchRoute

@Serializable
data object AdminPaymentLogs : BMatchRoute

@Serializable
data object UserProfileManagement : BMatchRoute

@Serializable
data object ChatHub : BMatchRoute

@Serializable
data object MatchingPage : BMatchRoute

@Serializable
data object AdminMatches : BMatchRoute

