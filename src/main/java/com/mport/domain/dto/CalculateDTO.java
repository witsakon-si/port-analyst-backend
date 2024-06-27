package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CalculateDTO implements Serializable {
    private List<Integer> groupIndex;
    private BigDecimal totalUnit;
    private BigDecimal totalAmount;
    private BigDecimal totalNetAmount;
    private BigDecimal totalNetAmountBuy;
    private BigDecimal totalNetAmountSell;
    private BigDecimal totalCost;
    private BigDecimal totalCommission;
    private BigDecimal totalFee;
    private BigDecimal totalClearingFee;
    private BigDecimal totalVat;
    private BigDecimal totalCashBalance;
}
