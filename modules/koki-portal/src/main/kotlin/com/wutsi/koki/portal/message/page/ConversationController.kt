package com.wutsi.koki.portal.message.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.message.model.ConversationModel
import com.wutsi.koki.portal.message.model.MessageModel
import com.wutsi.koki.portal.message.service.MessageService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.model.UserModel
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

@Controller
@RequiresPermission(["message"])
class MessageTabController(
    private val service: MessageService,
) : AbstractPageController() {
    @GetMapping("/messages/tab")
    fun list(
        @RequestParam(required = false, name = "owner-id") ownerId: Long,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false, name = "test-mode") testMode: String? = null,
        @RequestParam(required = false) folder: String? = "inbox",
        model: Model,
    ): String {
        val conversations = loadConversations(ownerId, ownerType)
        model.addAttribute("conversations", conversations)

//        model.addAttribute("ownerId", ownerId)
//        model.addAttribute("ownerType", ownerType)
//        model.addAttribute("testMode", testMode)
//        model.addAttribute("folder", folder)
//        model.addAttribute("refreshUrl", "/messages/tab/more?owner-id=$ownerId&owner-type=$ownerType")
        return "messages/tab/list"
    }

    private fun loadConversations(ownerId: Long, ownerType: ObjectType): List<ConversationModel> {
        return listOf(
            ConversationModel(
                id = UUID.randomUUID().toString(),
                viewed = false,
                lastMessage = MessageModel(
                    body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
                    createdAtMoment = "30 min. ago",
                    sender = UserModel(
                        displayName = "Ray Sponsible",
                        photoUrl = "https://picsum.photos/800/600",
                    )
                ),
                totalMessages = 10,
            ),
            ConversationModel(
                id = UUID.randomUUID().toString(),
                viewed = false,
                lastMessage = MessageModel(
                    body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
                    createdAtMoment = "3 hr ago",
                    sender = UserModel(
                        displayName = "Roger Milla",
                        photoUrl = "https://picsum.photos/100/100",
                    )
                ),
                totalMessages = 7,
            ),
            ConversationModel(
                id = UUID.randomUUID().toString(),
                viewed = false,
                lastMessage = MessageModel(
                    body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
                    createdAtMoment = "yesterday",
                    sender = UserModel(
                        displayName = "Omam Mbiyick",
                        photoUrl = "https://picsum.photos/150/100",
                    )
                ),
                totalMessages = 5,
            ),
            ConversationModel(
                id = UUID.randomUUID().toString(),
                viewed = true,
                lastMessage = MessageModel(
                    body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
                    createdAtMoment = "3 days ago",
                    sender = UserModel(
                        displayName = "Jane Doe",
                        photoUrl = "https://picsum.photos/200/200",
                    )
                ),
                totalMessages = 1,
            ),
            ConversationModel(
                id = UUID.randomUUID().toString(),
                viewed = true,
                lastMessage = MessageModel(
                    body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
                    createdAtMoment = "7 days ago",
                    sender = UserModel(
                        displayName = "Lovelly",
                        photoUrl = "https://picsum.photos/150/150",
                    )
                ),
                totalMessages = 5,
            ),
        )
    }
}
