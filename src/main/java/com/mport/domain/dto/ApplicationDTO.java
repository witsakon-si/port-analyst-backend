package com.mport.domain.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class ApplicationDTO implements Serializable {
    private Map<String, AssetRefThDTO> assetRefTh;

    public ApplicationDTO() {

    }
}
