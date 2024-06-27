package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RealizePLStockDTO extends RealizePLDTO implements Serializable {
    private String name;

    public RealizePLStockDTO(String name) {
        this.name = name;
    }

}
