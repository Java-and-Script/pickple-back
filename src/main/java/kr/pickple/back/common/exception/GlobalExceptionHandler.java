package kr.pickple.back.common.exception;

import static kr.pickple.back.common.exception.CommonExceptionCode.*;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import kr.pickple.back.common.dto.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNoHandlerFoundException(final NoHandlerFoundException e) {
        log.warn("{}, requestURL: {} ", COMMON_NOT_FOUND.getMessage(), e.getRequestURL(), e);

        final ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .code(COMMON_NOT_FOUND.getCode())
                .message(COMMON_NOT_FOUND.getMessage())
                .rejectedValues(new String[] {e.getRequestURL()})
                .build();

        return ResponseEntity.status(COMMON_NOT_FOUND.getStatus())
                .body(exceptionResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleHttpRequestMethodNotSupportedException(
            final HttpRequestMethodNotSupportedException e
    ) {
        log.warn(
                "{}, supportedHttpMethods: {}, requestMethod: {}",
                COMMON_METHOD_NOT_ALLOWED.getMessage(),
                e.getSupportedHttpMethods(),
                e.getMethod(),
                e
        );

        final ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .code(COMMON_METHOD_NOT_ALLOWED.getCode())
                .message(COMMON_METHOD_NOT_ALLOWED.getMessage())
                .build();

        return ResponseEntity.status(COMMON_METHOD_NOT_ALLOWED.getStatus())
                .body(exceptionResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e
    ) {
        final List<FieldError> errors = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(FieldError.class::cast)
                .toList();

        log.warn("{}", COMMON_BAD_REQUEST.getMessage(), e);
        errors.forEach(
                error -> log.warn("{}, field: {}, rejectedValue: {}",
                        error.getDefaultMessage(),
                        error.getField(),
                        error.getRejectedValue()
                )
        );

        final Object[] rejectedValues = errors.stream()
                .map(FieldError::getRejectedValue)
                .toArray();

        final ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .code(COMMON_BAD_REQUEST.getCode())
                .message(COMMON_BAD_REQUEST.getMessage())
                .rejectedValues(rejectedValues)
                .build();

        return ResponseEntity.badRequest()
                .body(exceptionResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException e
    ) {
        final Throwable cause = e.getCause().getCause();

        // LocalDatetime.parse()가 불가능한 형식으로 datetime 값을 입력받는 경우
        if (Objects.nonNull(cause) && cause instanceof DateTimeParseException dateTimeParseException) {
            final String parsedString = dateTimeParseException.getParsedString();
            log.warn("{}, rejectedValues: {}", COMMON_BAD_REQUEST.getMessage(), parsedString, e);

            final ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                    .code(COMMON_BAD_REQUEST.getCode())
                    .message(COMMON_BAD_REQUEST.getMessage())
                    .rejectedValues(new String[] {parsedString})
                    .build();

            return ResponseEntity.badRequest()
                    .body(exceptionResponse);
        }

        log.warn("{}", COMMON_BAD_REQUEST.getMessage(), e);
        final ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .code(COMMON_BAD_REQUEST.getCode())
                .message(COMMON_BAD_REQUEST.getMessage())
                .build();

        return ResponseEntity.badRequest()
                .body(exceptionResponse);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleBusinessException(final BusinessException e) {
        final ExceptionCode exceptionCode = e.getExceptionCode();
        final Object[] rejectedValues = e.getRejectedValues();

        log.warn("{}, rejectedValue: {}", exceptionCode.getMessage(), rejectedValues, e);

        final ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .code(exceptionCode.getCode())
                .message(exceptionCode.getMessage())
                .rejectedValues(rejectedValues)
                .build();

        return ResponseEntity.status(exceptionCode.getStatus())
                .body(exceptionResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(final Exception e) {
        log.error("{}", COMMON_INTERNAL_SERVER_ERROR.getMessage(), e);

        final ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .code(COMMON_INTERNAL_SERVER_ERROR.getCode())
                .message(COMMON_INTERNAL_SERVER_ERROR.getMessage())
                .build();

        return ResponseEntity.internalServerError()
                .body(exceptionResponse);
    }
}
