package com.wutsi.koki.room.web.tracking.page

import com.wutsi.koki.room.web.tracking.form.TrackForm
import com.wutsi.koki.room.web.tracking.service.TrackService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class TrackController(private val service: TrackService) {
    @ResponseBody
    @PostMapping("/track")
    fun track(@RequestBody form: TrackForm): Map<String, Any> {
        service.track(form)
        return mapOf("success" to true)
    }
}
