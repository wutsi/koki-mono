package com.wutsi.koki.portal.pub

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.platform.ai.llm.LLMType
import com.wutsi.koki.platform.translation.TranslationProvider
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.Tenant
import com.wutsi.koki.tenant.dto.TenantStatus
import com.wutsi.koki.tenant.dto.Type
import com.wutsi.koki.tenant.dto.TypeSummary
import kotlin.to

object TenantFixtures {
    val config = mapOf(
        ConfigurationName.AI_PROVIDER to LLMType.KOKI.name,
        ConfigurationName.AI_PROVIDER_GEMINI_MODEL to "gemini-2.0-flash",
        ConfigurationName.AI_PROVIDER_GEMINI_API_KEY to "kk-1203923-4390r-erf00943",

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
            websiteUrl = "https://www.google.com",
            portalUrl = "http://localhost:8080",
            clientPortalUrl = "https://localhost:8082",
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
}
