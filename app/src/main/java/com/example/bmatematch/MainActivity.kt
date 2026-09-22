package com.example.bmatematch

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.bmatematch.navigation.*
import com.example.bmatematch.ui.screens.*
import com.example.bmatematch.ui.theme.BMateMatchTheme
import com.example.bmatematch.ui.viewmodel.AdminViewModel
import com.example.bmatematch.ui.viewmodel.RegistrationViewModel
import com.example.bmatematch.ui.viewmodel.ChatViewModel
import com.example.bmatematch.update.AppUpdateManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            val updateManager = AppUpdateManager(this@MainActivity)
            val update = updateManager.check() ?: return@launch
            Toast.makeText(
                this@MainActivity,
                "BMatch update ${update.versionCode} is available. Downloading…",
                Toast.LENGTH_LONG
            ).show()
            updateManager.downloadAndInstall(update)
        }
        setContent {
            BMateMatchTheme {
                val registrationViewModel: RegistrationViewModel = viewModel()
                val registrationState by registrationViewModel.uiState.collectAsStateWithLifecycle()
                
                val adminViewModel: AdminViewModel = viewModel()
                val users by adminViewModel.users.collectAsStateWithLifecycle()
                val settings by adminViewModel.settings.collectAsStateWithLifecycle()
                val logs by adminViewModel.paymentLogs.collectAsStateWithLifecycle()
                val matches by adminViewModel.matches.collectAsStateWithLifecycle()

                val chatViewModel: ChatViewModel = viewModel()
                val conversations by chatViewModel.conversations.collectAsStateWithLifecycle()
                val selectedConversationId by chatViewModel.selectedConversationId.collectAsStateWithLifecycle()

                val backStack = rememberNavBackStack(RegistrationStep1)

                NavDisplay(
                    backStack = backStack,
                    onBack = { 
                        if (backStack.size > 1) {
                            backStack.removeAt(backStack.size - 1)
                        }
                    },
                    entryProvider = { key ->
                        when (key) {
                            is RegistrationStep1 -> NavEntry(key) {
                                Step1ConsentScreen(
                                    state = registrationState,
                                    onUpdateConsent = { registrationViewModel.updateConsent(it) },
                                    onNext = { backStack.add(RegistrationStep2) }
                                )
                            }
                            is RegistrationStep2 -> NavEntry(key) {
                                Step2PersonalDetailsScreen(
                                    state = registrationState,
                                    onUpdateDetails = { name, email, password, artId, facility ->
                                        registrationViewModel.updatePersonalDetails(name, email, password, artId, facility)
                                    },
                                    onNext = { backStack.add(RegistrationStep3) },
                                    onBack = { backStack.removeAt(backStack.size - 1) }
                                )
                            }
                            is RegistrationStep3 -> NavEntry(key) {
                                Step3LocationScreen(
                                    state = registrationState,
                                    onUpdateLocation = { state, lga ->
                                        registrationViewModel.updateLocation(state, lga)
                                    },
                                    onNext = { backStack.add(RegistrationStep4) },
                                    onBack = { backStack.removeAt(backStack.size - 1) }
                                )
                            }
                            is RegistrationStep4 -> NavEntry(key) {
                                Step4FurtherInfoScreen(
                                    state = registrationState,
                                    onUpdateFields = { address, age, dob ->
                                        registrationViewModel.updateStep4Fields(address, age, dob)
                                    },
                                    onNext = { backStack.add(RegistrationStep5) },
                                    onBack = { backStack.removeAt(backStack.size - 1) }
                                )
                            }
                            is RegistrationStep5 -> NavEntry(key) {
                                Step5FurtherInfoScreen(
                                    state = registrationState,
                                    onUpdateFields = { sex, religion ->
                                        registrationViewModel.updateStep5Fields(sex, religion)
                                    },
                                    onNext = { backStack.add(RegistrationStep6) },
                                    onBack = { backStack.removeAt(backStack.size - 1) }
                                )
                            }
                            is RegistrationStep6 -> NavEntry(key) {
                                Step6ReviewScreen(
                                    state = registrationState,
                                    onNext = {
                                        registrationViewModel.completeRegistration()
                                        registrationViewModel.syncToSupabase()
                                        backStack.add(PaymentInitiation)
                                    },
                                    onBack = { backStack.removeAt(backStack.size - 1) }
                                )
                            }
                            is PaymentInitiation -> NavEntry(key) {
                                PaymentInitiationScreen(
                                    state = registrationState,
                                    onReceiptSelected = { registrationViewModel.updatePaymentReceipt(it) },
                                    onNext = {
                                        backStack.clear()
                                        backStack.add(MainDashboard)
                                    }
                                )
                            }
                            is MainDashboard -> NavEntry(key) {
                                MainDashboardScreen(
                                    onNavigateToAdmin = { backStack.add(AdminDashboard) },
                                    onNavigateToProfile = { backStack.add(UserProfileManagement) },
                                    onNavigateToChat = { backStack.add(ChatHub) },
                                    onNavigateToMatching = { backStack.add(MatchingPage) }
                                )
                            }
                            is MatchingPage -> NavEntry(key) {
                                MatchingScreen(
                                    onBack = { backStack.removeAt(backStack.size - 1) }
                                )
                            }
                            is ChatHub -> NavEntry(key) {
                                AdaptiveChatScreen(
                                    conversations = conversations,
                                    selectedConversationId = selectedConversationId,
                                    onSelectConversation = { id -> chatViewModel.selectConversation(id) },
                                    onSendMessage = { id, text -> chatViewModel.sendMessage(id, text) },
                                    onBackToDashboard = { 
                                        chatViewModel.selectConversation(null)
                                        backStack.removeAt(backStack.size - 1) 
                                    }
                                )
                            }
                            is UserProfileManagement -> NavEntry(key) {
                                UserProfileScreen(
                                    state = registrationState,
                                    onUpdateProfile = { name, address, age, dob, sex, religion, status, avatar ->
                                        registrationViewModel.updateProfile(name, address, age, dob, sex, religion, status, avatar)
                                    },
                                    onBack = { backStack.removeAt(backStack.size - 1) }
                                )
                            }
                            is AdminDashboard -> NavEntry(key) {
                                AdminDashboardScreen(
                                    onNavigateToUsers = { backStack.add(AdminUserRegistry) },
                                    onNavigateToSettings = { backStack.add(AdminSettings) },
                                    onNavigateToLogs = { backStack.add(AdminPaymentLogs) },
                                    onNavigateToMatches = { backStack.add(AdminMatches) },
                                    onBack = { backStack.removeAt(backStack.size - 1) }
                                )
                            }
                            is AdminUserRegistry -> NavEntry(key) {
                                UserRegistryScreen(
                                    users = users,
                                    onBack = { backStack.removeAt(backStack.size - 1) }
                                )
                            }
                            is AdminSettings -> NavEntry(key) {
                                AdminSettingsScreen(
                                    settings = settings,
                                    onSaveSettings = { adminViewModel.updateSettings(it) },
                                    onBack = { backStack.removeAt(backStack.size - 1) }
                                )
                            }
                            is AdminPaymentLogs -> NavEntry(key) {
                                PaymentLogsScreen(
                                    logs = logs,
                                    onBack = { backStack.removeAt(backStack.size - 1) }
                                )
                            }
                            is AdminMatches -> NavEntry(key) {
                                AdminMatchesScreen(
                                    matches = matches,
                                    users = users,
                                    onBack = { backStack.removeAt(backStack.size - 1) }
                                )
                            }
                            else -> error("Unknown route: $key")
                        }
                    }
                )
            }
        }
    }
}
