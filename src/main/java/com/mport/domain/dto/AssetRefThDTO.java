package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AssetRefThDTO implements Serializable {
    private String market;
    private String symbol;
    private String symbolTh;
    private String currency;
    private BigDecimal divisor;
}
