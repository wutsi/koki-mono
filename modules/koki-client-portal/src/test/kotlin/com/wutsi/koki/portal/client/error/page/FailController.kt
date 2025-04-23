package com.wutsi.koki.portal.client.error.page

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class FailController {
    @GetMapping("/fail")
    fun fail() {
        throw RuntimeException("Failed")
    }
}
