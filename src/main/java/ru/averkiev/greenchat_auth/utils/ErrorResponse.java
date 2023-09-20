package ru.averkiev.greenchat_auth.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс обертка для отправки ошибок в ответе.
 * @author mrGreenNV
 */
@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Сущность ответа с ошибкой")
public class ErrorResponse {

    /** Временная метка, когда произошла ошибка. */
    @Schema(description = "Временная метка, когда произошла ошибка.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /** HTTP статус ошибки. */
    @Schema(description = "HTTP статус ошибки.")
    private HttpStatus status;

    /** Краткое описание ошибки. */
    @Schema(description = "Краткое описание ошибки.")
    private String error;

    /** Сообщение об ошибке. */
    @Schema(description = "Сообщение об ошибке")
    private String errorMessage;

    /** Путь к ресурсу, который вызвал ошибку. */
    @Schema(description = "Путь к ресурсу, который вызвал ошибку.")
    private String path;

    /** Список ошибок */
    @Schema(description = "Список сообщений ошибок")
    private List<String> errors;

    /**
     * Конструктор позволяет создать объект ErrorResponse.
     * @param status Http статус ошибки.
     * @param errorMessage сообщение ошибки.
     * @param path путь к ресурсу, который вызвал ошибку.
     */
    public ErrorResponse(HttpStatus status, String errorMessage, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = status.getReasonPhrase();
        this.errorMessage = errorMessage;
        this.path = path;
    }

    /**
     * Конструктор позволяет создать объект ErrorResponse для валидации данных.
     * @param status Http статус ошибки.
     * @param errorMessage сообщение ошибки.
     * @param path путь к ресурсу, который вызвал ошибку.
     * @param errors список ошибок при валидации данных.
     */
    public ErrorResponse(HttpStatus status, String errorMessage, String path, List<String> errors) {
        this.status = status;
        this.errorMessage = errorMessage;
        this.path = path;
        this.errors = errors;
    }
}