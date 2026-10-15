package com.example.ai

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    data class MessageTurn(
        val role: String, // "user" or "model"
        val text: String,
        val imageBitmap: Bitmap? = null
    )

    fun isApiKeyConfigured(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return !key.isNullOrBlank() && key != "MY_GEMINI_API_KEY"
    }

    suspend fun generateResponse(
        prompt: String,
        history: List<MessageTurn> = emptyList(),
        systemPrompt: String,
        imageBitmap: Bitmap? = null,
        modelName: String = "gemini-3.5-flash"
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException("GEMINI_API_KEY is not configured in AI Studio Secrets panel.")
            )
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"

            val rootJson = JSONObject()

            // System Instruction
            val sysInstructionObj = JSONObject()
            val sysParts = JSONArray()
            val sysPart = JSONObject().put("text", systemPrompt)
            sysParts.put(sysPart)
            sysInstructionObj.put("parts", sysParts)
            rootJson.put("systemInstruction", sysInstructionObj)

            // Contents array
            val contentsArr = JSONArray()

            // Add previous conversation turns (up to last 10 to keep within token budget)
            val recentHistory = history.takeLast(10)
            for (turn in recentHistory) {
                val contentObj = JSONObject()
                contentObj.put("role", if (turn.role == "zairon" || turn.role == "model") "model" else "user")
                val partsArr = JSONArray()
                if (!turn.text.isBlank()) {
                    partsArr.put(JSONObject().put("text", turn.text))
                }
                if (turn.imageBitmap != null) {
                    val base64Data = bitmapToBase64(turn.imageBitmap)
                    val inlineData = JSONObject()
                        .put("mimeType", "image/jpeg")
                        .put("data", base64Data)
                    partsArr.put(JSONObject().put("inlineData", inlineData))
                }
                contentObj.put("parts", partsArr)
                contentsArr.put(contentObj)
            }

            // Current turn
            val currentTurnObj = JSONObject()
            currentTurnObj.put("role", "user")
            val currentParts = JSONArray()
            currentParts.put(JSONObject().put("text", prompt))

            if (imageBitmap != null) {
                val base64Data = bitmapToBase64(imageBitmap)
                val inlineData = JSONObject()
                    .put("mimeType", "image/jpeg")
                    .put("data", base64Data)
                currentParts.put(JSONObject().put("inlineData", inlineData))
            }
            currentTurnObj.put("parts", currentParts)
            contentsArr.put(currentTurnObj)

            rootJson.put("contents", contentsArr)

            // Generation config
            val genConfig = JSONObject()
            genConfig.put("temperature", 0.7)
            rootJson.put("generationConfig", genConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = rootJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val respString = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                val errorMsg = try {
                    val errObj = JSONObject(respString).optJSONObject("error")
                    errObj?.optString("message") ?: "HTTP error ${response.code}"
                } catch (_: Exception) {
                    "HTTP error ${response.code}: $respString"
                }
                return@withContext Result.failure(Exception(errorMsg))
            }

            val respJson = JSONObject(respString)
            val candidates = respJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val parts = firstCandidate.optJSONObject("content")?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val textBuilder = StringBuilder()
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)
                        val text = part.optString("text", "")
                        textBuilder.append(text)
                    }
                    val resultText = textBuilder.toString()
                    if (resultText.isNotBlank()) {
                        return@withContext Result.success(resultText)
                    }
                }
            }

            Result.failure(Exception("No content received from Gemini model."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
