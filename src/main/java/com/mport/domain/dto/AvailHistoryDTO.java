package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class AvailHistoryDTO implements Serializable {
    private Date transactionDate;
    private String type;
    private String side;
    private boolean dividend;
    private BigDecimal unit;
    private BigDecimal netAmount;

    private BigDecimal availUnit;
    private BigDecimal availNetAmount;
    private BigDecimal availDividend;
}
