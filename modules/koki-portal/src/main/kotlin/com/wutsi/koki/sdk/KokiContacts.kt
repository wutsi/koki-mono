package com.wutsi.koki.sdk

import com.wutsi.koki.contact.dto.CreateContactRequest
import com.wutsi.koki.contact.dto.CreateContactResponse
import com.wutsi.koki.contact.dto.GetContactResponse
import com.wutsi.koki.contact.dto.GetContactTypeResponse
import com.wutsi.koki.contact.dto.SearchContactResponse
import com.wutsi.koki.contact.dto.SearchContactTypeResponse
import com.wutsi.koki.contact.dto.UpdateContactRequest
import org.springframework.web.client.RestTemplate

class KokiContacts(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val CONTACT_PATH_PREFIX = "/v1/contacts"
        private const val ACCOUNT_TYPE_PATH_PREFIX = "/v1/contact-types"
    }

    fun create(request: CreateContactRequest): CreateContactResponse {
        val url = urlBuilder.build(CONTACT_PATH_PREFIX)
        return rest.postForEntity(url, request, CreateContactResponse::class.java).body
    }

    fun update(id: Long, request: UpdateContactRequest) {
        val url = urlBuilder.build("$CONTACT_PATH_PREFIX/$id")
        rest.postForEntity(url, request, CreateContactResponse::class.java)
    }

    fun delete(id: Long) {
        val url = urlBuilder.build("$CONTACT_PATH_PREFIX/$id")
        rest.delete(url)
    }

    fun contact(id: Long): GetContactResponse {
        val url = urlBuilder.build("$CONTACT_PATH_PREFIX/$id")
        return rest.getForEntity(url, GetContactResponse::class.java).body
    }

    fun contacts(
        keyword: String?,
        ids: List<Long>,
        contactTypeIds: List<Long>,
        accountIds: List<Long>,
        createdByIds: List<Long>,
        limit: Int,
        offset: Int,
    ): SearchContactResponse {
        val url = urlBuilder.build(
            CONTACT_PATH_PREFIX,
            mapOf(
                "q" to keyword,
                "id" to ids,
                "contact-type-id" to contactTypeIds,
                "account-id" to accountIds,
                "created-by-id" to createdByIds,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchContactResponse::class.java).body
    }

    fun type(id: Long): GetContactTypeResponse {
        val url = urlBuilder.build("$ACCOUNT_TYPE_PATH_PREFIX/$id")
        return rest.getForEntity(url, GetContactTypeResponse::class.java).body
    }

    fun types(
        ids: List<Long>,
        names: List<String>,
        active: Boolean?,
        limit: Int,
        offset: Int,
    ): SearchContactTypeResponse {
        val url = urlBuilder.build(
            ACCOUNT_TYPE_PATH_PREFIX,
            mapOf(
                "id" to ids,
                "name" to names,
                "active" to active,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchContactTypeResponse::class.java).body
    }
}
