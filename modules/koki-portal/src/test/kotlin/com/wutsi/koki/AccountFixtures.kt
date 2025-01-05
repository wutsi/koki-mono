package com.wutsi.koki

import com.wutsi.koki.AttributeFixtures.attributes
import com.wutsi.koki.account.dto.Account
import com.wutsi.koki.account.dto.AccountSummary

object AccountFixtures {
    val NEW_ACCOUNT_ID = 5555L
    val accounts = listOf(
        AccountSummary(
            id = 100,
            name = "Yahoo Inc",
            managedById = UserFixtures.users[0].id,
            email = "info@yahoo.com",
            phone = "+9189990000",
            mobile = null,
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
            name = "Ray Sponsible Int",
            managedById = UserFixtures.users[0].id,
            email = "info@yahoo.com",
            phone = "+9189990011",
            mobile = "+9189990022",
            modifiedById = UserFixtures.users[0].id,
            createdById = UserFixtures.users[0].id
        ),
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
        attributes = mapOf(
            attributes[0].id to "0000001",
            attributes[1].id to "0000011",
            attributes[2].id to "2020",
        )
    )
}
