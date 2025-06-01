package com.wutsi.koki.portal.email.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.contact.service.ContactService
import com.wutsi.koki.portal.email.model.EmailForm
import com.wutsi.koki.portal.email.service.EmailService
import com.wutsi.koki.portal.file.service.FileService
import com.wutsi.koki.portal.invoice.service.InvoiceService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.service.CurrentUserHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["email:send"])
class ComposeEmailController(
    private val service: EmailService,
    private val currentUser: CurrentUserHolder,
    private val accountService: AccountService,
    private val contactService: ContactService,
    private val invoiceService: InvoiceService,
    private val fileService: FileService,
) {
    @GetMapping("/emails/compose")
    fun compose(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        @RequestParam(name = "attachment-file-id", required = false) attachmentFileIds: List<Long> = emptyList(),
        model: Model,
    ): String {
        model.addAttribute("user", currentUser.get())
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)

        val account = if (ownerType == ObjectType.ACCOUNT) {
            accountService.account(id = ownerId, fullGraph = false)
        } else if (ownerType == ObjectType.INVOICE) {
            invoiceService.invoice(ownerId).customer.account
        } else {
            null
        }
        model.addAttribute("account", account)

        val contact = if (ownerType == ObjectType.CONTACT) {
            contactService.contact(id = ownerId, fullGraph = false)
        } else {
            null
        }
        model.addAttribute("contact", contact)

        if (attachmentFileIds.isNotEmpty()) {
            val attachmentFiles = fileService.files(
                ids = attachmentFileIds,
                limit = attachmentFileIds.size,
            )
            if (attachmentFiles.isNotEmpty()) {
                model.addAttribute("attachmentFiles", attachmentFiles)
            }
        }

        model.addAttribute(
            "form",
            EmailForm(
                ownerType = ownerType,
                ownerId = ownerId,
                recipientType = if (account != null) ObjectType.ACCOUNT else ObjectType.CONTACT,
                accountId = account?.id,
                contactId = contact?.id,
            )
        )
        return "emails/compose"
    }

    @PostMapping("/emails/send")
    fun send(@ModelAttribute form: EmailForm, model: Model): String {
        service.send(form)
        return "emails/sent"
    }
}
