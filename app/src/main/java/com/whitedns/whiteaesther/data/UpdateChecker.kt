package com.whitedns.whiteaesther.data

import android.content.Context

/** Update checks remain disabled until Sobito has a verified release channel. */
object UpdateChecker {
    data class Available(val version: String, val url: String)

    @Suppress("UNUSED_PARAMETER")
    suspend fun dismiss(context: Context, version: String) = Unit

    internal fun isNewer(candidate: String, current: String): Boolean {
        val left = numericParts(candidate)
        val right = numericParts(current)
        if (left.isEmpty() || right.isEmpty()) return false
        for (index in 0 until maxOf(left.size, right.size)) {
            val a = left.getOrElse(index) { 0 }
            val b = right.getOrElse(index) { 0 }
            if (a != b) return a > b
        }
        return false
    }

    private fun numericParts(version: String): List<Int> =
        version.trim()
            .removePrefix("v")
            .takeWhile { it.isDigit() || it == '.' }
            .split('.')
            .mapNotNull { it.toIntOrNull() }

}
