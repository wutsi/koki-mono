package com.wutsi.koki.portal.tax.service

import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.tax.form.TaxForm
import com.wutsi.koki.portal.tax.form.TaxStatusForm
import com.wutsi.koki.portal.tax.mapper.TaxMapper
import com.wutsi.koki.portal.tax.model.TaxModel
import com.wutsi.koki.portal.tenant.service.TypeService
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.sdk.KokiTaxes
import com.wutsi.koki.tax.dto.CreateTaxRequest
import com.wutsi.koki.tax.dto.TaxStatus
import com.wutsi.koki.tax.dto.UpdateTaxRequest
import com.wutsi.koki.tax.dto.UpdateTaxStatusRequest
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.time.LocalDate
import kotlin.collections.flatMap

@Service
class TaxService(
    private val koki: KokiTaxes,
    private val mapper: TaxMapper,
    private val typeService: TypeService,
    private val userService: UserService,
    private val accountService: AccountService
) {
    fun tax(id: Long, fullGraph: Boolean = true): TaxModel {
        val tax = koki.tax(id).tax
        val taxType = tax.taxTypeId?.let { id -> typeService.type(id) }

        val account = if (fullGraph) {
            accountService.account(tax.accountId)
        } else {
            AccountModel(tax.accountId)
        }

        val userIds = listOf(tax.accountantId, tax.technicianId, tax.assigneeId, tax.createdById, tax.modifiedById)
            .filterNotNull()
            .toSet()
        val users = if (userIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.users(
                ids = userIds.toList(),
                limit = userIds.size
            ).associateBy { user -> user.id }
        }

        return mapper.toTax(
            entity = tax,
            account = account,
            users = users,
            taxType = taxType
        )
    }

    fun taxes(
        ids: List<Long> = emptyList(),
        taxTypeIds: List<Long> = emptyList(),
        accountIds: List<Long> = emptyList(),
        participantIds: List<Long> = emptyList(),
        assigneeIds: List<Long> = emptyList(),
        createdByIds: List<Long> = emptyList(),
        statuses: List<TaxStatus> = emptyList(),
        fiscalYear: Int? = null,
        startAtFrom: LocalDate? = null,
        startAtTo: LocalDate? = null,
        dueAtFrom: LocalDate? = null,
        dueAtTo: LocalDate? = null,
        limit: Int = 20,
        offset: Int = 0,
        fullGraph: Boolean = true,
    ): List<TaxModel> {
        val taxes = koki.taxes(
            ids = ids,
            taxTypeIds = taxTypeIds,
            accountIds = accountIds,
            participantIds = participantIds,
            assigneeIds = assigneeIds,
            createdByIds = createdByIds,
            statuses = statuses,
            fiscalYear = fiscalYear,
            startAtFrom = startAtFrom,
            startAtTo = startAtTo,
            dueAtFrom = dueAtFrom,
            dueAtTo = dueAtTo,
            limit = limit,
            offset = offset,
        ).taxes

        // Account
        val accountIds = taxes.map { tax -> tax.accountId }.toSet()
        val accounts = if (accountIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            accountService.accounts(
                ids = accountIds.toList(),
                limit = accountIds.size,
                fullGraph = false,
            ).associateBy { accountant -> accountant.id }
        }

        // Types
        val taxTypeIds = taxes.mapNotNull { tax -> tax.taxTypeId }.toSet()
        val taxTypes = typeService.types(
            ids = taxTypeIds.toList(),
            limit = taxTypeIds.size
        ).associateBy { taxType -> taxType.id }

        // Users
        val userIds = taxes.flatMap { tax ->
            listOf(tax.accountantId, tax.technicianId, tax.assigneeId, tax.createdById, tax.modifiedById)
        }
            .filterNotNull()
            .toSet()
        val users = if (userIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.users(
                ids = userIds.toList(),
                limit = userIds.size
            ).associateBy { user -> user.id }
        }

        return taxes.map { tax ->
            mapper.toTax(
                entity = tax,
                account = accounts[tax.accountId] ?: AccountModel(tax.accountId),
                users = users,
                taxType = taxTypes[tax.taxTypeId]
            )
        }
    }

    fun delete(id: Long) {
        koki.delete(id)
    }

    fun create(form: TaxForm): Long {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val request = CreateTaxRequest(
            fiscalYear = form.fiscalYear,
            accountId = form.accountId,
            taxTypeId = form.taxTypeId,
            assigneeId = form.assigneeId,
            accountantId = form.accountantId,
            technicianId = form.technicianId,
            description = form.description?.ifEmpty { null },
            startAt = form.startAt.ifEmpty { null }?.let { date -> fmt.parse(date) },
            dueAt = form.dueAt.ifEmpty { null }?.let { date -> fmt.parse(date) },
        )
        return koki.create(request).taxId
    }

    fun update(id: Long, form: TaxForm) {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val request = UpdateTaxRequest(
            fiscalYear = form.fiscalYear,
            accountId = form.accountId,
            taxTypeId = form.taxTypeId,
            accountantId = form.accountantId,
            technicianId = form.technicianId,
            description = form.description?.ifEmpty { null },
            startAt = form.startAt.ifEmpty { null }?.let { date -> fmt.parse(date) },
            dueAt = form.dueAt.ifEmpty { null }?.let { date -> fmt.parse(date) },
        )
        koki.update(id, request)
    }

    fun status(id: Long, form: TaxStatusForm) {
        val request = UpdateTaxStatusRequest(
            assigneeId = form.assigneeId,
            status = form.status,
        )
        koki.status(id, request)
    }
}
