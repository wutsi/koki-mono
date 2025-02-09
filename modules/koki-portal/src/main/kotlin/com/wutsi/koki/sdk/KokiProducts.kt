package com.wutsi.koki.sdk

import com.wutsi.koki.product.dto.CreateProductRequest
import com.wutsi.koki.product.dto.CreateProductResponse
import com.wutsi.koki.product.dto.GetProductResponse
import com.wutsi.koki.product.dto.ProductType
import com.wutsi.koki.product.dto.SearchProductResponse
import com.wutsi.koki.product.dto.UpdateProductRequest
import org.springframework.web.client.RestTemplate

class KokiProducts(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val PRODUCT_PATH_PREFIX = "/v1/products"
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
}
