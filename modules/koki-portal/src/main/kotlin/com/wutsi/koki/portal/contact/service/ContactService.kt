package com.wutsi.koki.portal.contact.service

import com.wutsi.koki.contact.dto.CreateContactRequest
import com.wutsi.koki.contact.dto.UpdateContactRequest
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.contact.form.ContactForm
import com.wutsi.koki.portal.contact.mapper.ContactMapper
import com.wutsi.koki.portal.contact.model.ContactModel
import com.wutsi.koki.portal.service.UserService
import com.wutsi.koki.sdk.KokiContacts
import org.springframework.stereotype.Service

@Service
class ContactService(
    private val koki: KokiContacts,
    private val mapper: ContactMapper,
    private val userService: UserService,
    private val contactTypeService: ContactTypeService,
    private val accountService: AccountService,
) {
    fun contact(id: Long): ContactModel {
        val contact = koki.contact(id).contact

        // Users
        val userIds = listOf(contact.createdById, contact.modifiedById)
            .filterNotNull()
            .toSet()
        val userMap = if (userIds.isEmpty()) {
            emptyMap()
        } else {
            userService.users(ids = userIds.toList(), limit = userIds.size)
                .associateBy { user -> user.id }
        }

        // Contact Type
        val contactType = contact.contactTypeId?.let { id -> contactTypeService.contactType(id) }

        // Account
        val account = contact.accountId?.let { id -> accountService.account(id) }

        return mapper.toContactModel(
            entity = contact,
            account = account,
            contactType = contactType,
            users = userMap
        )
    }

    fun contacts(
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        contactTypeIds: List<Long> = emptyList(),
        accountIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<ContactModel> {
        // Contacts
        val contacts = koki.contacts(
            keyword = keyword,
            ids = ids,
            contactTypeIds = contactTypeIds,
            accountIds = accountIds,
            limit = limit,
            offset = offset,
        ).contacts

        // Users
        val userIds = contacts.flatMap { contact ->
            listOf(contact.createdById, contact.modifiedById)
        }
            .filterNotNull()
            .toSet()
        val userMap = if (userIds.isEmpty()) {
            emptyMap()
        } else {
            userService.users(
                ids = userIds.toList(),
                limit = userIds.size
            )
                .associateBy { user -> user.id }
        }

        // Contact Types
        val contactTypeIds = contacts.mapNotNull { contact -> contact.contactTypeId }
            .toSet()
        val contactTypeMap = if (contactTypeIds.isEmpty()) {
            emptyMap()
        } else {
            contactTypeService.contactTypes(
                ids = contactTypeIds.toList(),
                limit = contactTypeIds.size,
            )
                .associateBy { contactType -> contactType.id }
        }

        // Accounts
        val accountIds = contacts.mapNotNull { contact -> contact.accountId }
        val accountMap = if (accountIds.isEmpty()) {
            emptyMap()
        } else {
            accountService.accounts(
                ids = accountIds.toList(),
                limit = accountIds.size,
                fullGraph = false
            )
                .associateBy { account -> account.id }
        }

        return contacts.map { contact ->
            mapper.toContactModel(
                entity = contact,
                account = contact.accountId?.let { id -> accountMap[id] },
                contactType = contact.contactTypeId?.let { id -> contactTypeMap[id] },
                users = userMap,
            )
        }
    }

    fun delete(id: Long) {
        koki.delete(id)
    }

    fun create(form: ContactForm): Long {
        val request = CreateContactRequest(
            firstName = form.firstName.trim(),
            lastName = form.lastName.trim(),
            salutations = form.salutation?.ifEmpty { null },
            contactTypeId = if (form.contactTypeId == -1L) null else form.contactTypeId,
            accountId = if (form.accountId == -1L) null else form.accountId,
            email = form.email?.trim()?.ifEmpty { null },
            phone = form.phone?.trim()?.ifEmpty { null },
            mobile = form.mobile?.trim()?.ifEmpty { null },
            gender = form.gender,
            profession = form.profession?.trim()?.ifEmpty { null },
            employer = form.employer?.trim()?.ifEmpty { null },
        )
        return koki.create(request).contactId
    }

    fun update(id: Long, form: ContactForm) {
        val request = UpdateContactRequest(
            firstName = form.firstName.trim(),
            lastName = form.lastName.trim(),
            salutations = form.salutation?.ifEmpty { null },
            contactTypeId = if (form.contactTypeId == -1L) null else form.contactTypeId,
            accountId = if (form.accountId == -1L) null else form.accountId,
            email = form.email?.trim()?.ifEmpty { null },
            phone = form.phone?.trim()?.ifEmpty { null },
            mobile = form.mobile?.trim()?.ifEmpty { null },
            gender = form.gender,
            profession = form.profession?.trim()?.ifEmpty { null },
            employer = form.employer?.trim()?.ifEmpty { null },
        )
        koki.update(id, request)
    }
}
