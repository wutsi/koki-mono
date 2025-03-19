package com.wutsi.koki.payment.server.service.stripe

import com.stripe.param.checkout.SessionCreateParams.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StripeLocaleTranslatorTest {
    val translator = StripeLocaleTranslator()

    @Test
    fun `language - country`() {
        assertEquals(Locale.FR_CA, translator.translate("fr_CA"))
    }

    @Test
    fun language() {
        assertEquals(Locale.FR, translator.translate("fr"))
    }

    @Test
    fun `fallback to language`() {
        assertEquals(Locale.FR, translator.translate("fr_CM"))
    }

    @Test
    fun `not supported`() {
        assertNull(translator.translate("xxx"))
    }

    @Test
    fun `null`() {
        assertNull(translator.translate(null))
    }

    @Test
    fun empty() {
        assertNull(translator.translate(""))
    }
}
