package com.mport.domain.service;

import com.mport.domain.dto.DailySumByTypeDTO;
import com.mport.domain.dto.DailySumDTO;
import com.mport.domain.model.DailySum;
import com.mport.domain.repository.DailySumRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class DailySumService {

    private final DailySumRepository dailySumRepository;
    private final ModelMapper modelMapper;
    private final DateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final BigDecimal ZERO = BigDecimal.ZERO;

    public DailySumService(DailySumRepository dailySumRepository, ModelMapper modelMapper) {
        this.dailySumRepository = dailySumRepository;
        this.modelMapper = modelMapper;
    }

    public DailySumByTypeDTO getDailySumList() {
        List<DailySum> dailySumList = dailySumRepository.findAll();
        List<DailySumDTO> dailySumDTOS = modelMapper.map(dailySumList, new TypeToken<List<DailySumDTO>>() {
        }.getType());
        List<String> dateLabel = null;
        Map<String, List<BigDecimal>> resultPL = new HashMap<>();
        Map<String, List<DailySumDTO>> groupByType = dailySumDTOS.stream().collect(Collectors.groupingBy(DailySumDTO::getType));
        int days = 0;
        for (Map.Entry<String, List<DailySumDTO>> entry : groupByType.entrySet()) {
            days = Math.max(entry.getValue().size(), days);
        }
        for (Map.Entry<String, List<DailySumDTO>> entry : groupByType.entrySet()) {
            List<DailySumDTO> sumDTOS = entry.getValue();
            if (sumDTOS.size() > 0 && (sumDTOS.get(sumDTOS.size() - 1).getNetBalance().compareTo(BigDecimal.ZERO) != 0)) {
                List<BigDecimal> pl = new ArrayList<>();
                if (entry.getValue().size() < days) {
                    for (int i = entry.getValue().size(); i < days; i++) {
                        pl.add(ZERO);
                    }
                }
                for (DailySumDTO sumDTO : entry.getValue()) {
                    pl.add(sumDTO.getProfitLoss());
                }
                resultPL.put(entry.getKey(), pl);
                if (dateLabel == null) {
                    dateLabel = entry.getValue().stream().map(x -> displayDateFormat.format(x.getDate())).collect(Collectors.toList());
                }
            }
        }

        DailySumByTypeDTO sumByTypeDTO = new DailySumByTypeDTO();
        sumByTypeDTO.setDateLabel(dateLabel);
        sumByTypeDTO.setProfitLoss(resultPL);

        return sumByTypeDTO;
    }

}
