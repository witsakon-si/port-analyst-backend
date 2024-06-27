package com.mport.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HistoryDTO implements Serializable {
    private Long id;
    private String side;
    private String name;
    private String type;
    private Date transactionDate;
    private BigDecimal amount;
    private BigDecimal netAmount;
    private BigDecimal unit;
    private BigDecimal unitPrice;
    private BigDecimal commission;
    private BigDecimal fee;
    private BigDecimal vat;
    private BigDecimal commissionRate;
    private BigDecimal vatRate;
    private BigDecimal feeRate;
    private BigDecimal clearingFee;
    private BigDecimal clearingFeeRate;
    private boolean dividend;
    private boolean dividendSt;
    private boolean interest;
    private Long cashInOutId;
    private String orderMatch;

    // only output
    private BigDecimal cost;        // cashIn (deposit) - cashOut (withdraw)
    private BigDecimal realizePL;
    private BigDecimal percentPL;
    private BigDecimal unPL;
    private BigDecimal percentUnPL;
    private BigDecimal mktPrice;
    private Date mktPriceDt;
    private BigDecimal refPrice;
    private String status;
    private String periodHold;
    private String startDate;
    private String endDate;
    private int day;
    private int month;
    private int year;
    private BigDecimal totalDividend;

    private BigDecimal availUnit;
    private BigDecimal availNetAmount;
    private BigDecimal availDividend;

    private BigDecimal cashBalance;

    // only calculate
    private List<Integer> groupIndex;
}
