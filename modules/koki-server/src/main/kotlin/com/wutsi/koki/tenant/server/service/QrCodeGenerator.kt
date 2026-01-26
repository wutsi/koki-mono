package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.tenant.server.domain.TenantEntity
import org.springframework.stereotype.Service
import qrcode.QRCode
import qrcode.QRCodeShapesEnum
import qrcode.color.Colors
import qrcode.raw.ErrorCorrectionLevel
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.net.URL
import javax.imageio.ImageIO

@Service
class QrCodeGenerator {
    companion object {
        const val LOGO_WIDTH = 150
        const val LOGO_HEIGHT = 150
        const val MARGIN = 10
    }

    fun generate(
        data: String,
        tenant: TenantEntity,
        output: OutputStream,
    ) {
        val logo = icon(tenant)

        val builder = QRCode.ofSquares()
            .withBackgroundColor(Colors.TRANSPARENT)
            .withShape(QRCodeShapesEnum.ROUNDED_SQUARE)
            .withErrorCorrectionLevel(ErrorCorrectionLevel.VERY_HIGH)
            .withMargin(MARGIN)

        if (logo != null) {
            builder.withLogo(logo, LOGO_WIDTH, LOGO_HEIGHT, true)
        }

        val qr = builder.build(data).render()
        val img = ImageIO.read(ByteArrayInputStream(qr.getBytes()))
        ImageIO.write(img, "png", output)
    }

    private fun icon(tenant: TenantEntity): ByteArray? {
        val iconUrl = tenant.qrCodeIconUrl ?: return null
        val output = ByteArrayOutputStream()
        output.use {
            URL(iconUrl).openStream().use { os ->
                os.transferTo(output)
            }
            return output.toByteArray()
        }
    }
}
