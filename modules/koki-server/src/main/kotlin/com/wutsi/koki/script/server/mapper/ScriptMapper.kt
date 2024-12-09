package com.wutsi.koki.script.server.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.script.dto.Script
import com.wutsi.koki.script.dto.ScriptSummary
import com.wutsi.koki.script.server.domain.ScriptEntity
import org.springframework.stereotype.Service

@Service
class ScriptMapper(private val objectMapper: ObjectMapper) {
    fun toScript(entity: ScriptEntity): Script {
        return Script(
            id = entity.id ?: "",
            name = entity.name,
            title = entity.title,
            description = entity.description,
            language = entity.language,
            active = entity.active,
            code = entity.code,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            parameters = entity.parameterAsList()
        )
    }

    fun toScriptSummary(entity: ScriptEntity): ScriptSummary {
        return ScriptSummary(
            id = entity.id ?: "",
            name = entity.name,
            title = entity.title,
            language = entity.language,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }
}
