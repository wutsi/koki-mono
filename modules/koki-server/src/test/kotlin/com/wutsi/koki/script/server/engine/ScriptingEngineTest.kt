package com.wutsi.koki.script.server.engine

import com.wutsi.koki.script.dto.Language
import com.wutsi.koki.script.server.exception.LanguageNotSupportedException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import java.io.StringWriter
import javax.script.ScriptException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ScriptingEngineTest {
    val engine = ScriptingEngine()

    @BeforeEach
    fun setUp() {
        engine.init()
    }

    @Test
    fun javascript() {
        val code = """
            function compute(a){
              return 2*a;
            }

            console.log('Hello world');
            result = compute(input);
            result
        """.trimIndent()
        val parameters = mapOf("input" to 10)
        val writer = StringWriter()

        val result = engine.eval(code, Language.JAVASCRIPT, parameters, listOf("result"), writer)

        assertEquals("Hello world\n", writer.toString())
        assertEquals(20, result["return"])
        assertEquals(20, result["result"])
    }

    @Test
    fun python() {
        val code = """
            def compute(a):
              return 2*a

            print('Hello world')
            result = compute(input)
        """.trimIndent()
        val parameters = mapOf("input" to 10)
        val writer = StringWriter()

        val result = engine.eval(code, Language.PYTHON, parameters, listOf("result"), writer)

        assertEquals("Hello world\n", writer.toString())
        assertEquals(20, result["result"])
        assertNull(result["return"])
    }

    @Test
    fun `not supported`() {
        assertThrows<LanguageNotSupportedException> {
            engine.eval("", Language.UNKNOWN, emptyMap(), emptyList(), StringWriter())
        }
    }

    @Test
    fun `syntax error`() {
        assertThrows<ScriptException> {
            engine.eval("xxx???", Language.JAVASCRIPT, emptyMap(), emptyList(), StringWriter())
        }
    }
}
