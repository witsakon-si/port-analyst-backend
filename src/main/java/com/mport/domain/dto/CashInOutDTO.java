package com.mport.domain.dto;

import com.mport.domain.enums.CashType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CashInOutDTO implements Serializable {
    private Long id;
    private CashType cashType;
    private Date transactionDate;
    private BigDecimal amount;
    private String account;
    private String remark;
    private boolean dividend;
}
