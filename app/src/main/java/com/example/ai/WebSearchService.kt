package com.example.ai

import com.example.data.model.WebSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class WebSearchService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build()
) {
    suspend fun searchWeb(query: String): List<WebSource> = withContext(Dispatchers.IO) {
        val results = mutableListOf<WebSource>()
        try {
            // 1. Query Wikipedia Search API
            val encoded = URLEncoder.encode(query, "UTF-8")
            val wikiUrl = "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=$encoded&utf8=&format=json&srlimit=3"
            val wikiReq = Request.Builder()
                .url(wikiUrl)
                .header("User-Agent", "ZaironAI/2.0 (Personal AI Assistant)")
                .build()

            val wikiResp = client.newCall(wikiReq).execute()
            if (wikiResp.isSuccessful) {
                val body = wikiResp.body?.string()
                if (!body.isNullOrBlank()) {
                    val root = JSONObject(body)
                    val searchArr = root.optJSONObject("query")?.optJSONArray("search")
                    if (searchArr != null) {
                        for (i in 0 until searchArr.length()) {
                            val item = searchArr.getJSONObject(i)
                            val title = item.optString("title", "Wikipedia Entry")
                            val snippetHtml = item.optString("snippet", "")
                            val cleanSnippet = snippetHtml.replace(Regex("<[^>]*>"), "")
                            val articleUrl = "https://en.wikipedia.org/wiki/${URLEncoder.encode(title.replace(" ", "_"), "UTF-8")}"
                            results.add(
                                WebSource(
                                    title = title,
                                    snippet = cleanSnippet,
                                    url = articleUrl
                                )
                            )
                        }
                    }
                }
            }

            // 2. Query DuckDuckGo Instant Answer if needed
            if (results.size < 2) {
                val ddgUrl = "https://api.duckduckgo.com/?q=$encoded&format=json&no_html=1&skip_disambig=1"
                val ddgReq = Request.Builder()
                    .url(ddgUrl)
                    .header("User-Agent", "ZaironAI/2.0")
                    .build()
                val ddgResp = client.newCall(ddgReq).execute()
                if (ddgResp.isSuccessful) {
                    val body = ddgResp.body?.string()
                    if (!body.isNullOrBlank()) {
                        val ddgObj = JSONObject(body)
                        val abstractText = ddgObj.optString("AbstractText", "")
                        val abstractSource = ddgObj.optString("AbstractSource", "DuckDuckGo Instant Answer")
                        val abstractUrl = ddgObj.optString("AbstractURL", "")
                        if (abstractText.isNotBlank()) {
                            results.add(
                                WebSource(
                                    title = if (abstractSource.isNotBlank()) abstractSource else "Knowledge Abstract",
                                    snippet = abstractText,
                                    url = if (abstractUrl.isNotBlank()) abstractUrl else "https://duckduckgo.com/?q=$encoded"
                                )
                            )
                        }
                    }
                }
            }
        } catch (_: Exception) {
            // Network fallback gracefully
        }
        results
    }
}
