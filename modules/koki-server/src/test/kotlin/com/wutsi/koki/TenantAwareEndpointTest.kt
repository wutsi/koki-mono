package com.wutsi.koki

import com.amazonaws.util.IOUtils
import com.wutsi.koki.common.dto.HttpHeader
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.test.annotation.DirtiesContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.test.assertEquals

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class TenantAwareEndpointTest : ClientHttpRequestInterceptor {
    companion object {
        const val TENANT_ID = 1L
    }

    @Autowired
    protected lateinit var rest: TestRestTemplate

    protected var ignoreTenantIdHeader: Boolean = false

    protected open fun getTenantId() = TENANT_ID

    private val folder = File(File(System.getProperty("user.home")), "__wutsi")

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        if (!ignoreTenantIdHeader) {
            request.headers.add(HttpHeader.TENANT_ID, getTenantId().toString())
        } else {
            request.headers.remove(HttpHeader.TENANT_ID)
        }
        return execution.execute(request, body)
    }

    @BeforeEach
    fun setUp() {
        ignoreTenantIdHeader = false
        rest.restTemplate.interceptors.add(this)
    }

    protected fun download(
        u: String,
        expectedStatusCode: Int,
        expectedFileName: String?,
        expectedContentType: String,
        accessToken: String? = null,
    ): File? {
        val url = URL(u)
        val cnn = url.openConnection() as HttpURLConnection
        try {
            if (accessToken != null) {
                cnn.setRequestProperty("Authorization", "Bearer $accessToken")
            }
            cnn.connect()

            assertEquals(expectedStatusCode, cnn.responseCode)

            if (expectedStatusCode == 200) {
                assertEquals(expectedContentType, cnn.contentType)
                assertEquals("attachment; filename=\"$expectedFileName\"", cnn.getHeaderField("Content-Disposition"))

                val file = File(folder, expectedFileName ?: "")
                val output = FileOutputStream(file)
                output.use {
                    IOUtils.copy(cnn.inputStream, output)
                }
                return file
            } else {
                return null
            }
        } finally {
            cnn.disconnect()
        }
    }
}
