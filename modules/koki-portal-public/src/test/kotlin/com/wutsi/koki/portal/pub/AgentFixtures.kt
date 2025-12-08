package com.wutsi.koki.portal.pub

import com.wutsi.koki.agent.dto.Agent
import com.wutsi.koki.agent.dto.AgentMetric
import com.wutsi.koki.agent.dto.AgentSummary
import com.wutsi.koki.agent.dto.MetricPeriod
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.portal.pub.UserFixtures.users

object AgentFixtures {
    val agent = Agent(
        id = 100,
        userId = users[0].id,
        totalSales = 300,
        totalRentals = 100,
        past12mSales = 50,
        past12mRentals = 20,
        metrics = listOf(
            AgentMetric(
                id = 110L,
                currency = "CAD",
                totalPrice = 500000,
                minPrice = 40000,
                maxPrice = 100000,
                averagePrice = 250000,
                total = 12,
                listingType = ListingType.SALE,
                period = MetricPeriod.OVERALL,
            ),
            AgentMetric(
                id = 111L,
                currency = "CAD",
                totalPrice = 50000,
                minPrice = 4000,
                maxPrice = 10000,
                averagePrice = 25000,
                total = 5,
                listingType = ListingType.SALE,
                period = MetricPeriod.PAST_12M,
            ),

            AgentMetric(
                id = 220L,
                currency = "CAD",
                totalPrice = 5000,
                minPrice = 400,
                maxPrice = 1000,
                averagePrice = 2500,
                total = 50,
                listingType = ListingType.RENTAL,
                period = MetricPeriod.OVERALL,
            ),
            AgentMetric(
                id = 222L,
                currency = "CAD",
                totalPrice = 3500,
                minPrice = 400,
                maxPrice = 800,
                averagePrice = 2500,
                total = 15,
                listingType = ListingType.RENTAL,
                period = MetricPeriod.PAST_12M,
            ),
        )
    )

    val agents = listOf(
        AgentSummary(
            id = 100,
            userId = users[0].id,
            totalSales = 300,
            totalRentals = 100,
            past12mSales = 50,
            past12mRentals = 20,
        ),
        AgentSummary(
            id = 101,
            userId = users[1].id,
            totalSales = 10,
            totalRentals = 20,
            past12mSales = 10,
            past12mRentals = 15,
        ),
        AgentSummary(
            id = 102,
            userId = users[2].id,
            totalSales = 110,
            totalRentals = 210,
            past12mSales = 110,
            past12mRentals = 55,
        ),
    )
}
