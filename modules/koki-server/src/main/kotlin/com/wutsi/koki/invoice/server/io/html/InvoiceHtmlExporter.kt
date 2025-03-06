package com.wutsi.koki.invoice.server.io.html

import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.invoice.server.service.InvoiceService
import com.wutsi.koki.platform.templating.TemplatingEngine
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.refdata.server.service.SalesTaxService
import com.wutsi.koki.refdata.server.service.UnitService
import com.wutsi.koki.tenant.server.domain.BusinessEntity
import com.wutsi.koki.tenant.server.service.TenantService
import com.wutsi.koki.util.CurrencyUtil
import org.apache.commons.io.IOUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Service
class InvoiceHtmlExporter(
    private val templatingEngine: TemplatingEngine,
    private val locationService: LocationService,
    private val tenantService: TenantService,
    private val unitService: UnitService,
    private val salesTaxService: SalesTaxService,
    private val invoiceService: InvoiceService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(InvoiceHtmlExporter::class.java)
    }

    fun export(invoice: InvoiceEntity, business: BusinessEntity, output: OutputStream) {
        val doc = loadDocument(invoice, business)
        val writer = OutputStreamWriter(output)
        writer.use {
            val xml = doc.html()
            if (LOGGER.isDebugEnabled) {
                LOGGER.debug("-----------\n$xml\n")
            }
            writer.write(xml)
        }
    }

    private fun loadDocument(invoice: InvoiceEntity, business: BusinessEntity): Document {
        val input = InvoiceHtmlExporter::class.java.getResourceAsStream("/invoice/template/default.html")
        val html = IOUtils.toString(input, "utf-8")
        val data = createData(invoice, business)
        val xhtml = templatingEngine.apply(html, data)
        val doc = Jsoup.parse(xhtml, "utf-8")
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        return doc
    }

    private fun createData(invoice: InvoiceEntity, business: BusinessEntity): Map<String, Any> {
        val items = invoiceService.getInvoiceItems(invoice)
        val taxes = invoiceService.getInvoiceTaxes(invoice)

        // Locations
        val locationIds = listOf(
            invoice.shippingCityId,
            invoice.shippingStateId,
            invoice.billingCityId,
            invoice.billingStateId,
            business.addressCityId,
            business.addressStateId
        ).filterNotNull().distinct()
        val locations = if (locationIds.isEmpty()) {
            emptyMap()
        } else {
            locationService.search(ids = locationIds, limit = locationIds.size)
                .associateBy { location -> location.id!! }
        }

        // Units
        val unitIds = items.map { item -> item.unitId }.filterNotNull().distinct()
        val units = unitService.all().filter { unit -> unitIds.contains(unit.id) }
            .associateBy { unit -> unit.id }

        // Sale Taxes
        val saleTaxIds = taxes.map { tax -> tax.salesTaxId }
            .distinct()
        val salesTaxes = salesTaxService.search(ids = saleTaxIds, limit = saleTaxIds.size)
            .associateBy { tax -> tax.id }

        // Data
        val tenant = tenantService.get(invoice.tenantId)
        val dateFormat = SimpleDateFormat(tenant.dateFormat)
        val moneyFormat = CurrencyUtil.getNumberFormat(
            items.firstOrNull()?.currency ?: tenant.currency
        )
        val data = mutableMapOf<String, Any>()
        data["businessName"] = business.companyName
        data["businessPhone"] = (business.phone ?: "")
        data["businessEmail"] = (business.email ?: "")
        data["businessWebsite"] = (business.website?.let { URL(business.website).host } ?: "")
        data["businessAddress"] = toAddressHtml(
            street = business.addressStreet,
            cityId = business.addressCityId,
            stateId = business.addressStateId,
            country = business.addressCountry,
            postalCode = business.addressPostalCode,
            locations = locations,
        )

        data["invoiceNumber"] = invoice.number
        invoice.invoicedAt?.let { data["invoiceDate"] = dateFormat.format(invoice.invoicedAt) }
        if (isSameDay(invoice.invoicedAt, invoice.dueAt)) {
            data["invoicePayUponReception"] = true
        } else {
            invoice.dueAt?.let {
                data["invoiceDueDate"] = dateFormat.format(invoice.dueAt)
            }
        }
        data["totalAmount"] = moneyFormat.format(invoice.totalAmount)
        data["invoicePaid"] = (invoice.status == InvoiceStatus.PAID)
        data["invoiceVoided"] = (invoice.status == InvoiceStatus.VOIDED)
        data["invoiceDraft"] = (invoice.status == InvoiceStatus.DRAFT)
        data["amountPaid"] = moneyFormat.format(invoice.amountPaid)
        data["amountDue"] = moneyFormat.format(invoice.amountDue)

        data["customerName"] = invoice.customerName
        data["customerEmail"] = invoice.customerEmail
        invoice.customerPhone?.let { data["customerPhone"] = invoice.customerPhone }
        data["customerShippingAddress"] = toAddressHtml(
            street = invoice.shippingStreet,
            cityId = invoice.shippingCityId,
            stateId = invoice.shippingStateId,
            country = invoice.shippingCountry,
            postalCode = invoice.shippingPostalCode,
            locations = locations,
        )
        data["customerBillingAddress"] = toAddressHtml(
            street = invoice.billingStreet,
            cityId = invoice.billingCityId,
            stateId = invoice.billingStateId,
            country = invoice.billingCountry,
            postalCode = invoice.billingPostalCode,
            locations = locations,
        )

        data["items"] = items.map { item ->
            mapOf(
                "description" to (item.description ?: ""),
                "unitPrice" to moneyFormat.format(item.unitPrice),
                "quantity" to item.quantity,
                "subTotal" to moneyFormat.format(item.subTotal),
                "unit" to (item.unitId?.let { id -> units[id]?.name } ?: "")
            )
        }

        data["taxes"] = taxes.groupBy { tax -> tax.salesTaxId }
            .map { entry ->
                mapOf(
                    "name" to (salesTaxes[entry.value[0].salesTaxId]?.let { tax -> tax.name } ?: ""),
                    "rate" to entry.value[0].rate,
                    "amount" to moneyFormat.format(entry.value.sumOf { it.amount })
                )
            }
        return data
    }

    private fun toAddressHtml(
        street: String?,
        cityId: Long?,
        stateId: Long?,
        country: String?,
        postalCode: String?,
        locations: Map<Long, LocationEntity>,
    ): String {
        val city = cityId?.let { id -> locations[id] }
        val state = stateId?.let { id -> locations[id] }
        return listOf(
            street?.ifEmpty { null },
            listOf(city?.name, state?.name).filterNotNull().joinToString(", ").ifEmpty { null },
            postalCode?.ifEmpty { null },
            country?.let { country -> Locale("en", country).displayCountry },
        ).filterNotNull()
            .joinToString("<br>")
    }

    private fun isSameDay(date1: Date?, date2: Date?): Boolean {
        if (date1 == null || date2 == null) {
            return false
        } else {
            return Math.abs(date1.time - date2.time) <= 86400000L
        }
    }
}
