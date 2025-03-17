package com.wutsi.koki.payment.server.job

import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.event.TransactionCompletedEvent
import com.wutsi.koki.payment.server.domain.TransactionEntity
import com.wutsi.koki.payment.server.service.TransactionService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.mq.Publisher
import org.apache.commons.lang3.time.DateUtils
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.Date

@Service
class TransactionCronJobs(
    private val service: TransactionService,
    private val publisher: Publisher,
) {
    @Scheduled(cron = "\${koki.module.transaction.pending.cron}")
    fun pending() {
        var offset = 0
        val from = DateUtils.addMinutes(Date(), -30)
        val logger = DefaultKVLogger()
        while (true) {
            val txs = service.findByStatusAndCreatedAtBefore(
                status = TransactionStatus.PENDING,
                createdAt = from,
                offset = offset,
            )
            if (txs.isEmpty()) {
                break
            } else {
                offset += txs.size
            }

            txs.forEach { tx ->
                logger.add("job", "TransactionCronJobs.pending")
                logger.add("transaction_id", tx.id)
                try {
                    val xtx = service.sync(tx)
                    if (xtx.status != TransactionStatus.PENDING) {
                        logger.add("transaction_status", xtx.id)
                        logger.add("transaction_status_changed", true)

                        publish(tx)
                    } else {
                        logger.add("transaction_status_changed", false)
                    }
                } finally {
                    logger.log()
                }
            }
        }
    }

    private fun publish(tx: TransactionEntity) {
        publisher.publish(
            TransactionCompletedEvent(
                transactionId = tx.id!!,
                tenantId = tx.tenantId,
                status = tx.status,
            )
        )
    }
}
