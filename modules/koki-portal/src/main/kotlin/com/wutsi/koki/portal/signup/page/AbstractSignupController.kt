package com.wutsi.koki.portal.signup.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.signup.service.SignupService
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.portal.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractSignupController : AbstractPageController() {
    @Autowired
    protected lateinit var userService: UserService

    @Autowired
    protected lateinit var signupService: SignupService

    protected fun resolveUser(id: Long): UserModel {
        return userService.user(id)
    }
}
