package com.wutsi.koki.portal.email.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.contact.service.ContactService
import com.wutsi.koki.portal.email.model.EmailForm
import com.wutsi.koki.portal.email.service.EmailService
import com.wutsi.koki.portal.tax.service.TaxService
import com.wutsi.koki.portal.user.service.CurrentUserHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ComposeEmailController(
    private val service: EmailService,
    private val currentUser: CurrentUserHolder,
    private val accountService: AccountService,
    private val contactService: ContactService,
    private val taxService: TaxService,
) {
    @GetMapping("/emails/compose")
    fun compose(
        @RequestParam(name = "owner-id", required = true) ownerId: Long,
        @RequestParam(name = "owner-type", required = true) ownerType: ObjectType,
        model: Model,
    ): String {
        model.addAttribute("user", currentUser.get())
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)

        val account = if (ownerType == ObjectType.ACCOUNT) {
            accountService.account(id = ownerId, fullGraph = false)
        } else if (ownerType == ObjectType.TAX) {
            taxService.tax(ownerId).account
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
