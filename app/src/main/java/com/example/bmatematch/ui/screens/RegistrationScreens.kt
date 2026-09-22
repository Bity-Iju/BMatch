package com.example.bmatematch.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.net.Uri
import com.example.bmatematch.data.NigeriaLocations
import com.example.bmatematch.ui.theme.BMateMatchTheme
import com.example.bmatematch.ui.viewmodel.RegistrationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationStepLayout(
    title: String,
    step: Int,
    onNext: () -> Unit,
    onBack: (() -> Unit)? = null,
    isNextEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registration - Step $step of 6") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Progress Bar
                LinearProgressIndicator(
                    progress = { step / 6f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                content()
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = isNextEnabled,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (step == 6) "Complete Registration" else "Continue",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun Step1ConsentScreen(
    state: RegistrationState,
    onUpdateConsent: (Boolean) -> Unit,
    onNext: () -> Unit
) {
    RegistrationStepLayout(
        title = "Secure Consent & Safety",
        step = 1,
        onNext = onNext,
        isNextEnabled = state.consentAccepted
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Rounded.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Legal Disclaimer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Text(
                    "By proceeding, you acknowledge that BMatch is a platform for healthcare coordination. " +
                            "All data entered must be accurate and truthful. Unauthorized access or misuse of patient data is strictly prohibited and punishable under Nigerian law.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = state.consentAccepted,
                onCheckedChange = onUpdateConsent
            )
            Text(
                "I have read and agree to the Safety Acknowledgment and Terms of Service.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun Step2PersonalDetailsScreen(
    state: RegistrationState,
    onUpdateDetails: (String, String, String, String, String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(state.name) }
    var email by remember { mutableStateOf(state.email) }
    var password by remember { mutableStateOf(state.password) }
    var artId by remember { mutableStateOf(state.artId) }
    var facilityName by remember { mutableStateOf(state.facilityName) }

    RegistrationStepLayout(
        title = "Personal Details",
        step = 2,
        onNext = {
            onUpdateDetails(name, email, password, artId, facilityName)
            onNext()
        },
        onBack = onBack,
        isNextEnabled = name.isNotBlank() && email.isNotBlank() && password.length >= 8 && artId.isNotBlank() && facilityName.isNotBlank()
    ) {
        Text("Provide your professional information to set up your profile.")

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password (8+ characters)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = artId,
            onValueChange = { artId = it },
            label = { Text("ART ID Number") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = facilityName,
            onValueChange = { facilityName = it },
            label = { Text("Facility Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step3LocationScreen(
    state: RegistrationState,
    onUpdateLocation: (String, String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var selectedState by remember { mutableStateOf(state.selectedState) }
    var selectedLga by remember { mutableStateOf(state.selectedLga) }
    var stateExpanded by remember { mutableStateOf(false) }
    var lgaExpanded by remember { mutableStateOf(false) }

    val lgas = NigeriaLocations.states.find { it.name == selectedState }?.lgas ?: emptyList()

    RegistrationStepLayout(
        title = "Location Selection",
        step = 3,
        onNext = {
            onUpdateLocation(selectedState, selectedLga)
            onNext()
        },
        onBack = onBack,
        isNextEnabled = selectedState.isNotBlank() && selectedLga.isNotBlank()
    ) {
        Text("Select your primary operational location in Nigeria.")

        ExposedDropdownMenuBox(
            expanded = stateExpanded,
            onExpandedChange = { stateExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedState,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select State") },
                leadingIcon = { Icon(Icons.Rounded.LocationOn, contentDescription = null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = stateExpanded,
                onDismissRequest = { stateExpanded = false }
            ) {
                NigeriaLocations.states.forEach { stateItem ->
                    DropdownMenuItem(
                        text = { Text(stateItem.name) },
                        onClick = {
                            selectedState = stateItem.name
                            selectedLga = "" // Reset LGA when state changes
                            stateExpanded = false
                        }
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = lgaExpanded,
            onExpandedChange = { if (selectedState.isNotBlank()) lgaExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedLga,
                onValueChange = {},
                readOnly = true,
                enabled = selectedState.isNotBlank(),
                label = { Text("Select LGA") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = lgaExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = lgaExpanded,
                onDismissRequest = { lgaExpanded = false }
            ) {
                lgas.forEach { lgaItem ->
                    DropdownMenuItem(
                        text = { Text(lgaItem) },
                        onClick = {
                            selectedLga = lgaItem
                            lgaExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step4FurtherInfoScreen(
    state: RegistrationState,
    onUpdateFields: (String, String, String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var address by remember { mutableStateOf(state.address) }
    var age by remember { mutableStateOf(state.age) }
    var dob by remember { mutableStateOf(state.dob) }

    RegistrationStepLayout(
        title = "Address & Identity",
        step = 4,
        onNext = {
            onUpdateFields(address, age, dob)
            onNext()
        },
        onBack = onBack,
        isNextEnabled = address.isNotBlank() && age.isNotBlank() && dob.isNotBlank()
    ) {
        Text("Provide your contact and age information to verify your identity.")

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Full Residential Address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(Icons.Rounded.LocationOn, contentDescription = null) }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = age,
                onValueChange = { if (it.all { char -> char.isDigit() }) age = it },
                label = { Text("Age") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Rounded.Info, contentDescription = null) }
            )

            OutlinedTextField(
                value = dob,
                onValueChange = { dob = it },
                label = { Text("DOB (DD/MM/YYYY)") },
                modifier = Modifier.weight(2f),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Rounded.Info, contentDescription = null) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step5FurtherInfoScreen(
    state: RegistrationState,
    onUpdateFields: (String, String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var sex by remember { mutableStateOf(state.sex) }
    var religion by remember { mutableStateOf(state.religion) }
    var sexExpanded by remember { mutableStateOf(false) }
    var religionExpanded by remember { mutableStateOf(false) }

    val sexOptions = listOf("Male", "Female", "Other")
    val religionOptions = listOf("Christianity", "Islam", "Traditional", "None", "Other")

    RegistrationStepLayout(
        title = "Demographics",
        step = 5,
        onNext = {
            onUpdateFields(sex, religion)
            onNext()
        },
        onBack = onBack,
        isNextEnabled = sex.isNotBlank() && religion.isNotBlank()
    ) {
        Text("We collect demographic data for statistical purposes and to personalize your experience.")

        ExposedDropdownMenuBox(
            expanded = sexExpanded,
            onExpandedChange = { sexExpanded = !sexExpanded }
        ) {
            OutlinedTextField(
                value = sex,
                onValueChange = {},
                readOnly = true,
                label = { Text("Sex") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sexExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) }
            )
            ExposedDropdownMenu(
                expanded = sexExpanded,
                onDismissRequest = { sexExpanded = false }
            ) {
                sexOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            sex = option
                            sexExpanded = false
                        }
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = religionExpanded,
            onExpandedChange = { religionExpanded = !religionExpanded }
        ) {
            OutlinedTextField(
                value = religion,
                onValueChange = {},
                readOnly = true,
                label = { Text("Religion") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = religionExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Rounded.Info, contentDescription = null) }
            )
            ExposedDropdownMenu(
                expanded = religionExpanded,
                onDismissRequest = { religionExpanded = false }
            ) {
                religionOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            religion = option
                            religionExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Step6ReviewScreen(
    state: RegistrationState,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    RegistrationStepLayout(
        title = "Final Review",
        step = 6,
        onNext = onNext,
        onBack = onBack
    ) {
        Text("Please review your registration details before submission.")

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ReviewItem(label = "Full Name", value = state.name)
                ReviewItem(label = "Email", value = state.email)
                ReviewItem(label = "Identity", value = "${state.artId} | ${state.facilityName}")
                ReviewItem(label = "Location", value = "${state.selectedState}, ${state.selectedLga}")
                ReviewItem(label = "Address", value = state.address)
                ReviewItem(label = "Personal", value = "Age: ${state.age} | DOB: ${state.dob}")
                ReviewItem(label = "Demographics", value = "Sex: ${state.sex} | Religion: ${state.religion}")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Rounded.Info, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            Text(
                "Submitting this form will finalize your professional profile on BMatch.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ReviewItem(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
        Text(text = value.ifBlank { "Not provided" }, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    onNavigateToAdmin: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToChat: () -> Unit = {},
    onNavigateToMatching: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BMatch Dashboard") },
                actions = {
                    IconButton(onClick = onNavigateToAdmin) {
                        Icon(imageVector = Icons.Rounded.AdminPanelSettings, contentDescription = "Admin")
                    }
                    IconButton(onClick = onNavigateToChat) {
                        Icon(imageVector = Icons.Rounded.ChatBubbleOutline, contentDescription = "Chats")
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(imageVector = Icons.Rounded.Person, contentDescription = "Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .size(120.dp)
                        .clickable(onClick = onNavigateToProfile)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text(
                    "Welcome to BMatch!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Your professional profile has been successfully created.",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onNavigateToChat,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Icon(imageVector = Icons.Rounded.ChatBubbleOutline, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Conversations Hub")
                    }

                    Button(
                        onClick = onNavigateToProfile,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Rounded.Person, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Profile Panel")
                    }
                }
                OutlinedButton(
                    onClick = onNavigateToMatching,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.Lock, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Matching (available after approval)")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchingScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Matching") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Rounded.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(72.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Matching is not available yet", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Your profile, chat, and other pages are available after registration. Matching will be enabled when an administrator approves your account.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Step1Preview() {
    BMateMatchTheme {
        Step1ConsentScreen(state = RegistrationState(), onUpdateConsent = {}, onNext = {})
    }
}

@Preview(showBackground = true)
@Composable
fun Step3Preview() {
    BMateMatchTheme {
        Step3LocationScreen(state = RegistrationState(), onUpdateLocation = {_, _ ->}, onNext = {}, onBack = {})
    }
}

@Preview(showBackground = true)
@Composable
fun Step6Preview() {
    BMateMatchTheme {
        Step6ReviewScreen(
            state = RegistrationState(
                name = "Dr. Jane Doe",
                email = "jane.doe@example.com",
                artId = "ART-12345",
                facilityName = "Lagos General Hospital",
                selectedState = "Lagos",
                selectedLga = "Ikeja",
                furtherInfo1 = "HIV Specialist"
            ),
            onNext = {},
            onBack = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentInitiationScreen(
    state: RegistrationState,
    onReceiptSelected: (String) -> Unit = {},
    onNext: () -> Unit
) {
    val receiptPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let { onReceiptSelected(it.toString()) }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment & Security Verification") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Security Layer Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (state.paymentExempt) {
                            Text(
                                "You are one of the first 20 registered users. Payment is waived and your account is eligible for access.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Security,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Security Layer & Identity Anchoring",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                        
                        ReviewItem(label = "Anchored Email Address", value = state.email)
                        ReviewItem(label = "Unique User Version Identifier", value = state.uniqueVersionNumber)
                        
                        Text(
                            text = "Your profile is secured and uniquely anchored to your professional email address. Each registration generates an immutable unique version identifier.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }

                if (!state.paymentExempt) {
                // Payment Initiation Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Initial Engagement Payment Info",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))

                        Text(
                            text = "Make your payment using the details below, then attach the receipt for administrator review.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Platform / Bank",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = "Opay",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                OutlinedButton(
                                    onClick = { receiptPicker.launch(arrayOf("image/*", "application/pdf")) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Rounded.AttachFile, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (state.paymentReceiptUri == null) "Attach payment receipt" else "Receipt attached")
                                }
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Account Number",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = "9031335993",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Account Name",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = "Fidelis Bitrus Ijudona",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Complete & Open Dashboard",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_9_pro")
@Composable
fun PaymentInitiationPreview() {
    BMateMatchTheme {
        PaymentInitiationScreen(
            state = RegistrationState(
                email = "user@example.com",
                uniqueVersionNumber = "BM-VER-12345678-ABCDEF"
            ),
            onNext = {}
        )
    }
}
