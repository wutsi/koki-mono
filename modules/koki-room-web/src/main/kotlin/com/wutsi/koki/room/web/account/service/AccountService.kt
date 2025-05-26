package com.wutsi.koki.room.web.account.service

import com.wutsi.koki.room.web.account.mapper.AccountMapper
import com.wutsi.koki.room.web.account.model.AccountModel
import com.wutsi.koki.room.web.refdata.model.LocationService
import com.wutsi.koki.room.web.tenant.service.TypeService
import com.wutsi.koki.room.web.user.service.UserService
import com.wutsi.koki.sdk.KokiAccounts
import org.springframework.stereotype.Service
import kotlin.collections.flatMap

@Service
class AccountService(
    private val koki: KokiAccounts,
    private val mapper: AccountMapper,
    private val userService: UserService,
    private val typeService: TypeService,
    private val locationService: LocationService,
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

        val accountTypeMap = if (account.accountTypeId == null || !fullGraph) {
            emptyMap()
        } else {
            val id = account.accountTypeId!!
            mapOf(id to typeService.type(id))
        }

        val locationIds = listOf(
            account.shippingAddress?.cityId,
            account.shippingAddress?.stateId,
            account.billingAddress?.cityId,
            account.billingAddress?.stateId,
        ).filterNotNull().toSet()
        val locations = if (locationIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            locationService.locations(
                ids = locationIds.toList(),
                limit = locationIds.size
            ).associateBy { location -> location.id }
        }

        return mapper.toAccountModel(
            entity = account,
            accountTypes = accountTypeMap,
            users = userMap,
            locations = locations
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
}
