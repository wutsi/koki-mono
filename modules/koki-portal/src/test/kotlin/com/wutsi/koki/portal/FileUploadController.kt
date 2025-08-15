package com.wutsi.koki.portal

import com.wutsi.koki.FileFixtures
import com.wutsi.koki.file.dto.UploadFileResponse
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FileUploadController {
    @PostMapping("/file/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun file(): UploadFileResponse {
        return UploadFileResponse(
            id = FileFixtures.file.id,
            name = FileFixtures.file.name,
        )
    }
}
