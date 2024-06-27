package com.mport.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;

@Data
public class TransactionDTO implements Serializable {
    private String type;
    private AccountInfoDTO accountInfo;
    private TreeMap<Integer, TreeMap<String, RealizePLByYearTypeDTO>> mapRealizePLByYear;
    private TreeMap<Integer, TreeMap<Integer, RealizePLByWeekDTO>> mapRealizePLByWeek;
    private TreeMap<String, RealizePLStockDTO> mapRealizePL;
    List<HistoryDTO> list;
}
