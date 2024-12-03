package com.kcb.projectassessment.utils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseModel<T> {

    private String status;
    private String message;
    private T data;
}
