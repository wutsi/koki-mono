package com.wutsi.koki.invoice.server.io.pdf

import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.platform.templating.TemplatingEngine
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.refdata.server.service.SalesTaxService
import com.wutsi.koki.refdata.server.service.UnitService
import com.wutsi.koki.tenant.server.domain.BusinessEntity
import com.wutsi.koki.tenant.server.service.TenantService
import org.apache.commons.io.IOUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service
import org.xhtmlrenderer.pdf.ITextRenderer
import java.io.OutputStream
import java.net.URL
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Service
class InvoicePDFExporter(
    private val templatingEngine: TemplatingEngine,
    private val locationService: LocationService,
    private val tenantService: TenantService,
    private val unitService: UnitService,
    private val salesTaxService: SalesTaxService,
) {
    fun export(invoice: InvoiceEntity, business: BusinessEntity, output: OutputStream) {
        val doc = loadDocument(invoice, business)
        val renderer = ITextRenderer()
        val context = renderer.sharedContext
        context.isPrint = true
        context.isInteractive = true
        renderer.setDocumentFromString(doc.html())
        renderer.layout()
        renderer.createPDF(output)
    }

    private fun loadDocument(invoice: InvoiceEntity, business: BusinessEntity): Document {
        val input = InvoicePDFExporter::class.java.getResourceAsStream("/invoice/default/template.html")
        val html = IOUtils.toString(input, "utf-8")
        val data = createData(invoice, business)
        val xhtml = templatingEngine.apply(html, data)
        val doc = Jsoup.parse(xhtml, "utf-8")
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        return doc
    }

    private fun createData(invoice: InvoiceEntity, business: BusinessEntity): Map<String, Any> {
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
        val unitIds = invoice.items.map { item -> item.unitId }.filterNotNull().distinct()
        val units = unitService.all().filter { unit -> unitIds.contains(unit.id) }
            .associateBy { unit -> unit.id }

        // Sale Taxes
        val saleTaxIds = invoice.items.flatMap { item -> item.taxes }
            .map { tax -> tax.salesTaxId }
            .distinct()
        val salesTaxes = salesTaxService.search(ids = saleTaxIds, limit = saleTaxIds.size)
            .associateBy { tax -> tax.id }

        // Data
        val tenant = tenantService.get(invoice.tenantId)
        val dateFormat = SimpleDateFormat(tenant.dateFormat)
        val moneyFormat = getMoneyFormatter(invoice.items.firstOrNull()?.currency ?: tenant.currency)
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
        data["invoiceDate"] = dateFormat.format(invoice.createdAt)
        invoice.dueAt?.let { data["invoiceDueDate"] = dateFormat.format(invoice.dueAt) }
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

        data["items"] = invoice.items.map { item ->
            mapOf(
                "description" to (item.description ?: ""),
                "unitPrice" to moneyFormat.format(item.unitPrice),
                "quantity" to item.quantity,
                "subTotal" to moneyFormat.format(item.subTotal),
                "unit" to (item.unitId?.let { id -> units[id]?.name } ?: "")
            )
        }

        data["taxes"] = invoice.items.flatMap { item -> item.taxes }
            .groupBy { tax -> tax.salesTaxId }
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

    private fun getMoneyFormatter(currency: String): NumberFormat {
        NumberFormat.getAvailableLocales().forEach { locale ->
            val fmt = NumberFormat.getCurrencyInstance(locale)
            if (fmt.getCurrency().getCurrencyCode() == currency) {
                return fmt
            }
        }
        throw IllegalStateException("Currency not supported: $currency")
    }

}
