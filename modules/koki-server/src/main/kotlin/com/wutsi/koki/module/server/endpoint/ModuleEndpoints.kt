package com.wutsi.koki.module.server.endpoint

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/modules")
class ModuleEndpoints {
    @GetMapping("/{id}")
    fun get(id: Long): GetModuleResponse {
        TODO()
    }

    @GetMapping("/{id}")
    fun search(
        limit: Int = 20,
        offset: Int = 0
    ): GetModuleResponse {
        TODO()
    }
}
