package com.wutsi.koki.script.server.service

import com.wutsi.koki.script.dto.Language
import com.wutsi.koki.script.server.exception.LanguageNotSupportedException
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.Writer
import javax.script.Compilable
import javax.script.CompiledScript
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

    fun compile(code: String, language: Language): CompiledScript {
        val engine = getEngine(language)
        return (engine as Compilable).compile(code)
    }

    /**
     * Evaluate a script
     *
     * @param code - Code to execute
     * @param language - Programming language
     * @param input - Map of input to inject into the script
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
        input: Map<String, Any>,
        writer: Writer
    ): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        val engine = getEngine(language)

        val bindings = engine.createBindings()
        bindings.putAll(input)

        engine.getContext().writer = writer
        engine.getContext().errorWriter = writer

        val value = engine.eval(code, bindings)
        result.putAll(
            bindings
                .filter { entry -> entry.value != null } // Filter out null value
                .filter { entry -> !input.keys.contains(entry.key) } // Filter out inputs
        )

        value?.let { result["return"] = value }
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
