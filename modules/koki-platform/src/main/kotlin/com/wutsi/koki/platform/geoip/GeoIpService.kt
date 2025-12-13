package com.wutsi.koki.platform.geoip

import org.springframework.boot.restclient.RestTemplateBuilder
import org.springframework.cache.Cache
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.json.JsonMapper

class GeoIpService(private val cache: Cache) {
    private val jsonMapper = JsonMapper.builderWithJackson2Defaults()
        .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build()
    private val rest = RestTemplateBuilder()
        .additionalMessageConverters(JacksonJsonHttpMessageConverter(jsonMapper))
        .build()

    fun resolve(ip: String): GeoIp? {
        val key = cacheKey(ip)
        val cached = cacheGet(key)
        if (cached == null) {
            val response = rest.getForEntity("http://ip-api.com/json/$ip", GeoIp::class.java)
            val geoip = response.body
            if (geoip == null || geoip.country.isEmpty()) {
                return null
            } else {
                cachePut(key, geoip)
            }
            return geoip
        } else {
            return cached
        }
    }

    private fun cacheKey(ip: String): String {
        return "geoip.$ip"
    }

    private fun cacheGet(key: String): GeoIp? {
        try {
            return cache.get(key, GeoIp::class.java)
        } catch (ex: Throwable) {
            return null
        }
    }

    private fun cachePut(key: String, value: GeoIp) {
        try {
            cache.put(key, value)
        } catch (ex: Throwable) {
        }
    }
}
