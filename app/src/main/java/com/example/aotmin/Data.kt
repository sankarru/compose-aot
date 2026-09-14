package com.example.aotmin

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Fact(
    val title: String,
    val detail: String,
    val level: Int = 0,
)

private val FACTS_JSON = """
    [
      {"title": "R8 full mode", "detail": "Shrinks, optimizes and obfuscates at compile time", "level": 3},
      {"title": "D8", "detail": "Dexes the R8 output; the only dexer since AGP 3.1", "level": 2},
      {"title": "Baseline profile", "detail": "dexopt AOT-compiles hot methods at install time", "level": 2},
      {"title": "No hardcoded paths", "detail": "SDK via ANDROID_HOME, Gradle via wrapper", "level": 1}
    ]
""".trimIndent()

private val json = Json { ignoreUnknownKeys = true }

/** Parsed off the main thread so the demo exercises coroutines + serialization. */
suspend fun loadFacts(): List<Fact> = withContext(Dispatchers.Default) {
    json.decodeFromString<List<Fact>>(FACTS_JSON)
}
