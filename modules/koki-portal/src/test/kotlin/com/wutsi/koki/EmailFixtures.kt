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
            summary = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has...",
            recipient = Recipient(id = accounts[0].id, type = ObjectType.ACCOUNT),
            createdAt = DateUtils.addMinutes(Date(), 50),
        ),
        EmailSummary(
            senderId = users[0].id,
            id = "101",
            subject = "Your invoice is ready",
            summary = "This is the summary of the email",
            recipient = Recipient(id = contacts[0].id, type = ObjectType.CONTACT),
            createdAt = DateUtils.addDays(Date(), -1),
        ),
        EmailSummary(
            senderId = users[0].id,
            id = "102",
            subject = "REMINDER: Get your shit ready!!!",
            summary = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has...",
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
            <p>
                <b>Lorem Ipsum</b> is simply dummy text of the printing and typesetting industry.
                Lorem Ipsum has been the industry's standard dummy text ever since the 1500s,
                when an unknown printer took a galley of type and scrambled it to make a type specimen book.
                It has survived not only five centuries, but also the leap into electronic typesetting,
                remaining essentially unchanged.
                It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages,
                and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.
            </p>
            <p>
                Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...
            </p>
            <p>
                There is no one who loves pain itself, who seeks after it and wants to have it, simply because it is pain...
            </p>
        """.trimIndent(),
        summary = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has...",
    )
}
