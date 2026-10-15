package com.example

import com.example.ai.OfflineKnowledgeEngine
import com.example.ai.SimpleMathParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ZaironBrainUnitTest {

    private val engine = OfflineKnowledgeEngine()

    @Test
    fun testCreatorIdentityIdentifiesAzhan() {
        val answer = engine.query("Who created you?")
        assertTrue(answer.isRecognized)
        assertTrue(answer.text.contains("Azhan"))
        assertTrue(answer.text.contains("Zairon AI 2.0"))
    }

    @Test
    fun testMathEvaluationExactCalculations() {
        val res1 = SimpleMathParser.eval("12 + 34 * 2")
        assertEquals(80.0, res1, 0.001)

        val res2 = SimpleMathParser.eval("sqrt(144) + 8")
        assertEquals(20.0, res2, 0.001)

        val queryAnswer = engine.query("solve 15 * 4 + 10")
        assertTrue(queryAnswer.isRecognized)
        assertTrue(queryAnswer.text.contains("70.0"))
    }

    @Test
    fun testPhysicsTheoryRelativity() {
        val answer = engine.query("Explain Einstein's theory of relativity")
        assertTrue(answer.isRecognized)
        assertTrue(answer.text.contains("Special Relativity"))
        assertTrue(answer.text.contains("E = mc^2"))
    }

    @Test
    fun testKotlinCoroutinesKnowledge() {
        val answer = engine.query("How do Kotlin coroutines and flows work?")
        assertTrue(answer.isRecognized)
        assertTrue(answer.text.contains("Dispatchers.IO"))
        assertTrue(answer.text.contains("Flow"))
    }
}
