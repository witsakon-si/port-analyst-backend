package com.mport.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@AllArgsConstructor
public class Note {
    @Id
    private String name;

    private String note;
    private Date updatedAt;
}
