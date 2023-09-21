package ru.averkiev.greenchat_auth.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.averkiev.greenchat_auth.utils.ErrorResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс отлавливает все исключения возникающие на уровне контроллера, для предоставления ошибки клиенту в виде JSON.
 * @author mrGreenNV
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Позволяет обработать ошибки связанные с валидацией пользовательских данных.
     * @param ex ошибки при валидации данных.
     * @param request HTTP запрос.
     * @return ResponseEntity, содержащий информацию об ошибке.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        List<String> errorMessages = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Ошибки при валидации данных",
                request.getRequestURI(),
                errorMessages
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * Позволяет обработать ошибки связанные с аутентификацией пользователя.
     * @param authEx ошибка при аутентификации пользователя.
     * @param request HTTP запрос.
     * @return ResponseEntity, содержащий информацию об ошибке.
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthException authEx, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.FORBIDDEN,
                authEx.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * Позволяет обработать ошибки связанные с поиском токена.
     * @param tnfEx ошибка при поиске токена.
     * @param request HTTP запрос.
     * @return ResponseEntity, содержащий информацию об ошибке.
     */
    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleActivityFeedException(TokenNotFoundException tnfEx, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                tnfEx.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * Позволяет обработать ошибки связанные с поиском пользователя.
     * @param unfEx ошибка при поиске пользователя.
     * @param request HTTP запрос.
     * @return ResponseEntity, содержащий информацию об ошибке.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException unfEx, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND,
                unfEx.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * Позволяет обработать прочие ошибки возникшие при взаимодействии с сервисом.
     * @param ex ошибка при взаимодействии с сервисом.
     * @param request HTTP запрос.
     * @return ResponseEntity, содержащий информацию об ошибке.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
