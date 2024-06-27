package com.mport.domain.dto;

import lombok.Data;

import java.util.List;


@Data
public class TransactionRes extends ResponseDTO {
    List<TransactionDTO> result;
    List<SimpleDTO> assetList;
    List<String> groupList;
    List<RealizePLByYearTypeDTO> realizePLByYearType;
    List<RealizePLByWeekDTO> realizePLByWeek;
    List<RealizePLStockDTO> realizePL;
}
