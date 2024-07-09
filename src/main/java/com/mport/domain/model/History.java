package com.mport.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@AllArgsConstructor
public class History extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String side;
    private String type;
    private String name;
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
    private boolean interest;
    private Long cashInOutId;
    private String orderMatch;
}
