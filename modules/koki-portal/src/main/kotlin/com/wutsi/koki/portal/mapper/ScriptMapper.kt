package com.wutsi.koki.portal.mapper

import com.wutsi.koki.portal.model.ScriptModel
import com.wutsi.koki.script.dto.Script
import com.wutsi.koki.script.dto.ScriptSummary
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class ScriptMapper {
    fun toScriptModel(entity: ScriptSummary): ScriptModel {
        val fmt = createDateFormat()
        return ScriptModel(
            id = entity.id,
            name = entity.name,
            title = entity.title ?: "",
            active = entity.active,
            language = entity.language,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
        )
    }

    fun toScriptModel(entity: Script): ScriptModel {
        val fmt = createDateFormat()
        return ScriptModel(
            id = entity.id,
            name = entity.name,
            title = entity.title ?: "",
            description = entity.description,
            active = entity.active,
            language = entity.language,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            code = entity.code,
            parameters = entity.parameters,
        )
    }

    private fun createDateFormat(): SimpleDateFormat {
        return SimpleDateFormat("yyyy/MM/dd HH:mm")
    }
}
