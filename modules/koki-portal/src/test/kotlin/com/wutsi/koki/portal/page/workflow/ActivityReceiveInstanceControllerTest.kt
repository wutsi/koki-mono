package com.wutsi.koki.portal.page.workflow.instance

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.WorkflowFixtures.activityInstance
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.GetActivityInstanceResponse
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class ActivityReceiveInstanceControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(
            GetActivityInstanceResponse(
                activityInstance.copy(
                    activity = activityInstance.activity.copy(type = ActivityType.RECEIVE)
                )
            )
        ).whenever(kokiWorkflowInstances)
            .activity(activityInstance.id)
    }

    @Test
    fun show() {
        navigateTo("/workflows/activities/${activityInstance.id}")

        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)
        assertElementNotPresent(".widget-toolbar")
    }
}
