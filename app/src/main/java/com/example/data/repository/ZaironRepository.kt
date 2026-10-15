package com.example.data.repository

import com.example.data.local.ChatMessageDao
import com.example.data.local.ConversationDao
import com.example.data.local.MemoryDao
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ConversationEntity
import com.example.data.model.PersonalMemoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class ZaironRepository(
    private val conversationDao: ConversationDao,
    private val chatMessageDao: ChatMessageDao,
    private val memoryDao: MemoryDao
) {
    val conversations: Flow<List<ConversationEntity>> = conversationDao.getAllConversations()
    val memories: Flow<List<PersonalMemoryEntity>> = memoryDao.getAllMemories()
    val recentMessages: Flow<List<ChatMessageEntity>> = chatMessageDao.getRecentMessages()

    fun getMessagesForConversation(conversationId: Long): Flow<List<ChatMessageEntity>> {
        return chatMessageDao.getMessagesForConversation(conversationId)
    }

    suspend fun getMessagesList(conversationId: Long): List<ChatMessageEntity> {
        return chatMessageDao.getMessagesList(conversationId)
    }

    suspend fun createConversation(title: String): Long {
        val conv = ConversationEntity(
            title = title,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return conversationDao.insertConversation(conv)
    }

    suspend fun updateConversationTitle(id: Long, title: String) {
        val existing = conversationDao.getConversationById(id)
        if (existing != null) {
            conversationDao.updateConversation(existing.copy(title = title, updatedAt = System.currentTimeMillis()))
        }
    }

    suspend fun touchConversation(id: Long) {
        val existing = conversationDao.getConversationById(id)
        if (existing != null) {
            conversationDao.updateConversation(existing.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    suspend fun deleteConversation(id: Long) {
        chatMessageDao.deleteMessagesForConversation(id)
        conversationDao.deleteConversationById(id)
    }

    suspend fun insertMessage(message: ChatMessageEntity): Long {
        val id = chatMessageDao.insertMessage(message)
        touchConversation(message.conversationId)
        return id
    }

    suspend fun deleteMessage(id: Long) {
        chatMessageDao.deleteMessageById(id)
    }

    suspend fun insertMemory(key: String, value: String, category: String = "preference"): Long {
        return memoryDao.insertMemory(
            PersonalMemoryEntity(
                key = key,
                value = value,
                category = category,
                isEnabled = true
            )
        )
    }

    suspend fun updateMemory(memory: PersonalMemoryEntity) {
        memoryDao.updateMemory(memory)
    }

    suspend fun deleteMemory(id: Long) {
        memoryDao.deleteMemoryById(id)
    }

    suspend fun getActiveMemories(): List<PersonalMemoryEntity> {
        return memoryDao.getActiveMemoriesList()
    }

    suspend fun clearAllData() {
        chatMessageDao.clearAllMessages()
        conversationDao.clearAllConversations()
        memoryDao.clearAllMemories()
    }

    suspend fun initializeDefaultDataIfEmpty() {
        val existing = conversations.firstOrNull()
        if (existing.isNullOrEmpty()) {
            val convId = createConversation("System Initialization")
            insertMessage(
                ChatMessageEntity(
                    conversationId = convId,
                    sender = "zairon",
                    content = "Good day, Sir. All systems are initialized and standing by. I am Zairon AI 2.0, your personal intelligent assistant engineered by Azhan. How may I be of service?",
                    timestamp = System.currentTimeMillis()
                )
            )

            insertMemory("Creator", "Engineered and created by Azhan", "fact")
            insertMemory("Personality", "Sophisticated, professional, JARVIS-inspired demeanor", "preference")
            insertMemory("Designation", "Zairon AI 2.0 Quantum Assistant", "fact")
            insertMemory("Primary Focus", "Scientific analysis, coding, problem solving, mathematics, and live research", "project")
        }
    }
}
