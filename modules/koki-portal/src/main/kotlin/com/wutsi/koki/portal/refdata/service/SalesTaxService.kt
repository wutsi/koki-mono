package com.wutsi.koki.portal.refdata.service

import com.wutsi.koki.portal.refdata.mapper.RefDataMapper
import com.wutsi.koki.portal.refdata.model.SalesTaxModel
import com.wutsi.koki.sdk.KokiRefData
import org.springframework.stereotype.Service

@Service
class SalesTaxService(
    private val koki: KokiRefData,
    private val mapper: RefDataMapper,
) {
    fun salesTaxes(
        ids: List<Long> = emptyList(),
        juridictionIds: List<Long> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0
    ): List<SalesTaxModel> {
        val salesTaxes = koki.salesTaxes(
            ids = ids,
            juridictionIds = juridictionIds,
            active = active,
            limit = limit,
            offset = offset,
        ).salesTaxes

        return salesTaxes.map { salesTax -> mapper.toSalesTaxModel(salesTax) }
    }
}
