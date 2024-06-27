package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FullPriceRes implements Serializable {
    private FxPriceDTO fullPrice;
    private PriceDTO refPrice;
}
