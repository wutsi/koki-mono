package com.wutsi.koki

import com.wutsi.koki.UserFixtures.USER_ID
import com.wutsi.koki.tenant.dto.Invitation
import com.wutsi.koki.tenant.dto.InvitationStatus
import com.wutsi.koki.tenant.dto.InvitationSummary
import com.wutsi.koki.tenant.dto.InvitationType

object InvitationFixtures {
    val INVITATION_ID = "1093209-43490f-4449"

    val invitation = Invitation(
        id = INVITATION_ID,
        email = "ray.sponsible@gmail.com",
        displayName = "Ray Sponsible",
        createdById = USER_ID,
        status = InvitationStatus.PENDING,
        type = InvitationType.AGENT,
    )

    val invitations = listOf(
        InvitationSummary(
            id = INVITATION_ID,
            email = "ray.sponsible@gmail.com",
            displayName = "Ray Sponsible",
            createdById = USER_ID,
            status = InvitationStatus.PENDING,
            type = InvitationType.AGENT,
        ),
        InvitationSummary(
            id = "xxx",
            email = "joe.smith@gmail.com",
            displayName = "Joe Smith",
            createdById = USER_ID,
            status = InvitationStatus.PENDING,
            type = InvitationType.AGENT,
        ),
        InvitationSummary(
            id = "yyy",
            email = "john.doe@gmail.com",
            displayName = "John Doe",
            createdById = USER_ID,
            status = InvitationStatus.PENDING,
            type = InvitationType.AGENT,
        ),
    )
}
