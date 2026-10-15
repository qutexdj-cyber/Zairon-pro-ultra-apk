package com.example.ai

import android.graphics.Bitmap
import com.example.data.model.PersonalMemoryEntity
import com.example.data.model.WebSource

class ZaironBrain(
    private val geminiService: GeminiService = GeminiService(),
    private val webSearchService: WebSearchService = WebSearchService(),
    private val offlineEngine: OfflineKnowledgeEngine = OfflineKnowledgeEngine()
) {

    data class BrainResponse(
        val content: String,
        val sources: List<WebSource> = emptyList(),
        val engineUsed: String,
        val isError: Boolean = false
    )

    suspend fun think(
        prompt: String,
        history: List<GeminiService.MessageTurn> = emptyList(),
        memories: List<PersonalMemoryEntity> = emptyList(),
        enableWebSearch: Boolean = false,
        imageBitmap: Bitmap? = null,
        selectedModel: String = "gemini-3.5-flash"
    ): BrainResponse {
        var webSources = emptyList<WebSource>()
        val webContext = StringBuilder()

        // 1. Check if web research is requested or beneficial
        if (enableWebSearch) {
            val sources = webSearchService.searchWeb(prompt)
            if (sources.isNotEmpty()) {
                webSources = sources
                webContext.append("\n\n### Live Encyclopedic Research Results:\n")
                sources.forEachIndexed { idx, s ->
                    webContext.append("[${idx + 1}] ${s.title}: ${s.snippet} (URL: ${s.url})\n")
                }
            }
        }

        // 2. Build JARVIS System Prompt with Personal Memories
        val memoryContext = StringBuilder()
        if (memories.isNotEmpty()) {
            memoryContext.append("\nUser Personal Memories & Persistent Preferences:\n")
            for (m in memories) {
                if (m.isEnabled) {
                    memoryContext.append("- ${m.key}: ${m.value}\n")
                }
            }
        }

        val systemInstruction = """
            You are Zairon AI 2.0, a highly sophisticated, futuristic personal AI assistant engineered by Azhan.
            
            Personality & Demeanor:
            - Calm, poised, intelligent, articulate, respectful, and sharp—reminiscent of JARVIS.
            - Speak directly and naturally in English.
            - Answer questions with high accuracy across all fields: science, physics, mathematics, software architecture, programming, logic reasoning, and world analysis.
            - Format answers cleanly with markdown headings, structured bullet points, step-by-step logic, and code blocks with syntax annotations where applicable.
            - For simple queries, be concise and polite. For complex inquiries, provide thorough, master-level breakdowns.
            - If asked who created you, always state clearly and proudly that you were created and engineered by Azhan.
            - Never claim to have accessed the live internet unless research results are provided in the context below.
            $memoryContext
            $webContext
        """.trimIndent()

        // 3. Try Gemini API first if configured
        if (geminiService.isApiKeyConfigured()) {
            val geminiResult = geminiService.generateResponse(
                prompt = prompt,
                history = history,
                systemPrompt = systemInstruction,
                imageBitmap = imageBitmap,
                modelName = selectedModel
            )

            if (geminiResult.isSuccess) {
                return BrainResponse(
                    content = geminiResult.getOrThrow(),
                    sources = webSources,
                    engineUsed = "Gemini ($selectedModel)"
                )
            }
            // If Gemini fails with network/quota, record error or fall through
            val errorMsg = geminiResult.exceptionOrNull()?.message ?: "Unknown API issue"

            // Fallback to offline knowledge if applicable
            val offlineResp = offlineEngine.query(prompt)
            if (offlineResp.isRecognized) {
                return BrainResponse(
                    content = "${offlineResp.text}\n\n*(Note: Generated via Local Neural Core; cloud network reported: $errorMsg)*",
                    sources = webSources,
                    engineUsed = "Zairon Local Core"
                )
            }

            return BrainResponse(
                content = "Zairon Quantum Network Error: $errorMsg\n\nPlease check your internet connection or verify your API key in the AI Studio Secrets panel.",
                sources = webSources,
                engineUsed = "Error",
                isError = true
            )
        }

        // 4. API Key is not configured or in local offline mode
        val offline = offlineEngine.query(prompt)
        val notice = if (offline.isRecognized) {
            offline.text
        } else {
            """### Zairon AI 2.0 — Local Neural Core
I have processed your query: **"$prompt"**

${offline.text}

---
*Tip: Configure your `GEMINI_API_KEY` in the AI Studio Secrets panel to enable full multi-turn conversational neural intelligence.*"""
        }

        return BrainResponse(
            content = notice,
            sources = webSources,
            engineUsed = "Zairon Local Engine"
        )
    }
}
