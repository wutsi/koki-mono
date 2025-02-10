package com.wutsi.koki.product.server.endpoint

import com.wutsi.koki.product.dto.SearchUnitResponse
import com.wutsi.koki.product.server.mapper.UnitMapper
import com.wutsi.koki.product.server.service.UnitService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/units")
class UnitEndpoints(
    private val service: UnitService,
    private val mapper: UnitMapper,
) {
    @GetMapping
    fun all(): SearchUnitResponse {
        val units = service.all()
        return SearchUnitResponse(
            units = units.map { product -> mapper.toUnit(product) }
        )
    }
}
