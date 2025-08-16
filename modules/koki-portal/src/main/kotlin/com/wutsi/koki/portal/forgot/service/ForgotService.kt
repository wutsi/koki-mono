package com.wutsi.koki.portal.forgot.service

import com.wutsi.koki.portal.forgot.form.ForgotForm
import com.wutsi.koki.sdk.KokiUsers
import com.wutsi.koki.tenant.dto.SendUsernameRequest
import org.springframework.stereotype.Service

@Service
class ForgotService(private val koki: KokiUsers) {
    fun sendUsername(form: ForgotForm) {
        koki.sendUsername(
            request = SendUsernameRequest(
                email = form.email
            )
        )
    }
}
