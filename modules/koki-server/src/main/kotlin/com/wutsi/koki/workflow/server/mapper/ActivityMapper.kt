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
            name = entity.name,
            description = entity.description,
            active = entity.active,
            requiresApproval = entity.requiresApproval,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            tags = entity.tags?.let { tags -> toMap(tags) } ?: emptyMap(),
            predecessorIds = entity.predecessors.mapNotNull { pred -> pred.id },
            roleId = entity.role?.id
        )
    }

    private fun toMap(tags: String): Map<String, String> {
        try {
            val map = mutableMapOf<String, String>()
            val properties: Properties = Properties()
            properties.load(StringReader(tags))
            properties.keys().toList().map { key ->
                val name = key.toString()
                map[name] = properties.getProperty(name)
            }
            return map
        } catch (ex: Exception) {
            LOGGER.warn("Unable to convert to Properties: $tags", ex)
            return emptyMap()
        }
    }
}
