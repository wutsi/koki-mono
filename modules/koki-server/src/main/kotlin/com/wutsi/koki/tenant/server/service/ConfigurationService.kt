package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import com.wutsi.koki.tenant.dto.SaveConfigurationResponse
import com.wutsi.koki.tenant.server.dao.ConfigurationRepository
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
open class ConfigurationService(
    private val dao: ConfigurationRepository,
    private val em: EntityManager,
) {
    fun search(
        tenantId: Long,
        names: List<String> = emptyList(),
        keyword: String? = null,
    ): List<ConfigurationEntity> {
        val jql = StringBuilder("SELECT C FROM ConfigurationEntity C  WHERE C.tenantId = :tenantId")

        if (names.isNotEmpty()) {
            jql.append(" AND UPPER(C.name) IN :names")
        }
        if (keyword != null) {
            jql.append(" AND UPPER(C.name) LIKE :keyword")
        }
        jql.append(" ORDER BY C.name")

        val query = em.createQuery(jql.toString(), ConfigurationEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (keyword != null) {
            query.setParameter("keyword", "%${keyword.uppercase()}%")
        }
        if (names.isNotEmpty()) {
            query.setParameter("names", names.map { name -> name.uppercase() })
        }
        return query.resultList
    }

    @Transactional
    open fun save(request: SaveConfigurationRequest, tenantId: Long): SaveConfigurationResponse {
        val names = request.values.keys
        if (names.isEmpty()) {
            return SaveConfigurationResponse()
        }

        val now = Date()
        val adds = mutableListOf<ConfigurationEntity>()
        val deletes = mutableListOf<ConfigurationEntity>()
        val updates = mutableListOf<ConfigurationEntity>()
        request.values.forEach { entry ->
            val config = dao.findByNameIgnoreCaseAndTenantId(entry.key, tenantId)
            if (config == null) {
                if (entry.value.isNotEmpty()) {
                    adds.add(
                        ConfigurationEntity(
                            name = entry.key,
                            value = entry.value,
                            tenantId = tenantId
                        )
                    )
                }
            } else {
                if (entry.value.isEmpty()) {
                    deletes.add(config)
                } else {
                    config.value = entry.value
                    config.modifiedAt = now
                    updates.add(config)
                }
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
