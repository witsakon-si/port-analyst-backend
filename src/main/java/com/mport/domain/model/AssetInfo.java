package com.mport.domain.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@AllArgsConstructor
public class AssetInfo {
    @Id
    private String name;
    private String url;
    private String refName;
    private String refURL;
    private String fullName;
    private boolean vi;
}
