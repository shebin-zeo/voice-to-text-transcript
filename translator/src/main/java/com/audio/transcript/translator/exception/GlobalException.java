package com.audio.transcript.translator.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.util.Map;

@RestControllerAdvice
public class GlobalException {



    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<Map<Object,Object>> handleFileSizeUpload()
    {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(
                Map.of("Message","Maximum allowed file size is 50MB")
        );
    }

    @ExceptionHandler(MultipartException.class)
    ResponseEntity<Map<Object,Object>> handleInvalidFileUpload()
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("Message","Please Upload Valid Audio File")
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<Object,Object>> handleRuntimeException(
            RuntimeException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        Map.of(
                                "Message",
                                ex.getMessage()
                        )
                );
    }


}
