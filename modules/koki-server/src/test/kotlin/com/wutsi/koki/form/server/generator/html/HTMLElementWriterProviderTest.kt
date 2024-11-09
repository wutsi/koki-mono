package com.wutsi.koki.form.server.generator.html

import com.wutsi.koki.form.dto.FormElementType
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException
import kotlin.test.Test

class HTMLElementWriterProviderTest {
    private val provider = HTMLElementWriterProvider()

    @Test
    fun generate() {
        assertTrue(provider.get(FormElementType.SECTION) is HTMLSectionWriter)
        assertTrue(provider.get(FormElementType.IMAGE) is HTMLImageWriter)
        assertTrue(provider.get(FormElementType.VIDEO) is HTMLVideoWriter)
        assertTrue(provider.get(FormElementType.URL) is HTMLTextWriter)
        assertTrue(provider.get(FormElementType.TEXT) is HTMLTextWriter)
        assertTrue(provider.get(FormElementType.EMAIL) is HTMLTextWriter)
        assertTrue(provider.get(FormElementType.NUMBER) is HTMLTextWriter)
        assertTrue(provider.get(FormElementType.DATE) is HTMLTextWriter)
        assertTrue(provider.get(FormElementType.TIME) is HTMLTextWriter)
        assertTrue(provider.get(FormElementType.PARAGRAPH) is HTMLParagraphWriter)
        assertTrue(provider.get(FormElementType.DROPDOWN) is HTMLDropdownWriter)
        assertTrue(provider.get(FormElementType.MULTIPLE_CHOICE) is HTMLCheckboxesWriter)
        assertTrue(provider.get(FormElementType.CHECKBOXES) is HTMLCheckboxesWriter)

        assertThrows<IllegalStateException> { provider.get(FormElementType.FILE_UPLOAD) }
        assertThrows<IllegalStateException> { provider.get(FormElementType.LINEAR_SCALE) }
        assertThrows<IllegalStateException> { provider.get(FormElementType.CHECKBOX_GRID) }
        assertThrows<IllegalStateException> { provider.get(FormElementType.MULTIPLE_CHOICE_GRID) }
    }
}
