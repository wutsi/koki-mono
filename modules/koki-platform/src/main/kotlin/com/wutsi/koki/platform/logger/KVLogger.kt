package com.wutsi.koki.platform.logger

import java.util.Optional

interface KVLogger {
    fun log()
    fun add(key: String, value: String?)
    fun add(key: String, value: Long?)
    fun add(key: String, value: Double?)
    fun add(key: String, value: Optional<*>)
    fun add(key: String, values: Collection<*>?)
    fun add(key: String, value: Any?)
    fun setException(ex: Throwable)
}
