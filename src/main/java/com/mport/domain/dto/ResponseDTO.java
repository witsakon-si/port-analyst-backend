package com.mport.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mport.domain.enums.ResponseCode;
import lombok.Data;

@Data
public class ResponseDTO {
    private String message;
    private ResponseCode responseCode;

    @JsonIgnore
    private String token;
}

