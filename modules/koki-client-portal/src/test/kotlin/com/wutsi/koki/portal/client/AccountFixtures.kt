package com.wutsi.koki.portal.client

import com.wutsi.koki.account.dto.Account
import com.wutsi.koki.account.dto.AccountSummary
import com.wutsi.koki.account.dto.AccountUser
import com.wutsi.koki.account.dto.Invitation
import org.apache.commons.lang3.time.DateUtils
import java.util.Date
import java.util.UUID

object AccountFixtures {
    // Accounts
    val accounts = listOf(
        AccountSummary(
            id = 100,
            name = "Yahoo Inc",
            managedById = null,
            email = "info@yahoo.com",
            modifiedById = UserFixtures.users[1].id,
            createdById = UserFixtures.users[1].id
        ),
        AccountSummary(
            id = 101,
            name = "Google Inc",
            managedById = UserFixtures.users[0].id,
        ),
        AccountSummary(
            id = 102,
            accountTypeId = null,
            name = "Ray Sponsible Int",
            managedById = UserFixtures.users[0].id,
            email = "info@yahoo.com",
            modifiedById = UserFixtures.users[0].id,
            createdById = UserFixtures.users[0].id
        ),
    )

    val accountUser = AccountUser(
        id = 555L,
        username = "ray.sponsible",
        createdAt = DateUtils.addDays(Date(), -3),
        modifiedAt = DateUtils.addDays(Date(), -3),
    )
    val invitation = Invitation(
        id = UUID.randomUUID().toString(),
        createdAt = DateUtils.addDays(Date(), -1),
        createdById = UserFixtures.user.id,
    )
    val account = Account(
        id = 100,
        name = "Yahoo Inc",
        managedById = UserFixtures.users[0].id,
        email = "info@yahoo.com",
        phone = "+9189990000",
        mobile = "+9189990011",
        modifiedById = UserFixtures.users[1].id,
        createdById = UserFixtures.users[1].id,
        description = "This is an example of account",
        language = "en",
        website = "https://yahoo.com",
        userId = accountUser.id,
        invitationId = invitation.id,
        createdAt = DateUtils.addDays(Date(), -10),
        modifiedAt = DateUtils.addDays(Date(), -1),
    )
}
