package com.wutsi.koki.portal.page.settings.form

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.form.dto.FormSummary
import com.wutsi.koki.form.dto.SearchFormResponse
import com.wutsi.koki.portal.page.PageName
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class ListFormControllerTest : AbstractPageControllerTest() {
    private val forms = listOf(
        FormSummary(
            id = "1",
            name = "FRM-001",
            title = "Indicent Manager",
            active = true,
        ),
        FormSummary(
            id = "2",
            name = "FRM-002",
            title = "Reimbursement Form",
            active = true,
        ),
        FormSummary(
            id = "3",
            name = "FRM-003",
            title = "Receipt Submission",
            active = false,
        ),
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchFormResponse(forms)).whenever(kokiForms)
            .searchForms(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )
    }

    @Test
    fun list() {
        navigateTo("/settings/forms")
        assertCurrentPageIs(PageName.FORM_LIST)

        assertElementCount("tr.form", forms.size)
        assertElementNotPresent(".empty")
    }

    @Test
    fun empty() {
        doReturn(SearchFormResponse()).whenever(kokiForms)
            .searchForms(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        navigateTo("/settings/forms")
        assertCurrentPageIs(PageName.FORM_LIST)

        assertElementNotPresent("tr.form")
        assertElementPresent(".empty")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/forms")
        assertCurrentPageIs(PageName.LOGIN)
    }

//    @Test
//    fun view() {
//        val form = Message(
//            id = "1",
//            name = "M-001",
//            subject = "Message #1",
//            active = true,
//        )
//        doReturn(GetMessageResponse(form)).whenever(kokiMessages).get(any())
//
//        navigateTo("/settings/forms")
//        click("tr.form .btn-view")
//        assertCurrentPageIs(PageName.MESSAGE)
//    }
//
//    @Test
//    fun edit() {
//        val form = Message(
//            id = "1",
//            name = "M-001",
//            subject = "Message #1",
//            active = true,
//        )
//        doReturn(GetMessageResponse(form)).whenever(kokiMessages).get(any())
//
//        navigateTo("/settings/forms")
//        click("tr.form .btn-edit")
//        assertCurrentPageIs(PageName.MESSAGE_EDIT)
//    }
//
//    @Test
//    fun create() {
//        navigateTo("/settings/forms")
//        click(".btn-create")
//        assertCurrentPageIs(PageName.MESSAGE_CREATE)
//    }
}
