package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.tenant.server.dao.InvitationRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/DeleteInvitationEndpoint.sql"])
class DeleteInvitationEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: InvitationRepository

    @Test
    fun delete() {
        rest.delete("/v1/invitations/100")

        val invitation = dao.findById("100").get()
        assertEquals(USER_ID, invitation.deletedById)
        assertNotNull(invitation.deletedById)
        assertEquals(true, invitation.deleted)
    }
}
