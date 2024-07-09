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
public class DailySum {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String type;
    private BigDecimal cost;
    private BigDecimal netBalance;
    private BigDecimal profitLoss;
    private BigDecimal percentPL;
    private Date date;
    private Date createdAt;
    private String remark;

}
