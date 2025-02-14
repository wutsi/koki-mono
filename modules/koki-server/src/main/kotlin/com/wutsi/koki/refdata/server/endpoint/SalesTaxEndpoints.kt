package com.wutsi.koki.refdata.server.endpoint

import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.refdata.dto.SearchSalesTaxResponse
import com.wutsi.koki.refdata.server.io.SalesTaxImporter
import com.wutsi.koki.refdata.server.mapper.SalesTaxMapper
import com.wutsi.koki.refdata.server.service.SalesTaxService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/sales-taxes")
class SalesTaxEndpoints(
    private val importer: SalesTaxImporter,
    private val mapper: SalesTaxMapper,
    private val service: SalesTaxService,
) {
    @GetMapping("/import")
    fun import(@RequestParam country: String): ImportResponse {
        return importer.import(country)
    }

    @GetMapping
    fun search(
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "state-id") stateId: Long? = null,
        @RequestParam(required = false) country: String? = null,
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchSalesTaxResponse {
        val salesTaxes = service.search(
            ids = ids,
            stateId = stateId,
            country = country,
            active = active,
            limit = limit,
            offset = offset
        )
        return SearchSalesTaxResponse(
            salesTaxes = salesTaxes.map { salesTax -> mapper.toSalesTax(salesTax) }
        )
    }
}
