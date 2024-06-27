package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class DailySumDTO implements Serializable {
    private String type;
    private BigDecimal cost;
    private BigDecimal netBalance;
    private BigDecimal percentPL;
    private BigDecimal profitLoss;
    private Date date;
}

