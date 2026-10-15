package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiService
import com.example.ai.ZaironBrain
import com.example.data.local.ZaironDatabase
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ConversationEntity
import com.example.data.model.PersonalMemoryEntity
import com.example.data.repository.ZaironRepository
import com.example.ui.components.OrbState
import com.example.ui.theme.AccentTheme
import com.example.voice.VoiceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ZaironViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ZaironDatabase.getDatabase(application)
    private val repository = ZaironRepository(
        conversationDao = db.conversationDao(),
        chatMessageDao = db.chatMessageDao(),
        memoryDao = db.memoryDao()
    )

    private val brain = ZaironBrain()
    val voiceManager = VoiceManager(application)

    // Conversations & Active Selection
    val conversations: StateFlow<List<ConversationEntity>> = repository.conversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentConversationId = MutableStateFlow<Long?>(null)
    val currentConversationId: StateFlow<Long?> = _currentConversationId.asStateFlow()

    // Current Messages
    val currentMessages: StateFlow<List<ChatMessageEntity>> = _currentConversationId
        .flatMapLatest { id ->
            if (id != null) repository.getMessagesForConversation(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Memories
    val memories: StateFlow<List<PersonalMemoryEntity>> = repository.memories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI States
    private val _orbState = MutableStateFlow(OrbState.IDLE)
    val orbState: StateFlow<OrbState> = _orbState.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _webSearchEnabled = MutableStateFlow(false)
    val webSearchEnabled: StateFlow<Boolean> = _webSearchEnabled.asStateFlow()

    private val _accentTheme = MutableStateFlow(AccentTheme.EMERALD)
    val accentTheme: StateFlow<AccentTheme> = _accentTheme.asStateFlow()

    private val _selectedModel = MutableStateFlow("gemini-3.5-flash")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _speakingMessageId = MutableStateFlow<Long?>(null)
    val speakingMessageId: StateFlow<Long?> = _speakingMessageId.asStateFlow()

    val isListening: StateFlow<Boolean> = voiceManager.isListening
    val isSpeaking: StateFlow<Boolean> = voiceManager.isSpeaking
    val liveTranscription: StateFlow<String> = voiceManager.liveTranscription
    val audioRms: StateFlow<Float> = voiceManager.audioRms

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
            conversations.collect { list ->
                if (_currentConversationId.value == null && list.isNotEmpty()) {
                    _currentConversationId.value = list.first().id
                }
            }
        }

        viewModelScope.launch {
            isListening.collect { listening ->
                if (listening) {
                    _orbState.value = OrbState.LISTENING
                } else if (_isGenerating.value) {
                    _orbState.value = OrbState.THINKING
                } else if (isSpeaking.value) {
                    _orbState.value = OrbState.SPEAKING
                } else {
                    _orbState.value = OrbState.IDLE
                }
            }
        }

        viewModelScope.launch {
            isSpeaking.collect { speaking ->
                if (speaking) {
                    _orbState.value = OrbState.SPEAKING
                } else if (!isListening.value && !_isGenerating.value) {
                    _orbState.value = OrbState.IDLE
                    _speakingMessageId.value = null
                }
            }
        }
    }

    fun selectConversation(id: Long) {
        _currentConversationId.value = id
    }

    fun createNewConversation(initialTitle: String = "New Objective") {
        viewModelScope.launch {
            val newId = repository.createConversation(initialTitle)
            _currentConversationId.value = newId
        }
    }

    fun deleteConversation(id: Long) {
        viewModelScope.launch {
            repository.deleteConversation(id)
            if (_currentConversationId.value == id) {
                val remaining = conversations.value.filter { it.id != id }
                _currentConversationId.value = remaining.firstOrNull()?.id
            }
        }
    }

    fun renameConversation(id: Long, newTitle: String) {
        viewModelScope.launch {
            repository.updateConversationTitle(id, newTitle)
        }
    }

    fun toggleWebSearch() {
        _webSearchEnabled.value = !_webSearchEnabled.value
    }

    fun setAccentTheme(theme: AccentTheme) {
        _accentTheme.value = theme
    }

    fun setSelectedModel(model: String) {
        _selectedModel.value = model
    }

    fun setPitch(pitch: Float) {
        voiceManager.pitch = pitch
    }

    fun setSpeechRate(rate: Float) {
        voiceManager.speechRate = rate
    }

    fun sendMessage(
        prompt: String,
        imageBitmap: Bitmap? = null,
        autoSpeak: Boolean = false
    ) {
        val trimmed = prompt.trim()
        if (trimmed.isBlank() && imageBitmap == null) return

        viewModelScope.launch {
            var convId = _currentConversationId.value
            if (convId == null) {
                convId = repository.createConversation(trimmed.take(30))
                _currentConversationId.value = convId
            }

            // Save user message
            repository.insertMessage(
                ChatMessageEntity(
                    conversationId = convId,
                    sender = "user",
                    content = trimmed,
                    timestamp = System.currentTimeMillis()
                )
            )

            // Update conversation title if default
            val activeConv = conversations.value.find { it.id == convId }
            if (activeConv?.title == "New Objective" || activeConv?.title == "System Initialization") {
                repository.updateConversationTitle(convId, trimmed.take(28))
            }

            _isGenerating.value = true
            _orbState.value = OrbState.THINKING

            // Fetch active memories
            val activeMemories = repository.getActiveMemories()

            // Prepare history turns
            val historyList = currentMessages.value.map {
                GeminiService.MessageTurn(
                    role = if (it.sender == "user") "user" else "model",
                    text = it.content
                )
            }

            val brainResult = brain.think(
                prompt = trimmed,
                history = historyList,
                memories = activeMemories,
                enableWebSearch = _webSearchEnabled.value,
                imageBitmap = imageBitmap,
                selectedModel = _selectedModel.value
            )

            _isGenerating.value = false

            val sourcesJson = if (brainResult.sources.isNotEmpty()) {
                val arr = JSONArray()
                brainResult.sources.forEach {
                    val obj = JSONObject()
                    obj.put("title", it.title)
                    obj.put("snippet", it.snippet)
                    obj.put("url", it.url)
                    arr.put(obj)
                }
                arr.toString()
            } else null

            val assistantMsgId = repository.insertMessage(
                ChatMessageEntity(
                    conversationId = convId,
                    sender = "zairon",
                    content = brainResult.content,
                    timestamp = System.currentTimeMillis(),
                    sourcesJson = sourcesJson,
                    isError = brainResult.isError,
                    modelUsed = brainResult.engineUsed
                )
            )

            if (brainResult.isError) {
                _orbState.value = OrbState.ERROR
            } else {
                _orbState.value = OrbState.IDLE
            }

            if (autoSpeak && !brainResult.isError) {
                speakMessage(assistantMsgId, brainResult.content)
            }
        }
    }

    fun regenerateLastAnswer() {
        val messages = currentMessages.value
        val lastUserMsg = messages.lastOrNull { it.sender == "user" }
        if (lastUserMsg != null) {
            sendMessage(lastUserMsg.content)
        }
    }

    fun speakMessage(messageId: Long, text: String) {
        _speakingMessageId.value = messageId
        voiceManager.speak(text)
    }

    fun stopSpeaking() {
        voiceManager.stopSpeaking()
        _speakingMessageId.value = null
        if (!isListening.value && !_isGenerating.value) {
            _orbState.value = OrbState.IDLE
        }
    }

    fun startVoiceListening(onError: (String) -> Unit) {
        voiceManager.startListening(
            onFinalResult = { recognizedText ->
                if (recognizedText.isNotBlank()) {
                    sendMessage(recognizedText, autoSpeak = true)
                }
            },
            onError = onError
        )
    }

    fun stopVoiceListening() {
        voiceManager.stopListening()
    }

    fun addMemory(key: String, value: String, category: String = "preference") {
        viewModelScope.launch {
            repository.insertMemory(key, value, category)
        }
    }

    fun toggleMemory(memory: PersonalMemoryEntity) {
        viewModelScope.launch {
            repository.updateMemory(memory.copy(isEnabled = !memory.isEnabled))
        }
    }

    fun deleteMemory(id: Long) {
        viewModelScope.launch {
            repository.deleteMemory(id)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            repository.initializeDefaultDataIfEmpty()
        }
    }

    fun exportChatText(): String {
        val conv = conversations.value.find { it.id == _currentConversationId.value }
        val title = conv?.title ?: "Zairon Chat"
        val sb = StringBuilder()
        sb.append("=== ZAIRON AI 2.0 CONVERSATION EXPORT ===\n")
        sb.append("Title: $title\n")
        sb.append("Export Date: ${java.util.Date()}\n\n")

        for (msg in currentMessages.value) {
            val sender = if (msg.sender == "user") "USER" else "ZAIRON AI 2.0"
            sb.append("[$sender]:\n")
            sb.append("${msg.content}\n\n")
            sb.append("-------------------------------------------\n\n")
        }
        return sb.toString()
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.destroy()
    }
}
