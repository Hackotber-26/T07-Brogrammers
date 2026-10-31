package com.brogrammers.payana.ai

import com.brogrammers.payana.data.RideMatch
import com.brogrammers.payana.data.RideRequest
import com.brogrammers.payana.matching.MatchingEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class ExplanationResult(
    val text: String,
    val fromAi: Boolean,
)

class MatchExplainer(
    private val apiKey: String,
    private val model: String = DEFAULT_MODEL,
    private val baseUrl: String = DEFAULT_BASE_URL,
    private val timeoutMs: Long = 12_000L,
) {
    suspend fun explain(request: RideRequest, match: RideMatch): ExplanationResult {
        val fallback = match.compatibilitySummary.ifBlank {
            MatchingEngine.buildLocalExplanation(
                request = request,
                candidate = match.commuter,
                detour = match.estimatedDetourMinutes,
                delta = match.departureDeltaMinutes,
            )
        }

        if (apiKey.isBlank()) return ExplanationResult(fallback, false)

        return try {
            val aiText = withTimeout(timeoutMs) {
                withContext(Dispatchers.IO) { callGemini(request, match) }
            }
            if (aiText.isNullOrBlank()) ExplanationResult(fallback, false)
            else ExplanationResult(aiText.trim(), true)
        } catch (_: Exception) {
            ExplanationResult(fallback, false)
        }
    }

    private fun callGemini(request: RideRequest, match: RideMatch): String? {
        val instructions =
            "You explain already-validated metro last-mile ride matches for Payana. " +
                    "Write 1-2 short friendly sentences. Do not invent eligibility. " +
                    "Do not claim real-time traffic, live fares, or verified identities. " +
                    "Mention illustrative demo estimates when relevant."

        val body = JSONObject()
            .put(
                "contents",
                JSONArray().put(
                    JSONObject()
                        .put("role", "user")
                        .put(
                            "parts",
                            JSONArray().put(
                                JSONObject().put(
                                    "text",
                                    "$instructions\n\n${buildPrompt(request, match)}",
                                ),
                            ),
                        ),
                ),
            )
            .put(
                "generationConfig",
                JSONObject().put("temperature", 0.3).put("maxOutputTokens", 120),
            )

        val url = URL("$baseUrl/models/$model:generateContent")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 8_000
            readTimeout = 10_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("x-goog-api-key", apiKey)
        }

        try {
            OutputStreamWriter(conn.outputStream).use { it.write(body.toString()) }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val response = stream?.bufferedReader()?.use(BufferedReader::readText).orEmpty()
            if (code !in 200..299) return null

            val parts = JSONObject(response)
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")

            return buildString {
                for (index in 0 until parts.length()) {
                    val text = parts.getJSONObject(index).optString("text")
                    if (text.isNotBlank()) append(text)
                }
            }
        } finally {
            conn.disconnect()
        }
    }

    private fun buildPrompt(request: RideRequest, match: RideMatch): String {
        val c = match.commuter
        return """
            Explain why this validated match is suitable (facts only):
            - Requester leaves at ${MatchingEngine.formatTime(request.departureMinutes)} from station ${request.startStationId}, going to ${request.destinationId}, ${request.passengerCount} passenger(s), max detour ${request.preferences.maxDetourMinutes} min, womenOnly=${request.preferences.womenOnly}.
            - Candidate ${c.id} leaves at ${MatchingEngine.formatTime(c.departureMinutes)} from ${c.startStationId} to ${c.destinationId}, groupSize=${c.groupSize}, seatsAvailable=${c.seatsAvailable}, womenOnlyGroup=${c.womenOnlyGroup}.
            - Estimated detour: ${match.estimatedDetourMinutes} min (demo heuristic).
            - Departure delta: ${match.departureDeltaMinutes} min.
            - Illustrative fare share: ${match.fareSplit.perPersonPaise} paise of ${match.fareSplit.totalFarePaise} paise across ${match.fareSplit.passengerCount} people.
        """.trimIndent()
    }

    companion object {
        const val DEFAULT_BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
        const val DEFAULT_MODEL = "gemini-3.8-flash"
    }
}
