package com.mport.domain.model;

import com.mport.domain.enums.CashType;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@AllArgsConstructor
public class CashInOut extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CashType cashType;
    private Date transactionDate;
    private BigDecimal amount;
    private String account;
    private String remark;
    private boolean dividend;
}
