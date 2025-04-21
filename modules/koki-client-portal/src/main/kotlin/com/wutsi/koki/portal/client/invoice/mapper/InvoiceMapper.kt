package com.wutsi.koki.portal.client.invoice.mapper

import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.InvoiceSummary
import com.wutsi.koki.portal.client.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.client.common.service.MoneyMapper
import com.wutsi.koki.portal.client.invoice.model.InvoiceModel
import com.wutsi.koki.sdk.KokiInvoices
import org.springframework.stereotype.Service

@Service
class InvoiceMapper(
    private val moneyMapper: MoneyMapper,
    private val koki: KokiInvoices,
) : TenantAwareMapper() {
    fun toInvoiceModel(entity: InvoiceSummary): InvoiceModel {
        val fmt = createDateTimeFormat()
        val dateFormat = createDateFormat()
        val tenant = currentTenant.get()!!
        return InvoiceModel(
            id = entity.id,
            paynowId = entity.paynowId,
            number = entity.number,
            status = entity.status,
            totalAmount = moneyMapper.toMoneyModel(entity.totalAmount, entity.currency),
            amountPaid = moneyMapper.toMoneyModel(entity.amountPaid, entity.currency),
            amountDue = moneyMapper.toMoneyModel(entity.amountDue, entity.currency),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            createdAt = entity.createdAt,
            createdAtText = dateFormat.format(entity.createdAt),
            downloadUrl = koki.url(entity.id),
            paynowUrl = if (entity.status == InvoiceStatus.OPENED) {
                "${tenant.portalUrl}/paynow/${entity.paynowId}.${entity.id}"
            } else {
                null
            }
        )
    }
}
