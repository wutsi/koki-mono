package com.wutsi.koki.portal.product.mapper

import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.model.MoneyMapper
import com.wutsi.koki.portal.product.model.PriceModel
import com.wutsi.koki.portal.product.model.ProductModel
import com.wutsi.koki.portal.product.model.ServiceDetailsModel
import com.wutsi.koki.portal.refdata.model.UnitModel
import com.wutsi.koki.portal.tenant.model.TypeModel
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.product.dto.Price
import com.wutsi.koki.product.dto.PriceSummary
import com.wutsi.koki.product.dto.Product
import com.wutsi.koki.product.dto.ProductSummary
import org.springframework.stereotype.Service

@Service
class ProductMapper(private val moneyMapper: MoneyMapper) : TenantAwareMapper() {
    fun toProductModel(
        entity: Product, units: Map<Long, UnitModel>, users: Map<Long, UserModel>
    ): ProductModel {
        val fmt = createDateFormat()
        return ProductModel(id = entity.id,
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
            serviceDetails = entity.serviceDetails?.let { details ->
                ServiceDetailsModel(quantity = details.quantity, unit = details.unitId?.let { id -> units[id] })
            })
    }

    fun toProductModel(
        entity: ProductSummary, users: Map<Long, UserModel>
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

    fun toPriceModel(
        entity: Price, accountType: TypeModel?
    ): PriceModel {
        val fmt = createDateFormat()
        return PriceModel(
            id = entity.id,
            accountType = accountType,
            name = entity.name,
            amount = moneyMapper.toMoneyModel(entity.amount, entity.currency),
            startAt = entity.startAt,
            startAtText = entity.startAt?.let { date -> fmt.format(date) },
            endAt = entity.endAt,
            endAtText = entity.endAt?.let { date -> fmt.format(date) },
            active = entity.active,
        )
    }

    fun toPriceModel(
        entity: PriceSummary,
        accountTypes: Map<Long, TypeModel>,
    ): PriceModel {
        val fmt = createDateFormat()
        return PriceModel(
            id = entity.id,
            accountType = entity.accountTypeId?.let { id -> accountTypes[id] },
            name = entity.name,
            amount = moneyMapper.toMoneyModel(entity.amount, entity.currency),
            startAt = entity.startAt,
            startAtText = entity.startAt?.let { date -> fmt.format(date) },
            endAt = entity.endAt,
            endAtText = entity.endAt?.let { date -> fmt.format(date) },
            active = entity.active,
        )
    }
}
