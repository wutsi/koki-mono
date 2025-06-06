package com.wutsi.koki.tracking.server.service

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class IpApiService(private val rest: RestTemplate) {
    fun resolveCountry(ip: String): String? {
        val response = rest.getForEntity("http://ip-api.com/json/$ip", Map::class.java).body!!
        return response.get("countryCode")?.toString()
    }
}
