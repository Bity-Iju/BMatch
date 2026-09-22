package com.example.bmatematch

import com.example.bmatematch.ui.viewmodel.RegistrationViewModel
import org.junit.Test
import org.junit.Assert.*
import kotlin.math.absoluteValue

/**
 * Unit tests for BMatch Security Layer and Identity Anchoring.
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun registration_requiresEmail_forIdentityAnchoring() {
        val viewModel = RegistrationViewModel()
        
        // Given personal details are updated but email is blank
        viewModel.updatePersonalDetails(
            name = "Fidelis Bitrus",
            email = "",
            artId = "ART12345",
            facilityName = "Opay Facility"
        )
        
        // When completing registration
        val success = viewModel.completeRegistration()
        
        // Then it should fail because email is required for identity anchoring
        assertFalse(success)
        assertTrue(viewModel.uiState.value.uniqueVersionNumber.isEmpty())
    }

    @Test
    fun registration_assignsUniqueVersionNumber_tiedToEmail() {
        val viewModel = RegistrationViewModel()
        val email = "fidelis@example.com"
        
        // Given valid details with email provided
        viewModel.updatePersonalDetails(
            name = "Fidelis Bitrus",
            email = email,
            artId = "ART12345",
            facilityName = "Opay Facility"
        )
        
        // When completing registration
        val success = viewModel.completeRegistration()
        
        // Then it should succeed
        assertTrue(success)
        
        val uniqueId = viewModel.uiState.value.uniqueVersionNumber
        assertFalse(uniqueId.isEmpty())
        
        // Verify that the email's hash code or identity component is tied into the unique identifier
        assertTrue(uniqueId.contains(email.hashCode().absoluteValue.toString()))
        assertTrue(uniqueId.startsWith("BM-VER-"))
    }
}
