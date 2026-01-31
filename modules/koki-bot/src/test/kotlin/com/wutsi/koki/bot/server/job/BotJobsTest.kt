package com.wutsi.koki.bot.server.job

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.bot.dto.ScrapeWebsiteRequest
import com.wutsi.koki.bot.dto.event.WebpageScrapedEvent
import com.wutsi.koki.bot.server.dao.WebpageRepository
import com.wutsi.koki.bot.server.dao.WebsiteRepository
import com.wutsi.koki.bot.server.domain.WebpageEntity
import com.wutsi.koki.bot.server.domain.WebsiteEntity
import com.wutsi.koki.bot.server.service.WebscraperService
import com.wutsi.koki.platform.mq.Publisher
import java.net.URL
import java.util.concurrent.Executors.newFixedThreadPool
import kotlin.test.Test
import kotlin.test.assertEquals

class BotJobsTest {
    private val websiteDao = mock<WebsiteRepository>()
    private val webpageDao = mock<WebpageRepository>()
    private val scraper = mock<WebscraperService>()
    private val executorService = newFixedThreadPool(1)
    private val publisher = mock<Publisher>()
    private val jobs = BotJobs(websiteDao, webpageDao, scraper, executorService, publisher)

    @Test
    fun scrape() {
        // GIVEN
        val websites = listOf(WebsiteEntity(name = "A"))
        doReturn(websites).whenever(websiteDao).findAll()

        val webpages = listOf(WebpageEntity(url = "https://a.com/page1"), WebpageEntity(url = "https://a.com/page2"))
        doReturn(webpages).whenever(scraper).scrape(any(), any())

        val contentUrls = listOf(
            URL("https://a.com/page1/content.json"),
            URL("https://a.com/page2/content.json"),
        )
        doReturn(contentUrls[0])
            .doReturn(contentUrls[1])
            .whenever(webpageDao).toContentUrl(any(), any())

        // WHEN
        jobs.scrape()
        Thread.sleep(100)

        // THEN
        val site = argumentCaptor<WebsiteEntity>()
        val request = argumentCaptor<ScrapeWebsiteRequest>()
        verify(scraper).scrape(site.capture(), request.capture())

        assertEquals(websites[0], site.firstValue)
        assertEquals(false, request.firstValue.overwrite)
        assertEquals(100, request.firstValue.limit)

        val event = argumentCaptor<WebpageScrapedEvent>()
        verify(publisher, times(2)).publish(event.capture())

        assertEquals(websites[0].name, event.firstValue.website)
        assertEquals(webpages[0].url, event.firstValue.url)
        assertEquals(contentUrls[0].toString(), event.firstValue.contentUrl)

        assertEquals(websites[0].name, event.secondValue.website)
        assertEquals(webpages[1].url, event.secondValue.url)
        assertEquals(contentUrls[1].toString(), event.secondValue.contentUrl)
    }

    @Test
    fun `skip errors`() {
        // GIVEN
        val websites = listOf(WebsiteEntity(name = "A"), WebsiteEntity(name = "B"), WebsiteEntity(name = "C"))
        doReturn(websites).whenever(websiteDao).findAll()

        val webpages = listOf(WebpageEntity(url = "https://a.com/page1"), WebpageEntity(url = "https://a.com/page2"))
        doThrow(RuntimeException::class)
            .doReturn(webpages).whenever(scraper).scrape(any(), any())

        val contentUrls = listOf(
            URL("https://a.com/page1/content.json"),
            URL("https://a.com/page2/content.json"),
        )
        doReturn(contentUrls[0])
            .doReturn(contentUrls[1])
            .whenever(webpageDao).toContentUrl(any(), any())

        // WHEN
        jobs.scrape()
        Thread.sleep(100)

        // THEN
        verify(scraper, times(3)).scrape(any(), any())
        verify(publisher, times(4)).publish(any())
    }
}
