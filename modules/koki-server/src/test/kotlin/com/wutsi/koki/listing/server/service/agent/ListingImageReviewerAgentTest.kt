package com.wutsi.koki.listing.server.service.agent

import com.amazonaws.util.IOUtils
import com.wutsi.koki.ai.server.service.LLMProvider
import com.wutsi.koki.file.dto.ImageQuality
import com.wutsi.koki.platform.ai.agent.Agent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import tools.jackson.databind.json.JsonMapper
import java.io.File
import java.io.FileOutputStream
import kotlin.test.Test

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ListingImageReviewerAgentTest {
    @Autowired
    private lateinit var provider: LLMProvider

    @Test
    fun tools() {
        assertEquals(0, createAgent().tools().size)
    }

    @Test
    fun run() {
        Thread.sleep(30000L) // Delay to avoid rate limiting

        val agent = createAgent()
        val file = getValidFile("/fs/listing/room.jpg")
        val json = agent.run(ListingImageReviewerAgent.QUERY, listOf(file))
        val result = JsonMapper().readValue(json, ListingImageReviewerAgentResult::class.java)
        assertEquals(ImageQuality.HIGH, result.quality)
        assertEquals(true, result.valid)
        assertEquals(null, result.reason)
    }

    @Test
    fun `invalid file`() {
        Thread.sleep(30000L) // Delay to avoid rate limiting

        val agent = createAgent()
        val file = getValidFile("/fs/listing/bad-image.jpg")
        val json = agent.run(ListingImageReviewerAgent.QUERY, listOf(file))
        println(json)
        val result = JsonMapper().readValue(json, ListingImageReviewerAgentResult::class.java)
        assertEquals(ImageQuality.POOR, result.quality)
        assertEquals(false, result.valid)
        assertNotNull(result.reason)
    }

    private fun getValidFile(path: String): File {
        val file = File.createTempFile("test", ".jpg")
        val fin = ListingImageReviewerAgentTest::class.java.getResourceAsStream(path)
        val fout = FileOutputStream(file)
        fout.use {
            IOUtils.copy(fin, fout)
        }
        return file
    }

    private fun createAgent(): Agent {
        return ListingImageReviewerAgent(provider.visionLLM)
    }
}
