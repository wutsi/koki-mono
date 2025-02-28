package com.wutsi.koki.product.server.mapper

import com.wutsi.koki.product.dto.Product
import com.wutsi.koki.product.dto.ProductSummary
import com.wutsi.koki.product.dto.ServiceDetails
import com.wutsi.koki.product.server.domain.ProductEntity
import com.wutsi.koki.product.server.domain.ServiceDetailsData
import org.springframework.stereotype.Service

@Service
class ProductMapper {
    fun toProduct(entity: ProductEntity): Product {
        return Product(id = entity.id!!,
            name = entity.name,
            code = entity.code,
            description = entity.description,
            type = entity.type,
            categoryId = entity.categoryId,
            active = entity.active,
            createdAt = entity.createdAt,
            createdById = entity.createdById,
            modifiedAt = entity.modifiedAt,
            modifiedById = entity.modifiedById,
            serviceDetails = entity.serviceDetails?.let { details -> toServiceDetails(details) })
    }

    fun toProductSummary(entity: ProductEntity): ProductSummary {
        return ProductSummary(
            id = entity.id!!,
            name = entity.name,
            code = entity.code,
            type = entity.type,
            categoryId = entity.categoryId,
            active = entity.active,
            createdAt = entity.createdAt,
            createdById = entity.createdById,
            modifiedAt = entity.modifiedAt,
            modifiedById = entity.modifiedById,
            serviceDetails = entity.serviceDetails?.let { details -> toServiceDetails(details) },
        )
    }

    fun toServiceDetails(details: ServiceDetailsData): ServiceDetails {
        return ServiceDetails(
            unitId = details.unitId,
            quantity = details.quantity,
        )
    }
}
