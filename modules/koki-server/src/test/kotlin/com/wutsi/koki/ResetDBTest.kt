package com.wutsi.koki

import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test

@Sql(value = ["/db/test/clean.sql"])
class ResetDBTest {
    @Test
    fun reset() {
        // Do nothing
    }
}
