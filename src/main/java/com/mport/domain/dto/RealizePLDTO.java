package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class RealizePLDTO implements Serializable {
    private BigDecimal realizePL;
    private List<SimpleHistoryDTO> details;

    public RealizePLDTO() {
        this.realizePL = BigDecimal.ZERO;
        this.details = new ArrayList<>();
    }

    public void addRealizePL(BigDecimal add) {
        this.realizePL = this.realizePL.add(add);
    }

    public void addDetail(SimpleHistoryDTO detail) {
        this.details.add(detail);
    }

}
