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
        "today", "is", "the", "day", "make", "it", "count", "never", "give", "up",
        "abundance", "achievement", "adventure", "ambition", "appreciation",
        "balance", "belief", "bravery", "brilliance", "celebration",
        "character", "clarity", "commitment", "compassion", "confidence",
        "contribution", "creativity", "curiosity", "dedication", "determination",
        "empathy", "enthusiasm", "excellence", "exploration", "faith",
        "flexibility", "focus", "forgiveness", "generosity", "gratitude",
        "harmony", "honesty", "hope", "imagination", "independence",
        "integrity", "intuition", "joy", "kindness", "leadership",
        "learning", "loyalty", "motivation", "optimism", "passion",
        "patience", "peace", "perseverance", "positivity", "purpose"
    )

    private val random = Random()

    /**
     * Generates a random challenge string of 12 to 18 words.
     */
    fun generateChallenge(): String {
        val length = random.nextInt(7) + 12 // 12 to 18
        val challengeWords = mutableListOf<String>()
        
        repeat(length) {
            if (random.nextInt(10) > 7) { // 20% chance of a random number string
                challengeWords.add(random.nextInt(1000).toString())
            } else {
                challengeWords.add(wordPool[random.nextInt(wordPool.size)])
            }
        }
        
        return challengeWords.joinToString(" ")
    }

    /**
     * Checks if the user input matches the challenge exactly (case-insensitive).
     */
    fun isMatch(input: String, challenge: String): Boolean {
        return input.trim().equals(challenge.trim(), ignoreCase = true)
    }
}