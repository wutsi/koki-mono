package com.wutsi.koki.portal.pub

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.error.dto.Parameter
import com.wutsi.koki.listing.dto.GetListingResponse
import com.wutsi.koki.listing.dto.SearchListingResponse
import com.wutsi.koki.platform.geoip.GeoIpService
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.platform.security.AccessTokenHolder
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.portal.pub.TenantFixtures.tenants
import com.wutsi.koki.refdata.dto.GetLocationResponse
import com.wutsi.koki.refdata.dto.SearchAmenityResponse
import com.wutsi.koki.refdata.dto.SearchCategoryResponse
import com.wutsi.koki.refdata.dto.SearchJuridictionResponse
import com.wutsi.koki.refdata.dto.SearchLocationResponse
import com.wutsi.koki.refdata.dto.SearchSalesTaxResponse
import com.wutsi.koki.security.dto.ApplicationName
import com.wutsi.koki.security.dto.JWTDecoder
import com.wutsi.koki.security.dto.JWTPrincipal
import com.wutsi.koki.tenant.dto.Configuration
import com.wutsi.koki.tenant.dto.CreateUserRequest
import com.wutsi.koki.tenant.dto.CreateUserResponse
import com.wutsi.koki.tenant.dto.GetTypeResponse
import com.wutsi.koki.tenant.dto.GetUserResponse
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import com.wutsi.koki.tenant.dto.SearchConfigurationResponse
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
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.time.Duration
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
        val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246"
    }

    @LocalServerPort
    protected val port: Int = 0

    @Value("\${koki.sdk.base-url}")
    protected lateinit var sdkBaseUrl: String

    protected lateinit var driver: WebDriver

    @MockitoBean
    protected lateinit var rest: RestTemplate

    @MockitoBean
    private lateinit var geoIpService: GeoIpService

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

    @MockitoBean
    protected lateinit var storageService: StorageService

    @MockitoBean
    protected lateinit var storageBuilder: StorageServiceBuilder

    @MockitoBean
    protected lateinit var publisher: Publisher

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
        setupStorage()
        setupDefaultApiResponses()
    }

    private fun setupSelenium() {
        val options = ChromeOptions()
        options.addArguments("--disable-web-security") // To prevent CORS issues
        options.addArguments("--lang=en")
        options.addArguments("--allowed-ips=")
        options.addArguments("--remote-allow-origins=*")
        options.addArguments("--user-agent=$USER_AGENT")
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

    private fun setupStorage() {
        doReturn(storageService).whenever(storageBuilder).build(any())
        doAnswer { invocation ->
            val output = invocation.getArgument<OutputStream>(1)
            IOUtils.copy(ByteArrayInputStream("Hello world".toByteArray()), output)
        }.whenever(storageService).get(any(), any())
    }

    private fun setupDefaultApiResponses() {
        setupRefDataModule()
        setupTenantModule()
        setupUserModule()
        setupListingModule()
    }

    private fun setupRefDataModule() {
        // Amenity
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

    protected fun setupTenantModule() {
        // Tenant
        doReturn(
            ResponseEntity(
                SearchTenantResponse(
                    tenants.map { tenant -> tenant.copy(clientPortalUrl = "http://localhost:$port") }
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
    }

    protected fun setupUserModule() {
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

        // Access Token
        doReturn(accessToken).whenever(accessTokenHolder).get()
        val principal = mock<JWTPrincipal>()
        doReturn(USER_ID).whenever(principal).getUserId()
        doReturn("Ray Sponsible").whenever(principal).name
        doReturn(ApplicationName.PORTAL).whenever(principal).getApplication()
        doReturn(principal).whenever(jwtDecoder).decode(any())
    }

    fun setupListingModule() {
        // Listing
        doReturn(
            ResponseEntity(
                SearchListingResponse(
                    listings = ListingFixtures.listings,
                    total = ListingFixtures.listings.size.toLong(),
                ),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchListingResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                GetListingResponse(ListingFixtures.listing),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetListingResponse::class.java)
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

    protected fun assertElementAttributePresent(selector: String, name: String) {
        assertNotNull(driver.findElement(By.cssSelector(selector)).getDomAttribute(name))
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

    protected fun scrollToBottom(selector: String? = null) {
        val js = driver as JavascriptExecutor
        if (selector == null) {
            js.executeScript("window.scrollBy(0,document.body.scrollHeight)")
        } else {
            js.executeScript("document.querySelector('$selector').scrollBy(0,document.body.scrollHeight)")
        }
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
}
