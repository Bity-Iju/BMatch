package com.example.bmatematch.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.bmatematch.data.model.Conversation
import com.example.bmatematch.data.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatViewModel : ViewModel() {

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _selectedConversationId = MutableStateFlow<String?>(null)
    val selectedConversationId: StateFlow<String?> = _selectedConversationId.asStateFlow()

    init {
        loadMockConversations()
    }

    private fun loadMockConversations() {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = dateFormat.format(Date())

        val conv1 = Conversation(
            id = "c1",
            participantName = "Dr. Amina Bello",
            participantTitle = "Chief Consultant, National Hospital",
            avatarSeed = "amina",
            isOnline = true,
            unreadCount = 2,
            lastMessageText = "We reviewed your professional profile and would love to schedule a briefing.",
            lastMessageTimestamp = "14:20",
            messages = listOf(
                Message("m1_1", "other", "Hello there! Welcome to BMatch.", "10:00", false),
                Message("m1_2", "me", "Thank you Dr. Amina. Glad to connect.", "10:15", true),
                Message("m1_3", "other", "We reviewed your professional profile and would love to schedule a briefing.", "14:20", false)
            )
        )

        val conv2 = Conversation(
            id = "c2",
            participantName = "St. Nicholas Hospital",
            participantTitle = "HR Department / Facility Match",
            avatarSeed = "stnicholas",
            isOnline = false,
            unreadCount = 0,
            lastMessageText = "Are you available for the locum shift this weekend?",
            lastMessageTimestamp = "Yesterday",
            messages = listOf(
                Message("m2_1", "other", "Are you available for the locum shift this weekend?", "Yesterday", false)
            )
        )

        val conv3 = Conversation(
            id = "c3",
            participantName = "Matron Eunice Taiwo",
            participantTitle = "Nursing Director, Crestview Clinic",
            avatarSeed = "eunice",
            isOnline = true,
            unreadCount = 0,
            lastMessageText = "Perfect, the credentials check passed successfully.",
            lastMessageTimestamp = "Sunday",
            messages = listOf(
                Message("m3_1", "me", "Sent over the required documentation.", "Sunday", true),
                Message("m3_2", "other", "Perfect, the credentials check passed successfully.", "Sunday", false)
            )
        )

        val conv4 = Conversation(
            id = "c4",
            participantName = "Lagoon Hospitals Group",
            participantTitle = "Clinical Operations Recruiter",
            avatarSeed = "lagoon",
            isOnline = false,
            unreadCount = 5,
            lastMessageText = "Please confirm your availability for an onboarding session.",
            lastMessageTimestamp = "Friday",
            messages = listOf(
                Message("m4_1", "other", "Hi there, we saw your match details.", "Friday", false),
                Message("m4_2", "other", "Please confirm your availability for an onboarding session.", "Friday", false)
            )
        )

        _conversations.value = listOf(conv1, conv2, conv3, conv4)
    }

    fun selectConversation(conversationId: String?) {
        _selectedConversationId.value = conversationId
        if (conversationId != null) {
            clearUnread(conversationId)
        }
    }

    fun sendMessage(conversationId: String, text: String) {
        if (text.isBlank()) return

        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeString = dateFormat.format(Date())

        _conversations.value = _conversations.value.map { conv ->
            if (conv.id == conversationId) {
                val newMsg = Message(
                    id = "msg_${System.currentTimeMillis()}",
                    senderId = "me",
                    text = text,
                    timestamp = timeString,
                    isMe = true
                )
                conv.copy(
                    messages = conv.messages + newMsg,
                    lastMessageText = text,
                    lastMessageTimestamp = timeString,
                    unreadCount = 0
                )
            } else {
                conv
            }
        }
    }

    fun clearUnread(conversationId: String) {
        _conversations.value = _conversations.value.map { conv ->
            if (conv.id == conversationId) {
                conv.copy(unreadCount = 0)
            } else {
                conv
            }
        }
    }
}
