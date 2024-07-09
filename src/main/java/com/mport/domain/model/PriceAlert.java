package com.mport.domain.model;

import com.mport.domain.enums.PriceAlertCond;
import com.mport.domain.enums.PriceAlertFreq;
import jakarta.persistence.*;
import lombok.*;

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
