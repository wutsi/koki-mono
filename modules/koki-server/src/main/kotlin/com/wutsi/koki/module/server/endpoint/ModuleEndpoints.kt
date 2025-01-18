package com.wutsi.koki.module.server.endpoint

import com.wutsi.koki.module.dto.GetModuleResponse
import com.wutsi.koki.module.dto.SearchModuleResponse
import com.wutsi.koki.module.server.mapper.ModuleMapper
import com.wutsi.koki.module.server.service.ModuleService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/modules")
class ModuleEndpoints(
    private val service: ModuleService,
    private val mapper: ModuleMapper,
) {
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): GetModuleResponse {
        val module = service.get(id)
        return GetModuleResponse(
            module = mapper.toModule(module)
        )
    }

    @GetMapping
    fun search(): SearchModuleResponse {
        val modules = service.search()
        return SearchModuleResponse(
            modules = modules.map { module -> mapper.toModule(module) }
        )
    }
}
