package com.brogrammers.payana.core

import com.brogrammers.payana.ai.MatchExplainer
import com.brogrammers.payana.data.RideMatch
import com.brogrammers.payana.data.RideRequest
import com.brogrammers.payana.matching.MatchingEngine

data class ExplanationResult(
    val text: String,
    val fromAi: Boolean,
)

interface MatchExplanationService {
    suspend fun explain(request: RideRequest, match: RideMatch): ExplanationResult
}

class LocalMatchExplanationService : MatchExplanationService {
    override suspend fun explain(request: RideRequest, match: RideMatch): ExplanationResult {
        val text = match.compatibilitySummary.ifBlank {
            MatchingEngine.buildLocalExplanation(
                request = request,
                candidate = match.commuter,
                detour = match.estimatedDetourMinutes,
                delta = match.departureDeltaMinutes,
            )
        }
        return ExplanationResult(text = text, fromAi = false)
    }
}

class GeminiMatchExplanationService(
    private val explainer: MatchExplainer,
) : MatchExplanationService {
    override suspend fun explain(request: RideRequest, match: RideMatch): ExplanationResult {
        val result = explainer.explain(request, match)
        return ExplanationResult(text = result.text, fromAi = result.fromAi)
    }
}

/** Keeps the app demoable when Gemini is unavailable or the key is missing. */
class FallbackMatchExplanationService(
    private val primary: MatchExplanationService,
    private val fallback: MatchExplanationService = LocalMatchExplanationService(),
) : MatchExplanationService {
    override suspend fun explain(request: RideRequest, match: RideMatch): ExplanationResult {
        return try {
            val result = primary.explain(request, match)
            if (result.text.isBlank()) fallback.explain(request, match) else result
        } catch (_: Exception) {
            fallback.explain(request, match)
        }
    }
}
