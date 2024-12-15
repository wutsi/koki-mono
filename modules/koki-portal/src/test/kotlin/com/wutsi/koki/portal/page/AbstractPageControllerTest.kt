package com.wutsi.blog.app.page

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.FileFixtures.files
import com.wutsi.koki.FormFixtures
import com.wutsi.koki.LogFixtures.logEntries
import com.wutsi.koki.LogFixtures.logEntry
import com.wutsi.koki.MessageFixtures
import com.wutsi.koki.RoleFixtures.roles
import com.wutsi.koki.ScriptFixtures
import com.wutsi.koki.UserFixtures.user
import com.wutsi.koki.UserFixtures.users
import com.wutsi.koki.WorkflowFixtures.activities
import com.wutsi.koki.WorkflowFixtures.activityInstance
import com.wutsi.koki.WorkflowFixtures.activityInstances
import com.wutsi.koki.WorkflowFixtures.workflow
import com.wutsi.koki.WorkflowFixtures.workflowInstance
import com.wutsi.koki.WorkflowFixtures.workflowInstances
import com.wutsi.koki.WorkflowFixtures.workflowPictureUrl
import com.wutsi.koki.WorkflowFixtures.workflows
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.error.dto.Parameter
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.form.dto.GetFormResponse
import com.wutsi.koki.form.dto.SearchFormResponse
import com.wutsi.koki.message.dto.GetMessageResponse
import com.wutsi.koki.message.dto.SearchMessageResponse
import com.wutsi.koki.portal.service.AccessTokenHolder
import com.wutsi.koki.script.dto.GetScriptResponse
import com.wutsi.koki.script.dto.RunScriptResponse
import com.wutsi.koki.script.dto.SearchScriptResponse
import com.wutsi.koki.sdk.KokiAuthentication
import com.wutsi.koki.sdk.KokiFiles
import com.wutsi.koki.sdk.KokiForms
import com.wutsi.koki.sdk.KokiLogs
import com.wutsi.koki.sdk.KokiMessages
import com.wutsi.koki.sdk.KokiScripts
import com.wutsi.koki.sdk.KokiTenant
import com.wutsi.koki.sdk.KokiUser
import com.wutsi.koki.sdk.KokiWorkflow
import com.wutsi.koki.sdk.KokiWorkflowInstance
import com.wutsi.koki.security.dto.JWTDecoder
import com.wutsi.koki.security.dto.JWTPrincipal
import com.wutsi.koki.tenant.dto.GetUserResponse
import com.wutsi.koki.tenant.dto.SearchConfigurationResponse
import com.wutsi.koki.tenant.dto.SearchRoleResponse
import com.wutsi.koki.tenant.dto.SearchUserResponse
import com.wutsi.koki.workflow.dto.GetActivityInstanceResponse
import com.wutsi.koki.workflow.dto.GetLogEntryResponse
import com.wutsi.koki.workflow.dto.GetWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.GetWorkflowResponse
import com.wutsi.koki.workflow.dto.SearchActivityInstanceResponse
import com.wutsi.koki.workflow.dto.SearchActivityResponse
import com.wutsi.koki.workflow.dto.SearchLogEntryResponse
import com.wutsi.koki.workflow.dto.SearchWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.SearchWorkflowResponse
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
import org.openqa.selenium.support.ui.Select
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatusCode
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.web.client.HttpClientErrorException
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractPageControllerTest {
    companion object {
        const val USER_ID = 11L
    }

    @LocalServerPort
    protected val port: Int = 0

    protected lateinit var driver: WebDriver

    @MockitoBean
    protected lateinit var kokiAuthentication: KokiAuthentication

    @MockitoBean
    protected lateinit var kokiFiles: KokiFiles

    @MockitoBean
    protected lateinit var kokiForms: KokiForms

    @MockitoBean
    protected lateinit var kokiLogs: KokiLogs

    @MockitoBean
    protected lateinit var kokiMessages: KokiMessages

    @MockitoBean
    protected lateinit var kokiScripts: KokiScripts

    @MockitoBean
    protected lateinit var kokiTenant: KokiTenant

    @MockitoBean
    protected lateinit var kokiUser: KokiUser

    @MockitoBean
    protected lateinit var kokiWorkflow: KokiWorkflow

    @MockitoBean
    protected lateinit var kokiWorkflowInstance: KokiWorkflowInstance

    @MockitoBean
    protected lateinit var accessTokenHolder: AccessTokenHolder

    @MockitoBean
    protected lateinit var jwtDecoder: JWTDecoder

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    protected val accessToken: String =
        "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJpc3MiOiJLb2tpIiwic3ViIjoiSGVydmUgVGNoZXBhbm5vdSIsInVzZXJJZCI6MjA0LCJ0ZW5hbnRJZCI6MSwiaWF0IjoxNzMxNTA5MDM0LCJleHAiOjE3MzE1OTU0MzR9."

    fun setUpAnonymousUser() {
        doReturn(null).whenever(accessTokenHolder).get(any())
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

        this.driver = ChromeDriver(options)
        if (System.getProperty("headless") == "true") { // In headless mode, set a size that will not require vertical scrolling
            driver.manage().window().size = Dimension(1920, 1280)
        }
    }

    private fun setupDefaultApiResponses() {
        setupRoles()
        setupFiles()
        setupForms()
        setupLogs()
        setupScripts()
        setupMessages()
        setupWorkflows()
        setupUsers()

        doReturn(SearchConfigurationResponse()).whenever(kokiTenant).configurations(anyOrNull(), anyOrNull())
    }

    private fun setupRoles() {
        doReturn(SearchRoleResponse(roles)).whenever(kokiUser)
            .roles(anyOrNull(), anyOrNull(), anyOrNull())
    }

    private fun setupFiles() {
        doReturn(SearchFileResponse(files)).whenever(kokiFiles)
            .files(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )
    }

    private fun setupForms() {
        doReturn(GetFormResponse(FormFixtures.form)).whenever(kokiForms).form(any())

        doReturn(SearchFormResponse(FormFixtures.forms)).whenever(kokiForms)
            .forms(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )
    }

    private fun setupScripts() {
        doReturn(GetScriptResponse(ScriptFixtures.script)).whenever(kokiScripts).script(any())

        doReturn(SearchScriptResponse(ScriptFixtures.scripts)).whenever(kokiScripts)
            .scripts(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        doReturn(
            RunScriptResponse(
                bindings = mapOf(
                    "return" to "122",
                    "var1" to "11"
                ),
                console = """
                Hello world
                Computing the results...
            """.trimIndent()
            )
        ).whenever(kokiScripts).run(any())
    }

    private fun setupMessages() {
        doReturn(GetMessageResponse(MessageFixtures.message)).whenever(kokiMessages).message(any())

        doReturn(SearchMessageResponse(MessageFixtures.messages)).whenever(kokiMessages)
            .messages(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )
    }

    private fun setupWorkflows() {
        doReturn(workflowPictureUrl).whenever(kokiWorkflow).imageUrl(any())
        doReturn(workflowPictureUrl).whenever(kokiWorkflowInstance).imageUrl(any())

        val json = getResourceAsString("/workflow-001.json")
        doReturn(json).whenever(kokiWorkflow).json(any())

        doReturn(GetWorkflowResponse(workflow)).whenever(kokiWorkflow).workflow(anyOrNull())
        doReturn(SearchWorkflowResponse(workflows)).whenever(kokiWorkflow)
            .workflows(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        doReturn(SearchActivityResponse(activities)).whenever(kokiWorkflow)
            .activities(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        doReturn(GetWorkflowInstanceResponse(workflowInstance)).whenever(kokiWorkflowInstance).workflow(any())
        doReturn(SearchWorkflowInstanceResponse(workflowInstances)).whenever(kokiWorkflowInstance)
            .workflows(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )

        doReturn(GetActivityInstanceResponse(activityInstance)).whenever(kokiWorkflowInstance)
            .activity(activityInstance.id)
        doReturn(SearchActivityInstanceResponse(activityInstances)).whenever(kokiWorkflowInstance)
            .activities(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
    }

    protected fun setupUsers() {
        doReturn(GetUserResponse(user)).whenever(kokiUser).user(USER_ID)
        doReturn(SearchUserResponse(users)).whenever(kokiUser)
            .users(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        doReturn(accessToken).whenever(accessTokenHolder).get(any())

        val principal = mock<JWTPrincipal>()
        doReturn(USER_ID).whenever(principal).getUserId()
        doReturn(USER_ID.toString()).whenever(principal).name
        doReturn(principal).whenever(jwtDecoder).decode(any())
    }

    private fun setupLogs() {
        doReturn(GetLogEntryResponse(logEntry)).whenever(kokiLogs).log(any())
        doReturn(SearchLogEntryResponse(logEntries)).whenever(kokiLogs)
            .logs(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
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
        assertEquals(page, driver.findElement(By.cssSelector("meta[name=wutsi\\:page_name]"))?.getAttribute("content"))
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
            assertNull(driver.findElement(By.cssSelector(selector)).getAttribute(name))
        } else {
            assertEquals(value, driver.findElement(By.cssSelector(selector)).getAttribute(name))
        }
    }

    protected fun assertElementAttributeNull(selector: String, name: String) {
        val value = driver.findElement(By.cssSelector(selector)).getAttribute(name)
        assertTrue(value.isNullOrEmpty())
    }

    protected fun assertElementAttributeStartsWith(selector: String, name: String, value: String) {
        assertEquals(true, driver.findElement(By.cssSelector(selector)).getAttribute(name)?.startsWith(value))
    }

    protected fun assertElementAttributeEndsWith(selector: String, name: String, value: String) {
        assertEquals(true, driver.findElement(By.cssSelector(selector)).getAttribute(name)?.endsWith(value))
    }

    protected fun assertElementAttributeContains(selector: String, name: String, value: String) {
        assertEquals(true, driver.findElement(By.cssSelector(selector)).getAttribute(name)?.contains(value))
    }

    protected fun assertElementHasClass(selector: String, value: String) {
        assertEquals(true, driver.findElement(By.cssSelector(selector)).getAttribute("class")?.contains(value))
    }

    protected fun assertElementHasNotClass(selector: String, value: String) {
        assertEquals(true, driver.findElement(By.cssSelector(selector)).getAttribute("class")?.contains(value))
    }

    protected fun click(selector: String, delayMillis: Long? = null) {
        driver.findElement(By.cssSelector(selector)).click()
        delayMillis?.let { Thread.sleep(delayMillis) }
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

    protected fun inputCodeMiror(code: String) {
        val cm = driver.findElement(By.cssSelector("div.CodeMirror"))
        val js = driver as JavascriptExecutor
        js.executeScript("arguments[0].CodeMirror.setValue('" + code + "');", cm)
    }

    protected fun select(selector: String, index: Int) {
        val by = By.cssSelector(selector)
        val select = Select(driver.findElement(by))
        select.selectByIndex(index)
    }
}
