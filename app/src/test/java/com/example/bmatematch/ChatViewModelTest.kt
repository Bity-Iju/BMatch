package com.example.bmatematch

import com.example.bmatematch.ui.viewmodel.ChatViewModel
import org.junit.Test
import org.junit.Assert.*

class ChatViewModelTest {

    @Test
    fun chatViewModel_initializesWithMockConversations() {
        val viewModel = ChatViewModel()
        val conversations = viewModel.conversations.value
        
        assertFalse(conversations.isEmpty())
        assertEquals(4, conversations.size)
        assertEquals("Dr. Amina Bello", conversations[0].participantName)
    }

    @Test
    fun chatViewModel_selectConversation_clearsUnreadCount() {
        val viewModel = ChatViewModel()
        
        // Given a conversation with unread counts
        val initialUnread = viewModel.conversations.value.find { it.id == "c1" }?.unreadCount ?: 0
        assertTrue(initialUnread > 0)
        
        // When selecting it
        viewModel.selectConversation("c1")
        
        // Then selected conversation ID is updated and unread count is cleared
        assertEquals("c1", viewModel.selectedConversationId.value)
        val finalUnread = viewModel.conversations.value.find { it.id == "c1" }?.unreadCount ?: -1
        assertEquals(0, finalUnread)
    }

    @Test
    fun chatViewModel_sendMessage_appendsMessageAndUpdatesLastMessage() {
        val viewModel = ChatViewModel()
        val messageText = "I will attend the onboarding session."
        
        // When sending a message to conversation 'c1'
        viewModel.sendMessage("c1", messageText)
        
        // Then the conversation last message is updated
        val updatedConv = viewModel.conversations.value.find { it.id == "c1" }
        assertNotNull(updatedConv)
        assertEquals(messageText, updatedConv?.lastMessageText)
        
        // And the message list contains the new message from 'me'
        val lastMessage = updatedConv?.messages?.last()
        assertNotNull(lastMessage)
        assertEquals(messageText, lastMessage?.text)
        assertTrue(lastMessage?.isMe ?: false)
    }
}
