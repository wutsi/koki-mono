package com.wutsi.koki.sdk

import com.wutsi.koki.product.dto.CreatePriceRequest
import com.wutsi.koki.product.dto.CreatePriceResponse
import com.wutsi.koki.product.dto.CreateProductRequest
import com.wutsi.koki.product.dto.CreateProductResponse
import com.wutsi.koki.product.dto.GetPriceResponse
import com.wutsi.koki.product.dto.GetProductResponse
import com.wutsi.koki.product.dto.ProductType
import com.wutsi.koki.product.dto.SearchPriceResponse
import com.wutsi.koki.product.dto.SearchProductResponse
import com.wutsi.koki.product.dto.UpdatePriceRequest
import com.wutsi.koki.product.dto.UpdateProductRequest
import org.springframework.web.client.RestTemplate
import java.time.LocalDate

class KokiProducts(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val PRODUCT_PATH_PREFIX = "/v1/products"
        private const val PRICE_PATH_PREFIX = "/v1/prices"
    }

    fun create(request: CreateProductRequest): CreateProductResponse {
        val url = urlBuilder.build(PRODUCT_PATH_PREFIX)
        return rest.postForEntity(url, request, CreateProductResponse::class.java).body
    }

    fun update(id: Long, request: UpdateProductRequest) {
        val url = urlBuilder.build("$PRODUCT_PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun delete(id: Long) {
        val url = urlBuilder.build("$PRODUCT_PATH_PREFIX/$id")
        rest.delete(url)
    }

    fun product(id: Long): GetProductResponse {
        val url = urlBuilder.build("$PRODUCT_PATH_PREFIX/$id")
        return rest.getForEntity(url, GetProductResponse::class.java).body
    }

    fun products(
        ids: List<Long>,
        types: List<ProductType>,
        active: Boolean?,
        limit: Int,
        offset: Int,
    ): SearchProductResponse {
        val url = urlBuilder.build(
            PRODUCT_PATH_PREFIX,
            mapOf(
                "id" to ids,
                "type" to types,
                "active" to active,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchProductResponse::class.java).body
    }

    fun create(request: CreatePriceRequest): CreatePriceResponse {
        val url = urlBuilder.build(PRICE_PATH_PREFIX)
        return rest.postForEntity(url, request, CreatePriceResponse::class.java).body
    }

    fun update(id: Long, request: UpdatePriceRequest) {
        val url = urlBuilder.build("$PRICE_PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun deletePrice(id: Long) {
        val url = urlBuilder.build("$PRICE_PATH_PREFIX/$id")
        rest.delete(url)
    }

    fun price(id: Long): GetPriceResponse {
        val url = urlBuilder.build("$PRICE_PATH_PREFIX/$id")
        return rest.getForEntity(url, GetPriceResponse::class.java).body
    }

    fun prices(
        ids: List<Long>,
        productIds: List<Long>,
        accountTypeIds: List<Long>,
        currency: String?,
        date: LocalDate?,
        active: Boolean? = null,
        limit: Int,
        offset: Int,
    ): SearchPriceResponse {
        val url = urlBuilder.build(
            PRICE_PATH_PREFIX,
            mapOf(
                "id" to ids,
                "product-id" to productIds,
                "account-type-id" to accountTypeIds,
                "currency" to currency,
                "date" to date?.toString(),
                "active" to active,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchPriceResponse::class.java).body
    }
}
