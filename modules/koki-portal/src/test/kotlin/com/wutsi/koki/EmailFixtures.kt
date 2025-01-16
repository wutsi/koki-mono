package com.wutsi.koki

import com.wutsi.koki.AccountFixtures.accounts
import com.wutsi.koki.ContactFixtures.contacts
import com.wutsi.koki.UserFixtures.users
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.Email
import com.wutsi.koki.email.dto.EmailSummary
import com.wutsi.koki.email.dto.Recipient
import org.apache.commons.lang3.time.DateUtils
import java.util.Date
import java.util.UUID

object EmailFixtures {
    val NEW_EMAIL_ID = UUID.randomUUID().toString()
    val emails = listOf(
        EmailSummary(
            senderId = users[0].id,
            id = "100",
            subject = "You tax report is ready",
            recipient = Recipient(id = accounts[0].id, type = ObjectType.ACCOUNT),
            createdAt = DateUtils.addMinutes(Date(), 50),
        ),
        EmailSummary(
            senderId = users[0].id,
            id = "101",
            subject = "Your invoice is ready",
            recipient = Recipient(id = contacts[0].id, type = ObjectType.CONTACT),
            createdAt = DateUtils.addDays(Date(), -1),
        ),
        EmailSummary(
            senderId = users[0].id,
            id = "102",
            subject = "REMINDER: Get your shit ready!!!",
            recipient = Recipient(id = contacts[0].id, type = ObjectType.CONTACT),
            createdAt = DateUtils.addMinutes(DateUtils.addDays(Date(), -1), 15),
        ),
        EmailSummary(
            senderId = users[0].id,
            id = "103",
            subject = "REMINDER: Get your shit ready!!!",
            recipient = Recipient(id = contacts[0].id, type = ObjectType.CONTACT),
            createdAt = DateUtils.addDays(Date(), -6),
        ),
        EmailSummary(
            senderId = users[0].id,
            id = "103",
            subject = "Hello",
            recipient = Recipient(id = contacts[0].id, type = ObjectType.CONTACT),
            createdAt = DateUtils.addDays(Date(), -7),
        ),
    )

    val email = Email(
        senderId = users[0].id,
        id = "100",
        subject = "You tax report is ready",
        recipient = Recipient(id = contacts[0].id, type = ObjectType.CONTACT),
        body = """
            <p>Hi!</p>
            <p>
                We have completed your tax report. Please take the time to review it.
            </p>
            <p>
                Do not forget to download it from our portal. It's available <a href="{{tax_url}}">here</a>
            </p>
        """.trimIndent()
    )
}
