package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SimpleDTO implements Serializable {
    private String code;
    private String name;

    public SimpleDTO() {
    }

    public SimpleDTO(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
