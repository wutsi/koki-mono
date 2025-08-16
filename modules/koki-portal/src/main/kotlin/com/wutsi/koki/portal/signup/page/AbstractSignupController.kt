package com.wutsi.koki.portal.signup.page

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.signup.form.SignupForm
import com.wutsi.koki.portal.signup.service.SignupService
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.portal.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ui.Model
import org.springframework.web.client.HttpClientErrorException

abstract class AbstractSignupController : AbstractPageController() {
    @Autowired
    protected lateinit var logger: KVLogger

    @Autowired
    protected lateinit var userService: UserService

    @Autowired
    protected lateinit var signupService: SignupService

    protected fun loadError(form: SignupForm, ex: HttpClientErrorException, model: Model) {
        val response = toErrorResponse(ex)
        logger.add("backend_error", response.error.code)

        if (response.error.code == ErrorCode.USER_DUPLICATE_EMAIL) {
            model.addAttribute("error", getMessage("error.user.duplicate-email", arrayOf(form.email)))
        } else if (response.error.code == ErrorCode.USER_DUPLICATE_USERNAME) {
            model.addAttribute("error", getMessage("error.user.duplicate-username", arrayOf(form.username)))
        } else {
            model.addAttribute("error", getMessage("error.unexpected-error"))
        }
    }

    protected fun resolveUser(id: Long): UserModel {
        return userService.user(id)
    }
}
