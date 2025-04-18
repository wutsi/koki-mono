package com.wutsi.koki

import com.wutsi.koki.ModuleFixtures.modules
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.platform.ai.llm.LLMType
import com.wutsi.koki.platform.translation.TranslationProvider
import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.tenant.dto.Business
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.Tenant
import com.wutsi.koki.tenant.dto.TenantStatus
import com.wutsi.koki.tenant.dto.Type
import com.wutsi.koki.tenant.dto.TypeSummary

object TenantFixtures {
    val config = mapOf(
        ConfigurationName.AI_PROVIDER to LLMType.KOKI.name,
        ConfigurationName.AI_PROVIDER_GEMINI_MODEL to "gemini-2.0-flash",
        ConfigurationName.AI_PROVIDER_GEMINI_API_KEY to "kk-1203923-4390r-erf00943",

        ConfigurationName.INVOICE_DUE_DAYS to "30",
        ConfigurationName.INVOICE_START_NUMBER to "1550",
        ConfigurationName.INVOICE_EMAIL_ENABLED to "1",
        ConfigurationName.INVOICE_EMAIL_SUBJECT to "Invoice #{{invoiceNumber}}",
        ConfigurationName.INVOICE_EMAIL_BODY to "You have a new invoice!",

        ConfigurationName.PAYMENT_EMAIL_ENABLED to "1",
        ConfigurationName.PAYMENT_EMAIL_SUBJECT to "Thank you for your payment - Invoice #{{invoiceNumber}}",
        ConfigurationName.PAYMENT_EMAIL_BODY to "Thank you!",
        ConfigurationName.PAYMENT_METHOD_BANK_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_CASH_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_CASH_INSTRUCTIONS to "You can send your email at address:\n3030 Linton",
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

        ConfigurationName.SMTP_PORT to "25",
        ConfigurationName.SMTP_HOST to "smtp.gmail.com",
        ConfigurationName.SMTP_USERNAME to "ray.sponsible",
        ConfigurationName.SMTP_PASSWORD to "secret",
        ConfigurationName.SMTP_FROM_ADDRESS to "no-reply@koki.com",
        ConfigurationName.SMTP_FROM_PERSONAL to "Koki",

        ConfigurationName.STORAGE_TYPE to "S3",
        ConfigurationName.STORAGE_S3_BUCKET to "tenant-koki",
        ConfigurationName.STORAGE_S3_REGION to "us-west-2",
        ConfigurationName.STORAGE_S3_SECRET_KEY to "SEC-43430409-340430-490109",
        ConfigurationName.STORAGE_S3_ACCESS_KEY to "ACC-f000ffff-340430-490109",

        ConfigurationName.TAX_AI_AGENT_ENABLED to "1",
        ConfigurationName.TAX_EMAIL_ASSIGNEE_ENABLED to "1",
        ConfigurationName.TAX_EMAIL_ASSIGNEE_SUBJECT to "You have a new task",
        ConfigurationName.TAX_EMAIL_ASSIGNEE_BODY to "You have a new task assigned to you",
        ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_ENABLED to "1",
//        ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_SUBJECT to "The tax season has started",
//        ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_BODY to "<p>Get ready for the {{taxFiscalYear}} season</p>",
        ConfigurationName.TAX_EMAIL_DONE_ENABLED to "1",
        ConfigurationName.TAX_EMAIL_DONE_SUBJECT to "The tax season has started",
        ConfigurationName.TAX_EMAIL_DONE_BODY to "<p>Get ready for the {{taxFiscalYear}} season</p>",

        ConfigurationName.TRANSLATION_PROVIDER to TranslationProvider.AWS.name,
        ConfigurationName.TRANSLATION_PROVIDER_AWS_REGION to "us-east-1",
        ConfigurationName.TRANSLATION_PROVIDER_AWS_ACCESS_KEY to "sk-540954-xoioi",
        ConfigurationName.TRANSLATION_PROVIDER_AWS_SECRET_KEY to "sk-439093049",
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
            clientPortalUrl = "https://localhost:8082",
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
