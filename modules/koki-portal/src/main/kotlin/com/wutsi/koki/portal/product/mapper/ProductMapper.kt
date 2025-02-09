package com.wutsi.koki.portal.product.mapper

import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.product.model.ProductModel
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.product.dto.Product
import com.wutsi.koki.product.dto.ProductSummary
import org.springframework.stereotype.Service

@Service
class ProductMapper : TenantAwareMapper() {
    fun toProductModel(
        entity: Product,
        users: Map<Long, UserModel>
    ): ProductModel {
        val fmt = createDateFormat()
        return ProductModel(
            id = entity.id,
            name = entity.name,
            code = entity.code,
            description = entity.description,
            type = entity.type,
            active = entity.active,
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            modifiedBy = entity.modifiedById?.let { id -> users[id] },
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdBy = entity.createdById?.let { id -> users[id] },
        )
    }

    fun toProductModel(
        entity: ProductSummary,
        users: Map<Long, UserModel>
    ): ProductModel {
        val fmt = createDateFormat()
        return ProductModel(
            id = entity.id,
            name = entity.name,
            code = entity.code,
            type = entity.type,
            active = entity.active,
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            modifiedBy = entity.modifiedById?.let { id -> users[id] },
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdBy = entity.createdById?.let { id -> users[id] },
        )
    }
}
