package com.brogrammers.payana.data

/**
 * Core domain models for Payana.
 * Times are minutes since midnight for deterministic comparisons.
 * Money is stored in integer paise (1 INR = 100 paise).
 */

data class MetroStation(
    val id: String,
    val name: String,
    val line: String,
)

data class Destination(
    val id: String,
    val name: String,
    /** Nearest metro station used for simplified last-mile routing. */
    val nearestStationId: String,
    /** Demo corridor tag for coarse direction compatibility. */
    val corridor: String,
)

data class MatchPreferences(
    val maxDetourMinutes: Int,
    val womenOnly: Boolean,
    /** Allowed departure difference in minutes (hard matching window). */
    val departureWindowMinutes: Int = 20,
)

data class RideRequest(
    val startStationId: String,
    val destinationId: String,
    val departureMinutes: Int,
    val passengerCount: Int,
    val preferences: MatchPreferences,
)

/**
 * Fictional demo commuter already looking for riders.
 * Gender / women-only flags are explicit demo attributes only — never inferred from name.
 */
data class Commuter(
    val id: String,
    val displayName: String,
    val startStationId: String,
    val destinationId: String,
    val departureMinutes: Int,
    val groupSize: Int,
    val seatsAvailable: Int,
    /** Explicit fictional demo attribute for women-only matching. */
    val womenOnlyGroup: Boolean,
    /** Simplified assumed total auto fare for this ride, in paise. */
    val assumedRideFarePaise: Int,
)

data class FareSplit(
    val totalFarePaise: Int,
    val passengerCount: Int,
    val perPersonPaise: Int,
) {
    init {
        require(passengerCount > 0) { "passengerCount must be > 0" }
    }
}

data class RideMatch(
    val commuter: Commuter,
    val estimatedDetourMinutes: Int,
    val departureDeltaMinutes: Int,
    val fareSplit: FareSplit,
    /** Deterministic local reason; AI may polish this later without changing eligibility. */
    val compatibilitySummary: String,
)

sealed class ScreenState {
    data object Home : ScreenState()
    data class Loading(val message: String = "Finding compatible rides…") : ScreenState()
    data class Results(
        val request: RideRequest,
        val matches: List<RideMatch>,
    ) : ScreenState()
    data class NoMatch(val request: RideRequest) : ScreenState()
    data class Details(
        val request: RideRequest,
        val match: RideMatch,
        val aiExplanation: String,
        val explanationFromAi: Boolean,
        val requestConfirmed: Boolean = false,
    ) : ScreenState()
    data class Accepted(
        val request: RideRequest,
        val match: RideMatch,
    ) : ScreenState()
    data class Tracking(
        val request: RideRequest,
        val match: RideMatch,
    ) : ScreenState()
    data class Error(val message: String, val returnTo: ScreenState = Home) : ScreenState()
}
