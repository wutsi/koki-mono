package com.wutsi.koki

import com.wutsi.koki.common.dto.ObjectType
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
            objectType = ObjectType.ACCOUNT,
            jsUrl = "/js/accounts.js",
        ),
        Module(
            id = 110L,
            name = "contact",
            title = "Contacts",
            homeUrl = "/contacts",
            tabUrl = "/contacts/tab",
            settingsUrl = "/settings/contacts",
            objectType = ObjectType.CONTACT,
            jsUrl = "/js/contacts.js",
        ),
        Module(
            id = 120L,
            name = "tax",
            title = "Taxes",
            homeUrl = "/taxes",
            tabUrl = "/taxes/tab",
            settingsUrl = null,
            objectType = ObjectType.TAX,
            jsUrl = null,
        ),
        Module(
            id = 130L,
            name = "email",
            title = "Email",
            homeUrl = null,
            tabUrl = "/emails/tab",
            settingsUrl = "/settings/email",
            objectType = ObjectType.EMAIL,
            jsUrl = "/js/emails.js",
        ),
        Module(
            id = 140L,
            name = "note",
            title = "Notes",
            homeUrl = null,
            tabUrl = "/notes/tab",
            settingsUrl = "/settings/notes",
            objectType = ObjectType.NOTE,
            jsUrl = "/js/notes.js",
        ),
        Module(
            id = 150L,
            name = "file",
            title = "Files",
            homeUrl = null,
            tabUrl = "/files/tab",
            settingsUrl = "/settings/files",
            objectType = ObjectType.FILE,
            jsUrl = "/js/files.js",
        ),
        Module(
            id = 160,
            name = "security",
            title = "Security",
            homeUrl = null,
            tabUrl = null,
            settingsUrl = "/settings/security",
            jsUrl = null,
        ),
        Module(
            id = 170,
            name = "employee",
            title = "Employee",
            homeUrl = "/employees",
            tabUrl = null,
            settingsUrl = null,
            jsUrl = null,
        ),

        Module(
            id = 180,
            name = "tenant",
            title = "Tenant",
            homeUrl = null,
            tabUrl = null,
            settingsUrl = "/settings/tenant",
            jsUrl = null,
        ),

        Module(
            id = 190,
            name = "product",
            title = "Product",
            homeUrl = "/products",
            tabUrl = null,
            settingsUrl = null,
            jsUrl = "/js/products.js",
        ),
    )

    val permissions = listOf(
        Permission(id = 1001, moduleId = 100, name = "account", description = "Access accounts"),
        Permission(id = 1002, moduleId = 100, name = "account:admin", description = "Configure accounts"),
        Permission(id = 1003, moduleId = 100, name = "account:manage", description = "Manage accounts"),
        Permission(id = 1004, moduleId = 100, name = "account:delete", description = "Delete accounts"),

        Permission(id = 1101, moduleId = 110, name = "contact", description = "Access contacts"),
        Permission(id = 1102, moduleId = 110, name = "contact:manage", description = "Manage contacts"),
        Permission(id = 1103, moduleId = 110, name = "contact:delete", description = "Delete contacts"),

        Permission(id = 1201, moduleId = 120, name = "tax", description = "Access taxes"),
        Permission(id = 1202, moduleId = 120, name = "tax:manage", description = "Manage taxes"),
        Permission(id = 1203, moduleId = 120, name = "tax:delete", description = "Delete taxes"),
        Permission(id = 1204, moduleId = 120, name = "tax:status", description = "Manage taxes status and assignment"),

        Permission(id = 1301, moduleId = 130, name = "email", description = "Access emails"),
        Permission(id = 1302, moduleId = 130, name = "email:send", description = "Send emails"),
        Permission(id = 1303, moduleId = 130, name = "email:admin", description = "Configure emails"),

        Permission(id = 1401, moduleId = 140, name = "note", description = "Access notes"),
        Permission(id = 1402, moduleId = 140, name = "note:manage", description = "Manage notes"),
        Permission(id = 1403, moduleId = 140, name = "note:delete", description = "Delete notes"),

        Permission(id = 1501, moduleId = 150, name = "file", description = "Access files"),
        Permission(id = 1502, moduleId = 150, name = "file:admin", description = "Configure files"),
        Permission(id = 1503, moduleId = 150, name = "file:manage", description = "Manage files"),
        Permission(id = 1504, moduleId = 150, name = "file:delete", description = "Delete files"),

        Permission(id = 1601, moduleId = 160, name = "security:admin", description = "Configure system security"),

        Permission(id = 1701, moduleId = 170, name = "employee", description = "Access employees"),
        Permission(id = 1702, moduleId = 170, name = "employee:manage", description = "Manage employees"),

        Permission(id = 1801, moduleId = 180, name = "tenant:admin", description = "Manage Tenant"),

        Permission(id = 1901, moduleId = 190, name = "product", description = "Access products"),
        Permission(id = 1902, moduleId = 190, name = "product:admin", description = "Configure products"),
        Permission(id = 1903, moduleId = 190, name = "product:manage", description = "Manage products"),
        Permission(id = 1904, moduleId = 190, name = "product:delete", description = "Delete products"),
    )
}
