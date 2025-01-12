package com.wutsi.koki

import com.wutsi.koki.account.dto.Account
import com.wutsi.koki.account.dto.AccountSummary
import com.wutsi.koki.account.dto.AccountType
import com.wutsi.koki.account.dto.AccountTypeSummary
import com.wutsi.koki.account.dto.Attribute
import com.wutsi.koki.account.dto.AttributeSummary
import com.wutsi.koki.account.dto.AttributeType

object AccountFixtures {
    // Attributes
    val attributes = listOf(
        AttributeSummary(
            id = 100,
            name = "id_tps",
            label = "ID/TPS",
            type = AttributeType.TEXT,
            active = true,
            required = false,
        ),
        AttributeSummary(
            id = 101,
            name = "id_tvp",
            label = "ID/TVQ",
            type = AttributeType.TEXT,
            active = true,
            required = false,
        ),
        AttributeSummary(
            id = 102,
            name = "since",
            label = "Client Since",
            type = AttributeType.NUMBER,
            active = true,
            required = true,
        ),
    )

    val attribute = Attribute(
        id = 100,
        name = "id_tps",
        label = "ID/TPS",
        type = AttributeType.TEXT,
        active = true,
        required = true,
        description = "Company TPS Number",
        choices = listOf(
            "TPS-000001",
            "TPS-000002",
            "TPS-000003",
            "TPS-000004",
            "TPS-000005",
            "TPS-000006",
        )
    )

    // Account Types
    val accountTypes = listOf(
        AccountTypeSummary(
            id = 100,
            name = "T1",
            title = "Tier-1"
        ),
        AccountTypeSummary(
            id = 101,
            name = "T2",
        ),
        AccountTypeSummary(
            id = 102,
            name = "T4",
        ),
    )

    val accountType = AccountType(
        id = 100,
        name = "T1",
        description = "Account for personal user",
    )

    // Accounts
    val NEW_ACCOUNT_ID = 5555L
    val accounts = listOf(
        AccountSummary(
            id = 100,
            accountTypeId = accountTypes[0].id,
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
            accountTypeId = accountTypes[1].id,
            name = "Google Inc",
            managedById = UserFixtures.users[0].id,
        ),
        AccountSummary(
            id = 102,
            accountTypeId = null,
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
        accountTypeId = accountTypes[0].id,
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
