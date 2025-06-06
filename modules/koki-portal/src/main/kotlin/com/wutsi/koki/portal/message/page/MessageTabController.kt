package com.wutsi.koki.portal.message.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.message.dto.MessageStatus
import com.wutsi.koki.platform.util.StringUtils
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.message.service.MessageService
import com.wutsi.koki.portal.room.service.RoomService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequiresPermission(["message"])
class MessageTabController(
    private val service: MessageService,
    private val roomService: RoomService,
) : AbstractPageController() {
    @GetMapping("/messages/tab")
    fun list(
        @RequestParam(required = false, name = "owner-id") ownerId: Long,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false, name = "test-mode") testMode: String? = null,
        @RequestParam(required = false) folder: String? = "inbox",
        model: Model,
    ): String {
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        model.addAttribute("testMode", testMode)
        model.addAttribute("folder", folder)
        more(ownerId, ownerType, folder, 20, 0, model)
        return "messages/tab/list"
    }

    @GetMapping("/messages/tab/more")
    fun more(
        @RequestParam(required = false, name = "owner-id") ownerId: Long,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false) folder: String? = "inbox",
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model,
    ): String {
        val messages = service.messages(
            ownerId = ownerId,
            ownerType = ownerType,
            statuses = if (folder == "archive") {
                listOf(MessageStatus.ARCHIVED)
            } else {
                MessageStatus.entries.filter { status -> status != MessageStatus.ARCHIVED }
            },
            limit = limit,
            offset = offset,
        )
        model.addAttribute("messages", messages)

        if (messages.size >= limit) {
            val nextOffset = offset + limit
            var url = "/messages/tab/more?limit=$limit&offset=$nextOffset&owner-id=$ownerId&owner-type=$ownerType"
            if (folder != null) {
                url = "$url&folder=$folder"
            }
            model.addAttribute("moreUrl", url)
        }
        return "messages/tab/more"
    }

    @ResponseBody
    @GetMapping("/messages/{id}/archive")
    fun archive(@PathVariable id: Long): Map<String, Any> {
        service.status(id, MessageStatus.ARCHIVED)
        return mapOf("success" to true)
    }

    @ResponseBody
    @GetMapping("/messages/{id}/unarchive")
    fun unarchive(@PathVariable id: Long): Map<String, Any> {
        service.status(id, MessageStatus.NEW)
        return mapOf("success" to true)
    }

    @GetMapping("/messages/{id}/whatsapp")
    fun whatsapp(@PathVariable id: Long): String {
        val message = service.message(id)
        if (message.senderPhone.isNullOrEmpty()) {
            throw HttpClientErrorException(HttpStatusCode.valueOf(404), "Message without phone number")
        }

        if (message.ownerType == ObjectType.ROOM && message.ownerId != null) {
            val room = roomService.room(id, fullGraph = false)
            val url = tenantHolder.get()?.clientPortalUrl + room.listingUrl
            return "redirect:" + StringUtils.toWhatsappUrl(message.senderPhone, "$url\n")
        } else {
            return "redirect:" + StringUtils.toWhatsappUrl(message.senderPhone)
        }
    }
}
