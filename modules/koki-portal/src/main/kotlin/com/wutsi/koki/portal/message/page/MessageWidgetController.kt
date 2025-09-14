package com.wutsi.koki.portal.message.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.message.model.ConversationModel
import com.wutsi.koki.portal.message.model.MessageModel
import com.wutsi.koki.portal.message.service.MessageService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@Controller
@RequestMapping("/messages/widget")
@RequiresPermission(["message"])
class MessageWidgetController : AbstractPageController() {
    @GetMapping
    fun show(
        @RequestParam(required = false, name = "test-mode") testMode: String? = null,
        model: Model,
    ): String {
        model.addAttribute("testMode", testMode)
        model.addAttribute("conversations", getConversations())
        model.addAttribute(
            "page",
            createPageModel(PageName.MESSAGE_WIDGET, "Messages")
        )

        return "messages/widget"
    }

    private fun getConversations(): List<ConversationModel> {
        return listOf(
            ConversationModel(
                id = UUID.randomUUID().toString(),
                viewed = false,
                lastMessage = MessageModel(
                    body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt.Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt.",
                    createdAtMoment = "30 min. ago",
                    sender = UserModel(
                        displayName = "Ray Sponsible",
                        photoUrl = "https://picsum.photos/800/600",
                    )
                ),
                interlocutor = UserModel(
                    displayName = "Ray Sponsible",
                    photoUrl = "https://picsum.photos/800/600",
                ),
                totalUnreadMessages = 3,
                url = "/listings/1?tab=message",
            ),
            ConversationModel(
                id = UUID.randomUUID().toString(),
                viewed = false,
                lastMessage = MessageModel(
                    body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod",
                    createdAtMoment = "3 hours ago",
                    sender = UserModel(
                        displayName = "Jane Doe",
                        photoUrl = "https://picsum.photos/100/100",
                    )
                ),
                interlocutor = UserModel(
                    displayName = "Jane Doe",
                    photoUrl = "https://picsum.photos/100/100",
                ),
                totalUnreadMessages = 5,
                url = "/listings/1?tab=message",
            ),
            ConversationModel(
                id = UUID.randomUUID().toString(),
                viewed = false,
                lastMessage = MessageModel(
                    body = "Received",
                    createdAtMoment = "Yesterday",
                    sender = UserModel(
                        displayName = "Roger Milla",
                        photoUrl = "https://picsum.photos/800/600",
                    )
                ),
                interlocutor = UserModel(
                    displayName = "Roger Milla",
                    photoUrl = "https://picsum.photos/800/600",
                ),
                totalUnreadMessages = 5,
                url = "/listings/1?tab=message",
            )

        )
    }
}
