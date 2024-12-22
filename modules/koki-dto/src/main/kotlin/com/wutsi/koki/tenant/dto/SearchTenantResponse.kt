package com.wutsi.koki.tenant.dto

data class SearchTenantResponse(
    val tenants: List<Tenant> = emptyList()
)
