package com.mport.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

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
