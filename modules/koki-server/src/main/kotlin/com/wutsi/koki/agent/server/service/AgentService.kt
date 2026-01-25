package com.wutsi.koki.agent.server.service

import com.wutsi.koki.agent.server.dao.AgentRepository
import com.wutsi.koki.agent.server.domain.AgentEntity
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.tenant.server.service.QrCodeGenerator
import com.wutsi.koki.tenant.server.service.StorageProvider
import com.wutsi.koki.tenant.server.service.TenantService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.util.Date

@Service
class AgentService(
    private val dao: AgentRepository,
    private val em: EntityManager,
    private val tenantService: TenantService,
    private val storageProvider: StorageProvider,
    private val qrCodeGenerator: QrCodeGenerator,
    private val logger: KVLogger,
) {
    fun get(id: Long, tenantId: Long): AgentEntity {
        val agent = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.AGENT_NOT_FOUND)) }
        if (agent.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.AGENT_NOT_FOUND))
        }
        return agent
    }

    fun getByUser(userId: Long, tenantId: Long): AgentEntity {
        val agent = dao.findByUserId(userId)
            ?: throw NotFoundException(Error(ErrorCode.AGENT_NOT_FOUND))
        if (agent.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.AGENT_NOT_FOUND))
        }
        return agent
    }

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        userIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<AgentEntity> {
        val jql = StringBuilder("SELECT A FROM AgentEntity A WHERE A.tenantId = :tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND A.id IN :ids")
        }
        if (userIds.isNotEmpty()) {
            jql.append(" AND A.userId IN :userIds")
        }

        val query = em.createQuery(jql.toString(), AgentEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (userIds.isNotEmpty()) {
            query.setParameter("userIds", userIds)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun save(agent: AgentEntity): AgentEntity {
        agent.modifiedAt = Date()
        return dao.save(agent)
    }

    @Transactional
    fun create(userId: Long, tenantId: Long): AgentEntity {
        return dao.save(
            AgentEntity(
                userId = userId,
                tenantId = tenantId
            )
        )
    }

    @Transactional
    fun generateQrCode(id: Long, tenantId: Long): AgentEntity {
        val agent = get(id, tenantId)
        return generateQrCode(agent)
    }

    private fun generateQrCode(agent: AgentEntity): AgentEntity {
        val id = agent.id ?: -1
        val tenantId = agent.tenantId
        val tenant = tenantService.get(tenantId)
        val file = File.createTempFile("qr-agent-$id", ".png")
        val fout = FileOutputStream(file)
        try {
            // Generate the QR code
            val data = tenant.clientPortalUrl.trimEnd('/') + "/qr-codes/agents/$id"
            qrCodeGenerator.generate(data, tenant, fout)
            logger.add("qr_code_file", file.absoluteFile)

            // Store the QR code
            val storage = storageProvider.get(tenantId)
            val fin = file.inputStream()
            val url = fin.use {
                storage.store(
                    path = "tenant/$tenantId/agent/$id/qr-code/default.png",
                    content = fin,
                    contentType = "image/png",
                    contentLength = file.length()
                )
            }
            logger.add("qr_code_url", url)

            // Store it
            agent.qrCodeUrl = url.toString()
            agent.modifiedAt = Date()
            return dao.save(agent)
        } finally {
            fout.close()
            file.delete()
        }
    }
}
