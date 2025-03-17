package com.wutsi.koki.payment.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.TransactionType
import com.wutsi.koki.payment.server.dao.TransactionRepository
import com.wutsi.koki.payment.server.domain.TransactionEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.Date
import kotlin.jvm.optionals.getOrNull

@Service
class TransactionService(
    private val dao: TransactionRepository,
    private val em: EntityManager,
    private val paymentGatewayServiceProvider: PaymentGatewayServiceProvider,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TransactionService::class.java)
    }

    fun get(id: String, tenantId: Long): TransactionEntity {
        val tx = dao.findById(id).getOrNull()
        if (tx == null || tx?.tenantId != tenantId) {
            throw NotFoundException(
                error = Error(
                    code = ErrorCode.TRANSACTION_NOT_FOUND,
                )
            )
        }
        return tx
    }

    @Transactional
    fun sync(tx: TransactionEntity): TransactionEntity {
        try {
            val gatewayService = paymentGatewayServiceProvider.getOrNull(tx.gateway)
            if (gatewayService == null) {
                return tx
            } else {
                gatewayService.sync(tx)
            }
        } catch (ex: PaymentGatewayException) {
            LOGGER.warn("Failure", ex)

            tx.status = TransactionStatus.FAILED
            tx.errorCode = ex.errorCode
            tx.supplierErrorCode = ex.supplierErrorCode
            tx.supplierErrorMessage = ex.message
        }
        return dao.save(tx)
    }

    fun search(
        tenantId: Long,
        ids: List<String> = emptyList(),
        invoiceId: Long? = null,
        types: List<TransactionType> = emptyList(),
        statuses: List<TransactionStatus> = emptyList(),
        createdAtFrom: Date? = null,
        createdAtTo: Date? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<TransactionEntity> {
        val jql = StringBuilder("SELECT T FROM TransactionEntity AS T WHERE T.tenantId=:tenantId")

        if (ids.isNotEmpty()) {
            jql.append(" AND T.id IN :ids")
        }
        if (invoiceId != null) {
            jql.append(" AND T.invoiceId = :invoiceId")
        }
        if (types.isNotEmpty()) {
            jql.append(" AND T.type IN :types")
        }
        if (statuses.isNotEmpty()) {
            jql.append(" AND T.status IN :statuses")
        }
        if (createdAtFrom != null) {
            jql.append(" AND T.createdAt >= :createdAtFrom")
        }
        if (createdAtTo != null) {
            jql.append(" AND T.createdAt <= :createdAtTo")
        }
        jql.append(" ORDER BY T.createdAt DESC")

        val query = em.createQuery(jql.toString(), TransactionEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (invoiceId != null) {
            query.setParameter("invoiceId", invoiceId)
        }
        if (types.isNotEmpty()) {
            query.setParameter("types", types)
        }
        if (statuses.isNotEmpty()) {
            query.setParameter("statuses", statuses)
        }
        if (createdAtFrom != null) {
            query.setParameter("createdAtFrom", createdAtFrom)
        }
        if (createdAtTo != null) {
            query.setParameter("createdAtTo", createdAtTo)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    fun findByStatusAndCreatedAtBefore(
        status: TransactionStatus,
        createdAt: Date,
        limit: Int = 20,
        offset: Int = 0
    ): List<TransactionEntity> {
        return dao.findByStatusAndCreatedAtBefore(
            status = status,
            createdAt = createdAt,
            pageable = PageRequest.of(offset / limit, limit)
        )
    }
}
