package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class MonthlyPLAssetDTO implements Serializable {
    List<String> monthLabel;    // format: mm/yy
    Map<String, List<List<BigDecimal>>> profitLoss;
}
