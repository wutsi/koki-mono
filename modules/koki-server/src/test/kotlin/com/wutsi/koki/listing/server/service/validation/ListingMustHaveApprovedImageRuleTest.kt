package com.wutsi.koki.listing.server.service.validation

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.listing.server.domain.ListingEntity
import jakarta.validation.ValidationException
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingMustHaveApprovedImageRuleTest {
    private val fileService = mock<FileService>()
    private val rule = ListingMustHaveApprovedImageRule(fileService)
    private val listing = ListingEntity(id = 1, tenantId = 11L)

    @Test
    fun success() {
        doReturn(
            listOf(
                FileEntity(id = 1),
                FileEntity(id = 2),
                FileEntity(id = 3),
                FileEntity(id = 4),
                FileEntity(id = 5),
                FileEntity(id = 6),
                FileEntity(id = 7),
            )
        ).whenever(fileService).search(
            listing.tenantId,
            emptyList(), // ids
            listing.id, // ownerId
            ObjectType.LISTING, // ownerType
            FileType.IMAGE, // fileType
            FileStatus.APPROVED, // fileStatus
            1, // limit
            0, // offset
        )
        rule.validate(listing)
    }

    @Test
    fun `no image`() {
        doReturn(
            emptyList<FileEntity>()
        ).whenever(fileService).search(
            any(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )

        val ex = assertThrows<ValidationException> {
            rule.validate(listing)
        }
        assertEquals(ErrorCode.LISTING_MISSING_APPROVED_IMAGE, ex.message)
    }
}
