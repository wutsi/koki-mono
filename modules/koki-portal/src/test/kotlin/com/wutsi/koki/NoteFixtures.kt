package com.wutsi.koki

import com.wutsi.koki.UserFixtures.users
import com.wutsi.koki.note.dto.Note
import com.wutsi.koki.note.dto.NoteSummary
import org.apache.commons.lang3.time.DateUtils
import java.util.Date

object NoteFixtures {
    val notes = listOf(
        NoteSummary(
            id = 100,
            subject = "Phone call",
            body = "This is the <b>content</b> of the note",
            createdAt = DateUtils.addDays(Date(), -5),
            createdById = users[0].id,
            modifiedAt = DateUtils.addDays(Date(), -5),
            modifiedById = users[1].id,
        ),
        NoteSummary(
            id = 101,
            subject = "Email sent",
            body = "This is the content of the note",
            createdById = users[0].id,
            modifiedById = users[1].id,
        ),
        NoteSummary(
            id = 103,
            subject = "Another note",
            body = "This is the content of the note",
            createdById = users[0].id,
            modifiedById = users[1].id,
        ),
        NoteSummary(
            id = 104,
            subject = "Another note",
            body = "This is the content of the note",
            createdById = users[1].id,
            modifiedById = users[1].id,
        ),
        NoteSummary(
            id = 105,
            subject = "Another note",
            body = "This is the content of the note",
        ),
    )

    val note = Note(
        id = 100,
        subject = "Phone call",
        body = "This is the content of the note",
        createdAt = DateUtils.addDays(Date(), -5),
        createdById = users[0].id,
        modifiedAt = DateUtils.addDays(Date(), -5),
        modifiedById = users[1].id,
    )

    val NEW_NOTE_ID = 1111L
}
