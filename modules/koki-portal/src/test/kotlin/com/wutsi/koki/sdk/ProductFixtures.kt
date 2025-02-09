package com.wutsi.koki

import com.wutsi.koki.product.dto.Product
import com.wutsi.koki.product.dto.ProductSummary
import com.wutsi.koki.product.dto.ProductType

object ProductFixtures {
    // Products
    val products = listOf(
        ProductSummary(
            id = 100,
            type = ProductType.PHYSICAL,
            name = "Soulier 123",
            code = "SOU-123",
            active = true,
            modifiedById = UserFixtures.users[0].id,
            createdById = UserFixtures.users[0].id
        ),
        ProductSummary(
            id = 101,
            type = ProductType.SUBSCRIPTION,
            name = "Netflix 100",
            code = "NET-100",
            active = false,
            modifiedById = UserFixtures.users[1].id,
            createdById = UserFixtures.users[1].id
        ),
        ProductSummary(
            id = 102,
            type = ProductType.SERVICE,
            name = "Lavage de livres",
            code = null,
            active = false,
            modifiedById = UserFixtures.users[2].id,
            createdById = UserFixtures.users[2].id
        ),
        ProductSummary(
            id = 103,
            type = ProductType.SERVICE,
            name = "Preparation des taxes",
            code = null,
            active = false,
            modifiedById = UserFixtures.users[0].id,
            createdById = UserFixtures.users[2].id
        ),
        ProductSummary(
            id = 104,
            type = ProductType.DIGITAL,
            name = "Le Seigneur des Anneaux",
            code = "BBN-1111",
            active = true,
            modifiedById = UserFixtures.users[0].id,
            createdById = UserFixtures.users[2].id
        ),
    )

    val product = Product(
        id = 100,
        type = ProductType.PHYSICAL,
        name = "Soulier 123",
        code = "SOU-123",
        active = true,
        description = "This is the description of the product",
        modifiedById = UserFixtures.users[0].id,
        createdById = UserFixtures.users[0].id
    )
}
