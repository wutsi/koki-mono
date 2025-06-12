package com.wutsi.koki.portal.message.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.message.dto.MessageStatus
import com.wutsi.koki.platform.util.StringUtils
import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.message.service.MessageService
import com.wutsi.koki.portal.room.service.RoomService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/messages")
@RequiresPermission(["message"])
class MessageController(
    private val service: MessageService,
    private val roomService: RoomService,
) : AbstractPageController() {
    @GetMapping("/{id}")
    fun show(
        @PathVariable id: Long,
        @RequestParam(name = "test-mode", required = false) testMode: Boolean? = null,
        model: Model,
    ): String {
        val message = service.message(id)
        model.addAttribute("message", message)
        model.addAttribute("testMode", testMode)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.MESSAGE,
                title = "Message",
            )
        )

        if (message.status == MessageStatus.NEW) {
            service.status(id, MessageStatus.READ)
        }
        return "messages/show"
    }

    @ResponseBody
    @GetMapping("/{id}/archive")
    @RequiresPermission(["message:manage"])
    fun archive(@PathVariable id: Long): Map<String, Any> {
        service.status(id, MessageStatus.ARCHIVED)
        return mapOf("success" to true)
    }

    @ResponseBody
    @GetMapping("/{id}/unarchive")
    @RequiresPermission(["message:manage"])
    fun unarchive(@PathVariable id: Long): Map<String, Any> {
        service.status(id, MessageStatus.NEW)
        return mapOf("success" to true)
    }

    @GetMapping("/{id}/whatsapp")
    fun whatsapp(@PathVariable id: Long): String {
        val message = service.message(id)
        if (message.senderPhone.isNullOrEmpty()) {
            throw HttpClientErrorException(HttpStatusCode.valueOf(404), "Message without phone number")
        }

        if (message.owner?.type == ObjectType.ROOM) {
            val room = roomService.room(message.owner.id, fullGraph = false)
            val url = tenantHolder.get()?.clientPortalUrl + room.listingUrl
            return "redirect:" + StringUtils.toWhatsappUrl(message.senderPhone, "$url\n")
        } else {
            return "redirect:" + StringUtils.toWhatsappUrl(message.senderPhone)
        }
    }
}
