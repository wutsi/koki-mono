package com.wutsi.koki.platform.storage

import java.net.URL

interface StorageVisitor {
    fun visit(url: URL)
}
