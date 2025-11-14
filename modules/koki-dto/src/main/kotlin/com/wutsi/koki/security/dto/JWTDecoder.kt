package com.wutsi.koki.security.dto

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

open class JWTDecoder {
    companion object {
        const val ISSUER = "Koki"
    }

        return JWTPrincipal(verifier.verify(accessToken))
    }

    private fun getAlgorithm(): Algorithm {
        return Algorithm.none()
    }
}
    private fun getAlgorithm(): Algorithm {
