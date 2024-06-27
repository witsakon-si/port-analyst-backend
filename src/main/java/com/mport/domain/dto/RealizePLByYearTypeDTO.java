package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RealizePLByYearTypeDTO implements Serializable {
    private int year;
    private String type;
    private BigDecimal realizePL;
    private BigDecimal realizePLYear;

    public RealizePLByYearTypeDTO(int year) {
        this.year = year;
        this.realizePL = BigDecimal.ZERO;
        this.realizePLYear = BigDecimal.ZERO;
    }

    public void addRealizePL(BigDecimal add) {
        this.realizePL = this.realizePL.add(add);
    }

}
