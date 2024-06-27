package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class DailySumByTypeDTO implements Serializable {
    List<String> dateLabel;
    Map<String, List<BigDecimal>> profitLoss;
}
