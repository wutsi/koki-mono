package com.wutsi.koki.portal.client.invoice.service

import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.portal.client.common.page.AbstractPageController
import com.wutsi.koki.portal.client.invoice.mapper.InvoiceMapper
import com.wutsi.koki.portal.client.invoice.model.InvoiceModel
import com.wutsi.koki.sdk.KokiInvoices
import org.springframework.stereotype.Service

@Service
class InvoiceService(
    private val koki: KokiInvoices,
    private val mapper: InvoiceMapper,
) : AbstractPageController() {
    fun invoice(id: Long): InvoiceModel {
        val invoice = koki.invoice(id, null).invoice
        return mapper.toInvoiceModel(invoice)
    }

    fun invoices(limit: Int = 20, offset: Int = 0): List<InvoiceModel> {
        val invoices = koki.invoices(
            statuses = listOf(
                InvoiceStatus.PAID,
                InvoiceStatus.VOIDED,
                InvoiceStatus.OPENED,
            ),
            accountId = userHolder.get()!!.account.id,
            limit = limit,
            offset = offset,

            ids = emptyList(),
            taxId = null,
            number = null,
            orderId = null
        ).invoices
        return invoices.map { invoice -> mapper.toInvoiceModel(invoice) }
    }

    fun pdfUrl(id: Long): String {
        return koki.pdfUrl(id)
    }
}
