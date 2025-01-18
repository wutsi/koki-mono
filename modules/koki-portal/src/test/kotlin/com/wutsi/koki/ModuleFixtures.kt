package com.wutsi.koki

import com.wutsi.koki.module.dto.Module
import com.wutsi.koki.module.dto.Permission

object ModuleFixtures {
    // Modules
    val modules = listOf(
        Module(
            id = 100L,
            name = "account",
            title = "Account",
            homeUrl = "/accounts",
            settingsUrl = "/settings/accounts",
            tabUrl = null,
        ),
        Module(
            id = 101L,
            name = "contact",
            title = "Contacts",
            homeUrl = "/contacts",
            settingsUrl = "/settings/contacts",
            tabUrl = "/tab/contacts",
        ),
        Module(
            id = 102L,
            name = "tax",
            title = "Taxes",
            homeUrl = "/taxes",
            settingsUrl = "/settings/taxes",
            tabUrl = "/tab/taxes",
        ),
        Module(
            id = 103L,
            name = "email",
            title = "Email",
            homeUrl = null,
            settingsUrl = "/settings/emails",
            tabUrl = "/tab/emails",
        ),
        Module(
            id = 104L,
            name = "note",
            title = "Notes",
            homeUrl = null,
            settingsUrl = "/settings/notes",
            tabUrl = "/tab/notes",
        ),
    )

    val permissions = listOf(
        Permission(id = 1001, moduleId = 100, name = "account"),
        Permission(id = 1002, moduleId = 100, name = "account:admin"),
        Permission(id = 1011, moduleId = 101, name = "contact"),
        Permission(id = 1012, moduleId = 101, name = "contact:admin"),
        Permission(id = 1021, moduleId = 102, name = "tax"),
        Permission(id = 1022, moduleId = 102, name = "tax:admin"),
        Permission(id = 1031, moduleId = 103, name = "email"),
        Permission(id = 1032, moduleId = 103, name = "email:admin"),
        Permission(id = 1031, moduleId = 104, name = "note"),
        Permission(id = 1032, moduleId = 104, name = "note:admin"),
    )
}
