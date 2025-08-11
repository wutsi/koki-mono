package com.wutsi.koki.portal.message.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.message.form.MessageForm
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/messages/compose")
@RequiresPermission(["message:manage"])
class ComposeMessageController : AbstractPageController() {
    @GetMapping
    fun compose(
        @RequestParam(name = "to-user-id") toUserId: Long,
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        model: Model,
    ): String {
        model.addAttribute(
            "form",
            MessageForm(
                fromUserId = userHolder.get()?.id ?: -1,
                toUserId = toUserId,
                ownerId = ownerId,
                ownerType = ownerType,
            )
        )
        model.addAttribute("to", findUser(toUserId))

        model.addAttribute(
            "page",
            PageModel(
                name = PageName.MESSAGE_COMPOSE,
                title = "Message",
            )
        )

        return "messages/compose"
    }

    @ResponseBody
    @PostMapping
    fun send(@ModelAttribute form: MessageForm, model: Model): Map<String, Any?> {
        val recipient = findUser(form.toUserId)
        return mapOf(
            "success" to true,
            "errorMessage" to null,
            "successMessage" to getMessage("page.message.compose.message-sent", arrayOf(recipient.displayName))
        )
    }

    private fun findUser(id: Long): UserModel {
        return UserModel(
            id = id,
            displayName = "Ray Sponsible",
            employer = "Courtier Immobilier SARL",
            phone = "+15147580100",
            photoUrl = "https://picsum.photos/128/128"
        )
    }
}
