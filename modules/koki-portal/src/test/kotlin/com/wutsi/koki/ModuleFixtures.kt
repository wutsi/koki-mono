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
            title = "Accounts",
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
            id = 130L,
            name = "email",
            title = "Emails",
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
            id = 151L,
            name = "image",
            title = "Images",
            homeUrl = null,
            tabUrl = "/images/tab",
            settingsUrl = null,
            objectType = ObjectType.FILE,
            jsUrl = "/js/images.js",
            cssUrl = "/css/images.css",
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
            id = 180,
            name = "tenant",
            title = "Tenant",
            homeUrl = null,
            tabUrl = null,
            settingsUrl = "/settings/tenant",
            jsUrl = null,
        ),

        Module(
            id = 220,
            name = "ai",
            title = "AI",
            homeUrl = null,
            tabUrl = null,
            settingsUrl = "/settings/ai",
            jsUrl = null,
        ),

        Module(
            id = 240,
            name = "translation",
            title = "Translation",
            homeUrl = null,
            tabUrl = null,
            settingsUrl = "/settings/translations",
            jsUrl = null,
        ),
        Module(
            id = 260,
            name = "message",
            title = "Messages",
            homeUrl = null,
            tabUrl = "/messages/tab",
            settingsUrl = null,
            jsUrl = "/js/messages.js",
            cssUrl = "/css/messages.css",
        ),
        Module(
            id = 270,
            name = "listing",
            title = "Listings",
            homeUrl = "/listings",
            tabUrl = null,
            settingsUrl = null,
            jsUrl = "/js/listings.js",
            cssUrl = "/css/listings.css",
        ),
        Module(
            id = 280,
            name = "offer",
            title = "offer",
            homeUrl = "/offer",
            tabUrl = null,
            settingsUrl = null,
            jsUrl = "/js/offers.js",
            cssUrl = "/css/offers.css",
        ),
        Module(
            id = 290,
            name = "agent",
            title = "Agents",
            homeUrl = "/agents",
            tabUrl = null,
            settingsUrl = null,
            jsUrl = null,
            cssUrl = null,
        ),
    )

    val permissions = listOf(
        Permission(id = 1001, moduleId = 100, name = "account", description = "Access accounts"),
        Permission(id = 1002, moduleId = 100, name = "account:admin", description = "Configure accounts"),
        Permission(id = 1003, moduleId = 100, name = "account:manage", description = "Manage accounts"),
        Permission(id = 1004, moduleId = 100, name = "account:delete", description = "Delete accounts"),
        Permission(id = 1005, moduleId = 100, name = "account:full_access", description = "Full access to accounts"),

        Permission(id = 1101, moduleId = 110, name = "contact", description = "Access contacts"),
        Permission(id = 1102, moduleId = 110, name = "contact:manage", description = "Manage contacts"),
        Permission(id = 1103, moduleId = 110, name = "contact:delete", description = "Delete contacts"),
        Permission(id = 1104, moduleId = 110, name = "contact:full_access", description = "Full access to contacts"),

        Permission(id = 1300, moduleId = 130, name = "email:admin", description = "Configure emails"),

        Permission(id = 1401, moduleId = 140, name = "note", description = "Access notes"),
        Permission(id = 1402, moduleId = 140, name = "note:manage", description = "Manage notes"),
        Permission(id = 1403, moduleId = 140, name = "note:delete", description = "Delete notes"),

        Permission(id = 1501, moduleId = 150, name = "file", description = "Access files"),
        Permission(id = 1502, moduleId = 150, name = "file:admin", description = "Configure files"),
        Permission(id = 1503, moduleId = 150, name = "file:manage", description = "Manage files"),
        Permission(id = 1504, moduleId = 150, name = "file:delete", description = "Delete files"),

        Permission(id = 1511, moduleId = 151, name = "image", description = "Access images"),
        Permission(id = 1512, moduleId = 151, name = "image:manage", description = "Manage images"),
        Permission(id = 1513, moduleId = 151, name = "image:admin", description = "Configure images"),

        Permission(id = 1601, moduleId = 160, name = "security:admin", description = "Configure system security"),

        Permission(id = 1801, moduleId = 180, name = "tenant:admin", description = "Manage Tenant"),

        Permission(id = 2200, moduleId = 220, name = "ai:admin", description = "Configure AI"),

        Permission(id = 2400, moduleId = 240, name = "translation:admin", description = "Configure Translation"),

        Permission(id = 2600, moduleId = 260, name = "message", description = "View Messages"),
        Permission(id = 2601, moduleId = 260, name = "message:manage", description = "Manage Messages"),

        Permission(id = 2700, moduleId = 270, name = "listing", description = "View Listings"),
        Permission(id = 2701, moduleId = 270, name = "listing:manage", description = "Manage Listings"),
        Permission(id = 2702, moduleId = 270, name = "listing:full_access", description = "Full access Listings"),

        Permission(id = 2800, moduleId = 280, name = "offer", description = "View Offers"),
        Permission(id = 2801, moduleId = 280, name = "offer:manage", description = "Manage Offers"),
        Permission(id = 2802, moduleId = 280, name = "offer:full_access", description = "Full access Offers"),

        Permission(id = 2900, moduleId = 290, name = "agent", description = "View Agents"),
    )
}
