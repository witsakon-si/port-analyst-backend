package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AssetInfoDTO implements Serializable {
    private String name;
    private String url;
    private String refName;
    private String refURL;
    private Boolean isEdit;
    private String fullName;
    private boolean vi;
}
