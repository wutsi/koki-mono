package com.wutsi.koki.platform.util

import com.fasterxml.jackson.databind.ObjectMapper

object MapUtils {
    fun filterOutEmpties(map: Map<String, Any>): Map<String, Any> {
        return map.filter { entry -> !isEmpty(entry.value) }
    }

    fun toJsonString(data: Map<String, Any>, objectMapper: ObjectMapper): String? {
        return if (data.isEmpty()) {
            null
        } else {
            objectMapper.writeValueAsString(
                MapUtils.filterOutEmpties(data)
            )
        }
    }

    private fun isEmpty(value: Any): Boolean {
        if (value is String) {
            return value.trim().isNullOrEmpty()
        } else if (value is Collection<*>) {
            return value.isEmpty()
        }
        return false
    }
}
