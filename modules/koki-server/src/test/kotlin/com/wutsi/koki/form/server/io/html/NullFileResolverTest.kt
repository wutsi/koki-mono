package com.wutsi.koki.form.server.generator.html

import kotlin.test.Test
import kotlin.test.assertNull

class NullFileResolverTest {
    private val resolver = NullFileResolver()

    @Test
    fun resolve() {
        assertNull(resolver.resolve("xxx", -1))
    }
}
