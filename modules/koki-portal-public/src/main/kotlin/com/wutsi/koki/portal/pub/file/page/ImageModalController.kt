package com.wutsi.koki.portal.pub.file.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.portal.pub.file.service.FileService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/images/modal")
class ImageModalController(
    private val service: FileService,
) {
    @GetMapping
    fun show(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        model: Model
    ): String {
        val images = service.search(
            type = FileType.IMAGE,
            ownerId = ownerId,
            ownerType = ownerType,
            limit = 100,
        )
        model.addAttribute("images", images)
        return "files/images/modal"
    }
}
