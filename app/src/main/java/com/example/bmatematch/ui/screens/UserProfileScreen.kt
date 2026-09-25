package com.example.bmatematch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bmatematch.ui.theme.BMateMatchTheme
import com.example.bmatematch.ui.viewmodel.RegistrationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    state: RegistrationState,
    onUpdateProfile: (String, String, String, String, String, String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(state.name) }
    var address by remember { mutableStateOf(state.address) }
    var age by remember { mutableStateOf(state.age) }
    var dob by remember { mutableStateOf(state.dob) }
    var sex by remember { mutableStateOf(state.sex) }
    var religion by remember { mutableStateOf(state.religion) }
    var statusUpdate by remember { mutableStateOf(state.statusUpdate) }
    var avatarRes by remember { mutableStateOf(state.avatarRes) }

    var sexExpanded by remember { mutableStateOf(false) }
    var religionExpanded by remember { mutableStateOf(false) }

    val sexOptions = listOf("Male", "Female", "Other")
    val religionOptions = listOf("Christianity", "Islam", "Traditional", "None", "Other")
    val avatarOptions = listOf("avatar_1", "avatar_2", "avatar_3", "avatar_4")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile Management") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar Selector Area
            Text(
                text = "Select Avatar",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.align(Alignment.Start)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                avatarOptions.forEach { avatar ->
                    val isSelected = avatarRes == avatar
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { avatarRes = avatar },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary 
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            // Status Update Field
            OutlinedTextField(
                value = statusUpdate,
                onValueChange = { statusUpdate = it },
                label = { Text("Current Status / Bio") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Rounded.Info, contentDescription = null) }
            )

            // Finalized Profile Details
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) }
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Full Address") },
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
                    onValueChange = { if (it.all { c -> c.isDigit() }) age = it },
                    label = { Text("Age") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Rounded.Info, contentDescription = null) }
                )

                OutlinedTextField(
                    value = dob,
                    onValueChange = { dob = it },
                    label = { Text("DOB") },
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Rounded.Info, contentDescription = null) }
                )
            }

            // Sex Select Dropdown
            ExposedDropdownMenuBox(
                expanded = sexExpanded,
                onExpandedChange = { sexExpanded = !sexExpanded },
                modifier = Modifier.fillMaxWidth()
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

            // Religion Select Dropdown
            ExposedDropdownMenuBox(
                expanded = religionExpanded,
                onExpandedChange = { religionExpanded = !religionExpanded },
                modifier = Modifier.fillMaxWidth()
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

            Spacer(modifier = Modifier.height(12.dp))

            // Save Changes Button
            Button(
                onClick = {
                    onUpdateProfile(name, address, age, dob, sex, religion, statusUpdate, avatarRes)
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Profile Changes", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "BityMatch Platform v1.0",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Created and Owned by Bity · Protected by Bity Security Anchoring",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun UserProfileScreenPreview() {
    BMateMatchTheme {
        UserProfileScreen(
            state = RegistrationState(
                name = "Dr. John Doe",
                address = "123 Medical Center Way, Lagos",
                age = "34",
                dob = "12/05/1992",
                sex = "Male",
                religion = "Christianity",
                statusUpdate = "Ready to serve patients."
            ),
            onUpdateProfile = { _, _, _, _, _, _, _, _ -> },
            onBack = {}
        )
    }
}
