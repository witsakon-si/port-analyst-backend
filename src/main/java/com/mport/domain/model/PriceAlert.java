package com.mport.domain.model;

import com.mport.domain.enums.PriceAlertCond;
import com.mport.domain.enums.PriceAlertFreq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@AllArgsConstructor
public class PriceAlert extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String symbol;
    private BigDecimal price;
    private String note;
    private Date lastNotice;

    @Enumerated(EnumType.STRING)
    private PriceAlertCond condition;

    @Enumerated(EnumType.STRING)
    private PriceAlertFreq frequency;
}
