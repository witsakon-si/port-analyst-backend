package com.mport.domain.service;

import com.mport.domain.dto.AccountInfoDTO;
import com.mport.domain.dto.CashInOutByYearDTO;
import com.mport.domain.dto.CashInOutDTO;
import com.mport.domain.enums.CashType;
import com.mport.domain.exception.DataNotFoundException;
import com.mport.domain.model.CashInOut;
import com.mport.domain.repository.CashInOutRepository;
import com.mport.domain.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class CashInOutService {

    private final CashInOutRepository cashInOutRepository;
    private final ModelMapper modelMapper;

    public CashInOutService(CashInOutRepository cashInOutRepository, ModelMapper modelMapper) {
        this.cashInOutRepository = cashInOutRepository;
        this.modelMapper = modelMapper;
    }

    public List<CashInOutDTO> getCashInOutList(String account) {
        List<CashInOut> cashInOuts = cashInOutRepository.findByAccountAndActiveOrderByTransactionDateAsc(account, true);
        List<CashInOutDTO> cashInOutDTOS = modelMapper.map(cashInOuts, new TypeToken<List<CashInOutDTO>>() {
        }.getType());
        return cashInOutDTOS;
    }

    public List<CashInOutByYearDTO> getCashInOutByYear() {
        List<CashInOut> cashInOuts = cashInOutRepository.findByActiveOrderByAccount(true);
        List<CashInOutDTO> cashInOutDTOS = modelMapper.map(cashInOuts, new TypeToken<List<CashInOutDTO>>() {
        }.getType());

        TreeMap<Integer, TreeMap<String, CashInOutByYearDTO>> mapCashInOut = new TreeMap<>();
        for (CashInOutDTO cashInOutDTO : cashInOutDTOS) {
            int year = DateTimeUtil.getYear(cashInOutDTO.getTransactionDate());
            String type = cashInOutDTO.getAccount();
            if (!mapCashInOut.containsKey(year)) {
                mapCashInOut.put(year, new TreeMap<>(Collections.reverseOrder()));
            }
            if (!mapCashInOut.get(year).containsKey(type)) {
                mapCashInOut.get(year).put(type, new CashInOutByYearDTO(year, type));
            }
            if (cashInOutDTO.getCashType() == CashType.DEPOSIT) {
                mapCashInOut.get(year).get(type).addCashIn(cashInOutDTO.getAmount());
                mapCashInOut.get(year).get(type).addNetCash(cashInOutDTO.getAmount());
                if (cashInOutDTO.isDividend()) {
                    mapCashInOut.get(year).get(type).addCashOutDividend(cashInOutDTO.getAmount());
                }
            } else {
                mapCashInOut.get(year).get(type).addCashOut(cashInOutDTO.getAmount());
                mapCashInOut.get(year).get(type).subtractNetCash(cashInOutDTO.getAmount());
                if (cashInOutDTO.isDividend()) {
                    mapCashInOut.get(year).get(type).addCashOutDividend(cashInOutDTO.getAmount());
                }
            }
        }

        List<CashInOutByYearDTO> result = new ArrayList<>();
        List<CashInOutByYearDTO> cashInOutByYears;
        for (Map.Entry<Integer, TreeMap<String, CashInOutByYearDTO>> entry : mapCashInOut.entrySet()) {
            cashInOutByYears = new ArrayList<>();
            for (Map.Entry<String, CashInOutByYearDTO> subEntry : entry.getValue().entrySet()) {
                result.add(subEntry.getValue());
                cashInOutByYears.add(subEntry.getValue());
            }

            BigDecimal netCashYear = cashInOutByYears.stream()
                    .map(m -> m.getCashIn().subtract(m.getCashOut()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal dividendYear = cashInOutByYears.stream()
                    .map(m -> m.getCashOutDividend())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            for (CashInOutByYearDTO cash : cashInOutByYears) {
                cash.setNetCashYear(netCashYear);
                cash.setDividendYear(dividendYear);
            }
        }
        return result;
    }

    public Map<String, AccountInfoDTO> getAllAccountInfo() {
        List<CashInOut> cashInOuts = cashInOutRepository.findByActiveOrderByAccount(true);
        List<CashInOutDTO> cashInOutDTOS = modelMapper.map(cashInOuts, new TypeToken<List<CashInOutDTO>>() {
        }.getType());
        Map<String, AccountInfoDTO> accountInfoMap = new HashMap<>();
        Map<String, List<CashInOutDTO>> groupByAccount = cashInOutDTOS.stream().collect(Collectors.groupingBy(CashInOutDTO::getAccount));
        for (Map.Entry<String, List<CashInOutDTO>> entry : groupByAccount.entrySet()) {
            String account = entry.getKey();
            AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
            List<CashInOutDTO> cashInOutDTOs = entry.getValue();
            for (CashInOutDTO cashInOutDTO : cashInOutDTOs) {
                switch (cashInOutDTO.getCashType()) {
                    case DEPOSIT:
                        accountInfoDTO.setCost(accountInfoDTO.getCost().add(cashInOutDTO.getAmount()));
                        accountInfoDTO.setBalance(accountInfoDTO.getBalance().add(cashInOutDTO.getAmount()));
                        accountInfoDTO.setNetBalance(accountInfoDTO.getNetBalance().add(cashInOutDTO.getAmount()));
                        accountInfoDTO.setCashBalance(accountInfoDTO.getCashBalance().add(cashInOutDTO.getAmount()));
                        accountInfoDTO.setNetCashBalance(accountInfoDTO.getNetCashBalance().add(cashInOutDTO.getAmount()));
                        break;
                    case WITHDRAW:
                        accountInfoDTO.setBalance(accountInfoDTO.getBalance().subtract(cashInOutDTO.getAmount()));
                        accountInfoDTO.setNetBalance(accountInfoDTO.getNetBalance().subtract(cashInOutDTO.getAmount()));
                        if (cashInOutDTO.isDividend()) {
                            accountInfoDTO.setCost(accountInfoDTO.getCost().subtract(cashInOutDTO.getAmount()));
                        } else {
                            accountInfoDTO.setCashBalance(accountInfoDTO.getCashBalance().subtract(cashInOutDTO.getAmount()));
                            accountInfoDTO.setNetCashBalance(accountInfoDTO.getNetCashBalance().subtract(cashInOutDTO.getAmount()));
                        }
                        break;
                }
            }
            accountInfoMap.put(account, accountInfoDTO);
        }
        return accountInfoMap;
    }

    public CashInOut getCashInOut(Long id) throws DataNotFoundException {
        return cashInOutRepository.findByIdAndActive(id, true).orElseThrow(()
                -> new DataNotFoundException("CashInOut id {" + id + "} doesn't exist."));
    }

    public CashInOutDTO getCashInOutDTO(Long id) throws DataNotFoundException {
        CashInOut cashInOut = getCashInOut(id);
        CashInOutDTO cashInOutDTO = modelMapper.map(cashInOut, CashInOutDTO.class);
        return cashInOutDTO;
    }

    public CashInOutDTO create(CashInOutDTO cashInOutDTO) {
        Date now = DateTimeUtil.now();
        CashInOut cashInOut = modelMapper.map(cashInOutDTO, CashInOut.class);
        cashInOut.setCreatedAt(now);
        cashInOut.setUpdatedAt(now);
        cashInOutRepository.save(cashInOut);
        return modelMapper.map(cashInOut, CashInOutDTO.class);
    }

    public void update(Long id, CashInOutDTO cashInOutDTO) throws DataNotFoundException {
        Date now = DateTimeUtil.now();
        CashInOut cashInOut = getCashInOut(id);
        modelMapper.map(cashInOutDTO, cashInOut);

        cashInOut.setUpdatedAt(now);
        cashInOutRepository.save(cashInOut);
    }

    public void delete(Long historyId) throws DataNotFoundException {
        Date now = DateTimeUtil.now();
        CashInOut cashInOut = getCashInOut(historyId);
        cashInOut.setActive(false);
        cashInOut.setUpdatedAt(now);
        cashInOutRepository.save(cashInOut);
    }

}
