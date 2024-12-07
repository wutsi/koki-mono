package com.wutsi.koki

import com.wutsi.koki.message.dto.Message
import com.wutsi.koki.message.dto.MessageSummary

object MessageFixtures {
    val MSG_ID = "11111-22222-33333"
    val MSG_NAME = "GSF-001"

    val message = Message(
        id = MSG_ID,
        name = MSG_NAME,
        subject = "Message #1",
        active = true,
        body = """
            <p>
                Hello <b>{{recipient}}</b>
            </p>
            <p>
                Welcome to Koki!!!
            </p>
        """.trimIndent()
    )

    val messages = listOf(
        MessageSummary(
            id = MSG_ID,
            name = MSG_NAME,
            subject = "Message #1",
            active = true,
        ),
        MessageSummary(
            id = "2",
            name = "M-002",
            subject = "Message #3",
            active = true,
        ),
        MessageSummary(
            id = "3",
            name = "M-003",
            subject = "Message #3",
            active = false,
        ),
    )
}
