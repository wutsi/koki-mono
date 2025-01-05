package com.wutsi.koki

import com.wutsi.koki.account.dto.Attribute
import com.wutsi.koki.account.dto.AttributeSummary
import com.wutsi.koki.account.dto.AttributeType

object AttributeFixtures {
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
}
