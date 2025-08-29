package com.wutsi.koki.listing.server.service.agent

import com.amazonaws.util.IOUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.file.dto.ImageQuality
import com.wutsi.koki.platform.ai.llm.gemini.Gemini
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertNotNull
import java.io.File
import java.io.FileOutputStream
import kotlin.test.Test

class ListingImageReviewerAgentTest {
    private val llm = Gemini(
        apiKey = System.getenv("GEMINI_API_KEY"),
        model = "gemini-2.5-flash",
    )
    private val agent = ListingImageReviewerAgent(llm)

    @Test
    fun tools() {
        assertEquals(0, agent.tools().size)
    }

    @Test
    fun run() {
        val file = getValidFile("/fs/listing/room.jpg")
        val json = agent.run(ListingImageReviewerAgent.QUERY, listOf(file))
        val result = ObjectMapper().readValue(json, ListingImageReviewerAgentResult::class.java)
        assertEquals(ImageQuality.HIGH, result.quality)
        assertEquals(true, result.valid)
        assertEquals(null, result.reason)
    }

    @Test
    fun `invalid file`() {
        val file = getValidFile("/fs/listing/bad-image.jpg")
        val json = agent.run(ListingImageReviewerAgent.QUERY, listOf(file))
        println(json)
        val result = ObjectMapper().readValue(json, ListingImageReviewerAgentResult::class.java)
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
}
