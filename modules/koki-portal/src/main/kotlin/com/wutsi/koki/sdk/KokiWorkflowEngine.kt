package com.wutsi.koki.sdk

import com.wutsi.koki.workflow.dto.CompleteActivityInstanceRequest
import org.springframework.web.client.RestTemplate

@Deprecated("User koki workflow instead")
class KokiWorkflowEngine(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private val ACTIVITY_PATH_PREFIX = "/v1/activity-instances"
    }

    fun complete(activityInstanceId: String, data: Map<String, Any>) {
        val url = urlBuilder.build("$ACTIVITY_PATH_PREFIX/$activityInstanceId/complete")
        val request = CompleteActivityInstanceRequest(
            state = data
        )
        rest.postForEntity(url, request, Any::class.java)
    }
}
