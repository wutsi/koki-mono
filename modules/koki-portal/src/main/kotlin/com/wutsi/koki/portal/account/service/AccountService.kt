package com.wutsi.koki.portal.account.service

import com.wutsi.koki.account.dto.CreateAccountRequest
import com.wutsi.koki.account.dto.UpdateAccountRequest
import com.wutsi.koki.portal.account.form.AccountForm
import com.wutsi.koki.portal.account.mapper.AccountMapper
import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.tenant.service.TypeService
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.sdk.KokiAccounts
import org.springframework.stereotype.Service
import kotlin.collections.flatMap

@Service
class AccountService(
    private val koki: KokiAccounts,
    private val mapper: AccountMapper,
    private val userService: UserService,
    private val attributeService: AttributeService,
    private val typeService: TypeService,
) {
    fun account(id: Long, fullGraph: Boolean = true): AccountModel {
        val account = koki.account(id).account

        val userIds = listOf(account.createdById, account.modifiedById, account.managedById)
            .filterNotNull()
            .toSet()
        val userMap = if (userIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.users(ids = userIds.toList(), limit = userIds.size)
                .associateBy { user -> user.id }
        }

        val attributeIds = account.attributes.map { entry -> entry.key }
        val attributeMap = if (attributeIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            attributeService.attributes(ids = attributeIds, limit = attributeIds.size)
                .associateBy { attribute -> attribute.id }
        }

        val accountTypeMap = if (account.accountTypeId == null || !fullGraph) {
            emptyMap()
        } else {
            val id = account.accountTypeId!!
            mapOf(id to typeService.type(id))
        }

        return mapper.toAccountModel(
            entity = account,
            accountTypes = accountTypeMap,
            users = userMap,
            attributes = attributeMap,
        )
    }

    fun accounts(
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        accountTypeIds: List<Long> = emptyList(),
        managedByIds: List<Long> = emptyList(),
        createdByIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
        fullGraph: Boolean = true,
    ): List<AccountModel> {
        val accounts = koki.accounts(
            keyword = keyword,
            ids = ids,
            accountTypeIds = accountTypeIds,
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
        val userMap = if (userIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.users(
                ids = userIds.toList(),
                limit = userIds.size
            )
                .associateBy { user -> user.id }
        }

        val accountTypeIds = accounts.mapNotNull { account -> account.accountTypeId }
            .toSet()
        val accountTypeMap = if (accountTypeIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            typeService.types(
                ids = accountTypeIds.toList(),
                limit = accountTypeIds.size,
            )
                .associateBy { accountType -> accountType.id }
        }

        return accounts.map { account ->
            mapper.toAccountModel(
                entity = account,
                accountTypes = accountTypeMap,
                users = userMap
            )
        }
    }

    fun delete(id: Long) {
        koki.delete(id)
    }

    fun create(form: AccountForm): Long {
        val request = CreateAccountRequest(
            accountTypeId = if (form.accountTypeId == -1L) null else form.accountTypeId,
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
            accountTypeId = if (form.accountTypeId == -1L) null else form.accountTypeId,
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
