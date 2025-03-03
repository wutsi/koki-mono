package com.wutsi.koki.invoice.server.endpoint

import com.ibm.icu.text.SimpleDateFormat
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.invoice.dto.CreateInvoiceRequest
import com.wutsi.koki.invoice.dto.CreateInvoiceResponse
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.Item
import com.wutsi.koki.invoice.server.dao.InvoiceItemRepository
import com.wutsi.koki.invoice.server.dao.InvoiceLogRepository
import com.wutsi.koki.invoice.server.dao.InvoiceRepository
import com.wutsi.koki.invoice.server.dao.InvoiceSequenceRepository
import com.wutsi.koki.invoice.server.dao.InvoiceTaxRepository
import com.wutsi.koki.tax.server.dao.TaxRepository
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.apache.commons.lang3.time.DateUtils
import org.bouncycastle.oer.OERDefinition.seq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/invoice/CreateInvoiceEndpoint.sql"])
class CreateInvoiceEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: InvoiceRepository

    @Autowired
    private lateinit var itemDao: InvoiceItemRepository

    @Autowired
    private lateinit var invoiceTaxDao: InvoiceTaxRepository

    @Autowired
    private lateinit var seqDao: InvoiceSequenceRepository

    @Autowired
    private lateinit var logDao: InvoiceLogRepository

    @Autowired
    private lateinit var taxDao: TaxRepository

    @Autowired
    private lateinit var configService: ConfigurationService

    private val fmt = SimpleDateFormat("yyyy-MM-dd")

    private val request = CreateInvoiceRequest(
        taxId = null,
        orderId = null,
        customerAccountId = 333L,
        customerName = "Ray Sponsible Inc",
        customerPhone = "+5141110000",
        customerMobile = "+5141110011",
        customerEmail = "info@ray-sponsible-inc.com",

        currency = "CAD",
        description = "Sample invoice",

        shippingStreet = "340 Pascal",
        shippingPostalCode = "123 111",
        shippingCityId = 110L,
        shippingCountry = "CA",

        billingStreet = "333 Nicolet",
        billingPostalCode = "222 222",
        billingCityId = 110L,
        billingCountry = "CA",

        items = listOf(
            Item(
                productId = 111L,
                unitPriceId = 11100L,
                quantity = 5,
                unitPrice = 100.0,
                description = "Product #1",
                unitId = 1,
            ),
            Item(
                productId = 222L,
                unitPriceId = 22200L,
                quantity = 1,
                unitPrice = 300.0,
                description = "Product #2"
            ),
        ),
        dueAt = DateUtils.addDays(Date(), 30)
    )

    @Test
    fun create() {
        val response = rest.postForEntity("/v1/invoices", request, CreateInvoiceResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val invoiceId = response.body!!.invoiceId
        val invoice = dao.findById(invoiceId).get()
        val seq = seqDao.findByTenantId(TENANT_ID)
        assertEquals(seq?.current, invoice.number)
        assertEquals(InvoiceStatus.DRAFT, invoice.status)
        assertEquals(request.taxId, invoice.taxId)
        assertEquals(request.orderId, invoice.orderId)
        assertEquals(request.description, invoice.description)
        assertEquals(request.customerAccountId, invoice.customerAccountId)
        assertEquals(request.customerName, invoice.customerName)
        assertEquals(request.customerEmail, invoice.customerEmail)
        assertEquals(request.customerPhone, invoice.customerPhone)
        assertEquals(request.customerMobile, invoice.customerMobile)
        assertEquals(119.80, invoice.totalTaxAmount)
        assertEquals(0.00, invoice.totalDiscountAmount)
        assertEquals(800.00, invoice.subTotalAmount)
        assertEquals(919.80, invoice.totalAmount)
        assertEquals(invoice.totalAmount, invoice.amountDue)
        assertEquals(0.00, invoice.amountPaid)
        assertEquals(request.currency, invoice.currency)
        assertEquals(request.shippingCityId, invoice.shippingCityId)
        assertEquals(100L, invoice.shippingStateId)
        assertEquals("CA", invoice.shippingCountry)
        assertEquals(request.shippingStreet, invoice.shippingStreet)
        assertEquals(request.shippingPostalCode, invoice.shippingPostalCode)
        assertEquals(request.billingCityId, invoice.billingCityId)
        assertEquals(100L, invoice.billingStateId)
        assertEquals("CA", invoice.billingCountry)
        assertEquals(request.billingStreet, invoice.billingStreet)
        assertEquals(request.billingPostalCode, invoice.billingPostalCode)
        assertEquals(USER_ID, invoice.createdById)
        assertEquals(USER_ID, invoice.modifiedById)
        assertEquals(fmt.format(request.dueAt), fmt.format(invoice.dueAt))

        val items = itemDao.findByInvoice(invoice)
        assertEquals(2, items.size)
        assertEquals(request.items[0].productId, items[0].productId)
        assertEquals(request.items[0].unitPriceId, items[0].unitPriceId)
        assertEquals(request.items[0].unitId, items[0].unitId)
        assertEquals(request.items[0].unitPrice, items[0].unitPrice)
        assertEquals(request.items[0].quantity, items[0].quantity)
        assertEquals(request.items[0].quantity * request.items[0].unitPrice, items[0].subTotal)
        assertEquals(request.items[0].description, items[0].description)
        assertEquals(request.items[1].productId, items[1].productId)
        assertEquals(request.items[1].unitPriceId, items[1].unitPriceId)
        assertEquals(request.items[1].unitId, items[1].unitId)
        assertEquals(request.items[1].unitPrice, items[1].unitPrice)
        assertEquals(request.items[1].quantity, items[1].quantity)
        assertEquals(request.items[1].quantity * request.items[1].unitPrice, items[1].subTotal)
        assertEquals(request.items[1].description, items[1].description)

        val taxes0 = invoiceTaxDao.findByInvoiceItem(items[0])
        assertEquals(2, taxes0.size)
        assertEquals(20L, taxes0[0].salesTaxId)
        assertEquals(5.0, taxes0[0].rate)
        assertEquals(25.0, taxes0[0].amount)
        assertEquals("CAD", taxes0[0].currency)
        assertEquals(21L, taxes0[1].salesTaxId)
        assertEquals(9.975, taxes0[1].rate)
        assertEquals(49.88, taxes0[1].amount)
        assertEquals("CAD", taxes0[1].currency)

        val taxes1 = invoiceTaxDao.findByInvoiceItem(items[1])
        assertEquals(2, taxes1.size)
        assertEquals(20L, taxes1[0].salesTaxId)
        assertEquals(5.0, taxes1[0].rate)
        assertEquals(15.0, taxes1[0].amount)
        assertEquals("CAD", taxes1[0].currency)
        assertEquals(21L, taxes1[1].salesTaxId)
        assertEquals(9.975, taxes1[1].rate)
        assertEquals(29.93, taxes1[1].amount)
        assertEquals("CAD", taxes1[1].currency)

        val logs = logDao.findByInvoice(invoice)
        assertEquals(1, logs.size)
        assertEquals(invoice.status, logs[0].status)
        assertEquals(null, logs[0].comment)
        assertEquals(USER_ID, logs[0].createdById)
    }

    @Test
    fun `customer in another country`() {
        val response = rest.postForEntity(
            "/v1/invoices",
            request.copy(shippingCityId = null, shippingCountry = "CM"),
            CreateInvoiceResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)

        val invoiceId = response.body!!.invoiceId
        val invoice = dao.findById(invoiceId).get()
        val seq = seqDao.findByTenantId(TENANT_ID)
        assertEquals(seq?.current, invoice.number)
        assertEquals(InvoiceStatus.DRAFT, invoice.status)
        assertEquals(request.taxId, invoice.taxId)
        assertEquals(request.orderId, invoice.orderId)
        assertEquals(request.description, invoice.description)
        assertEquals(request.customerAccountId, invoice.customerAccountId)
        assertEquals(request.customerName, invoice.customerName)
        assertEquals(request.customerEmail, invoice.customerEmail)
        assertEquals(request.customerPhone, invoice.customerPhone)
        assertEquals(request.customerMobile, invoice.customerMobile)
        assertEquals(0.00, invoice.totalTaxAmount)
        assertEquals(0.00, invoice.totalDiscountAmount)
        assertEquals(800.00, invoice.subTotalAmount)
        assertEquals(800.00, invoice.totalAmount)
        assertEquals(invoice.totalAmount, invoice.amountDue)
        assertEquals(0.00, invoice.amountPaid)
        assertEquals(request.currency, invoice.currency)
        assertEquals(null, invoice.shippingCityId)
        assertEquals(null, invoice.shippingStateId)
        assertEquals("CM", invoice.shippingCountry)
        assertEquals(request.shippingStreet, invoice.shippingStreet)
        assertEquals(request.shippingPostalCode, invoice.shippingPostalCode)
        assertEquals(request.billingCityId, invoice.billingCityId)
        assertEquals(100L, invoice.billingStateId)
        assertEquals("CA", invoice.billingCountry)
        assertEquals(request.billingStreet, invoice.billingStreet)
        assertEquals(request.billingPostalCode, invoice.billingPostalCode)
        assertEquals(USER_ID, invoice.createdById)
        assertEquals(USER_ID, invoice.modifiedById)
        assertEquals(fmt.format(request.dueAt), fmt.format(invoice.dueAt))

        val items = itemDao.findByInvoice(invoice)
        assertEquals(2, items.size)
        assertEquals(request.items[0].productId, items[0].productId)
        assertEquals(request.items[0].unitPriceId, items[0].unitPriceId)
        assertEquals(request.items[0].unitId, items[0].unitId)
        assertEquals(request.items[0].unitPrice, items[0].unitPrice)
        assertEquals(request.items[0].quantity, items[0].quantity)
        assertEquals(request.items[0].quantity * request.items[0].unitPrice, items[0].subTotal)
        assertEquals(request.items[0].description, items[0].description)
        assertEquals(request.items[1].productId, items[1].productId)
        assertEquals(request.items[1].unitPriceId, items[1].unitPriceId)
        assertEquals(request.items[1].unitId, items[1].unitId)
        assertEquals(request.items[1].unitPrice, items[1].unitPrice)
        assertEquals(request.items[1].quantity, items[1].quantity)
        assertEquals(request.items[1].quantity * request.items[1].unitPrice, items[1].subTotal)
        assertEquals(request.items[1].description, items[1].description)

        val taxes0 = invoiceTaxDao.findByInvoiceItem(items[0])
        assertEquals(0, taxes0.size)

        val taxes1 = invoiceTaxDao.findByInvoiceItem(items[1])
        assertEquals(0, taxes1.size)
    }

    @Test
    fun `customer in another state`() {
        val response = rest.postForEntity(
            "/v1/invoices",
            request.copy(shippingCityId = 210L),
            CreateInvoiceResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)

        val invoiceId = response.body!!.invoiceId
        val invoice = dao.findById(invoiceId).get()
        val seq = seqDao.findByTenantId(TENANT_ID)
        assertEquals(seq?.current, invoice.number)
        assertEquals(InvoiceStatus.DRAFT, invoice.status)
        assertEquals(request.taxId, invoice.taxId)
        assertEquals(request.orderId, invoice.orderId)
        assertEquals(request.description, invoice.description)
        assertEquals(request.customerAccountId, invoice.customerAccountId)
        assertEquals(request.customerName, invoice.customerName)
        assertEquals(request.customerEmail, invoice.customerEmail)
        assertEquals(request.customerPhone, invoice.customerPhone)
        assertEquals(request.customerMobile, invoice.customerMobile)
        assertEquals(40.00, invoice.totalTaxAmount)
        assertEquals(0.00, invoice.totalDiscountAmount)
        assertEquals(800.00, invoice.subTotalAmount)
        assertEquals(840.00, invoice.totalAmount)
        assertEquals(invoice.totalAmount, invoice.amountDue)
        assertEquals(0.00, invoice.amountPaid)
        assertEquals(request.currency, invoice.currency)
        assertEquals(210L, invoice.shippingCityId)
        assertEquals(200L, invoice.shippingStateId)
        assertEquals("CA", invoice.shippingCountry)
        assertEquals(request.shippingStreet, invoice.shippingStreet)
        assertEquals(request.shippingPostalCode, invoice.shippingPostalCode)
        assertEquals(request.billingCityId, invoice.billingCityId)
        assertEquals(100L, invoice.billingStateId)
        assertEquals("CA", invoice.billingCountry)
        assertEquals(request.billingStreet, invoice.billingStreet)
        assertEquals(request.billingPostalCode, invoice.billingPostalCode)
        assertEquals(USER_ID, invoice.createdById)
        assertEquals(USER_ID, invoice.modifiedById)
        assertEquals(fmt.format(request.dueAt), fmt.format(invoice.dueAt))

        val items = itemDao.findByInvoice(invoice)
        assertEquals(2, items.size)
        assertEquals(request.items[0].productId, items[0].productId)
        assertEquals(request.items[0].unitPriceId, items[0].unitPriceId)
        assertEquals(request.items[0].unitId, items[0].unitId)
        assertEquals(request.items[0].unitPrice, items[0].unitPrice)
        assertEquals(request.items[0].quantity, items[0].quantity)
        assertEquals(request.items[0].quantity * request.items[0].unitPrice, items[0].subTotal)
        assertEquals(request.items[0].description, items[0].description)
        assertEquals(request.items[1].productId, items[1].productId)
        assertEquals(request.items[1].unitPriceId, items[1].unitPriceId)
        assertEquals(request.items[1].unitId, items[1].unitId)
        assertEquals(request.items[1].unitPrice, items[1].unitPrice)
        assertEquals(request.items[1].quantity, items[1].quantity)
        assertEquals(request.items[1].quantity * request.items[1].unitPrice, items[1].subTotal)
        assertEquals(request.items[1].description, items[1].description)

        val taxes0 = invoiceTaxDao.findByInvoiceItem(items[0])
        assertEquals(1, taxes0.size)
        assertEquals(10L, taxes0[0].salesTaxId)
        assertEquals(5.0, taxes0[0].rate)
        assertEquals(25.0, taxes0[0].amount)
        assertEquals("CAD", taxes0[0].currency)

        val taxes1 = invoiceTaxDao.findByInvoiceItem(items[1])
        assertEquals(1, taxes1.size)
        assertEquals(10L, taxes1[0].salesTaxId)
        assertEquals(5.0, taxes1[0].rate)
        assertEquals(15.0, taxes1[0].amount)
        assertEquals("CAD", taxes1[0].currency)
    }

    @Test
    fun `create tax invoice`() {
        val response = rest.postForEntity("/v1/invoices", request.copy(taxId = 111L), CreateInvoiceResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val tax = taxDao.findById(111L).get()
        assertEquals(response.body!!.invoiceId, tax.invoiceId)
    }

    @Test
    fun `create with start number`() {
        // GIVEN
        val startNumber = 1000
        configService.save(
            request = SaveConfigurationRequest(
                values = mapOf(ConfigurationName.INVOICE_START_NUMBER to startNumber.toString())
            ),
            tenantId = TENANT_ID,
        )

        val lastNumber = seqDao.findByTenantId(TENANT_ID)?.current ?: 0

        // WHEN
        val response = rest.postForEntity("/v1/invoices", request, CreateInvoiceResponse::class.java)

        // THEN
        val invoiceId = response.body!!.invoiceId
        val invoice = dao.findById(invoiceId).get()
        assertEquals(lastNumber + 1 + startNumber, invoice.number)
    }

    @Test
    fun `create with invalid start number`() {
        // GIVEN
        val startNumber = "xxx"
        configService.save(
            request = SaveConfigurationRequest(
                values = mapOf(ConfigurationName.INVOICE_START_NUMBER to startNumber)
            ),
            tenantId = TENANT_ID,
        )

        val lastNumber = seqDao.findByTenantId(TENANT_ID)?.current ?: 0

        // WHEN
        val response = rest.postForEntity("/v1/invoices", request, CreateInvoiceResponse::class.java)

        // THEN
        val invoiceId = response.body!!.invoiceId
        val invoice = dao.findById(invoiceId).get()
        assertEquals(lastNumber + 1, invoice.number)
    }
}
