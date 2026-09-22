package com.example.bmatematch.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String,
    val senderId: String,
    val text: String,
    val timestamp: String,
    val isMe: Boolean,
    val attachmentUrl: String? = null
)

@Serializable
data class Conversation(
    val id: String,
    val participantName: String,
    val participantTitle: String,
    val avatarSeed: String,
    val isOnline: Boolean,
    val unreadCount: Int,
    val lastMessageText: String,
    val lastMessageTimestamp: String,
    val messages: List<Message> = emptyList()
)
