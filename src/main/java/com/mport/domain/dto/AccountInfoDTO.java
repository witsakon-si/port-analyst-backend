package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AccountInfoDTO implements Serializable {
    private BigDecimal cost;
    private BigDecimal balance;
    private BigDecimal netBalance;
    private BigDecimal profitLoss;
    private BigDecimal percentPL;
    private BigDecimal cashBalance;     // exclude dividend
    private BigDecimal netCashBalance;  // cashBalance - buy + sell
    private BigDecimal netCommission;
    private BigDecimal netFee;
    private BigDecimal netVat;
    private BigDecimal netClearingFee;

    public AccountInfoDTO() {
        this.cost = BigDecimal.ZERO;
        this.balance = BigDecimal.ZERO;
        this.netBalance = BigDecimal.ZERO;
        this.profitLoss = BigDecimal.ZERO;
        this.percentPL = BigDecimal.ZERO;
        this.cashBalance = BigDecimal.ZERO;
        this.netCashBalance = BigDecimal.ZERO;
        this.netCommission = BigDecimal.ZERO;
        this.netFee = BigDecimal.ZERO;
        this.netVat = BigDecimal.ZERO;
        this.netClearingFee = BigDecimal.ZERO;
    }
}
