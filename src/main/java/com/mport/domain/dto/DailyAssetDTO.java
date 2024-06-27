package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class DailyAssetDTO implements Serializable {
    private String type;
    private String name;
    private BigDecimal cost;
    private BigDecimal netBalance;
    private BigDecimal profitLoss;
    private Date date;
}
