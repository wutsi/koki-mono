package com.wutsi.koki.platform.tracking

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test
import kotlin.test.assertEquals

class TrafficSourceDetectorTest {
    private val detector = TrafficSourceDetector()

    @Test
    fun email() {
        assertEquals(
            "gmail",
            detector.detect("https://www.wutsi.com/read/123?utm_medium=email&utm_source=gmail", null, null)
        )
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "Mozilla/5.0 (Linux; Android 13; moto g 5G (2022) Build/T1SAS33.73-40-0-25-6; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/136.0.7103.57 Mobile Safari/537.36 Instagram 379.1.0.43.80 Android (33/13; 306dpi; 720x1431; motorola; moto g 5G (2022); austin; mt6833; en_US; 731638302; IABMV/1)",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 17_6_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/21G93 Instagram 368.0.0.27.83 (iPhone16,2; iOS 17_6_1; en_GB; en-GB; scale=3.00; 1290x2796; 697963422; IABMV/1)"
        ]
    )
    fun instagramFromUA(ua: String) {
        assertEquals("instagram", detector.detect(null, null, ua))
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "Mozilla/5.0 (Linux; Android 14; T614SP Build/UP1A.231005.007; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/137.0.7151.115 Mobile Safari/537.36 [FB_IAB/FB4A;FBAV/513.1.0.46.107;]",
            "Dalvik/2.1.0 (Linux; U; Android 14; T614SP Build/UP1A.231005.007) [FBAN/Orca-Android;FBAV/513.1.0.46.107;FBPN/com.facebook.orca;FBLC/en_US;FBBV/753632239;FBCR/HOME;FBMF/TCL;FBBD/TCL;FBDV/T614SP;FBSV/14;FBCA/arm64-v8a:null;FBDM/{density=2.0,width=720,height=1489};FB_FW/1;]",
            "Mozilla/5.0 (Linux; Android 14; 22101316UG Build/UP1A.231005.007; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/137.0.7151.112 Mobile Safari/537.36 [FB_IAB/FB4A;FBAV/518.0.0.63.86;IABMV/1;]"
        ]
    )
    fun facebookFromUA(ua: String) {
        assertEquals("facebook", detector.detect(null, null, ua))
    }

    @ParameterizedTest
    @ValueSource(strings = ["https://l.facebook.com", "https://www.facebook.com"])
    fun facebookFromReferer(referer: String) {
        assertEquals("facebook", detector.detect(null, referer, null))
    }

    @Test
    fun reddit() {
        assertEquals("reddit", detector.detect(null, "https://m.reddit.com", null))
        assertEquals("reddit", detector.detect(null, "https://www.reddit.com", null))
    }

    @Test
    fun linkedin() {
        assertEquals("linkedin", detector.detect(null, "https://m.linkedin.com", null))
        assertEquals("linkedin", detector.detect(null, "https://www.linkedin.com", null))
    }

    @Test
    fun direct() {
        assertEquals(null, detector.detect("http://www.google.com", null, null))
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "Mozilla/5.0 (Linux; Android 7.0; VS987 Build/NRD90U; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 Mobile Safari/537.36 [FB_IAB/MESSENGER;FBAV/112.0.0.17.70;]",
            "Mozilla/5.0 (iPad; CPU OS 10_1_1 like Mac OS X) AppleWebKit/602.2.14 (KHTML, like Gecko) Mobile/14B100 [FBAN/MessengerForiOS;FBAV/122.0.0.40.69;FBBV/61279955;FBDV/iPad4,1;FBMD/iPad;FBSN/iOS;FBSV/10.1.1;FBSS/2;FBCR/;FBID/tablet;FBLC/vi_VN;FBOP/5;FBRV/0]",
        ],
    )
    fun messenger(ua: String) {
        assertEquals("messenger", detector.detect(null, null, ua))
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "TelegramBot (like TwitterBot)",
            "TelegramBot (like TwitterBot),gzip(gfe);GoogleHypersonic (via doubleclick.net)",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36 TelegramBot (like TwitterBot)",
        ],
    )
    fun telegram(ua: String) {
        assertEquals("telegram", detector.detect(null, null, ua))
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "Twitterbot/1.0",
            "Mozilla/5.0 (compatible; Twitterbot/1.0) Chrome/115.0.0.0",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 15_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/14F89 Twitter for iPhone/7.21.1",
            "Mozilla/5.0 (Linux; Android 10; M2004J19C Build/QP1A.190711.020; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/136.0.7103.60 Mobile Safari/537.36 TwitterAndroid"
        ],
    )
    fun twitterFromUA(ua: String) {
        assertEquals("twitter", detector.detect(null, null, ua))
    }

    @ParameterizedTest
    @ValueSource(strings = ["https://t.co", "https://www.twitter.com"])
    fun twitterFromReferer(referer: String) {
        assertEquals("twitter", detector.detect(null, referer, null))
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "WhatsApp/2.21.19.21 A",
            "WhatsApp/2.24.20.89 Android/14 Device/motorola-moto_g24",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) whatsapp-connector/1.0.36 Chrome/114.0.5735.199 Electron/25.3.0 Safari/537.36"
        ]
    )
    fun whatsappFromUA(ua: String) {
        assertEquals("whatsapp", detector.detect(null, null, ua))
    }

    @ParameterizedTest
    @ValueSource(strings = ["https://wa.me", "https://www.whatsapp.com"])
    fun whatsappFromReferer(referer: String) {
        assertEquals("whatsapp", detector.detect(null, referer, null))
    }
}
