package com.wutsi.koki.refdata.server.endpoint

import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.refdata.dto.SearchJuridictionResponse
import com.wutsi.koki.refdata.server.io.JuridictionImporter
import com.wutsi.koki.refdata.server.mapper.JuridictionMapper
import com.wutsi.koki.refdata.server.service.JuridictionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/juridictions")
class JuridictionEndpoints(
    private val importer: JuridictionImporter,
    private val service: JuridictionService,
    private val mapper: JuridictionMapper,
) {
    @GetMapping("/import")
    fun import(@RequestParam country: String): ImportResponse {
        return importer.import(country.uppercase())
    }

    @GetMapping
    fun search(
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "state-id") stateId: Long? = null,
        @RequestParam(required = false) country: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchJuridictionResponse {
        val juridictions = service.search(
            ids = ids,
            stateId = stateId,
            country = country,
            limit = limit,
            offset = offset,
        )
        return SearchJuridictionResponse(
            juridictions = juridictions.map { juridiction -> mapper.toJuridiction(juridiction) }
        )
    }
}
