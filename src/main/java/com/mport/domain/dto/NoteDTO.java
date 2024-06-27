package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class NoteDTO implements Serializable {
    private String name;
    private String note;
    private Date updatedAt;
}
