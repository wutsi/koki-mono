package com.wutsi.koki.platform.security

class NoAccessTokenHolder : AccessTokenHolder {
    override fun set(accessToken: String) {
    }

    override fun remove() {
    }

    override fun get(): String? {
        return null
    }
}
