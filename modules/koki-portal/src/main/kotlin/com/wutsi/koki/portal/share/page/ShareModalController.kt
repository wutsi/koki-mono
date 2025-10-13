package com.wutsi.koki.portal.share.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.share.form.ShareForm
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.net.URLEncoder

@Controller
@RequestMapping("/share/modal")
class ShareModalController : AbstractPageController() {
    @GetMapping
    fun show(
        @RequestParam url: String,
        @RequestParam(name = "test-mode", required = false) testMode: Boolean? = null,
        model: Model
    ): String {
        model.addAttribute("url", url)
        model.addAttribute("testMode", testMode)
        model.addAttribute("assetUrl", assetUrl)
        model.addAttribute("form", ShareForm(url = url))

        val xurl = URLEncoder.encode(url, "utf-8")
        model.addAttribute("facebookUrl", "https://www.facebook.com/sharer/sharer.php?display=page&u=$xurl")
        model.addAttribute("twitterUrl", "https://www.twitter.com/intent/tweet?url=$xurl")
        model.addAttribute("emailUrl", "mailto:?body=$xurl")
        return "share/modal"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ShareForm): String {
        val text = form.url + "\n\n${form.message}"
        val url = "https://wa.me/" + form.phoneFull.substring(1) + "?text=" + URLEncoder.encode(text, "utf-8")
        return "redirect:$url"
    }
}
