package com.wutsi.koki.portal.client

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.module.dto.Module

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
            id = 120L,
            name = "tax",
            title = "Taxes",
            homeUrl = "/taxes",
            tabUrl = "/taxes/tab",
            settingsUrl = "settings/taxes",
            objectType = ObjectType.TAX,
            jsUrl = null,
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
            title = "Employees",
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
            id = 230,
            name = "form",
            title = "Forms",
            homeUrl = "/forms",
            tabUrl = null,
            settingsUrl = null,
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
    )
}
