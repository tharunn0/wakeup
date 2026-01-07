package com.tharun.wakeup.util

import java.util.Random

class TextChallengeGenerator {

    private val wordPool = listOf(
        "The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog",
        "persistence", "is", "key", "to", "waking", "up", "early",
        "discipline", "equals", "freedom", "stay", "focused", "complete",
        "the", "challenge", "now", "success", "starts", "with", "action",
        "mind", "over", "matter", "stop", "dreaming", "start", "doing",
        "every", "morning", "is", "a", "new", "chance", "wake", "up",
        "and", "shine", "your", "future", "self", "will", "thank", "you",
        "consistency", "effort", "growth", "strength", "courage", "vision",
        "today", "is", "the", "day", "make", "it", "count", "never", "give", "up"
    )

    private val random = Random()

    /**
     * Generates a random challenge string of 12 to 18 words.
     */
    fun generateChallenge(): String {
        val length = random.nextInt(7) + 12 // 12 to 18
        val challengeWords = mutableListOf<String>()
        
        repeat(length) {
            challengeWords.add(wordPool[random.nextInt(wordPool.size)])
        }
        
        return challengeWords.joinToString(" ")
    }

    /**
     * Checks if the user input matches the challenge exactly (case-sensitive).
     */
    fun isMatch(input: String, challenge: String): Boolean {
        return input.trim() == challenge.trim()
    }
}