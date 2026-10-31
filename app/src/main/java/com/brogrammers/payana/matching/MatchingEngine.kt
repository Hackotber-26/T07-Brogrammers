package com.brogrammers.payana.matching

import com.brogrammers.payana.data.Commuter
import com.brogrammers.payana.data.Destination
import com.brogrammers.payana.data.RideMatch
import com.brogrammers.payana.data.RideRequest
import com.brogrammers.payana.data.SampleData

/**
 * Deterministic matching engine. AI must never call into eligibility decisions.
 *
 * Route / detour numbers are simplified demo assumptions, not navigation API results.
 */
object MatchingEngine {

    fun findMatches(
        request: RideRequest,
        commuters: List<Commuter> = SampleData.seedCommuters,
    ): List<RideMatch> {
        val requestDest = SampleData.destination(request.destinationId) ?: return emptyList()

        return commuters
            .mapNotNull { candidate -> evaluate(request, requestDest, candidate) }
            .sortedWith(
                compareBy<RideMatch> { it.estimatedDetourMinutes }
                    .thenBy { it.departureDeltaMinutes }
                    .thenBy { it.commuter.displayName },
            )
    }

    private fun evaluate(
        request: RideRequest,
        requestDest: Destination,
        candidate: Commuter,
    ): RideMatch? {
        val candidateDest = SampleData.destination(candidate.destinationId) ?: return null

        // 1. Route / station compatibility (hard)
        if (!isRouteCompatible(request, requestDest, candidate, candidateDest)) return null

        // 2. Departure window (hard)
        val delta = kotlin.math.abs(candidate.departureMinutes - request.departureMinutes)
        if (delta > request.preferences.departureWindowMinutes) return null

        // 3. Capacity (hard)
        if (candidate.seatsAvailable < request.passengerCount) return null

        // 4. Detour limit (hard) — simplified demo estimate
        val detour = estimateDetourMinutes(requestDest, candidateDest)
        if (detour > request.preferences.maxDetourMinutes) return null

        // 5. Women-only preference (hard) — explicit demo attributes only
        if (request.preferences.womenOnly && !candidate.womenOnlyGroup) return null

        val totalPassengers = candidate.groupSize + request.passengerCount
        val fare = FareCalculator.splitFare(candidate.assumedRideFarePaise, totalPassengers)

        return RideMatch(
            commuter = candidate,
            estimatedDetourMinutes = detour,
            departureDeltaMinutes = delta,
            fareSplit = fare,
            compatibilitySummary = buildLocalExplanation(
                request = request,
                candidate = candidate,
                detour = detour,
                delta = delta,
            ),
        )
    }

    /**
     * Simplified compatibility:
     * - Same start station, OR
     * - Same destination corridor and destinations share / neighbour a station area.
     */
    private fun isRouteCompatible(
        request: RideRequest,
        requestDest: Destination,
        candidate: Commuter,
        candidateDest: Destination,
    ): Boolean {
        if (request.startStationId != candidate.startStationId) return false
        // Same destination is always compatible.
        if (request.destinationId == candidate.destinationId) return true
        // Same corridor = roughly same last-mile direction for the demo.
        return requestDest.corridor == candidateDest.corridor
    }

    /**
     * Demo-only detour heuristic (minutes). Not live traffic or maps.
     */
    fun estimateDetourMinutes(a: Destination, b: Destination): Int {
        if (a.id == b.id) return 3
        if (a.corridor == b.corridor && a.nearestStationId == b.nearestStationId) return 8
        if (a.corridor == b.corridor) return 12
        // Different corridor from same start → large detour
        return 35
    }

    fun buildLocalExplanation(
        request: RideRequest,
        candidate: Commuter,
        detour: Int,
        delta: Int,
    ): String {
        val timeNote = when {
            delta == 0 -> "You plan to leave at the same time"
            delta <= 5 -> "Your departure times are nearly the same"
            else -> "Your departure times are within ${request.preferences.departureWindowMinutes} minutes"
        }
        val destNote = if (request.destinationId == candidate.destinationId) {
            "heading to the same landmark"
        } else {
            "heading in a compatible last-mile direction"
        }
        val womenNote = if (request.preferences.womenOnly && candidate.womenOnlyGroup) {
            " This group is marked women-only in the demo data."
        } else {
            ""
        }
        return "$timeNote (${formatTime(request.departureMinutes)} vs ${formatTime(candidate.departureMinutes)}), " +
            "$destNote, and the estimated extra detour is about $detour min — within your ${request.preferences.maxDetourMinutes}-min limit.$womenNote"
    }

    fun formatTime(minutesSinceMidnight: Int): String {
        val h = (minutesSinceMidnight / 60) % 24
        val m = minutesSinceMidnight % 60
        val period = if (h < 12) "AM" else "PM"
        val displayH = when {
            h == 0 -> 12
            h > 12 -> h - 12
            else -> h
        }
        return "%d:%02d %s".format(displayH, m, period)
    }
}
