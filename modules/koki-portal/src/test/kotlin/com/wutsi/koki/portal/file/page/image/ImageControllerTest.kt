package com.wutsi.koki.portal.file.page.image

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.FileFixtures.image
import com.wutsi.koki.ListingFixtures.listing
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.GetFileResponse
import com.wutsi.koki.listing.dto.GetListingResponse
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class ImageControllerTest : AbstractPageControllerTest() {

    @Test
    fun show() {
        val imageId = setupImage()

        navigateTo("/images/$imageId")

        assertCurrentPageIs(PageName.IMAGE)
        assertElementVisible("#btn-delete")
    }

    @Test
    fun readOnly() {
        val listingId = setupListing(ListingStatus.ACTIVE)
        val imageId = setupImage(ObjectReference(id = listingId, type = ObjectType.LISTING))

        navigateTo("/images/$imageId")

        assertElementNotPresent("#btn-delete")
    }

    @Test
    fun delete() {
        val listingId = setupListing(ListingStatus.DRAFT)
        val imageId = setupImage(ObjectReference(id = listingId, type = ObjectType.LISTING))

        navigateTo("/images/$imageId")
        click("#btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(rest).delete("$sdkBaseUrl/v1/files/$imageId")
        assertCurrentPageIs(PageName.LISTING)
    }

    @Test
    fun `delete read-only`() {
        val listingId = setupListing(ListingStatus.ACTIVE)
        val imageId = setupImage(ObjectReference(id = listingId, type = ObjectType.LISTING))

        navigateTo("/images/delete?id=$imageId")

        verify(rest, never()).delete(any<String>())
        assertCurrentPageIs(PageName.ERROR_403)
    }

    private fun setupImage(owner: ObjectReference? = null): Long {
        doReturn(
            ResponseEntity(
                GetFileResponse(image.copy(owner = owner)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetFileResponse::class.java)
            )
        return image.id
    }

    private fun setupListing(status: ListingStatus): Long {
        doReturn(
            ResponseEntity(
                GetListingResponse(listing.copy(status = status)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetListingResponse::class.java)
            )
        return listing.id
    }
}
