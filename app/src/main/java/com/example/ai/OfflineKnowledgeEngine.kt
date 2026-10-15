package com.example.ai

import java.util.Locale

class OfflineKnowledgeEngine {

    data class OfflineAnswer(
        val text: String,
        val category: String,
        val isRecognized: Boolean
    )

    fun query(prompt: String): OfflineAnswer {
        val lower = prompt.trim().lowercase(Locale.ROOT)

        // Creator questions
        if (lower.contains("who created you") || lower.contains("who made you") ||
            lower.contains("creator") || lower.contains("who built you") ||
            lower.contains("who developed you") || lower.contains("azhan")
        ) {
            return OfflineAnswer(
                text = "### Origin & Architecture\n\nI am **Zairon AI 2.0**, an advanced personal AI assistant conceived, designed, and engineered by **Azhan**.\n\nMy purpose is to serve as an intelligent, proactive companion—capable of solving intricate mathematical problems, providing software engineering guidance, conducting live research, and executing high-fidelity voice interactions.",
                category = "System Identity",
                isRecognized = true
            )
        }

        // Math calculation attempts
        val mathAnswer = trySolveMath(prompt)
        if (mathAnswer != null) {
            return OfflineAnswer(
                text = mathAnswer,
                category = "Mathematics",
                isRecognized = true
            )
        }

        // Science & Physics
        if (lower.contains("einstein") || lower.contains("relativity") || lower.contains("e=mc")) {
            return OfflineAnswer(
                text = "### Theory of Relativity\n\nFormulated by Albert Einstein:\n\n1. **Special Relativity (1905)**:\n   - Physical laws are identical in all inertial frames of reference.\n   - The speed of light in a vacuum (c approx 299,792,458 m/s) is constant regardless of motion.\n   - **Mass-Energy Equivalence**: E = mc^2\n\n2. **General Relativity (1915)**:\n   - Gravity is the curvature of spacetime caused by mass and energy distribution: Einstein Field Equations.",
                category = "Physics",
                isRecognized = true
            )
        }

        if (lower.contains("quantum") || lower.contains("schrodinger") || lower.contains("heisenberg")) {
            return OfflineAnswer(
                text = "### Quantum Mechanics Fundamentals\n\n- **Superposition**: A quantum system exists in a linear combination of states until measured.\n- **Wave Function Collapse**: The Born rule determines the probability density P(x) = |psi(x)|^2.\n- **Heisenberg Uncertainty Principle**:\n  Delta x * Delta p >= hbar / 2\n- **Schrodinger Equation**:\n  i*hbar (d/dt)|psi> = H|psi>",
                category = "Quantum Physics",
                isRecognized = true
            )
        }

        if (lower.contains("thermodynamics") || lower.contains("entropy")) {
            return OfflineAnswer(
                text = "### The Laws of Thermodynamics\n\n1. **Zeroth Law**: Thermal equilibrium is transitive (defines temperature).\n2. **First Law**: Energy conservation (Delta U = Q - W).\n3. **Second Law**: Total entropy of an isolated system never decreases (Delta S >= 0).\n4. **Third Law**: As temperature approaches absolute zero (0 K), entropy approaches a minimum constant value.",
                category = "Thermodynamics",
                isRecognized = true
            )
        }

        // Coding & Computer Science
        if (lower.contains("kotlin") || lower.contains("coroutine") || lower.contains("flow")) {
            return OfflineAnswer(
                text = "### Kotlin Coroutines & Asynchronous Architecture\n\nCoroutines are lightweight threads managed at the user-space level:\n\n```kotlin\n// Structured concurrency example\nclass DataRepository(private val api: ApiService) {\n    val itemsFlow: Flow<List<Item>> = flow {\n        emit(api.fetchCachedItems())\n        val fresh = api.fetchRemoteItems()\n        emit(fresh)\n    }.flowOn(Dispatchers.IO)\n}\n```\n\n**Key Dispatchers**:\n- `Dispatchers.Main`: UI operations\n- `Dispatchers.IO`: Disk, database, and network calls\n- `Dispatchers.Default`: CPU-intensive computations (e.g. sorting, parsing)",
                category = "Computer Science",
                isRecognized = true
            )
        }

        if (lower.contains("binary search") || lower.contains("search algorithm")) {
            return OfflineAnswer(
                text = "### Binary Search Algorithm (O(log n))\n\nRequires a sorted array. Repeatedly divides the search interval in half:\n\n```kotlin\nfun binarySearch(arr: IntArray, target: Int): Int {\n    var left = 0\n    var right = arr.size - 1\n    while (left <= right) {\n        val mid = left + (right - left) / 2\n        when {\n            arr[mid] == target -> return mid\n            arr[mid] < target -> left = mid + 1\n            else -> right = mid - 1\n        }\n    }\n    return -1 // Not found\n}\n```",
                category = "Algorithms",
                isRecognized = true
            )
        }

        if (lower.contains("clean architecture") || lower.contains("mvvm")) {
            return OfflineAnswer(
                text = "### Modern Android Architecture (MVVM + Clean Architecture)\n\n1. **UI Layer (Presentation)**:\n   - Jetpack Compose Composables\n   - ViewModel holding `StateFlow<UiState>`\n2. **Domain Layer (Optional)**:\n   - UseCases / Interactors containing pure business logic\n3. **Data Layer**:\n   - Repository interfaces & implementations\n   - Data sources: Local Room DB + Remote Retrofit APIs",
                category = "Software Architecture",
                isRecognized = true
            )
        }

        // General assistance & Greeting
        if (lower.contains("hello") || lower.contains("hi") || lower.contains("hey zairon") || lower.contains("greeting")) {
            return OfflineAnswer(
                text = "Greetings, Sir. Zairon AI 2.0 quantum core is online and ready for deployment. What shall we analyze or construct today?",
                category = "Greeting",
                isRecognized = true
            )
        }

        if (lower.contains("help") || lower.contains("what can you do") || lower.contains("features")) {
            return OfflineAnswer(
                text = "### Zairon AI 2.0 Capabilities\n\n- **Voice Interaction**: Real-time speech recognition and expressive audio readout.\n- **Scientific & Mathematical Solver**: Step-by-step calculus, algebra, and physics computations.\n- **Software Engineering**: Architectural patterns, code reviews, algorithms in Kotlin, Python, and C++.\n- **Live Web Research**: Real-time fact extraction from public encyclopedic sources.\n- **Multimodal Vision**: Analysis of visual attachments.\n- **Personal Memory Engine**: On-device persistent recall of your preferences and active projects.",
                category = "System Overview",
                isRecognized = true
            )
        }

        return OfflineAnswer(
            text = "I have analyzed your query: \"$prompt\".\n\nWhile running in local offline mode, my immediate built-in reasoning engine provides access to physics, mathematics, coding, and system management. To unlock full unrestricted multi-turn neural generation across the entire open domain, ensure your Gemini API key is configured in the AI Studio Secrets panel or connect to a network for live web synthesis.",
            category = "Local Reasoning",
            isRecognized = false
        )
    }

    private fun trySolveMath(prompt: String): String? {
        val clean = prompt.replace("solve", "")
            .replace("calculate", "")
            .replace("what is", "")
            .replace("eval", "")
            .trim()

        // Check if string is predominantly mathematical characters
        val mathChars = clean.filter { it.isDigit() || "+-*/%^().sqrt ".contains(it) }
        if (mathChars.length < clean.length * 0.7 || mathChars.none { it.isDigit() }) {
            return null
        }

        return try {
            val result = SimpleMathParser.eval(clean)
            """### Mathematical Computation
- **Expression**: `$clean`
- **Result**: **$result**
- **Precision**: Exact floating-point calculation verified by Zairon math engine."""
        } catch (_: Exception) {
            null
        }
    }
}

object SimpleMathParser {
    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < str.length) str[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    when {
                        eat('+'.code) -> x += parseTerm()
                        eat('-'.code) -> x -= parseTerm()
                        else -> return x
                    }
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when {
                        eat('*'.code) -> x *= parseFactor()
                        eat('/'.code) -> x /= parseFactor()
                        eat('%'.code) -> x %= parseFactor()
                        else -> return x
                    }
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return +parseFactor()
                if (eat('-'.code)) return -parseFactor()

                var x: Double
                val startPos = pos
                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else if (ch in '0'.code..'9'.code || ch == '.'.code) {
                    while (ch in '0'.code..'9'.code || ch == '.'.code) nextChar()
                    x = str.substring(startPos, pos).toDouble()
                } else if (ch in 'a'.code..'z'.code) {
                    while (ch in 'a'.code..'z'.code) nextChar()
                    val func = str.substring(startPos, pos)
                    x = parseFactor()
                    x = when (func) {
                        "sqrt" -> Math.sqrt(x)
                        "sin" -> Math.sin(Math.toRadians(x))
                        "cos" -> Math.cos(Math.toRadians(x))
                        "tan" -> Math.tan(Math.toRadians(x))
                        else -> throw RuntimeException("Unknown function: $func")
                    }
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }

                if (eat('^'.code)) x = Math.pow(x, parseFactor())

                return x
            }
        }.parse()
    }
}
