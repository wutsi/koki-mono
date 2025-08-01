package com.wutsi.koki.chatbot.telegram.endpoint

import com.wutsi.koki.chatbot.telegram.service.TrackingService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/click")
class ClickController(
    private val trackingService: TrackingService,
) {
    @GetMapping
    fun onClick(
        @RequestHeader(name = "User-Agent", required = false) ua: String? = null,
        @RequestParam url: String,
        @RequestParam(name = "product-id") productId: String,
        @RequestParam(name = "correlation-id") correlationId: String,
        @RequestParam(name = "device-id") deviceId: String,
        @RequestParam(name = "tenant-id") tenantId: Long,
        @RequestParam rank: Int,
    ): String {
        trackingService.click(
            productId = productId,
            tenantId = tenantId,
            correlationId = correlationId,
            deviceId = deviceId,
            rank = rank,
            ua = ua,
        )
        return "redirect:$url"
    }
}
