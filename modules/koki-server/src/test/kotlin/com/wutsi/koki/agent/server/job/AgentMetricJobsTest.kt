package com.wutsi.koki.agent.server.job

import com.wutsi.koki.agent.dto.MetricPeriod
import com.wutsi.koki.agent.server.dao.AgentMetricRepository
import com.wutsi.koki.agent.server.dao.AgentRepository
import com.wutsi.koki.listing.dto.ListingType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/agent/AgentMetricJobs.sql"])
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AgentMetricJobsTest {
    @Autowired
    private lateinit var job: AgentMetricJobs

    @Autowired
    private lateinit var agentDao: AgentRepository

    @Autowired
    private lateinit var metricDao: AgentMetricRepository

    @Test
    fun monthly() {
        job.monthly()

        val agent11 = agentDao.findById(11L).get()
        assertEquals(4, agent11.totalSales)
        assertEquals(3, agent11.totalRentals)
        assertEquals(2, agent11.past12mSales)
        assertEquals(3, agent11.past12mRentals)
        assertEquals(5, agent11.past12mTransactions)

        val sales11 = metricDao.findByAgentIdAndListingTypeAndPeriod(11L, ListingType.SALE, MetricPeriod.OVERALL)
        assertEquals(4, sales11?.total)
        assertEquals(100000, sales11?.minPrice)
        assertEquals(300000, sales11?.maxPrice)
        assertEquals(187500, sales11?.averagePrice)
        assertEquals(750000, sales11?.totalPrice)
        assertEquals("CAD", sales11?.currency)

        val sales1112m = metricDao.findByAgentIdAndListingTypeAndPeriod(11L, ListingType.SALE, MetricPeriod.PAST_12M)
        assertEquals(2, sales1112m?.total)
        assertEquals(100000, sales1112m?.minPrice)
        assertEquals(150000, sales1112m?.maxPrice)
        assertEquals(125000, sales1112m?.averagePrice)
        assertEquals(250000, sales1112m?.totalPrice)
        assertEquals("CAD", sales1112m?.currency)

        val rental11 = metricDao.findByAgentIdAndListingTypeAndPeriod(11L, ListingType.RENTAL, MetricPeriod.OVERALL)
        assertEquals(3, rental11?.total)
        assertEquals(500, rental11?.minPrice)
        assertEquals(2000, rental11?.maxPrice)
        assertEquals(1166, rental11?.averagePrice)
        assertEquals(3500, rental11?.totalPrice)
        assertEquals("CAD", rental11?.currency)

        val rental1112m = metricDao.findByAgentIdAndListingTypeAndPeriod(11L, ListingType.RENTAL, MetricPeriod.PAST_12M)
        assertEquals(2, rental1112m?.total)
        assertEquals(500, rental1112m?.minPrice)
        assertEquals(2000, rental1112m?.maxPrice)
        assertEquals(1250, rental1112m?.averagePrice)
        assertEquals(2500, rental1112m?.totalPrice)
        assertEquals("CAD", rental1112m?.currency)

        val agent22 = agentDao.findById(22L).get()
        assertEquals(1, agent22.totalSales)
        assertEquals(2, agent22.totalRentals)
        assertEquals(null, agent22.past12mSales)
        assertEquals(2, agent22.past12mRentals)
        assertEquals(3, agent22.totalTransactions)
    }
}
