package com.mport.domain.dto;

import lombok.Data;

@Data
public class DailySumDTORes extends ResponseDTO {
    DailySumByTypeDTO result;
}
