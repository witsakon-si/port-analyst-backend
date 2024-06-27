package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AvailHistoryListDTO implements Serializable {
    private String name;
    private List<AvailHistoryDTO> history;

    public AvailHistoryListDTO(String name, List<AvailHistoryDTO> history) {
        this.name = name;
        this.history = history;
    }
}
