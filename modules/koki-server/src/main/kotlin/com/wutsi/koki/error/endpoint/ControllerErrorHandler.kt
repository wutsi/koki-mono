package com.wutsi.koki.error.endpoint

import com.wutsi.koki.common.dto.HttpHeader
import com.wutsi.koki.common.logger.KVLogger
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.error.dto.Parameter
import com.wutsi.koki.error.dto.ParameterType.PARAMETER_TYPE_HEADER
import com.wutsi.koki.error.dto.ParameterType.PARAMETER_TYPE_PATH
import com.wutsi.koki.error.dto.ParameterType.PARAMETER_TYPE_QUERY
import com.wutsi.koki.error.exception.WutsiException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class ControllerErrorHandler(
    private val logger: KVLogger
) {
    @ExceptionHandler(Throwable::class)
    fun onException(request: HttpServletRequest, e: Throwable): ResponseEntity<ErrorResponse> {
        val error = if (e is WutsiException) e.error else null
        return handleException(
            request,
            code = error?.code ?: ErrorCode.HTTP_INTERNAL,
            status = status(e),
            e = e,
            parameter = error?.parameter,
            message = error?.message ?: e.message,
            data = error?.data,
            downstreamCode = error?.downstreamCode,
        )
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun onAccessDeniedException(request: HttpServletRequest, e: AccessDeniedException): ResponseEntity<ErrorResponse> =
        handleException(
            request,
            code = ErrorCode.HTTP_ACCESS_DENIED,
            status = FORBIDDEN,
            e = e,
            message = e.message,
        )

    @ExceptionHandler(AuthenticationException::class)
    fun onAuthenticationException(
        request: HttpServletRequest,
        e: AuthenticationException,
    ): ResponseEntity<ErrorResponse> =
        handleException(
            request,
            code = ErrorCode.HTTP_AUTHENTICATION_FAILED,
            status = UNAUTHORIZED,
            e = e,
            message = e.message,
        )

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun onHttpRequestMethodNotSupportedException(
        request: HttpServletRequest,
        e: HttpRequestMethodNotSupportedException,
    ): ResponseEntity<ErrorResponse> =
        handleException(
            request,
            code = ErrorCode.HTTP_METHOD_NOT_SUPPORTED,
            status = HttpStatus.NOT_FOUND,
            e = e,
            message = e.message,
        )

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun onMissingServletRequestParameterException(
        request: HttpServletRequest,
        e: MissingServletRequestParameterException,
    ): ResponseEntity<ErrorResponse> =
        handleBadRequest(
            request,
            code = ErrorCode.HTTP_MISSING_PARAMETER,
            e = e,
            parameter = Parameter(
                name = e.parameterName,
                type = PARAMETER_TYPE_QUERY,
            ),
        )

    @ExceptionHandler(MissingPathVariableException::class)
    fun onMissingPathVariableException(
        request: HttpServletRequest,
        e: MissingPathVariableException,
    ): ResponseEntity<ErrorResponse> =
        handleBadRequest(
            request,
            ErrorCode.HTTP_MISSING_PARAMETER,
            e,
            Parameter(
                name = e.variableName,
                type = PARAMETER_TYPE_PATH,
            ),
        )

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun onMissingRequestHeaderException(
        request: HttpServletRequest,
        e: MissingRequestHeaderException,
    ): ResponseEntity<ErrorResponse> =
        handleBadRequest(
            request,
            ErrorCode.HTTP_MISSING_PARAMETER,
            e,
            Parameter(
                name = e.headerName,
                type = PARAMETER_TYPE_HEADER,
            ),
        )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun onMethodArgumentNotValidException(
        request: HttpServletRequest,
        e: MethodArgumentNotValidException,
    ): ResponseEntity<ErrorResponse> =
        handleBadRequest(
            request,
            ErrorCode.HTTP_INVALID_PARAMETER,
            e,
            Parameter(
                name = e.parameter.parameterName ?: "",
            ),
        )

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun onMethodArgumentTypeMismatchException(
        request: HttpServletRequest,
        e: MethodArgumentTypeMismatchException,
    ): ResponseEntity<ErrorResponse> =
        handleBadRequest(
            request,
            ErrorCode.HTTP_INVALID_PARAMETER,
            e,
            Parameter(
                name = e.parameter.parameterName ?: "",
                value = e.value,
            ),
        )

    private fun handleBadRequest(
        request: HttpServletRequest,
        code: String,
        e: Throwable,
        parameter: Parameter? = null,
    ): ResponseEntity<ErrorResponse> =
        handleException(
            request,
            code,
            BAD_REQUEST,
            e,
            parameter,
            e.message,
        )

    private fun handleException(
        request: HttpServletRequest,
        code: String,
        status: HttpStatus,
        e: Throwable,
        parameter: Parameter? = null,
        message: String? = null,
        data: Map<String, Any>? = null,
        downstreamCode: String? = null,
    ): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            error = Error(
                code = code,
                traceId = request.getHeader(HttpHeader.TRACE_ID),
                parameter = parameter,
                message = message,
                data = data,
                downstreamCode = downstreamCode,
            ),
        )

        log(response.error, e)
        return ResponseEntity
            .status(status)
            .body(response)
    }

    private fun log(error: Error, e: Throwable) {
        logger.add("error_code", error.code)
        logger.add("error_message", error.message)
        logger.add("error_downstream_code", error.downstreamCode)
        logger.add("error_downstream_message", error.downstreamMessage)
        logger.add("error_parameter_name", error.parameter?.name)
        logger.add("error_parameter_value", error.parameter?.value)
        logger.add("error_parameter_type", error.parameter?.type)
        error.data?.forEach {
            logger.add("error_data_${it.key}", it.value)
        }

        logger.setException(e)
    }

    private fun status(e: Throwable): HttpStatus {
        val status = e::class.annotations.find { it is ResponseStatus } as ResponseStatus?
        return status?.value ?: INTERNAL_SERVER_ERROR
    }
}
