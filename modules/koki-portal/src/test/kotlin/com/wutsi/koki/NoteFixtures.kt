package com.wutsi.koki

import com.wutsi.koki.UserFixtures.users
import com.wutsi.koki.note.dto.Note
import com.wutsi.koki.note.dto.NoteSummary
import com.wutsi.koki.note.dto.NoteType
import org.apache.commons.lang3.time.DateUtils
import java.util.Date

object NoteFixtures {
    val notes = listOf(
        NoteSummary(
            id = 100,
            subject = "Phone call",
            type = NoteType.CALL,
            summary = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has...",
            duration = 75,
            createdAt = DateUtils.addDays(Date(), -5),
            createdById = users[0].id,
            modifiedAt = DateUtils.addDays(Date(), -5),
            modifiedById = users[1].id,
        ),
        NoteSummary(
            id = 101,
            subject = "Email sent",
            type = NoteType.ONLINE_MEETING,
            summary = "This is the content of the note",
            duration = 60,
            createdById = users[0].id,
            modifiedById = users[1].id,
        ),
        NoteSummary(
            id = 103,
            subject = "Another note",
            type = NoteType.IN_PERSON_MEETING,
            summary = "This is the content of the note",
            duration = 250,
            createdById = users[0].id,
            modifiedById = users[1].id,
        ),
        NoteSummary(
            id = 104,
            subject = "Another note",
            type = NoteType.EVENT,
            summary = "This is the content of the note",
            createdById = users[1].id,
            modifiedById = users[1].id,
        ),
        NoteSummary(
            id = 105,
            subject = "Another note",
            type = NoteType.TASK,
            summary = "This is the content of the note",
        ),
    )

    val note = Note(
        id = 100,
        subject = "Phone call",
        createdAt = DateUtils.addDays(Date(), -5),
        createdById = users[0].id,
        modifiedAt = DateUtils.addDays(Date(), -5),
        modifiedById = users[1].id,
        type = NoteType.CALL,
        duration = 75,
        summary = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has...",
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
    )

    val NEW_NOTE_ID = 1111L
}
