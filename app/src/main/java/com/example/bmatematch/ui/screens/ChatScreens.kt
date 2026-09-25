package com.example.bmatematch.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bmatematch.data.model.Conversation
import com.example.bmatematch.data.model.Message
import com.example.bmatematch.ui.theme.BMateMatchTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveChatScreen(
    conversations: List<Conversation>,
    selectedConversationId: String?,
    onSelectConversation: (String?) -> Unit,
    onSendMessage: (String, String) -> Unit,
    onBackToDashboard: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLargeScreen = configuration.screenWidthDp >= 600

    val selectedConv = conversations.find { it.id == selectedConversationId }

    if (isLargeScreen) {
        // Dual Pane Layout
        Row(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)) {
            // Left Pane: Chat List
            Box(modifier = Modifier.weight(0.4f).fillMaxHeight()) {
                ChatListPane(
                    conversations = conversations,
                    selectedConversationId = selectedConversationId,
                    onSelectConversation = onSelectConversation,
                    onBackClick = onBackToDashboard,
                    showBackButton = true
                )
            }
            
            VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

            // Right Pane: Message Detail or Placeholder
            Box(modifier = Modifier.weight(0.6f).fillMaxHeight()) {
                if (selectedConv != null) {
                    MessageDetailPane(
                        conversation = selectedConv,
                        onSendMessage = { text -> onSendMessage(selectedConv.id, text) },
                        onBackClick = { onSelectConversation(null) },
                        isDualPane = true
                    )
                } else {
                    ChatPlaceholderPane()
                }
            }
        }
    } else {
        // Single Pane Stack Layout
        if (selectedConv != null) {
            MessageDetailPane(
                conversation = selectedConv,
                onSendMessage = { text -> onSendMessage(selectedConv.id, text) },
                onBackClick = { onSelectConversation(null) },
                isDualPane = false
            )
        } else {
            ChatListPane(
                conversations = conversations,
                selectedConversationId = selectedConversationId,
                onSelectConversation = onSelectConversation,
                onBackClick = onBackToDashboard,
                showBackButton = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListPane(
    conversations: List<Conversation>,
    selectedConversationId: String?,
    onSelectConversation: (String) -> Unit,
    onBackClick: () -> Unit,
    showBackButton: Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conversations Hub", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBackClick) {
                            Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Rounded.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = "More")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        if (conversations.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Rounded.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No active conversations yet", style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(conversations) { conversation ->
                    val isSelected = conversation.id == selectedConversationId
                    ConversationItem(
                        conversation = conversation,
                        isSelected = isSelected,
                        onClick = { onSelectConversation(conversation.id) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 76.dp, end = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
fun ConversationItem(
    conversation: Conversation,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
    } else {
        Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with initials and Online Indicator
        Box(modifier = Modifier.size(52.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(getAvatarColor(conversation.avatarSeed)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = conversation.participantName.take(2).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            
            if (conversation.isOnline) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50))
                        .background(Color.White, shape = CircleShape)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name, text preview, timestamp, unread badge
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = conversation.participantName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = conversation.lastMessageTimestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (conversation.unreadCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = conversation.participantTitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conversation.lastMessageText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (conversation.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .defaultMinSize(minWidth = 20.dp, minHeight = 20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = conversation.unreadCount.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDetailPane(
    conversation: Conversation,
    onSendMessage: (String) -> Unit,
    onBackClick: () -> Unit,
    isDualPane: Boolean
) {
    var inputDelayText by remember(conversation.id) { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Scroll to bottom when conversation messages change
    LaunchedEffect(conversation.messages.size) {
        if (conversation.messages.isNotEmpty()) {
            listState.animateScrollToItem(conversation.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(getAvatarColor(conversation.avatarSeed)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = conversation.participantName.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                conversation.participantName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                if (conversation.isOnline) "Online" else "Offline",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (conversation.isOnline) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (!isDualPane) {
                        IconButton(onClick = onBackClick) {
                            Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = "Options")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Message History Thread
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(conversation.messages) { message ->
                    MessageBubble(message = message)
                }
            }

            // Bottom Input Field
            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                var showBottomSheet by remember { mutableStateOf(false) }

                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showBottomSheet = false }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                                .navigationBarsPadding(),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Text(
                                text = "Share Content",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                AttachmentOptionItem(
                                    icon = Icons.Rounded.AttachFile,
                                    label = "Document",
                                    color = Color(0xFF3F51B5)
                                ) {
                                    showBottomSheet = false
                                    onSendMessage("📎 Attached Document: Professional_Credentials.pdf")
                                }
                                AttachmentOptionItem(
                                    icon = Icons.Rounded.Person,
                                    label = "Contact",
                                    color = Color(0xFF009688)
                                ) {
                                    showBottomSheet = false
                                    onSendMessage("👤 Shared Contact: Dr. Amina Bello (ART-90313)")
                                }
                                AttachmentOptionItem(
                                    icon = Icons.Rounded.LocationOn,
                                    label = "Location",
                                    color = Color(0xFF4CAF50)
                                ) {
                                    showBottomSheet = false
                                    onSendMessage("📍 Shared Location: Ikeja General Hospital, Lagos")
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                AttachmentOptionItem(
                                    icon = Icons.Rounded.Info,
                                    label = "Audio / Voice",
                                    color = Color(0xFFFF9800)
                                ) {
                                    showBottomSheet = false
                                    onSendMessage("🎙️ Voice Note (0:14)")
                                }
                                AttachmentOptionItem(
                                    icon = Icons.Rounded.Search,
                                    label = "Gallery",
                                    color = Color(0xFFE91E63)
                                ) {
                                    showBottomSheet = false
                                    onSendMessage("📷 Photo Attachment (clinical_review_scan.jpg)")
                                }
                                Spacer(modifier = Modifier.width(60.dp))
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(
                            imageVector = Icons.Rounded.AttachFile,
                            contentDescription = "Attach File / Media",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    TextField(
                        value = inputDelayText,
                        onValueChange = { inputDelayText = it },
                        placeholder = { Text("Type a message...") },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    FloatingActionButton(
                        onClick = {
                            if (inputDelayText.isNotBlank()) {
                                onSendMessage(inputDelayText)
                                inputDelayText = ""
                            }
                        },
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Send,
                            contentDescription = "Send message"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val isMe = message.isMe
    val alignment = if (isMe) Alignment.End else Alignment.Start
    val bubbleColor = if (isMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }
    val contentColor = if (isMe) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSecondary
    }

    val bubbleShape = if (isMe) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = bubbleColor,
            contentColor = contentColor,
            shape = bubbleShape,
            tonalElevation = 1.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun ChatPlaceholderPane() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.ChatBubbleOutline,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Select a conversation to start messaging",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Your professional matches and active chat threads will appear here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 32.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

fun getAvatarColor(seed: String): Color {
    val hash = seed.hashCode()
    val colors = listOf(
        Color(0xFFE91E63), // Pink
        Color(0xFF9C27B0), // Purple
        Color(0xFF673AB7), // Deep Purple
        Color(0xFF3F51B5), // Indigo
        Color(0xFF2196F3), // Blue
        Color(0xFF009688), // Teal
        Color(0xFFFF5722)  // Deep Orange
    )
    return colors[Math.abs(hash) % colors.size]
}

@Composable
fun AttachmentOptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ChatListPanePhonePreview() {
    BMateMatchTheme(darkTheme = false) {
        val sampleList = listOf(
            Conversation("1", "Dr. Amina Bello", "Consultant", "amina", true, 2, "Hello there!", "12:30"),
            Conversation("2", "St. Nicholas Hospital", "Facility", "st", false, 0, "Shift details enclosed.", "Yesterday")
        )
        ChatListPane(
            conversations = sampleList,
            selectedConversationId = null,
            onSelectConversation = {},
            onBackClick = {},
            showBackButton = true
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 640)
@Composable
fun AdaptiveChatDualPanePreview() {
    BMateMatchTheme(darkTheme = false) {
        val sampleList = listOf(
            Conversation(
                "1", "Dr. Amina Bello", "Consultant", "amina", true, 2, "Hello there!", "12:30",
                messages = listOf(Message("m1", "other", "Hello there!", "12:30", false))
            ),
            Conversation("2", "St. Nicholas Hospital", "Facility", "st", false, 0, "Shift details enclosed.", "Yesterday")
        )
        AdaptiveChatScreen(
            conversations = sampleList,
            selectedConversationId = "1",
            onSelectConversation = {},
            onSendMessage = { _, _ -> },
            onBackToDashboard = {}
        )
    }
}
