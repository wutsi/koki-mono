package com.wutsi.koki.portal.account.service

import com.wutsi.koki.account.dto.CreateAccountRequest
import com.wutsi.koki.account.dto.UpdateAccountRequest
import com.wutsi.koki.portal.account.form.AccountForm
import com.wutsi.koki.portal.account.mapper.AccountMapper
import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.service.UserService
import com.wutsi.koki.sdk.KokiAccounts
import org.springframework.stereotype.Service
import kotlin.collections.flatMap

@Service
class AccountService(
    private val koki: KokiAccounts,
    private val mapper: AccountMapper,
    private val userService: UserService,
    private val attributeService: AttributeService,
) {
    fun account(id: Long): AccountModel {
        val account = koki.account(id).account

        val userIds = listOf(account.createdById, account.modifiedById, account.managedById)
            .filterNotNull()
            .toSet()
        val userMap = if (userIds.isEmpty()) {
            emptyMap()
        } else {
            userService.users(ids = userIds.toList(), limit = userIds.size)
                .associateBy { user -> user.id }
        }

        val attributeIds = account.attributes.map { entry -> entry.key }
        val attributeMap = if (attributeIds.isEmpty()) {
            emptyMap()
        } else {
            attributeService.attributes(ids = attributeIds, limit = attributeIds.size)
                .associateBy { attribute -> attribute.id }
        }

        return mapper.toAccountModel(account, userMap, attributeMap)
    }

    fun accounts(
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        managedByIds: List<Long> = emptyList(),
        createdByIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0
    ): List<AccountModel> {
        val accounts = koki.accounts(
            keyword = keyword,
            ids = ids,
            managedByIds = managedByIds,
            createdByIds = createdByIds,
            limit = limit,
            offset = offset
        ).accounts

        val userIds = accounts.flatMap { account ->
            listOf(account.createdById, account.modifiedById, account.managedById)
        }
            .filterNotNull()
            .toSet()

        val userMap = if (userIds.isEmpty()) {
            emptyMap()
        } else {
            userService.users(ids = userIds.toList(), limit = userIds.size)
                .associateBy { user -> user.id }
        }

        return accounts.map { account -> mapper.toAccountModel(account, userMap) }
    }

    fun delete(id: Long) {
        koki.delete(id)
    }

    fun create(form: AccountForm): Long {
        val request = CreateAccountRequest(
            name = form.name.trim(),
            description = form.description?.trim()?.ifEmpty { null },
            phone = form.phone?.trim()?.ifEmpty { null },
            mobile = form.mobile?.trim()?.ifEmpty { null },
            email = form.email?.trim()?.ifEmpty { null },
            website = form.website?.trim()?.ifEmpty { null },
            language = form.language?.trim()?.ifEmpty { null },
            managedById = if (form.managedById == -1L) null else form.managedById,
            attributes = form.attributes,
        )
        return koki.create(request).accountId
    }

    fun update(id: Long, form: AccountForm) {
        val request = UpdateAccountRequest(
            name = form.name.trim(),
            description = form.description?.trim()?.ifEmpty { null },
            phone = form.phone?.trim()?.ifEmpty { null },
            mobile = form.mobile?.trim()?.ifEmpty { null },
            email = form.email?.trim()?.ifEmpty { null },
            website = form.website?.trim()?.ifEmpty { null },
            language = form.language?.trim()?.ifEmpty { null },
            managedById = if (form.managedById == -1L) null else form.managedById,
            attributes = form.attributes,
        )
        koki.update(id, request)
    }
}
