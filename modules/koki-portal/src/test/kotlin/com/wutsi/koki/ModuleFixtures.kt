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
            id = 190,
            name = "product",
            title = "Products",
            homeUrl = "/products",
            tabUrl = null,
            settingsUrl = null,
            jsUrl = "/js/products.js",
        ),

        Module(
            id = 200,
            name = "invoice",
            title = "Invoices",
            homeUrl = "/invoices",
            tabUrl = "/invoices/tab",
            settingsUrl = "/settings/invoices",
            jsUrl = "/js/invoices.js",
            objectType = ObjectType.INVOICE,
        ),

        Module(
            id = 210,
            name = "payment",
            title = "Payments",
            homeUrl = "/payments",
            tabUrl = "/payments/tab",
            settingsUrl = "/settings/payments",
            jsUrl = "/js/payments.js",
            objectType = ObjectType.PAYMENT,
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
            id = 250,
            objectType = ObjectType.ROOM,
            name = "room",
            title = "Rooms",
            homeUrl = "/rooms",
            tabUrl = "/rooms/tab",
            settingsUrl = null,
            jsUrl = "/js/rooms.js",
            cssUrl = "/css/rooms.css",
        ),
        Module(
            id = 251,
            objectType = ObjectType.ROOM_UNIT,
            name = "room-unit",
            title = "Units",
            homeUrl = null,
            tabUrl = "/room-units/tab",
            settingsUrl = null,
            jsUrl = null,
            cssUrl = null,
        ),
        Module(
            id = 252,
            name = "room-amenity",
            title = "Amenities",
            homeUrl = null,
            tabUrl = "/room-amenities/tab",
            settingsUrl = null,
            jsUrl = null,
            cssUrl = null,
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

        Permission(id = 1511, moduleId = 151, name = "image", description = "Access images"),
        Permission(id = 1512, moduleId = 151, name = "image:manage", description = "Manage images"),
        Permission(id = 1513, moduleId = 151, name = "image:admin", description = "Configure images"),

        Permission(id = 1601, moduleId = 160, name = "security:admin", description = "Configure system security"),

        Permission(id = 1801, moduleId = 180, name = "tenant:admin", description = "Manage Tenant"),

        Permission(id = 1901, moduleId = 190, name = "product", description = "Access products"),
        Permission(id = 1902, moduleId = 190, name = "product:admin", description = "Configure products"),
        Permission(id = 1903, moduleId = 190, name = "product:manage", description = "Manage products"),
        Permission(id = 1904, moduleId = 190, name = "product:delete", description = "Delete products"),

        Permission(id = 2001, moduleId = 200, name = "invoice", description = "Access invoices"),
        Permission(id = 2002, moduleId = 200, name = "invoice:admin", description = "Configure invoices"),
        Permission(id = 2003, moduleId = 200, name = "invoice:void", description = "Void invoices"),
        Permission(id = 2004, moduleId = 200, name = "invoice:manage", description = "Manage invoices"),

        Permission(id = 2101, moduleId = 210, name = "payment", description = "Access payment"),
        Permission(id = 2102, moduleId = 210, name = "payment:admin", description = "Configure payment"),
        Permission(id = 2103, moduleId = 210, name = "payment:manage", description = "Manage payment"),

        Permission(id = 2200, moduleId = 220, name = "ai:admin", description = "Configure AI"),

        Permission(id = 2400, moduleId = 240, name = "translation:admin", description = "Configure Translation"),

        Permission(id = 2500, moduleId = 250, name = "room", description = "View Properties"),
        Permission(id = 2501, moduleId = 250, name = "room:manage", description = "Manage Properties"),
        Permission(id = 2502, moduleId = 250, name = "room:delete", description = "Delete Properties"),
        Permission(id = 2503, moduleId = 250, name = "room:full_access", description = "Full Access on Properties"),
        Permission(id = 2510, moduleId = 251, name = "room-unit", description = "View Room Unit"),
        Permission(id = 2511, moduleId = 251, name = "room-unit:manage", description = "Manage Room Units"),
        Permission(id = 2520, moduleId = 252, name = "room-amenity", description = "View Amenities"),
        Permission(id = 2521, moduleId = 252, name = "room-amenity:manage", description = "Manage Amenitires"),

        Permission(id = 2600, moduleId = 260, name = "message", description = "View Messages"),
        Permission(id = 2601, moduleId = 260, name = "message:manage", description = "Manage Messages"),
    )
}
