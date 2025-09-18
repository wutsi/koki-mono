package com.wutsi.koki.offer.server.job

import org.springframework.test.context.jdbc.Sql

@Sql(value = ["/db/test/clean.sql", "/db/test/offer/ExpireOfferJob.sql"])
class ExpireOfferJob {
    private lateinit var jobs: 
    @Test
    fun run(){

    }
}
