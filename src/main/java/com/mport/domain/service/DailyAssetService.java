package com.mport.domain.service;

import com.mport.domain.dto.DailyAssetDTO;
import com.mport.domain.dto.MonthlyPLAssetDTO;
import com.mport.domain.model.DailyAsset;
import com.mport.domain.repository.DailyAssetRepository;
import com.mport.domain.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class DailyAssetService {

    private final DailyAssetRepository dailyAssetRepository;
    private final ModelMapper modelMapper;
    private final DateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final DateFormat monthYearFormat = new SimpleDateFormat("yyyy-MM");

    public DailyAssetService(DailyAssetRepository dailyAssetRepository, ModelMapper modelMapper) {
        this.dailyAssetRepository = dailyAssetRepository;
        this.modelMapper = modelMapper;
    }

    public MonthlyPLAssetDTO getMonthlyPL(String type, String symbol) {
        List<DailyAsset> dailyAssetList = symbol.isEmpty() ? dailyAssetRepository.findByType(type) : dailyAssetRepository.findByTypeAndName(type, symbol);
        List<DailyAssetDTO> dailyAssetDTOS = modelMapper.map(dailyAssetList, new TypeToken<List<DailyAssetDTO>>() {
        }.getType());

        TreeMap<String, DailyAssetDTO> mapMaxPL = new TreeMap<>();      // key: NAME_YY/MM, value: object max abs(profitLoss)
        TreeMap<String, DailyAssetDTO> mapMinPL = new TreeMap<>();      // key: NAME_YY/MM, value: object min abs(profitLoss)
        String key;
        for (DailyAssetDTO dailyAssetDTO : dailyAssetDTOS) {
            key = dailyAssetDTO.getName() + "_" + monthYearFormat.format(dailyAssetDTO.getDate());
            if (!mapMaxPL.containsKey(key) || (dailyAssetDTO.getProfitLoss().compareTo(mapMaxPL.get(key).getProfitLoss()) > 0)) {
                mapMaxPL.put(key, dailyAssetDTO);
            }
            if (!mapMinPL.containsKey(key) || (dailyAssetDTO.getProfitLoss().compareTo(mapMaxPL.get(key).getProfitLoss()) < 0)) {
                mapMinPL.put(key, dailyAssetDTO);
            }
        }

        String strDate;
        List<String> monthLabel = new ArrayList<>();
        Date minDate = dailyAssetDTOS.stream().map(DailyAssetDTO::getDate).min(Date::compareTo).get();
        Date maxDate = dailyAssetDTOS.stream().map(DailyAssetDTO::getDate).max(Date::compareTo).get();
        List<String> names = dailyAssetDTOS.stream().map(DailyAssetDTO::getName).collect(Collectors.toList());

        // Add 0
        for (Date date = minDate; !date.after(maxDate); ) {
            strDate = monthYearFormat.format(date);
            monthLabel.add(strDate);
            for (String name : names) {
                if (!mapMaxPL.containsKey(name + "_" + strDate)) {
                    DailyAssetDTO addDTO = new DailyAssetDTO();
                    addDTO.setProfitLoss(BigDecimal.ZERO);
                    addDTO.setName(name);
                    addDTO.setDate(date);
                    mapMaxPL.put(name + "_" + strDate, addDTO);
                }
            }
            date = DateTimeUtil.datePlusMonth(date, 1);
        }

        Map<String, List<List<BigDecimal>>> resultPL = new HashMap<>();
        for (Map.Entry<String, DailyAssetDTO> entry : mapMaxPL.entrySet()) {
            key = entry.getKey().split("_")[0];
            if (!resultPL.containsKey(key)) {
                resultPL.put(key, new ArrayList<>());
            }
            BigDecimal max = entry.getValue().getProfitLoss();
            BigDecimal min = mapMinPL.get(entry.getKey()).getProfitLoss();
            resultPL.get(key).add(List.of(min, max));
        }


        MonthlyPLAssetDTO monthlyPLAssetDTO = new MonthlyPLAssetDTO();
        monthlyPLAssetDTO.setMonthLabel(monthLabel);
        monthlyPLAssetDTO.setProfitLoss(resultPL);
        log.debug("");

        return monthlyPLAssetDTO;
    }

}
