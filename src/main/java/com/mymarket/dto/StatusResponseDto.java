package com.mymarket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusResponseDto {

    private String message;
    private Integer statusCode;

    public StatusResponseDto(String message, Integer statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }


}
