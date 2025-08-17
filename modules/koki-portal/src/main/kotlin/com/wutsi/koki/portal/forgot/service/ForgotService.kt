package com.wutsi.koki.portal.forgot.service

import com.wutsi.koki.portal.forgot.form.ForgotForm
import com.wutsi.koki.portal.forgot.form.ResetPasswordForm
import com.wutsi.koki.sdk.KokiUsers
import com.wutsi.koki.tenant.dto.ResetPasswordRequest
import com.wutsi.koki.tenant.dto.SendPasswordRequest
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

    fun sendPassword(form: ForgotForm): String {
        return koki.sendPassword(
            request = SendPasswordRequest(
                email = form.email
            )
        ).tokenId
    }

    fun resetPassword(form: ResetPasswordForm) {
        koki.resetPassword(
            ResetPasswordRequest(
                tokenId = form.tokenId,
                password = form.password,
            )
        )
    }
}
