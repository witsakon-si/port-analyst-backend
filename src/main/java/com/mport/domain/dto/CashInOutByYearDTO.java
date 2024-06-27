package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CashInOutByYearDTO implements Serializable {
    private int year;
    private String type;
    private BigDecimal cashIn;
    private BigDecimal cashOut;
    private BigDecimal cashOutDividend;
    private BigDecimal netCash;
    private BigDecimal netCashYear;
    private BigDecimal dividendYear;

    public CashInOutByYearDTO(int year, String type) {
        this.year = year;
        this.type = type;
        this.cashIn = BigDecimal.ZERO;
        this.cashOut = BigDecimal.ZERO;
        this.cashOutDividend = BigDecimal.ZERO;
        this.netCash = BigDecimal.ZERO;
        this.netCashYear = BigDecimal.ZERO;
        this.dividendYear = BigDecimal.ZERO;
    }

    public void addCashIn(BigDecimal add) {
        this.cashIn = this.cashIn.add(add);
    }

    public void addCashOut(BigDecimal add) {
        this.cashOut = this.cashOut.add(add);
    }

    public void addCashOutDividend(BigDecimal add) {
        this.cashOutDividend = this.cashOutDividend.add(add);
    }

    public void addNetCash(BigDecimal add) {
        this.netCash = this.netCash.add(add);
    }

    public void subtractNetCash(BigDecimal subtract) {
        this.netCash = this.netCash.subtract(subtract);
    }

}
