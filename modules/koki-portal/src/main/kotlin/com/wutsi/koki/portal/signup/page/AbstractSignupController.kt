package com.wutsi.koki.portal.signup.page

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.refdata.service.CategoryService
import com.wutsi.koki.portal.signup.form.SignupForm
import com.wutsi.koki.portal.signup.service.SignupService
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.refdata.dto.CategoryType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException
import java.util.Locale

abstract class AbstractSignupProfileController : AbstractPageController() {
    @Autowired
    private lateinit var logger: KVLogger

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
}
