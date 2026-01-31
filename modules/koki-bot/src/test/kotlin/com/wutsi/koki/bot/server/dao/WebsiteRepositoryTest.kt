package com.wutsi.koki.bot.server.dao

import tools.jackson.databind.json.JsonMapper
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WebsiteRepositoryTest {
    private val jsonMapper = JsonMapper()
    private val dao = WebsiteRepository(jsonMapper)

    @Test
    fun findAll() {
        val websites = dao.findAll()
        assert(websites.isNotEmpty())
    }

    @Test
    fun findByName() {
        val website = dao.findByName("ereshomes.com")
        assertNotNull(website)
        assertEquals("ereshomes.com", website.name)
        assertEquals("https://www.ereshomes.com", website.baseUrl)
        assertEquals(listOf("/property-details/"), website.listingUrlPrefixes)
        assertEquals(
            ".sp-lg-title, .price, .ps-widget:nth-child(2) h4:first-child, .ps-widget:nth-child(2) p.text-justify, .ps-widget:nth-child(3) h4, .ps-widget:nth-child(3) p.text, .ps-widget:nth-child(4) h4, .ps-widget:nth-child(4) p.text",
            website.contentSelector
        )
        assertEquals(".sp-img-content img", website.imageSelector)
        assertTrue(website.homeUrls.contains("https://www.ereshomes.com/realestateforrent"))
        assertTrue(website.homeUrls.contains("https://www.ereshomes.com/realestateforrent?page=2"))
    }
}
