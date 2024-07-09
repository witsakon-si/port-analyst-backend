package com.mport.domain.model;

import lombok.*;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@AllArgsConstructor
public class Price {
    @Id
    private String name;

    private BigDecimal price;
    private Date syncDate;
    private boolean successSync;
}
