package com.wutsi.koki.bot.server.job

import com.wutsi.koki.bot.dto.ScrapeWebsiteRequest
import com.wutsi.koki.bot.dto.event.WebpageScrapedEvent
import com.wutsi.koki.bot.server.dao.WebpageRepository
import com.wutsi.koki.bot.server.dao.WebsiteRepository
import com.wutsi.koki.bot.server.domain.WebsiteEntity
import com.wutsi.koki.bot.server.service.WebscraperService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.mq.Publisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.ExecutorService

@RestController
@RequestMapping("/v1/bot/jobs")
@Service
class BotJobs(
    private val websiteDao: WebsiteRepository,
    private val webpageDao: WebpageRepository,
    private val scraper: WebscraperService,
    private val executorService: ExecutorService,
    private val publisher: Publisher,
) {
    @PostMapping("/scape")
    @Scheduled(cron = "\${koki.module.bot.cron.scape}")
    fun scrape() {
        val sites = websiteDao.findAll()
        val request = ScrapeWebsiteRequest(
            limit = 100,
            overwrite = false,
        )
        sites.forEach { site ->
            val job = this
            val task = object : Runnable {
                override fun run() {
                    job.scrape(site, request)
                }
            }
            executorService.submit(task)
        }
    }

    private fun scrape(site: WebsiteEntity, request: ScrapeWebsiteRequest) {
        val logger = DefaultKVLogger()
        logger.add("job", "BotJobs#scrape")
        logger.add("website", site.name)
        try {
            val pages = scraper.scrape(site, request)
            logger.add("page_scraped_count", pages.size)

            pages.forEach { page ->
                webpageDao.toContentUrl(page, site)?.let { url ->
                    publisher.publish(
                        WebpageScrapedEvent(
                            website = site.name,
                            url = page.url,
                            contentUrl = url.toString()
                        )
                    )
                }
            }
        } catch (ex: Exception) {
            logger.setException(ex)
        } finally {
            logger.log()
        }
    }
}
