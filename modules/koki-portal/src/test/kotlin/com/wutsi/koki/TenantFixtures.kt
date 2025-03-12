package com.wutsi.koki

import com.wutsi.koki.ModuleFixtures.modules
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.tenant.dto.Business
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.Tenant
import com.wutsi.koki.tenant.dto.TenantStatus
import com.wutsi.koki.tenant.dto.Type
import com.wutsi.koki.tenant.dto.TypeSummary

object TenantFixtures {
    val config = mapOf(
        ConfigurationName.SMTP_PORT to "25",
        ConfigurationName.SMTP_HOST to "smtp.gmail.com",
        ConfigurationName.SMTP_USERNAME to "ray.sponsible",
        ConfigurationName.SMTP_PASSWORD to "secret",
        ConfigurationName.SMTP_FROM_ADDRESS to "no-reply@koki.com",
        ConfigurationName.SMTP_FROM_PERSONAL to "Koki",

        ConfigurationName.INVOICE_DUE_DAYS to "30",
        ConfigurationName.INVOICE_START_NUMBER to "1550",
        ConfigurationName.INVOICE_EMAIL_OPENED_ENABLED to "1",
        ConfigurationName.INVOICE_EMAIL_OPENED_SUBJECT to "Invoice #{{invoiceNumber}}",
        ConfigurationName.INVOICE_EMAIL_OPENED_BODY to "You have a new invoice!",
        ConfigurationName.INVOICE_EMAIL_PAID_ENABLED to "",
        ConfigurationName.INVOICE_EMAIL_PAID_SUBJECT to "Invoice #{{invoiceNumber}} paid",
        ConfigurationName.INVOICE_EMAIL_PAID_BODY to "Thank you for your business!",

        ConfigurationName.PAYMENT_METHOD_BANK_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_CASH_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_CHECK_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_INTERAC_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY to "STRIPE",
        ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY_STRIPE_API_KEY to "SRP.1234567890",
        ConfigurationName.PAYMENT_METHOD_PAYPAL_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_PAYPAL_CLIENT_ID to "PP.1234567890",
        ConfigurationName.PAYMENT_METHOD_MOBILE_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_MOBILE_GATEWAY to "FLUTTERWAVE",
        ConfigurationName.PAYMENT_METHOD_MOBILE_GATEWAY_FLUTTERWAVE_SECRET_KEY to "FLT.1234567890",

        ConfigurationName.STORAGE_TYPE to "S3",
        ConfigurationName.STORAGE_S3_BUCKET to "tenant-koki",
        ConfigurationName.STORAGE_S3_REGION to "us-west-2",
        ConfigurationName.STORAGE_S3_SECRET_KEY to "SEC-43430409-340430-490109",
        ConfigurationName.STORAGE_S3_ACCESS_KEY to "ACC-f000ffff-340430-490109",
    )

    // Tenants
    val tenants = listOf(
        Tenant(
            id = 1,
            name = "test",
            domainName = "localhost",
            locale = "CA",
            currency = "CAD",
            currencySymbol = "C\$",
            timeFormat = "hh:mm a",
            dateFormat = "yyyy-MM-dd",
            dateTimeFormat = "yyyy-MM-dd hh:mm a",
            numberFormat = "#,###,##0",
            monetaryFormat = "C\$ #,###,##0.00",
            status = TenantStatus.ACTIVE,
            logoUrl = "https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/name-104x50.png",
            iconUrl = "https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/logo_512x512.png",
            moduleIds = modules.map { module -> module.id },
            websiteUrl = "https://www.google.com",
            portalUrl = "http://localhost:8080",
        ),
        Tenant(
            id = 2,
            name = "test.cm",
            domainName = "test.cm",
            locale = "fr_CM",
            currency = "XAF",
            currencySymbol = "FCFA",
            timeFormat = "HH:mm",
            dateFormat = "yyyy-MM-dd",
            dateTimeFormat = "yyyy-MM-dd HH:mm",
            numberFormat = "#,###,###.#0",
            monetaryFormat = "#,###,### FCFA",
            status = TenantStatus.ACTIVE,
            moduleIds = modules.map { module -> module.id }
        ),
    )

    // Types
    val types = listOf(
        TypeSummary(id = 110, objectType = ObjectType.EMPLOYEE, name = "F", title = "Full-Time"),
        TypeSummary(id = 111, objectType = ObjectType.EMPLOYEE, name = "P", title = "Part-Time"),
        TypeSummary(id = 112, objectType = ObjectType.EMPLOYEE, name = "C", title = "Contractor"),
        TypeSummary(id = 113, objectType = ObjectType.EMPLOYEE, name = "I", title = "Intern"),

        TypeSummary(id = 120, objectType = ObjectType.ACCOUNT, name = "Business"),
        TypeSummary(id = 121, objectType = ObjectType.ACCOUNT, name = "Household"),
    )

    val type = Type(
        id = 110,
        objectType = ObjectType.EMPLOYEE,
        name = "F",
        title = "Full-Time",
        description = "Full Time Employee"
    )

    val business = Business(
        id = 111,
        companyName = "Test Inc",
        email = "info@yahoo.com",
        phone = "+9189990000",
        fax = "+9189990011",
        website = "https://yahoo.com",
        juridictionIds = listOf(
            RefDataFixtures.juridictions[0].id,
            RefDataFixtures.juridictions[1].id,
        ),
        address = Address(
            street = "340 Pascal",
            postalCode = "H7K 1C7",
            cityId = RefDataFixtures.locations[2].id,
            stateId = RefDataFixtures.locations[2].parentId,
            country = "CA",
        ),
    )
}
