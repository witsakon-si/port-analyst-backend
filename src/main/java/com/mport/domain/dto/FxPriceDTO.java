package com.mport.domain.dto;

import com.mport.domain.util.DateTimeUtil;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class FxPriceDTO implements Serializable {
    private String symbol;
    private BigDecimal lastClose;
    private BigDecimal open;
    private BigDecimal current;
    private BigDecimal high;
    private BigDecimal low;
    private String change;
    private String percentChange;
    private Date lastUpdate;
    private boolean underlying;
    private List<TargetPriceDTO> targetPrices;

    public FxPriceDTO() {
        this.lastClose = BigDecimal.ZERO;
        this.current = BigDecimal.ZERO;
        this.high = BigDecimal.ZERO;
        this.low = BigDecimal.ZERO;
        this.change = "0.00";
        this.percentChange = "0.00";
        this.lastUpdate = DateTimeUtil.now();
    }
}
