package com.example.bmatematch.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import com.example.bmatematch.data.SupabaseClient
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.absoluteValue

data class RegistrationState(
    val consentAccepted: Boolean = false,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val artId: String = "",
    val facilityName: String = "",
    val selectedState: String = "",
    val selectedLga: String = "",
    val furtherInfo1: String = "",
    val furtherInfo2: String = "",
    val address: String = "",
    val age: String = "",
    val dob: String = "",
    val sex: String = "",
    val religion: String = "",
    val ageGroup: String = "",
    val maritalStatus: String = "",
    val professionalCategory: String = "",
    val statusUpdate: String = "Feeling great today!",
    val avatarRes: String = "avatar_1",
    val uniqueVersionNumber: String = "",
    val paymentReceiptUri: String? = null
    ,val paymentExempt: Boolean = false,
    val supabaseSyncError: String? = null
    ,val loginError: String? = null,
    val loginComplete: Boolean = false
)

class RegistrationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RegistrationState())
    val uiState: StateFlow<RegistrationState> = _uiState.asStateFlow()

    fun updateConsent(accepted: Boolean) {
        _uiState.update { it.copy(consentAccepted = accepted) }
    }

    fun updatePersonalDetails(name: String, email: String, password: String, artId: String, facilityName: String) {
        _uiState.update { 
            it.copy(name = name, email = email, password = password, artId = artId, facilityName = facilityName) 
        }
    }

    fun updatePersonalDetails(name: String, email: String, artId: String, facilityName: String) {
        updatePersonalDetails(name, email, "", artId, facilityName)
    }

    fun syncToSupabase() {
        val current = _uiState.value
        val registration = if (current.uniqueVersionNumber.isBlank()) {
            current.copy(uniqueVersionNumber = createUniqueVersion(current.email)).also { prepared ->
                _uiState.update { state -> state.copy(uniqueVersionNumber = prepared.uniqueVersionNumber) }
            }
        } else {
            current
        }
        viewModelScope.launch {
            runCatching { SupabaseClient().register(registration) }
                .onSuccess { access ->
                    if (access != null) _uiState.update { it.copy(paymentExempt = access.paymentExempt, supabaseSyncError = null) }
                }
                .onFailure { error -> _uiState.update { it.copy(supabaseSyncError = error.message) } }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            runCatching { SupabaseClient().login(email, password) }
                .onSuccess { _uiState.update { it.copy(loginError = null, loginComplete = true) } }
                .onFailure { error -> _uiState.update { it.copy(loginError = error.message ?: "Unable to sign in") } }
        }
    }

    fun clearLoginResult() {
        _uiState.update { it.copy(loginComplete = false, loginError = null) }
    }

    fun updateLocation(state: String, lga: String) {
        _uiState.update { it.copy(selectedState = state, selectedLga = lga) }
    }

    fun updateStep4Fields(address: String, age: String, dob: String) {
        _uiState.update { it.copy(address = address, age = age, dob = dob) }
    }

    fun updateStep5Fields(sex: String, religion: String, ageGroup: String, maritalStatus: String, professionalCategory: String) {
        _uiState.update { 
            it.copy(
                sex = sex, 
                religion = religion, 
                ageGroup = ageGroup, 
                maritalStatus = maritalStatus, 
                professionalCategory = professionalCategory
            ) 
        }
    }

    fun updateProfile(name: String, address: String, age: String, dob: String, sex: String, religion: String, statusUpdate: String, avatarRes: String) {
        _uiState.update {
            it.copy(
                name = name,
                address = address,
                age = age,
                dob = dob,
                sex = sex,
                religion = religion,
                statusUpdate = statusUpdate,
                avatarRes = avatarRes
            )
        }
    }

    fun updateFurtherInfo1(info: String) {
        _uiState.update { it.copy(furtherInfo1 = info) }
    }

    fun updateFurtherInfo2(info: String) {
        _uiState.update { it.copy(furtherInfo2 = info) }
    }

    fun updatePaymentReceipt(uri: String) {
        _uiState.update { it.copy(paymentReceiptUri = uri) }
    }


    fun completeRegistration(): Boolean {
        val currentEmail = _uiState.value.email
        if (currentEmail.isBlank()) return false
        
        // Tie registration to user's email and assign unique version number/identifier
        val uniqueId = createUniqueVersion(currentEmail)
        
        _uiState.update { it.copy(uniqueVersionNumber = uniqueId) }
        return true
    }

    private fun createUniqueVersion(email: String): String {
        val emailHash = email.hashCode().absoluteValue
        val randomPart = UUID.randomUUID().toString().substring(0, 8).uppercase()
        return "BM-VER-$emailHash-$randomPart"
    }
}
