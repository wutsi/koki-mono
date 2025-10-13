package com.wutsi.koki.portal.share.page

import com.wutsi.koki.portal.AbstractPageControllerTest
import java.net.URLEncoder
import kotlin.test.Test
import kotlin.test.assertEquals

class ShareModalControllerTest : AbstractPageControllerTest() {
    private val url = "https://www.google.ca"

    @Test
    fun whatsapp() {
        navigateTo("/share/modal?test-mode=true&url=" + URLEncoder.encode(url, "utf-8"))

        input("#phone", "5147580191")
        input("#message", "Hello")
        click("button[type=submit]")

        val windowHandles = driver.getWindowHandles().toList()
        driver.switchTo().window(windowHandles[1])
        assertEquals(true, driver.currentUrl?.contains("whatsapp.com"))
    }

    @Test
    fun twitter() {
        navigateTo("/share/modal?test-mode=true&url=" + URLEncoder.encode(url, "utf-8"))

        click("#btn-share-twitter")

        val windowHandles = driver.getWindowHandles().toList()
        driver.switchTo().window(windowHandles[1])
        assertEquals(true, driver.currentUrl?.contains("x.com"))
    }

    @Test
    fun facebook() {
        navigateTo("/share/modal?test-mode=true&url=" + URLEncoder.encode(url, "utf-8"))

        click("#btn-share-facebook")

        val windowHandles = driver.getWindowHandles().toList()
        driver.switchTo().window(windowHandles[1])
        assertEquals(true, driver.currentUrl?.contains("facebook.com"))
    }

    @Test
    fun email() {
        navigateTo("/share/modal?test-mode=true&url=" + URLEncoder.encode(url, "utf-8"))

        click("#btn-share-email")
    }
}
