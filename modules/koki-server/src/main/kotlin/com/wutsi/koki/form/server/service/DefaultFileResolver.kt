package com.wutsi.koki.form.server.generator.html

class NullFileResolver : FileResolver {
    override fun resolve(id: String): File? = null
}
