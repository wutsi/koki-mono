package com.wutsi.koki.platform.image

import com.wutsi.koki.platform.core.image.ImageService
import com.wutsi.koki.platform.core.image.Transformation

class NullImageService : ImageService {
    override fun transform(url: String, transformation: Transformation?): String =
        url
}
