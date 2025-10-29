package com.wutsi.koki.offer.server.job

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.offer.dto.UpdateOfferStatusRequest
import com.wutsi.koki.offer.dto.event.OfferStatusChangedEvent
import com.wutsi.koki.offer.server.domain.OfferEntity
import com.wutsi.koki.offer.server.service.OfferService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Publisher
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.Date

@Service
class OfferCronJobs(
    private val service: OfferService,
    private val publisher: Publisher,
    private val logger: KVLogger,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(OfferCronJobs::class.java)
    }

    @Scheduled(cron = "\${koki.module.offer.cron.expire}")
    fun expire() {
        val now = Date()
        val offers = service.searchNotExpired(now)
        offers.forEach { offer -> expire(offer) }

        logger.add("job", this::class.java.simpleName)
        logger.add("offer_expired_count", offers.size)
    }

    private fun expire(offer: OfferEntity) {
        try {
            service.status(
                id = offer.id ?: -1,
                request = UpdateOfferStatusRequest(status = OfferStatus.EXPIRED),
                tenantId = offer.tenantId
            )

            publisher.publish(
                OfferStatusChangedEvent(
                    offerId = offer.id ?: -1,
                    tenantId = offer.tenantId,
                    status = offer.status,
                    owner = if (offer.ownerId != null && offer.ownerType != null) {
                        ObjectReference(offer.ownerId, offer.ownerType)
                    } else {
                        null
                    },
                )
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unable to expire Offer#${offer.id}", ex)
        }
    }
}
