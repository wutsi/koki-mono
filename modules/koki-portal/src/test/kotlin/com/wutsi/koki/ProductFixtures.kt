package com.wutsi.koki

import com.wutsi.koki.product.dto.Price
import com.wutsi.koki.product.dto.PriceSummary
import com.wutsi.koki.product.dto.Product
import com.wutsi.koki.product.dto.ProductSummary
import com.wutsi.koki.product.dto.ProductType
import com.wutsi.koki.product.dto.ServiceDetails
import org.apache.commons.lang3.time.DateUtils
import java.util.Date

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
            type = ProductType.DIGITAL,
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
        type = ProductType.SERVICE,
        categoryId = RefDataFixtures.categories[0].id,
        name = "Soulier 123",
        code = "SOU-123",
        active = true,
        description = "This is the description of the product",
        modifiedById = UserFixtures.users[0].id,
        createdById = UserFixtures.users[0].id,
        serviceDetails = ServiceDetails(
            quantity = 1,
            unitId = RefDataFixtures.units[0].id,
        )
    )

    // Prices
    val prices = listOf(
        PriceSummary(
            id = 10001,
            productId = product.id,
            name = "List Price",
            amount = 1000.0,
            currency = "CAD",
            startAt = DateUtils.addDays(Date(), -300),
        ),
        PriceSummary(
            id = 10002,
            productId = product.id,
            accountTypeId = 120L,
            name = "Business Price",
            amount = 900.0,
            currency = "CAD",
            startAt = DateUtils.addDays(Date(), -300),
            endAt = DateUtils.addDays(Date(), -100),
        ),
        PriceSummary(
            id = 10003,
            productId = product.id,
            amount = 1500.0,
            currency = "USD",
            startAt = DateUtils.addDays(Date(), -300),
        ),
    )

    val price = Price(
        id = 10001,
        productId = product.id,
        accountTypeId = 120L,
        name = "List Price",
        amount = 1000.0,
        currency = "CAD",
        startAt = DateUtils.addDays(Date(), -300),
        endAt = DateUtils.addDays(Date(), -100),
    )
}
