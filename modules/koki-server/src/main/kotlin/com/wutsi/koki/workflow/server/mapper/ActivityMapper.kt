package com.wutsi.koki.workflow.server.mapper

import com.wutsi.koki.workflow.dto.Activity
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.StringReader
import java.util.Properties

@Service
class ActivityMapper {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ActivityMapper::class.java)
    }

    fun toActivity(entity: ActivityEntity): Activity {
        return Activity(
            id = entity.id ?: -1,
            workflowId = entity.workflow.id ?: -1,
            type = entity.type,
            code = entity.code,
            name = entity.name,
            description = entity.description,
            active = entity.active,
            requiresApproval = entity.requiresApproval,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            tags = entity.tags?.let { tags -> toMap(tags) } ?: emptyMap()
        )
    }

    private fun toMap(tags: String): Map<String, String> {
        try {
            return Properties().load(StringReader(tags)) as Map<String, String>
        } catch (ex: Exception) {
            LOGGER.warn("Unable to convert to Properties: $tags", ex)
            return emptyMap()
        }
    }
}
