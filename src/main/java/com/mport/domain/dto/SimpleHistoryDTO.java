package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SimpleHistoryDTO implements Serializable {
    private String type;
    private String name;
    private BigDecimal realizePL;
    private String periodHold;
}
