package com.wutsi.koki.form.server.generator.html

import com.wutsi.koki.form.dto.FormElementType
import kotlin.to

class HTMLElementWriterProvider(
    private val writers: Map<FormElementType, HTMLElementWriter> = mapOf(
        FormElementType.IMAGE to HTMLImageWriter(),
        FormElementType.VIDEO to HTMLVideoWriter(),
        FormElementType.SECTION to HTMLSectionWriter(),
        FormElementType.TEXT to HTMLTextWriter(),
        FormElementType.URL to HTMLTextWriter(),
        FormElementType.NUMBER to HTMLTextWriter(),
        FormElementType.EMAIL to HTMLTextWriter(),
        FormElementType.DATE to HTMLTextWriter(),
        FormElementType.TIME to HTMLTextWriter(),
        FormElementType.PARAGRAPH to HTMLParagraphWriter(),
        FormElementType.DROPDOWN to HTMLDropdownWriter(),
        FormElementType.CHECKBOXES to HTMLCheckboxesWriter(),
        FormElementType.MULTIPLE_CHOICE to HTMLCheckboxesWriter(),
        FormElementType.FILE_UPLOAD to HTMLFileUploadWriter(),
    )
) {
    fun get(type: FormElementType): HTMLElementWriter {
        return writers[type]
            ?: throw IllegalStateException("$type not supported")
    }
}
