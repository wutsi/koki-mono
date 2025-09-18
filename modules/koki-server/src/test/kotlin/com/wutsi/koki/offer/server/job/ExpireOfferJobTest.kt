package com.wutsi.koki.offer.server.job

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.offer.server.dao.OfferRepository
import com.wutsi.koki.offer.server.dao.OfferStatusRepository
import com.wutsi.koki.offer.server.dao.OfferVersionRepository
import com.wutsi.koki.platform.mq.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/offer/ExpireOfferJob.sql"])
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExpireOfferJobTest {
    @Autowired
    private lateinit var jobs: OfferCronJobs

    @Autowired
    private lateinit var offerDao: OfferRepository

    @Autowired
    private lateinit var versionDao: OfferVersionRepository

    @Autowired
    private lateinit var statusDao: OfferStatusRepository

    @MockitoBean
    private lateinit var publisher: Publisher

    @Test
    fun run() {
        jobs.expire()

        assertStatus(100, OfferStatus.EXPIRED)
        assertStatus(101, OfferStatus.SUBMITTED)
        assertStatus(102, OfferStatus.EXPIRED)
        assertStatus(103, OfferStatus.ACCEPTED)

        verify(publisher, times(2)).publish(any())
    }

    fun assertStatus(offerId: Long, status: OfferStatus) {
        val offer = offerDao.findById(offerId).get()
        assertEquals(status, offer.status)

        val version = versionDao.findById(offer.version?.id ?: -1).get()
        assertEquals(status, version.status)

        if (status == OfferStatus.EXPIRED) {
            val stat = statusDao.findByOfferOrderByIdDesc(offer).firstOrNull()
            assertEquals(status, stat?.status)
        }
    }
}
