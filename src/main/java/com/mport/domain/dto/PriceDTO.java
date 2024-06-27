package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PriceDTO implements Serializable {
    private String name;
    private BigDecimal price;
    private Date syncDate;
}
