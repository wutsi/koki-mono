package com.wutsi.koki.tax.server.mapper

import com.wutsi.koki.tax.dto.TaxProduct
import com.wutsi.koki.tax.server.domain.TaxProductEntity
import org.springframework.stereotype.Service

@Service
class TaxProductMapper {
    fun toTaxProduct(entity: TaxProductEntity): TaxProduct {
        return TaxProduct(
            id = entity.id!!,
            taxId = entity.taxId,
            productId = entity.productId,
            quantity = entity.quantity,
            unitPrice = entity.unitPrice,
            subTotal = entity.subTotal,
            description = entity.description,
        )
    }
}
