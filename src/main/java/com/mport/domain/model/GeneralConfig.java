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
public class GeneralConfig {
    @Id
    private String name;
    private String value;
}
