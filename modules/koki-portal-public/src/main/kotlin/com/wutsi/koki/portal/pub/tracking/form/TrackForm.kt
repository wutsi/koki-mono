package com.wutsi.koki.portal.pub.tracking.form

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.track.dto.TrackEvent

data class TrackForm(
    val time: Long = -1,
    val hitId: String = "",
    val productId: String? = null,
    val event: TrackEvent = TrackEvent.UNKNOWN,
    val ua: String = "",
    val value: String? = null,
    val url: String = "",
    val referrer: String? = null,
    val page: String? = null,
    val component: String? = null,
    val rank: Int? = null,
    val productType: ObjectType = ObjectType.UNKNOWN,
    val recipientId: String? = null,
)
