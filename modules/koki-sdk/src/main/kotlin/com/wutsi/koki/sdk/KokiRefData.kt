package com.wutsi.koki.sdk

import com.wutsi.koki.refdata.dto.CategoryType
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.dto.SearchAmenityResponse
import com.wutsi.koki.refdata.dto.SearchCategoryResponse
import com.wutsi.koki.refdata.dto.SearchJuridictionResponse
import com.wutsi.koki.refdata.dto.SearchLocationResponse
import com.wutsi.koki.refdata.dto.SearchSalesTaxResponse
import com.wutsi.koki.refdata.dto.SearchUnitResponse
import org.springframework.web.client.RestTemplate

class KokiRefData(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val UNIT_PATH_PREFIX = "/v1/units"
        private const val LOCATION_PATH_PREFIX = "/v1/locations"
        private const val CATEGORY_PATH_PREFIX = "/v1/categories"
        private const val JURIDICTION_PATH_PREFIX = "/v1/juridictions"
        private const val SALES_TAXES_PATH_PREFIX = "/v1/sales-taxes"
        private const val AMENITIES_PATH_PREFIX = "/v1/amenities"
    }

    fun units(): SearchUnitResponse {
        val url = urlBuilder.build(UNIT_PATH_PREFIX)
        return rest.getForEntity(url, SearchUnitResponse::class.java).body
    }

    fun locations(
        keyword: String?,
        ids: List<Long>,
        parentId: Long?,
        type: LocationType?,
        country: String?,
        limit: Int,
        offset: Int,
    ): SearchLocationResponse {
        val url = urlBuilder.build(
            LOCATION_PATH_PREFIX, mapOf(
                "q" to keyword,
                "id" to ids,
                "parent-id" to parentId,
                "type" to type,
                "country" to country,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchLocationResponse::class.java).body
    }

    fun categories(
        keyword: String?,
        ids: List<Long>,
        parentId: Long?,
        type: CategoryType?,
        active: Boolean?,
        level: Int?,
        limit: Int,
        offset: Int,
    ): SearchCategoryResponse {
        val url = urlBuilder.build(
            CATEGORY_PATH_PREFIX, mapOf(
                "q" to keyword,
                "id" to ids,
                "parent-id" to parentId,
                "type" to type,
                "active" to active,
                "level" to level,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchCategoryResponse::class.java).body
    }

    fun juridictions(
        ids: List<Long>,
        stateId: Long?,
        country: String?,
        limit: Int,
        offset: Int,
    ): SearchJuridictionResponse {
        val url = urlBuilder.build(
            JURIDICTION_PATH_PREFIX, mapOf(
                "id" to ids,
                "state-id" to stateId,
                "country" to country,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchJuridictionResponse::class.java).body
    }

    fun salesTaxes(
        ids: List<Long>,
        juridictionIds: List<Long>,
        active: Boolean?,
        limit: Int,
        offset: Int,
    ): SearchSalesTaxResponse {
        val url = urlBuilder.build(
            SALES_TAXES_PATH_PREFIX, mapOf(
                "id" to ids,
                "juridiction-id" to juridictionIds,
                "active" to active,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchSalesTaxResponse::class.java).body
    }

    fun amenities(
        ids: List<Long>,
        categoryId: Long?,
        active: Boolean?,
        limit: Int,
        offset: Int,
    ): SearchAmenityResponse {
        val url = urlBuilder.build(
            AMENITIES_PATH_PREFIX, mapOf(
                "id" to ids,
                "category-id" to categoryId,
                "active" to active,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchAmenityResponse::class.java).body
    }
}
