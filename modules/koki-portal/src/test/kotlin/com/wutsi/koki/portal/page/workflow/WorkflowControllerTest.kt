package com.wutsi.koki.portal.page.workflow.instance

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.FileFixtures.files
import com.wutsi.koki.LogFixtures.logEntries
import com.wutsi.koki.WorkflowFixtures.workflowInstance
import com.wutsi.koki.file.dto.FileSummary
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.workflow.dto.LogEntrySummary
import com.wutsi.koki.workflow.dto.SearchLogEntryResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.UUID
import kotlin.test.Test

class WorkflowControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/workflows/${workflowInstance.id}")

        assertCurrentPageIs(PageName.WORKFLOW)
        assertElementCount("tr.activity", workflowInstance.activityInstances.size)

        click("#pills-files-tab")
        waitForPresenceOf(".widget-files tr.file")
        assertElementCount(".widget-files tr.file", files.size)

        click("#pills-logs-tab")
        waitForPresenceOf(".logs-widget tr.log")
        assertElementCount(".logs-widget tr.log", logEntries.size)

        click("#pills-process-tab")
        waitForPresenceOf(".workflow-image")
        assertElementPresent(".workflow-image img")

        click("#pills-state-tab")
    }

    @Test
    fun `load more logs`() {
        var entries = mutableListOf<LogEntrySummary>()
        repeat(20) {
            entries.add(logEntries[0].copy(id = UUID.randomUUID().toString()))
        }
        doReturn(SearchLogEntryResponse(entries))
            .doReturn(SearchLogEntryResponse(logEntries))
            .whenever(kokiLogs)
            .logs(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        navigateTo("/workflows/${workflowInstance.id}")

        click("#pills-logs-tab")
        waitForPresenceOf(".logs-widget tr.log")
        assertElementCount(".logs-widget tr.log", entries.size)

        scrollToBottom()
        click("#log-load-more a", 1000)
        assertElementCount(".logs-widget tr.log", entries.size + logEntries.size)
    }

    @Test
    fun `load more files`() {
        var entries = mutableListOf<FileSummary>()
        repeat(20) {
            var seed = System.currentTimeMillis()
            entries.add(
                files[(Math.random() * files.size).toInt()].copy(
                    id = seed++,
                    contentLength = (Math.random() * 10 * 1024 * 1024).toLong(),
                )
            )
        }
        doReturn(
            ResponseEntity(
                SearchFileResponse(entries),
                HttpStatus.OK,
            )
        ).doReturn(
            ResponseEntity(
                SearchFileResponse(files),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchFileResponse::class.java)
            )

        navigateTo("/workflows/${workflowInstance.id}")

        click("#pills-files-tab", 1000)
        assertElementCount(".widget-files tr.file", entries.size)

        scrollToBottom()
        click("#file-load-more a", 1000)
        assertElementCount(".widget-files tr.file", entries.size + files.size)
    }

    @Test
    fun `open workflow`() {
        navigateTo("/workflows/${workflowInstance.id}")
        click("a.workflow")
        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW)
    }
}
