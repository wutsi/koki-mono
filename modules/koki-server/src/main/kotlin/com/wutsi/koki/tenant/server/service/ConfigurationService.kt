package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import com.wutsi.koki.tenant.dto.SaveConfigurationResponse
import com.wutsi.koki.tenant.server.dao.ConfigurationRepository
import com.wutsi.koki.tenant.server.domain.AttributeEntity
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
open class ConfigurationService(
    private val dao: ConfigurationRepository,
    private val attributeService: AttributeService,
) {
    fun search(attributes: List<AttributeEntity>): List<ConfigurationEntity> {
        return dao.findByAttributeIn(attributes)
    }

    @Transactional
    open fun save(request: SaveConfigurationRequest, tenantId: Long): SaveConfigurationResponse {
        val names = request.values.keys
        if (names.isEmpty()) {
            return SaveConfigurationResponse()
        }

        // Update/Delete
        val deletes = mutableListOf<ConfigurationEntity>()
        val updates = mutableListOf<ConfigurationEntity>()
        val attributes = attributeService.search(names.toList(), tenantId)
        val configurationMap = search(attributes).associateBy { it.attribute.name }
        configurationMap.forEach { entry ->
            val name = entry.key
            val config = entry.value
            val value = request.values[name]
            if (value.isNullOrEmpty()) {
                deletes.add(config)
            } else {
                config.value = value
                config.modifiedAt = Date()
                updates.add(config)
            }
        }

        // New
        val adds = mutableListOf<ConfigurationEntity>()
        val xnames = names
            .filter { !configurationMap.containsKey(it) }
            .filter { !request.values[it].isNullOrEmpty() }
        if (xnames.isNotEmpty()) {
            val attributeMap = attributeService.search(xnames, tenantId).associateBy { it.name }
            attributeMap.values.forEach { attr ->
                adds.add(
                    ConfigurationEntity(
                        attribute = attr,
                        value = request.values[attr.name]
                    )
                )
            }
        }

        // Persist
        if (deletes.isNotEmpty()) {
            dao.deleteAll(deletes)
        }
        if (updates.isNotEmpty()) {
            dao.saveAll(updates)
        }
        if (adds.isNotEmpty()) {
            dao.saveAll(adds)
        }

        return SaveConfigurationResponse(
            added = adds.size,
            updated = updates.size,
            deleted = deletes.size,
        )
    }
}
