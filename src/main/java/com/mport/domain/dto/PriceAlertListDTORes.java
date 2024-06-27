package com.mport.domain.dto;

import lombok.Data;

import java.util.List;
import java.util.TreeMap;


@Data
public class PriceAlertListDTORes extends ResponseDTO {
    List<SimpleDTO> conditionType;
    List<SimpleDTO> noticeFrequency;
    List<SimpleDTO> symbolList;
    List<PriceAlertDTO> result;
    List<FxPriceDTO> realtimePrice;
    TreeMap<String, AssetRefThDTO> assetInfo;
}
