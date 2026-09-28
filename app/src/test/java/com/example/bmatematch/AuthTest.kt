package com.example.bmatematch

import com.example.bmatematch.ui.viewmodel.RegistrationViewModel
import org.junit.Test
import org.junit.Assert.*

class AuthTest {
    @Test
    fun loginState_initializesWithDefaults() {
        val viewModel = RegistrationViewModel()
        assertFalse(viewModel.uiState.value.loginComplete)
        assertNull(viewModel.uiState.value.loginError)
    }

    @Test
    fun clearLoginResult_resetsErrorAndCompleteFlags() {
        val viewModel = RegistrationViewModel()
        viewModel.clearLoginResult()
        assertFalse(viewModel.uiState.value.loginComplete)
        assertNull(viewModel.uiState.value.loginError)
    }
}
