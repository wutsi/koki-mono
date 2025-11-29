package com.wutsi.koki.sdk

import com.wutsi.koki.contact.dto.CreateContactRequest
import com.wutsi.koki.contact.dto.CreateContactResponse
import com.wutsi.koki.contact.dto.GetContactResponse
import com.wutsi.koki.contact.dto.SearchContactResponse
import com.wutsi.koki.contact.dto.UpdateContactRequest
import org.springframework.web.client.RestTemplate

class KokiContacts(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val CONTACT_PATH_PREFIX = "/v1/contacts"
    }

    fun create(request: CreateContactRequest): CreateContactResponse {
        val url = urlBuilder.build(CONTACT_PATH_PREFIX)
        return rest.postForEntity(url, request, CreateContactResponse::class.java).body!!
    }

    fun update(id: Long, request: UpdateContactRequest) {
        val url = urlBuilder.build("$CONTACT_PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun delete(id: Long) {
        val url = urlBuilder.build("$CONTACT_PATH_PREFIX/$id")
        rest.delete(url)
    }

    fun contact(id: Long): GetContactResponse {
        val url = urlBuilder.build("$CONTACT_PATH_PREFIX/$id")
        return rest.getForEntity(url, GetContactResponse::class.java).body!!
    }

    fun contacts(
        keyword: String?,
        ids: List<Long>,
        contactTypeIds: List<Long>,
        accountIds: List<Long>,
        createdByIds: List<Long>,
        accountManagerIds: List<Long>,
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
                "account-manager-id" to accountManagerIds,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchContactResponse::class.java).body!!
    }
}
