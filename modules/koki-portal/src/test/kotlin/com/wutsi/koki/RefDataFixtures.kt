package com.wutsi.koki

import com.wutsi.koki.refdata.dto.Unit

object RefDataFixtures {
    // Units
    val units = listOf(
        Unit(id = 100, name = "Hours"),
        Unit(id = 110, name = "Days"),
        Unit(id = 120, name = "Month"),
        Unit(id = 130, name = "Visit"),
    )
}
