package com.brogrammers.payana.ui

fun maskedCommuterLabel(id: String): String {
    val index = id.filter { it.isDigit() }.toIntOrNull() ?: 0
    return if (index in 1..26) "Commuter ${('A'.code + index - 1).toChar()}" else "Another commuter"
}
