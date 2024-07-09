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
public class DailyAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String type;
    private String name;
    private BigDecimal cost;
    private BigDecimal netBalance;
    private BigDecimal profitLoss;
    private Date date;
    private Date createdAt;
    private String remark;

}
