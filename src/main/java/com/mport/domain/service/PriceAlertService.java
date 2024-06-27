package com.mport.domain.service;

import com.mport.dao.CommonDao;
import com.mport.domain.dto.PriceAlertDTO;
import com.mport.domain.dto.SimpleDTO;
import com.mport.domain.exception.DataNotFoundException;
import com.mport.domain.model.PriceAlert;
import com.mport.domain.repository.PriceAlertRepository;
import com.mport.domain.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class PriceAlertService {

    private final PriceAlertRepository priceAlertRepository;
    private final CommonDao commonDao;
    private final ModelMapper modelMapper;

    public PriceAlertService(PriceAlertRepository priceAlertRepository, CommonDao commonDao, ModelMapper modelMapper) {
        this.priceAlertRepository = priceAlertRepository;
        this.commonDao = commonDao;
        this.modelMapper = modelMapper;
    }

    public List<PriceAlertDTO> getPriceAlertList() {
        List<PriceAlert> priceAlerts = priceAlertRepository.findByActiveOrderBySymbolAscConditionDescPriceAsc(true);
        List<PriceAlertDTO> priceAlertDTOS = modelMapper.map(priceAlerts, new TypeToken<List<PriceAlertDTO>>() {
        }.getType());
        return priceAlertDTOS;
    }

    public PriceAlert getPriceAlert(Long id) throws DataNotFoundException {
        return priceAlertRepository.findByIdAndActive(id, true).orElseThrow(()
                -> new DataNotFoundException("PriceAlert id {" + id + "} doesn't exist."));
    }

    public PriceAlertDTO getPriceAlertDTO(Long id) throws DataNotFoundException {
        return modelMapper.map(getPriceAlert(id), PriceAlertDTO.class);
    }

    public PriceAlertDTO create(PriceAlertDTO priceAlertDTO) {
        Date now = DateTimeUtil.now();
        PriceAlert priceAlert = modelMapper.map(priceAlertDTO, PriceAlert.class);
        priceAlert.setCreatedAt(now);
        priceAlert.setUpdatedAt(now);
        priceAlertRepository.save(priceAlert);
        return modelMapper.map(priceAlert, PriceAlertDTO.class);
    }

    public void update(Long id, PriceAlertDTO priceAlertDTO) throws DataNotFoundException {
        Date now = DateTimeUtil.now();
        PriceAlert priceAlert = getPriceAlert(id);
        modelMapper.map(priceAlertDTO, priceAlert);

        priceAlert.setUpdatedAt(now);
        priceAlertRepository.save(priceAlert);
    }

    public void delete(Long id) throws DataNotFoundException {
        Date now = DateTimeUtil.now();
        PriceAlert priceAlert = getPriceAlert(id);
        priceAlert.setActive(false);
        priceAlert.setUpdatedAt(now);
        priceAlertRepository.save(priceAlert);
    }

    public List<SimpleDTO> getAllSymbol() {
        List<SimpleDTO> symbols = commonDao.findAllSymbol();
        return symbols;
    }

}
