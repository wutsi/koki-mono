package com.wutsi.koki.script.server.engine

import com.wutsi.koki.script.dto.Language
import com.wutsi.koki.script.server.exception.LanguageNotSupportedException
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.Writer
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

@Service
class ScriptingEngine {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ScriptingEngine::class.java)
    }

    private val manager = ScriptEngineManager()
    private val engines = mutableMapOf<Language, ScriptEngine>()

    @PostConstruct
    fun init() {
        registerEngine(Language.JAVASCRIPT, "js")
        registerEngine(Language.PYTHON, "python")
    }

    /**
     * Evaluate a script
     *
     * @param code - Code to execute
     * @param language - Programming language
     * @param inputs - Map of input to inject into the script
     * @param output - List of output to collect after the execution of the script
     *
     * @return Map containing the value of all the output. This map has the following keys:
     *  <ul>
     *      <li><code>return</code> - The value return from the script</li>
     *      <li>The list of outputs
     *  </ul>
     */
    fun eval(
        code: String,
        language: Language,
        inputs: Map<String, Any>,
        output: List<String>,
        writer: Writer
    ): Map<String, Any?> {
        val engine = getEngine(language)

        val bindings = engine.createBindings()
        bindings.putAll(inputs)

        engine.getContext().writer = writer
        engine.getContext().errorWriter = writer

        val value = engine.eval(code, bindings)

        val result = mutableMapOf<String, Any?>()
        result.putAll(output.map { name -> name to bindings.get(name) }.toMap())
        result.put("return", value)
        return result
    }

    private fun registerEngine(language: Language, name: String) {
        val engine = manager.getEngineByName(name)
        LOGGER.info("Registering Scripting Engine: $language - $name")
        engines[language] = engine
    }

    private fun getEngine(language: Language): ScriptEngine {
        return engines[language] ?: throw throw LanguageNotSupportedException(language.name)
    }
}
