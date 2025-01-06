package com.wutsi.koki

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test

@Sql(value = ["/db/test/clean.sql"])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ResetDBTest {
    @Test
    fun reset() {
        // Do nothing
    }
}
