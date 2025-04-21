package com.wutsi.koki.platform.security

interface AccessTokenHolder {
    fun set(accessToken: String)
    fun remove()
    fun get(): String?
}
