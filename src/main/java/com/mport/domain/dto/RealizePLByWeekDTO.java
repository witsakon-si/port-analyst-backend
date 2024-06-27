package com.mport.domain.dto;

import com.mport.domain.util.DateTimeUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RealizePLByWeekDTO extends RealizePLDTO implements Serializable {
    private Date startDate;
    private Date endDate;

    public RealizePLByWeekDTO(Date endDate) {
        this.startDate = DateTimeUtil.getFirstDateOfWeek(endDate);
        this.endDate = DateTimeUtil.datePlusDay(this.startDate, 5);
    }
}
