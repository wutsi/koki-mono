package com.wutsi.koki.tenant.server.job

import com.wutsi.koki.tenant.dto.InvitationStatus
import com.wutsi.koki.tenant.server.dao.InvitationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/ExpireInvitationJob.sql"])
class InvitationCronJobsTest {
    @Autowired
    private lateinit var jobs: InvitationCronJobs

    @Autowired
    private lateinit var dao: InvitationRepository

    @Test
    fun expire() {
        jobs.expire()

        assertStatus(InvitationStatus.EXPIRED, "100")
        assertStatus(InvitationStatus.ACCEPTED, "101")
        assertStatus(InvitationStatus.EXPIRED, "102")
        assertStatus(InvitationStatus.EXPIRED, "103")
        assertStatus(InvitationStatus.EXPIRED, "200")
    }

    private fun assertStatus(status: InvitationStatus, id: String) {
        val inv = dao.findById(id).get()
        assertEquals(status, inv.status)
    }
}
