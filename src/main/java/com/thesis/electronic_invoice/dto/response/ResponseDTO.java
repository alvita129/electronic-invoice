package com.thesis.electronic_invoice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {
    private Boolean success;
    private String message;
    private T data;
    private Instant timestamp;
    private Integer status;
    private Map<String, String> details;

    public static <T> ResponseDTO<T> success(String message, T data) {
        return new ResponseDTO<>(true, message, data, Instant.now(), null, Map.of());
    }

    public static <T> ResponseDTO<T> error(Integer status, String message, Map<String, String> details) {
        return new ResponseDTO<>(false, message, null, Instant.now(), status,
                details == null ? Map.of() : details);
    }
}
