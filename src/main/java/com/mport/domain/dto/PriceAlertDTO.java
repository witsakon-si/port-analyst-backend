package com.mport.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mport.domain.enums.PriceAlertCond;
import com.mport.domain.enums.PriceAlertFreq;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class PriceAlertDTO implements Serializable {
    private Long id;
    private String symbol;
    private BigDecimal price;
    private PriceAlertCond condition;
    private PriceAlertFreq frequency;
    private String note;
}
