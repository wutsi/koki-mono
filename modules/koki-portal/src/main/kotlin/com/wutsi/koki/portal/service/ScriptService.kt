package com.wutsi.koki.portal.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.portal.mapper.ScriptMapper
import com.wutsi.koki.portal.model.ScriptExecutionModel
import com.wutsi.koki.portal.model.ScriptModel
import com.wutsi.koki.portal.script.ScriptForm
import com.wutsi.koki.script.dto.CreateScriptRequest
import com.wutsi.koki.script.dto.Language
import com.wutsi.koki.script.dto.RunScriptRequest
import com.wutsi.koki.script.dto.ScriptSortBy
import com.wutsi.koki.script.dto.UpdateScriptRequest
import com.wutsi.koki.sdk.KokiScripts
import org.springframework.stereotype.Service

@Service
class ScriptService(
    private val koki: KokiScripts,
    private val mapper: ScriptMapper,
    private val objectMapper: ObjectMapper,
) {
    fun script(id: String): ScriptModel {
        val script = koki.script(id).script
        return mapper.toScriptModel(script)
    }

    fun scripts(
        ids: List<String> = emptyList(),
        names: List<String> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
        sortBy: ScriptSortBy? = null,
        ascending: Boolean = true,
    ): List<ScriptModel> {
        val scripts = koki.scripts(
            ids = ids,
            names = names,
            active = active,
            limit = limit,
            offset = offset,
            sortBy = sortBy,
            ascending = ascending,
        ).scripts
        return scripts.map { script -> mapper.toScriptModel(script) }
    }

    fun delete(id: String) {
        koki.delete(id)
    }

    fun create(form: ScriptForm): String {
        return koki.create(
            request = CreateScriptRequest(
                name = form.name,
                title = form.title,
                description = form.description,
                parameters = form.parameters.split("\n").toList().map { param -> param.trim() },
                active = form.active,
                language = Language.valueOf(form.language.uppercase()),
                code = form.code,
            )
        ).scriptId
    }

    fun update(id: String, form: ScriptForm) {
        koki.update(
            id = id,
            request = UpdateScriptRequest(
                name = form.name,
                title = form.title,
                description = form.description,
                parameters = form.parameters.split("\n").toList().map { param -> param.trim() },
                active = form.active,
                language = Language.valueOf(form.language.uppercase()),
                code = form.code,
            )
        )
    }

    fun run(form: ScriptForm): ScriptExecutionModel {
        val response = koki.run(
            request = RunScriptRequest(
                parameters = form.parameters
                    .split("\n").toList()
                    .map { param -> param.trim() }
                    .filter { param -> param.isNotEmpty() }
                    .mapNotNull { param ->
                        val parts = param.split("=")
                        if (parts.size > 0) {
                            parts[0].trim() to parts[1].trim()
                        } else {
                            null
                        }
                    }.toMap(),
                language = Language.valueOf(form.language.uppercase()),
                code = form.code,
            )
        )
        return ScriptExecutionModel(
            bindingsJSON = if (response.bindings.isEmpty()) {
                null
            } else {
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.bindings)
            },
            console = response.console.ifEmpty { null },
        )
    }
}
