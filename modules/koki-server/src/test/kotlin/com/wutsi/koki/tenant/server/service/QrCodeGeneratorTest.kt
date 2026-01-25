package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.tenant.server.domain.TenantEntity
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class QrCodeGeneratorTest {
    private val generator = QrCodeGenerator()

    @Test
    fun `generate without logo`() {
        val file = kotlin.io.path.createTempFile(suffix = ".png").toFile()
        file.outputStream().use { os ->
            generator.generate("https://www.google.com", TenantEntity(), os)
        }
        println("File: >>> ${file.absolutePath}")
        assertEquals(true, file.length() > 10000)
    }

    @Test
    fun `generate with logo`() {
        val tenant = TenantEntity(
            id = 1L,
            qrCodeIconUrl = "https://picsum.photos/150/150",
        )

        val file = kotlin.io.path.createTempFile(suffix = ".png").toFile()
        file.outputStream().use { os ->
            generator.generate("https://www.google.com", tenant, os)
        }
        println("File: >>> ${file.absolutePath}")
        assertEquals(true, file.length() > 10000)
    }
}
