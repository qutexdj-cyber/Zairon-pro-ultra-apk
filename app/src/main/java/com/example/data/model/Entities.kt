package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val conversationId: Long,
    val sender: String, // "user" or "zairon"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: String? = null,
    val sourcesJson: String? = null, // JSON list of web search citations
    val isError: Boolean = false,
    val modelUsed: String = "Gemini 3.5 Flash"
)

@Entity(tableName = "personal_memories")
data class PersonalMemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val key: String,
    val value: String,
    val category: String = "preference", // "preference", "fact", "project"
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

data class WebSource(
    val title: String,
    val snippet: String,
    val url: String
)
