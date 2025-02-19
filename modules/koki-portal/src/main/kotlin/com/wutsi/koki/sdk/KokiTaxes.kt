package com.wutsi.koki.sdk

import com.wutsi.koki.tax.dto.CreateTaxProductRequest
import com.wutsi.koki.tax.dto.CreateTaxProductResponse
import com.wutsi.koki.tax.dto.CreateTaxRequest
import com.wutsi.koki.tax.dto.CreateTaxResponse
import com.wutsi.koki.tax.dto.GetTaxProductResponse
import com.wutsi.koki.tax.dto.GetTaxResponse
import com.wutsi.koki.tax.dto.SearchTaxProductResponse
import com.wutsi.koki.tax.dto.SearchTaxResponse
import com.wutsi.koki.tax.dto.TaxStatus
import com.wutsi.koki.tax.dto.UpdateTaxProductRequest
import com.wutsi.koki.tax.dto.UpdateTaxRequest
import com.wutsi.koki.tax.dto.UpdateTaxStatusRequest
import org.springframework.web.client.RestTemplate
import java.time.LocalDate

class KokiTaxes(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val TAX_PATH_PREFIX = "/v1/taxes"
        private const val PRODUCT_PATH_PREFIX = "/v1/tax-products"
    }

    fun tax(id: Long): GetTaxResponse {
        val url = urlBuilder.build("$TAX_PATH_PREFIX/$id")
        return rest.getForEntity(url, GetTaxResponse::class.java).body
    }

    fun taxes(
        ids: List<Long>,
        taxTypeIds: List<Long>,
        accountIds: List<Long>,
        participantIds: List<Long>,
        assigneeIds: List<Long>,
        createdByIds: List<Long>,
        statuses: List<TaxStatus>,
        fiscalYear: Int?,
        startAtFrom: LocalDate?,
        startAtTo: LocalDate?,
        dueAtFrom: LocalDate?,
        dueAtTo: LocalDate?,
        limit: Int,
        offset: Int,
    ): SearchTaxResponse {
        val url = urlBuilder.build(
            TAX_PATH_PREFIX,
            mapOf(
                "id" to ids,
                "tax-type-id" to taxTypeIds,
                "account-id" to accountIds,
                "participant-id" to participantIds,
                "assignee-id" to assigneeIds,
                "created-by-id" to createdByIds,
                "status" to statuses,
                "fiscal-year" to fiscalYear,
                "start-at-from" to startAtFrom?.toString(),
                "start-at-to" to startAtTo?.toString(),
                "due-at-from" to dueAtFrom?.toString(),
                "due-at-to" to dueAtTo?.toString(),
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchTaxResponse::class.java).body
    }

    fun create(request: CreateTaxRequest): CreateTaxResponse {
        val url = urlBuilder.build(TAX_PATH_PREFIX)
        return rest.postForEntity(url, request, CreateTaxResponse::class.java).body
    }

    fun update(id: Long, request: UpdateTaxRequest) {
        val url = urlBuilder.build("$TAX_PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun status(id: Long, request: UpdateTaxStatusRequest) {
        val url = urlBuilder.build("$TAX_PATH_PREFIX/$id/status")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun delete(id: Long) {
        val url = urlBuilder.build("$TAX_PATH_PREFIX/$id")
        rest.delete(url)
    }

    fun addProduct(request: CreateTaxProductRequest): CreateTaxProductResponse {
        val url = urlBuilder.build(PRODUCT_PATH_PREFIX)
        return rest.postForEntity(url, request, CreateTaxProductResponse::class.java).body
    }

    fun updateProduct(id: Long, request: UpdateTaxProductRequest) {
        val url = urlBuilder.build("$PRODUCT_PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun deleteProduct(id: Long) {
        val url = urlBuilder.build("$PRODUCT_PATH_PREFIX/$id")
        rest.delete(url)
    }

    fun products(taxId: Long, limit: Int, offset: Int): SearchTaxProductResponse {
        val url = urlBuilder.build(
            path = PRODUCT_PATH_PREFIX,
            parameters = mapOf(
                "tax-id" to taxId,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchTaxProductResponse::class.java).body
    }

    fun product(id: Long): GetTaxProductResponse {
        val url = urlBuilder.build("$PRODUCT_PATH_PREFIX/$id")
        return rest.getForEntity(url, GetTaxProductResponse::class.java).body
    }
}
