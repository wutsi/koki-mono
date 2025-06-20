package com.wutsi.koki.portal

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AccountFixtures
import com.wutsi.koki.ContactFixtures
import com.wutsi.koki.EmailFixtures
import com.wutsi.koki.FileFixtures
import com.wutsi.koki.InvoiceFixtures
import com.wutsi.koki.MessageFixtures
import com.wutsi.koki.ModuleFixtures
import com.wutsi.koki.NoteFixtures
import com.wutsi.koki.PaymentFixtures
import com.wutsi.koki.ProductFixtures
import com.wutsi.koki.RefDataFixtures
import com.wutsi.koki.RoleFixtures
import com.wutsi.koki.RoomFixtures
import com.wutsi.koki.TenantFixtures
import com.wutsi.koki.UserFixtures
import com.wutsi.koki.account.dto.CreateAccountRequest
import com.wutsi.koki.account.dto.CreateAccountResponse
import com.wutsi.koki.account.dto.GetAccountResponse
import com.wutsi.koki.account.dto.GetAttributeResponse
import com.wutsi.koki.account.dto.SearchAccountResponse
import com.wutsi.koki.account.dto.SearchAttributeResponse
import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.contact.dto.CreateContactRequest
import com.wutsi.koki.contact.dto.CreateContactResponse
import com.wutsi.koki.contact.dto.GetContactResponse
import com.wutsi.koki.contact.dto.SearchContactResponse
import com.wutsi.koki.email.dto.GetEmailResponse
import com.wutsi.koki.email.dto.SearchEmailResponse
import com.wutsi.koki.email.dto.SendEmailRequest
import com.wutsi.koki.email.dto.SendEmailResponse
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.error.dto.Parameter
import com.wutsi.koki.file.dto.GetFileResponse
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.invoice.dto.CreateInvoiceRequest
import com.wutsi.koki.invoice.dto.CreateInvoiceResponse
import com.wutsi.koki.invoice.dto.GetInvoiceResponse
import com.wutsi.koki.invoice.dto.SearchInvoiceResponse
import com.wutsi.koki.message.dto.GetMessageResponse
import com.wutsi.koki.message.dto.SearchMessageResponse
import com.wutsi.koki.message.dto.SendMessageRequest
import com.wutsi.koki.message.dto.SendMessageResponse
import com.wutsi.koki.module.dto.SearchModuleResponse
import com.wutsi.koki.module.dto.SearchPermissionResponse
import com.wutsi.koki.note.dto.CreateNoteRequest
import com.wutsi.koki.note.dto.CreateNoteResponse
import com.wutsi.koki.note.dto.GetNoteResponse
import com.wutsi.koki.note.dto.SearchNoteResponse
import com.wutsi.koki.payment.dto.CreateCashPaymentRequest
import com.wutsi.koki.payment.dto.CreateCheckPaymentRequest
import com.wutsi.koki.payment.dto.CreateInteracPaymentRequest
import com.wutsi.koki.payment.dto.CreatePaymentResponse
import com.wutsi.koki.payment.dto.GetTransactionResponse
import com.wutsi.koki.payment.dto.SearchTransactionResponse
import com.wutsi.koki.platform.security.AccessTokenHolder
import com.wutsi.koki.product.dto.CreatePriceRequest
import com.wutsi.koki.product.dto.CreatePriceResponse
import com.wutsi.koki.product.dto.CreateProductRequest
import com.wutsi.koki.product.dto.CreateProductResponse
import com.wutsi.koki.product.dto.GetPriceResponse
import com.wutsi.koki.product.dto.GetProductResponse
import com.wutsi.koki.product.dto.SearchPriceResponse
import com.wutsi.koki.product.dto.SearchProductResponse
import com.wutsi.koki.refdata.dto.GetLocationResponse
import com.wutsi.koki.refdata.dto.SearchAmenityResponse
import com.wutsi.koki.refdata.dto.SearchCategoryResponse
import com.wutsi.koki.refdata.dto.SearchJuridictionResponse
import com.wutsi.koki.refdata.dto.SearchLocationResponse
import com.wutsi.koki.refdata.dto.SearchSalesTaxResponse
import com.wutsi.koki.refdata.dto.SearchUnitResponse
import com.wutsi.koki.room.dto.CreateRoomRequest
import com.wutsi.koki.room.dto.CreateRoomResponse
import com.wutsi.koki.room.dto.CreateRoomUnitRequest
import com.wutsi.koki.room.dto.CreateRoomUnitResponse
import com.wutsi.koki.room.dto.GetRoomResponse
import com.wutsi.koki.room.dto.GetRoomUnitResponse
import com.wutsi.koki.room.dto.SearchRoomResponse
import com.wutsi.koki.room.dto.SearchRoomUnitResponse
import com.wutsi.koki.security.dto.JWTDecoder
import com.wutsi.koki.security.dto.JWTPrincipal
import com.wutsi.koki.tenant.dto.Configuration
import com.wutsi.koki.tenant.dto.CreateRoleRequest
import com.wutsi.koki.tenant.dto.CreateRoleResponse
import com.wutsi.koki.tenant.dto.CreateUserRequest
import com.wutsi.koki.tenant.dto.CreateUserResponse
import com.wutsi.koki.tenant.dto.GetBusinessResponse
import com.wutsi.koki.tenant.dto.GetTypeResponse
import com.wutsi.koki.tenant.dto.GetUserResponse
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import com.wutsi.koki.tenant.dto.SearchConfigurationResponse
import com.wutsi.koki.tenant.dto.SearchRoleResponse
import com.wutsi.koki.tenant.dto.SearchTenantResponse
import com.wutsi.koki.tenant.dto.SearchTypeResponse
import com.wutsi.koki.tenant.dto.SearchUserResponse
import io.eotsevych.select2.Select2
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.time.Duration
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractPageControllerTest {
    companion object {
        val USER_ID = UserFixtures.USER_ID
    }

    @LocalServerPort
    protected val port: Int = 0

    @Value("\${koki.sdk.base-url}")
    protected lateinit var sdkBaseUrl: String

    protected lateinit var driver: WebDriver

    @MockitoBean
    protected lateinit var rest: RestTemplate

    @MockitoBean
    @Qualifier("RestWithoutTenantHeader")
    protected lateinit var restWithoutTenantHeader: RestTemplate

    @MockitoBean
    @Qualifier("RestForAuthentication")
    protected lateinit var restForAuthentication: RestTemplate

    @MockitoBean
    protected lateinit var accessTokenHolder: AccessTokenHolder

    @MockitoBean
    protected lateinit var jwtDecoder: JWTDecoder

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    protected val accessToken: String =
        "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJpc3MiOiJLb2tpIiwic3ViIjoiSGVydmUgVGNoZXBhbm5vdSIsInVzZXJJZCI6MjA0LCJ0ZW5hbnRJZCI6MSwiaWF0IjoxNzMxNTA5MDM0LCJleHAiOjE3MzE1OTU0MzR9."

    protected val downloadDir = java.io.File(System.getProperty("user.home") + "/__wutsi/selenuim/downloads")

    fun setUpAnonymousUser() {
        doReturn(null).whenever(accessTokenHolder).get()
    }

    fun getResourceAsString(path: String): String {
        val out = ByteArrayOutputStream()
        IOUtils.copy(
            AbstractPageControllerTest::class.java.getResourceAsStream(path),
            out
        )
        return out.toString(Charsets.UTF_8)
    }

    @BeforeEach
    fun setUp() {
        setupSelenium()
        setupDefaultApiResponses()
    }

    private fun setupSelenium() {
        val options = ChromeOptions()
        options.addArguments("--disable-web-security") // To prevent CORS issues
        options.addArguments("--lang=en")
        options.addArguments("--allowed-ips=")
        options.addArguments("--remote-allow-origins=*")
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246")
        if (System.getProperty("headless") == "true") {
            options.addArguments("--headless")
            options.addArguments("--no-sandbox")
            options.addArguments("--disable-dev-shm-usage")
        }
        options.setEnableDownloads(true)

        downloadDir.mkdirs()
        val prefs = mapOf(
            "download.default_directory" to downloadDir.absolutePath
        )
        options.setExperimentalOption("prefs", prefs)

        this.driver = ChromeDriver(options)
        if (System.getProperty("headless") == "true") { // In headless mode, set a size that will not require vertical scrolling
            driver.manage().window().size = Dimension(1920, 1280)
        }
    }

    private fun setupDefaultApiResponses() {
        setupRefDataModule()
        setupFileUploads()
        setupModuleModule()
        setupTenantModule()
        setupFileModule()
        setupEmailModule()
        setupMessageModule()
        setupNoteModule()
        setupAccountModule()
        setupContactModule()
        setupProductModule()
        setupInvoiceModule()
        setupPaymentModule()
        setupRoomModule()
    }

    protected fun setupFileUploads() {
        doReturn(
            ResponseEntity(
                ImportResponse(
                    added = 4,
                    updated = 5,
                    errors = 3,
                    errorMessages = listOf(
                        ImportMessage(location = "1", code = "INVALID_NAME"),
                        ImportMessage(location = "2", code = "INVALID_ADDRESS", "LocationModel is not valid"),
                    )
                ),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .exchange(
                any<String>(),
                any<HttpMethod>(),
                any(),
                any<Class<ImportResponse>>(),
            )
    }

    protected fun setupModuleModule() {
        // Modules
        doReturn(
            ResponseEntity(
                SearchModuleResponse(ModuleFixtures.modules),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(SearchModuleResponse::class.java)
            )

        // Permissions
        doReturn(
            ResponseEntity(
                SearchPermissionResponse(ModuleFixtures.permissions),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(SearchPermissionResponse::class.java)
            )
    }

    protected fun setupTenantModule() {
        // Tenant
        doReturn(
            ResponseEntity(
                SearchTenantResponse(
                    TenantFixtures.tenants.map { tenant -> tenant.copy(portalUrl = "http://localhost:$port") }
                ),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(SearchTenantResponse::class.java)
            )

        // Configuration
        doReturn(
            ResponseEntity(
                SearchConfigurationResponse(
                    configurations = TenantFixtures.config.map { cfg ->
                        Configuration(
                            name = cfg.key,
                            value = cfg.value
                        )
                    }
                ),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchConfigurationResponse::class.java)
            )

        // Types
        doReturn(
            ResponseEntity(
                SearchTypeResponse(TenantFixtures.types),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchTypeResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                GetTypeResponse(TenantFixtures.type),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetTypeResponse::class.java)
            )

        // Roles
        doReturn(
            ResponseEntity(
                SearchRoleResponse(RoleFixtures.roles),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchRoleResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                CreateRoleResponse(111L),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .postForEntity(
                any<String>(),
                any<CreateRoleRequest>(),
                eq(CreateRoleResponse::class.java)
            )

        // Users
        doReturn(
            ResponseEntity(
                GetUserResponse(UserFixtures.user),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetUserResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                SearchUserResponse(UserFixtures.users),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchUserResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                CreateUserResponse(777L),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .postForEntity(
                any<String>(),
                any<CreateUserRequest>(),
                eq(CreateUserResponse::class.java)
            )

        // Business
        doReturn(
            ResponseEntity(
                GetBusinessResponse(TenantFixtures.business),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetBusinessResponse::class.java)
            )

        // Access Token
        doReturn(accessToken).whenever(accessTokenHolder).get()
        val principal = mock<JWTPrincipal>()
        doReturn(USER_ID).whenever(principal).getUserId()
        doReturn(USER_ID.toString()).whenever(principal).name
        doReturn(principal).whenever(jwtDecoder).decode(any())
    }

    protected fun setupUserWithoutPermissions(names: List<String>) {
        val xpermissions = ModuleFixtures.permissions
            .filter { permission -> !names.contains(permission.name) }
            .filter { permission -> !permission.name.endsWith(":full_access") }
        val xrole = RoleFixtures.role.copy(permissionIds = xpermissions.map { permission -> permission.id })
        doReturn(
            ResponseEntity(
                SearchRoleResponse(listOf(xrole)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchRoleResponse::class.java)
            )

        val xuser = UserFixtures.user.copy(roleIds = listOf(xrole.id))
        doReturn(
            ResponseEntity(
                GetUserResponse(xuser),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetUserResponse::class.java)
            )
    }

    protected fun setupUserWithFullAccessPermissions(module: String) {
        val permissionId = ModuleFixtures.permissions
            .find { permission -> permission.name == "$module:full_access" }
            ?.id
            ?: -1
        val xrole = RoleFixtures.role.copy(permissionIds = listOf(permissionId))
        doReturn(
            ResponseEntity(
                SearchRoleResponse(listOf(xrole)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchRoleResponse::class.java)
            )

        val xuser = UserFixtures.user.copy(roleIds = listOf(xrole.id))
        doReturn(
            ResponseEntity(
                GetUserResponse(xuser),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetUserResponse::class.java)
            )
    }

    private fun setupFileModule() {
        doReturn(
            ResponseEntity(
                SearchFileResponse(FileFixtures.files),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchFileResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                GetFileResponse(FileFixtures.file),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetFileResponse::class.java)
            )
    }

    private fun setupEmailModule() {
        doReturn(
            ResponseEntity(
                SearchEmailResponse(EmailFixtures.emails),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchEmailResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                GetEmailResponse(EmailFixtures.email),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetEmailResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                SendEmailResponse(EmailFixtures.NEW_EMAIL_ID),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .postForEntity(
                any<String>(),
                any<SendEmailRequest>(),
                eq(SendEmailResponse::class.java)
            )
    }

    private fun setupMessageModule() {
        doReturn(
            ResponseEntity(
                SearchMessageResponse(MessageFixtures.messages),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchMessageResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                GetMessageResponse(MessageFixtures.message),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetMessageResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                SendMessageResponse(MessageFixtures.NEW_ID),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .postForEntity(
                any<String>(),
                any<SendMessageRequest>(),
                eq(SendMessageResponse::class.java)
            )
    }

    private fun setupNoteModule() {
        doReturn(
            ResponseEntity(
                SearchNoteResponse(NoteFixtures.notes),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchNoteResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                GetNoteResponse(NoteFixtures.note),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetNoteResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                CreateNoteResponse(NoteFixtures.NEW_NOTE_ID),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .postForEntity(
                any<String>(),
                any<CreateNoteRequest>(),
                eq(CreateNoteResponse::class.java)
            )
    }

    private fun setupAccountModule() {
        // Attributes
        doReturn(
            ResponseEntity(
                SearchAttributeResponse(AccountFixtures.attributes),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchAttributeResponse::class.java)
            )
        doReturn(
            ResponseEntity(
                GetAttributeResponse(AccountFixtures.attribute),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetAttributeResponse::class.java)
            )

        // Accounts
        doReturn(
            ResponseEntity(
                SearchAccountResponse(AccountFixtures.accounts),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchAccountResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                GetAccountResponse(AccountFixtures.account),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetAccountResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                CreateAccountResponse(AccountFixtures.NEW_ACCOUNT_ID),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .postForEntity(
                any<String>(),
                any<CreateAccountRequest>(),
                eq(CreateAccountResponse::class.java)
            )
    }

    private fun setupRefDataModule() {
        // Units
        doReturn(
            ResponseEntity(
                SearchAmenityResponse(RefDataFixtures.amenities),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(SearchAmenityResponse::class.java)
            )

        // Units
        doReturn(
            ResponseEntity(
                SearchUnitResponse(RefDataFixtures.units),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(SearchUnitResponse::class.java)
            )

        // Location
        doReturn(
            ResponseEntity(
                SearchLocationResponse(RefDataFixtures.locations),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(SearchLocationResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                GetLocationResponse(RefDataFixtures.locations[0]),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(GetLocationResponse::class.java)
            )

        // Categories
        doReturn(
            ResponseEntity(
                SearchCategoryResponse(RefDataFixtures.categories),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(SearchCategoryResponse::class.java)
            )

        // Juridiction
        doReturn(
            ResponseEntity(
                SearchJuridictionResponse(RefDataFixtures.juridictions),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(SearchJuridictionResponse::class.java)
            )

        // Sales Tax
        doReturn(
            ResponseEntity(
                SearchSalesTaxResponse(RefDataFixtures.salesTaxes),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(SearchSalesTaxResponse::class.java)
            )
    }

    private fun setupProductModule() {
        // Products
        doReturn(
            ResponseEntity(
                SearchProductResponse(ProductFixtures.products),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchProductResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                GetProductResponse(ProductFixtures.product),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetProductResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                CreateProductResponse(555L),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .postForEntity(
                any<String>(),
                any<CreateProductRequest>(),
                eq(CreateProductResponse::class.java)
            )

        // Prices
        doReturn(
            ResponseEntity(
                SearchPriceResponse(ProductFixtures.prices),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchPriceResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                GetPriceResponse(ProductFixtures.price),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetPriceResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                CreatePriceResponse(777L),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .postForEntity(
                any<String>(),
                any<CreatePriceRequest>(),
                eq(CreatePriceResponse::class.java)
            )
    }

    private fun setupContactModule() {
        // Contacts
        doReturn(
            ResponseEntity(
                SearchContactResponse(ContactFixtures.contacts),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchContactResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                GetContactResponse(ContactFixtures.contact),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetContactResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                CreateContactResponse(ContactFixtures.NEW_CONTACT_ID),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .postForEntity(
                any<String>(),
                any<CreateContactRequest>(),
                eq(CreateContactResponse::class.java)
            )
    }

    fun setupInvoiceModule() {
        // Invoice
        doReturn(
            ResponseEntity(
                SearchInvoiceResponse(InvoiceFixtures.invoices),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchInvoiceResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                GetInvoiceResponse(InvoiceFixtures.invoice),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetInvoiceResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                CreateInvoiceResponse(3333L),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .postForEntity(
                any<String>(),
                any<CreateInvoiceRequest>(),
                eq(CreateInvoiceResponse::class.java)
            )
    }

    fun setupPaymentModule() {
        doReturn(
            ResponseEntity(
                SearchTransactionResponse(PaymentFixtures.transactions),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchTransactionResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                GetTransactionResponse(PaymentFixtures.transaction),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetTransactionResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                CreatePaymentResponse(UUID.randomUUID().toString()),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .postForEntity(
                any<String>(),
                any<CreateCashPaymentRequest>(),
                eq(CreatePaymentResponse::class.java)
            )
        doReturn(
            ResponseEntity(
                CreatePaymentResponse(UUID.randomUUID().toString()),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .postForEntity(
                any<String>(),
                any<CreateCheckPaymentRequest>(),
                eq(CreatePaymentResponse::class.java)
            )
        doReturn(
            ResponseEntity(
                CreatePaymentResponse(UUID.randomUUID().toString()),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .postForEntity(
                any<String>(),
                any<CreateInteracPaymentRequest>(),
                eq(CreatePaymentResponse::class.java)
            )
    }

    fun setupRoomModule() {
        // Room
        doReturn(
            ResponseEntity(
                SearchRoomResponse(RoomFixtures.rooms),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchRoomResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                GetRoomResponse(RoomFixtures.room),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetRoomResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                CreateRoomResponse(1111),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .postForEntity(
                any<String>(),
                any<CreateRoomRequest>(),
                eq(CreateRoomResponse::class.java)
            )

        // RoomUnit
        doReturn(
            ResponseEntity(
                SearchRoomUnitResponse(RoomFixtures.roomUnits),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchRoomUnitResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                GetRoomUnitResponse(RoomFixtures.roomUnit),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetRoomUnitResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                CreateRoomUnitResponse(1111),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .postForEntity(
                any<String>(),
                any<CreateRoomUnitRequest>(),
                eq(CreateRoomUnitResponse::class.java)
            )
    }

    @AfterEach
    @Throws(Exception::class)
    fun tearDown() {
        driver.quit()
    }

    protected fun navigateTo(path: String) {
        driver.get("http://localhost:$port$path")
    }

    protected fun createHttpClientErrorException(
        statusCode: Int,
        errorCode: String,
        message: String? = null,
        param: String? = null,
        data: Map<String, Any>? = null,
    ): HttpClientErrorException {
        val charset = Charset.defaultCharset()
        val response = ErrorResponse(
            error = Error(
                code = errorCode,
                message = message,
                parameter = param?.let { Parameter(value = param) },
                data = data
            ),
        )
        return HttpClientErrorException(
            HttpStatusCode.valueOf(statusCode),
            "Error",
            objectMapper.writeValueAsString(response).toByteArray(charset),
            charset
        )
    }

    protected fun assertCurrentPageIs(page: String) {
        assertEquals(
            page,
            driver.findElement(By.cssSelector("meta[name=wutsi\\:page_name]")).getDomAttribute("content")
        )
    }

    protected fun assertElementNotPresent(selector: String) {
        assertTrue(driver.findElements(By.cssSelector(selector)).size == 0)
    }

    protected fun assertElementPresent(selector: String) {
        assertTrue(driver.findElements(By.cssSelector(selector)).size > 0)
    }

    protected fun assertElementText(selector: String, text: String?) {
        assertEquals(text, driver.findElement(By.cssSelector(selector)).text)
    }

    protected fun assertElementTextContains(selector: String, text: String) {
        assertTrue(driver.findElement(By.cssSelector(selector)).text.contains(text))
    }

    protected fun assertElementCount(selector: String, count: Int) {
        assertEquals(count, driver.findElements(By.cssSelector(selector)).size)
    }

    protected fun assertElementNotVisible(selector: String) {
        assertEquals("none", driver.findElement(By.cssSelector(selector)).getCssValue("display"))
    }

    protected fun assertElementVisible(selector: String) {
        assertFalse("none".equals(driver.findElement(By.cssSelector(selector)).getCssValue("display")))
    }

    protected fun assertElementAttribute(selector: String, name: String, value: String?) {
        if (value == null) {
            assertNull(driver.findElement(By.cssSelector(selector)).getDomAttribute(name))
        } else {
            assertEquals(value, driver.findElement(By.cssSelector(selector)).getDomAttribute(name))
        }
    }

    protected fun assertElementHasAttribute(selector: String, name: String) {
        assertNotNull(driver.findElement(By.cssSelector(selector)).getDomAttribute(name))
    }

    protected fun assertElementHasNotAttribute(selector: String, name: String) {
        assertNull(driver.findElement(By.cssSelector(selector)).getDomAttribute(name)?.ifEmpty { null })
    }

    protected fun assertSelectValue(selector: String, value: String) {
        val elt = driver.findElement(By.cssSelector(selector))
        val select = Select(elt)
        assertEquals(value, select.firstSelectedOption.getDomAttribute("value"))
    }

    protected fun waitForPresenceOf(selector: String, timeout: Long = 30, sleep: Long = 1) {
        val wait = WebDriverWait(driver, Duration.ofSeconds(timeout), Duration.ofSeconds(sleep))
        wait.until(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector))
        )
    }

    protected fun assertElementAttributeNull(selector: String, name: String) {
        val value = driver.findElement(By.cssSelector(selector)).getDomAttribute(name)
        assertTrue(value.isNullOrEmpty())
    }

    protected fun assertElementAttributeStartsWith(selector: String, name: String, value: String) {
        assertEquals(true, driver.findElement(By.cssSelector(selector)).getDomAttribute(name)?.startsWith(value))
    }

    protected fun assertElementAttributeEndsWith(selector: String, name: String, value: String) {
        assertEquals(true, driver.findElement(By.cssSelector(selector)).getDomAttribute(name)?.endsWith(value))
    }

    protected fun assertElementAttributeEquals(selector: String, name: String, value: String) {
        assertEquals(value, driver.findElement(By.cssSelector(selector)).getDomAttribute(name))
    }

    protected fun assertElementAttributeContains(selector: String, name: String, value: String) {
        assertEquals(true, driver.findElement(By.cssSelector(selector)).getDomAttribute(name)?.contains(value))
    }

    protected fun assertElementHasClass(selector: String, value: String) {
        assertEquals(true, driver.findElement(By.cssSelector(selector)).getDomAttribute("class")?.contains(value))
    }

    protected fun assertElementHasNotClass(selector: String, value: String) {
        assertEquals(true, driver.findElement(By.cssSelector(selector)).getDomAttribute("class")?.contains(value))
    }

    protected fun click(selector: String, delayMillis: Long? = 1000) {
        driver.findElement(By.cssSelector(selector)).click()
        delayMillis?.let { Thread.sleep(delayMillis) }
    }

    protected fun scrollToElement(selector: String) {
        val element = driver.findElement(By.cssSelector(selector))
        val actions = Actions(driver)
        actions.moveToElement(element)
        actions.perform()
    }

    protected fun scrollToBottom() {
        val js = driver as JavascriptExecutor
        js.executeScript("window.scrollBy(0,document.body.scrollHeight)")
        Thread.sleep(1000)
    }

    protected fun scrollToMiddle() {
        val js = driver as JavascriptExecutor
        js.executeScript("window.scrollBy(0,document.body.scrollHeight/2)")
        Thread.sleep(1000)
    }

    protected fun scroll(percent: Double) {
        val js = driver as JavascriptExecutor
        js.executeScript("window.scrollBy(0,document.body.scrollHeight*$percent)")
        Thread.sleep(1000)
    }

    protected fun input(selector: String, value: String) {
        val by = By.cssSelector(selector)
        driver.findElement(by).clear()
        driver.findElement(by).sendKeys(value)
    }

    protected fun inputCodeMirror(code: String) {
        val cm = driver.findElement(By.cssSelector("div.CodeMirror"))
        val js = driver as JavascriptExecutor
        js.executeScript("arguments[0].CodeMirror.setValue('" + code + "');", cm)
    }

    protected fun select(selector: String, index: Int) {
        val by = By.cssSelector(selector)
        val select = Select(driver.findElement(by))
        select.selectByIndex(index)
    }

    protected fun selectByValue(selector: String, value: String) {
        val by = By.cssSelector(selector)
        val select = Select(driver.findElement(by))
        select.selectByValue(value)
    }

    protected fun select2(selector: String, text: String) {
        val by = By.cssSelector(selector)
        val select = Select2(driver.findElement(by))
        select.selectByText(text)
    }

    protected fun assertConfig(expectedValue: String, name: String) {
        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals(expectedValue, request.firstValue.values[name])
    }

    protected fun disableConfig(name: String) {
        disableConfig(listOf(name))
    }

    protected fun disableConfig(names: List<String>) {
        doReturn(
            ResponseEntity(
                SearchConfigurationResponse(
                    configurations = TenantFixtures.config
                        .filter { cfg -> !names.contains(cfg.key) }
                        .map { cfg ->
                            Configuration(
                                name = cfg.key,
                                value = cfg.value
                            )
                        }
                ),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchConfigurationResponse::class.java)
            )
    }

    protected fun disableAllConfigs() {
        doReturn(
            ResponseEntity(
                SearchConfigurationResponse(),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchConfigurationResponse::class.java)
            )
    }
}
