package com.wutsi.koki.portal.file.model

import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.portal.common.model.ObjectReferenceModel
import com.wutsi.koki.portal.user.model.UserModel
import java.util.Date

data class FileModel(
    val id: Long = -1,
    val type: FileType = FileType.UNKNOWN,
    val name: String = "",
    val title: String? = null,
    val contentUrl: String = "",
    val contentType: String = "",
    val contentLength: Long = -1,
    val contentLengthText: String = "",
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val createdAtMoment: String = "",
    val createdBy: UserModel? = null,
    val modifiedAt: Date = Date(),
    val modifiedAtText: String = "",
    val description: String? = null,
    val extension: String = "",
    val language: String? = null,
    val languageText: String? = null,
    val numberOfPages: Int? = null,
    val labels: List<LabelModel> = emptyList(),
    val status: FileStatus = FileStatus.UNKNOWN,
    val rejectionReason: String? = null,
    val owner: ObjectReferenceModel? = null
) {
    val rejected: Boolean
        get() = status == FileStatus.REJECTED

    val reviewing: Boolean
        get() = status == FileStatus.UNDER_REVIEW

    val approved: Boolean
        get() = status == FileStatus.APPROVED
}
