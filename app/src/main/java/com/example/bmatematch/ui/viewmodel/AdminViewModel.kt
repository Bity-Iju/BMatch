package com.example.bmatematch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bmatematch.data.AdminRepository
import com.example.bmatematch.data.model.AppSettings
import com.example.bmatematch.data.model.PaymentLog
import com.example.bmatematch.data.model.User
import com.example.bmatematch.data.model.MatchRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class AdminViewModel(private val repository: AdminRepository = AdminRepository()) : ViewModel() {

    val users: StateFlow<List<User>> = repository.users.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val settings: StateFlow<AppSettings> = repository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings()
    )

    val paymentLogs: StateFlow<List<PaymentLog>> = repository.paymentLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val matches: StateFlow<List<MatchRecord>> = repository.matches.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSettings(newSettings: AppSettings) {
        repository.updateSettings(newSettings)
    }

    fun approveUser(artId: String) {
        repository.approveUser(artId)
    }
}
