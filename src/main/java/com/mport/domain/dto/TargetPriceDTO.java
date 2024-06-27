package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TargetPriceDTO implements Serializable {
    private BigDecimal price;
    private String change;
    private String percentChange;
    private BigDecimal refPrice;
    private BigDecimal refCurr;
    private boolean closest;
}
