package com.mport.domain.dto;

import lombok.Data;

import java.util.List;


@Data
public class AvailHistoryRes extends ResponseDTO {
    List<AvailHistoryListDTO> result;
}
