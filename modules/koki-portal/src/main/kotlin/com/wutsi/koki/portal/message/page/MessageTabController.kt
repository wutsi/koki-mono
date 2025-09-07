package com.wutsi.koki.portal.message.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.message.model.ConversationModel
import com.wutsi.koki.portal.message.model.MessageModel
import com.wutsi.koki.portal.message.service.MessageService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@Controller
@RequiresPermission(["message"])
class MessageTabController(
    private val service: MessageService,
) : AbstractPageController() {
    @GetMapping("/messages/tab")
    fun tab(
        @RequestParam(required = false, name = "owner-id") ownerId: Long,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false, name = "test-mode") testMode: String? = null,
        model: Model,
    ): String {
        model.addAttribute("testMode", testMode)
        more(ownerId, ownerType, model = model)
        return "messages/tab"
    }

    @GetMapping("/messages/tab/more")
    fun more(
        @RequestParam(required = false, name = "owner-id") ownerId: Long,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model,
    ): String {
        val conversations = getConversations(ownerId, ownerType, limit, offset)
        model.addAttribute("conversations", conversations)

        if (conversations.size >= limit) {
            val moreUrl =
                "/messages/tab/more?owner-id=$ownerId&owner-type=$ownerType&limit=$limit&offset=" + (offset + limit)
            model.addAttribute("moreUrl", moreUrl)
        }
        return "messages/tab-more"
    }

    private fun getConversations(
        ownerId: Long,
        ownerType: ObjectType,
        limit: Int,
        offset: Int,
    ): List<ConversationModel> {
        val template = ConversationModel(
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
            totalUnreadMessages = 0,
        )
        val conversations = mutableListOf<ConversationModel>()
        var i = 0 + offset
        repeat(20) {
            conversations.add(
                template.copy(
                    id = UUID.randomUUID().toString(),
                    totalUnreadMessages = if (i++ < 5) 11 - i else 0,
                )
            )
        }
        return conversations
    }
}
