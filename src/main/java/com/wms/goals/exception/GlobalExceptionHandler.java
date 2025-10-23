package com.wms.goals.exception;

import com.wms.goals.utility.ApiResponseUtil;
import com.wms.goals.web.ResponseWrapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleValidation(MethodArgumentNotValidException ex, Locale locale) {
        List<String> msgs = ex.getBindingResult().getAllErrors().stream()
                .map(err -> {
                    if (err instanceof FieldError) {
                        FieldError fe = (FieldError) err;
                        String m = fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage();
                        return fe.getField() + ": " + m;
                    }
                    return err.getDefaultMessage() == null ? "invalid" : err.getDefaultMessage();
                })
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(ApiResponseUtil.validationMessages(msgs));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleConstraint(ConstraintViolationException ex, Locale locale) {
        List<String> msgs = ex.getConstraintViolations().stream()
                .map(v -> v.getMessage()).collect(Collectors.toList());
        return ResponseEntity.badRequest().body(ApiResponseUtil.validationMessages(msgs));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleMissingHeader(MissingRequestHeaderException ex, Locale locale) {
        String msg = messageSource.getMessage(
                com.wms.goals.i18n.I18nMessageCollection.MISSING_OR_INVALID_TOKEN.getKey(), null, locale);
        return ResponseEntity.badRequest().body(ApiResponseUtil.validationMessages(java.util.List.of(msg)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleIllegalArg(IllegalArgumentException ex, Locale locale) {
        String code = ex.getMessage();
        String msg = messageSource.getMessage(code, null, code, locale);
        return ResponseEntity.badRequest().body(ApiResponseUtil.error(code, msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<Void>> handleOther(Exception ex, Locale locale) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseUtil.error(
                        com.wms.goals.i18n.I18nMessageCollection.INTERNAL_ERROR.name(),
                        messageSource.getMessage(
                                com.wms.goals.i18n.I18nMessageCollection.INTERNAL_ERROR.getKey(), null, locale)
                )
        );
    }
}
