package com.wutsi.koki

import com.wutsi.koki.RoomFixtures.rooms
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.message.dto.Message
import com.wutsi.koki.message.dto.MessageStatus
import com.wutsi.koki.message.dto.MessageSummary
import org.apache.commons.lang3.time.DateUtils
import java.util.Date

object MessageFixtures {
    val NEW_ID = 9548594L
    val messages = listOf(
        MessageSummary(
            id = 100L,
            senderName = "Ray Sponsible",
            senderEmail = "ray.sponsible@gmail.com",
            senderPhone = "5147580011",
            createdAt = Date(),
            status = MessageStatus.NEW,
            owner = ObjectReference(rooms[0].id, ObjectType.ROOM),
            body = "Lorem Ipsum is simply dummy text of the printing and typesetting industry"
        ),
        MessageSummary(
            id = 101L,
            senderName = "Roger Milla",
            senderEmail = "roger.milla@gmail.com",
            senderPhone = null,
            createdAt = DateUtils.addDays(Date(), -1),
            status = MessageStatus.ARCHIVED,
            owner = ObjectReference(rooms[0].id, ObjectType.ROOM),
            body = "It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages,"
        ),
        MessageSummary(
            id = 102L,
            senderName = "Omam Mbiyick",
            senderEmail = "Omam-Mbiyick@gmail.com",
            senderPhone = null,
            createdAt = DateUtils.addDays(Date(), -1),
            status = MessageStatus.NEW,
            owner = ObjectReference(rooms[0].id, ObjectType.ROOM),
            body = "Hello, Im interested in your appartment on xxx"
        ),
        MessageSummary(
            id = 103L,
            senderName = "Thomas Nkono",
            senderEmail = "Thomas.Nkono@gmail.com",
            senderPhone = null,
            createdAt = DateUtils.addDays(Date(), -2),
            status = MessageStatus.NEW,
            owner = ObjectReference(rooms[0].id, ObjectType.ROOM),
            body = "It has survived not only five centuries, but also the leap into electronic typesetting"
        ),
        MessageSummary(
            id = 104L,
            senderName = "Emanuel Kunde",
            senderEmail = "Emanuel.Kunde@gmail.com",
            senderPhone = null,
            createdAt = DateUtils.addDays(Date(), -3),
            status = MessageStatus.NEW,
            owner = ObjectReference(rooms[0].id, ObjectType.ROOM),
            body = "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s"
        ),
    )

    val message = Message(
        id = 100L,
        senderName = "Ray Sponsible",
        senderEmail = "ray.sponsible@gmail.com",
        senderPhone = "5147580011",
        createdAt = Date(),
        status = MessageStatus.NEW,
        owner = ObjectReference(rooms[0].id, ObjectType.ROOM),
        body = """
                Lorem Ipsum is simply dummy text of the printing and typesetting industry.
                Lorem Ipsum has been the industry's standard dummy text ever since the 1500s
        """.trimIndent(),
    )
}
