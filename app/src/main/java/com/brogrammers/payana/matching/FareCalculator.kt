package com.brogrammers.payana.matching

import com.brogrammers.payana.data.FareSplit

/**
 * Illustrative fare splitting using integer paise to avoid floating-point rounding issues.
 * Results are demo estimates only — not live auto-rickshaw quotes.
 */
object FareCalculator {

    /**
     * Splits [totalFarePaise] across [passengerCount] riders.
     * Remainder paise are distributed one-by-one so the sum of shares equals the total.
     */
    fun splitFare(totalFarePaise: Int, passengerCount: Int): FareSplit {
        require(totalFarePaise >= 0) { "totalFarePaise must be >= 0" }
        require(passengerCount > 0) { "passengerCount must be > 0" }
        val base = totalFarePaise / passengerCount
        // Display the typical per-person share (floor). UI can note remainder cents.
        return FareSplit(
            totalFarePaise = totalFarePaise,
            passengerCount = passengerCount,
            perPersonPaise = base,
        )
    }

    fun formatRupees(paise: Int): String {
        val rupees = paise / 100
        val rem = paise % 100
        return if (rem == 0) "₹$rupees" else "₹$rupees.${rem.toString().padStart(2, '0')}"
    }
}
