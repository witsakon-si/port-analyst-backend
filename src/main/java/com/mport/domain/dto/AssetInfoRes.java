package com.mport.domain.dto;

import lombok.Data;

import java.util.List;


@Data
public class AssetInfoRes extends ResponseDTO {
    List<AssetInfoDTO> result;
}
