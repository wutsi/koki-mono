package com.wutsi.koki.portal.message.page

import com.wutsi.koki.portal.common.model.ResultSetModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.message.form.MessageForm
import com.wutsi.koki.portal.message.model.ConversationModel
import com.wutsi.koki.portal.message.model.MessageModel
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequiresPermission(["message"])
class ConversationController : AbstractPageController() {
    @GetMapping("/messages/conversations/{id}")
    fun show(
        @PathVariable id: String,
        model: Model,
    ): String {
        val conversation = getConversation(id)
        model.addAttribute("conversation", conversation)
        model.addAttribute("form", MessageForm(conversationId = id))

        loadMessages(id, 20, 0, model)
        return "messages/conversation"
    }

    @ResponseBody
    @PostMapping("/messages/conversations/messages")
    fun submit(@ModelAttribute form: MessageForm, model: Model): Map<String, Any> {
        return mapOf("success" to true)
    }

    @GetMapping("/messages/conversations/{id}/messages")
    fun messages(
        @PathVariable id: String,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model,
    ): String {
        loadMessages(id, limit, offset, model)
        return "messages/messages"
    }

    private fun getConversation(id: String): ConversationModel {
        return ConversationModel(
            id = "id",
            interlocutor = UserModel(
                displayName = "Ray Sponsible",
                photoUrl = "https://picsum.photos/800/600",
                employer = "REIMAX, Auteuil"
            )
        )
    }

    private fun loadMessages(
        conversationId: String,
        limit: Int,
        offset: Int,
        model: Model
    ) {
        var items = mutableListOf<MessageModel>()
        val template = MessageModel(
            body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            createdAtMoment = "30 min. ago",
            sender = UserModel(
                displayName = "Ray Sponsible",
                photoUrl = "https://picsum.photos/800/600",
            )
        )
        var i = 0
        repeat(20) {
            items.add(
                template.copy(
                    sender = if (i++ % 2 == 0) template.sender else userHolder.get()!!
                )
            )
        }
        val messages = ResultSetModel(
            items = items,
            total = 35,
        )
        model.addAttribute("messages", messages)
        if (messages.items.size >= limit) {
            val moreUrl = "/messages/conversations/$conversationId/messages?limit=$limit&offset=" + (offset + limit)
            model.addAttribute("moreUrl", moreUrl)
        }
    }
}
