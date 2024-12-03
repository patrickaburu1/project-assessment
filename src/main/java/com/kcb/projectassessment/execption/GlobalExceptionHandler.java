package com.kcb.projectassessment.execption;

import com.kcb.projectassessment.utils.ResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ResponseModel<Object>> handleRecordNotFoundException(RecordNotFoundException ex) {
        ResponseModel<Object> responseModel = ResponseModel.builder()
                .status("error")
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(responseModel, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(UnProcessingRequestException.class)
    public ResponseEntity<ResponseModel<Object>> handleUnprocessedRequestFoundException(UnProcessingRequestException ex) {
        ResponseModel<Object> responseModel = ResponseModel.builder()
                .status("error")
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(responseModel, HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @ExceptionHandler(UnAuthorizedRequestException.class)
    public ResponseEntity<ResponseModel<Object>> handleUnAuthorizedRequestException(UnAuthorizedRequestException ex) {
        ResponseModel<Object> responseModel = ResponseModel.builder()
                .status("error")
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(responseModel, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(RecordAlreadyExistsException.class)
    public ResponseEntity<ResponseModel<Object>> handleRecordAlreadyExistsException(RecordAlreadyExistsException ex) {
        ResponseModel<Object> responseModel = ResponseModel.builder()
                .status("error")
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(responseModel, HttpStatus.CONFLICT);
    }

}