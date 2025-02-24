package com.wutsi.koki.refdata.server.endpoint

import com.wutsi.koki.refdata.server.service.RefdataService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/refdata")
class RefdataEndpoints(
    val service: RefdataService,
) {
    @GetMapping("/load")
    fun load() {
        service.load()
    }
}
