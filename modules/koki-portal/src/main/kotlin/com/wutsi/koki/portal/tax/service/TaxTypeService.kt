package com.wutsi.koki.portal.tax.service

import com.wutsi.koki.portal.tax.mapper.TaxMapper
import com.wutsi.koki.portal.tax.model.TaxTypeModel
import com.wutsi.koki.sdk.KokiTaxes
import org.springframework.stereotype.Service

@Service
class TaxTypeService(
    private val koki: KokiTaxes,
    private val mapper: TaxMapper,
) {
    fun taxType(id: Long): TaxTypeModel {
        val taxType = koki.type(id).taxType
        return mapper.toTaxTypeModel(taxType)
    }

    fun taxTypes(
        ids: List<Long> = emptyList(),
        names: List<String> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0
    ): List<TaxTypeModel> {
        val taxTypes = koki.types(
            ids = ids,
            names = names,
            active = active,
            limit = limit,
            offset = offset
        ).taxTypes

        return taxTypes.map { taxType -> mapper.toTaxTypeModel(taxType) }
    }
}
