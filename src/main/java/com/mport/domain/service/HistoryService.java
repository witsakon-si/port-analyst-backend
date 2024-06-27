package com.mport.domain.service;

import com.mport.dao.CommonDao;
import com.mport.domain.dto.AccountInfoDTO;
import com.mport.domain.dto.AssetInfoDTO;
import com.mport.domain.dto.AvailHistoryDTO;
import com.mport.domain.dto.AvailHistoryListDTO;
import com.mport.domain.dto.CalculateDTO;
import com.mport.domain.dto.CashInOutDTO;
import com.mport.domain.dto.HistoryDTO;
import com.mport.domain.dto.PriceDTO;
import com.mport.domain.dto.RealizePLByWeekDTO;
import com.mport.domain.dto.RealizePLByYearTypeDTO;
import com.mport.domain.dto.RealizePLStockDTO;
import com.mport.domain.dto.SimpleDTO;
import com.mport.domain.dto.SimpleHistoryDTO;
import com.mport.domain.dto.TransactionDTO;
import com.mport.domain.enums.CashType;
import com.mport.domain.exception.DataNotFoundException;
import com.mport.domain.exception.ValidationException;
import com.mport.domain.model.History;
import com.mport.domain.repository.HistoryRepository;
import com.mport.domain.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final CommonDao commonDao;
    private final ModelMapper modelMapper;
    private final PriceService priceService;
    private final AssetActiveService assetActiveService;
    private final CashInOutService cashInOutService;
    private final AssetInfoService assetInfoService;

    private final BigDecimal ONE_HUNDRED = new BigDecimal(100);
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private final DateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

    public HistoryService(HistoryRepository historyRepository, CommonDao commonDao, ModelMapper modelMapper, PriceService priceService, AssetActiveService assetActiveService, CashInOutService cashInOutService, AssetInfoService assetInfoService) {
        this.historyRepository = historyRepository;
        this.commonDao = commonDao;
        this.modelMapper = modelMapper;
        this.priceService = priceService;
        this.assetActiveService = assetActiveService;
        this.cashInOutService = cashInOutService;
        this.assetInfoService = assetInfoService;
    }

    public List<String> getAllType() {
        return commonDao.findAllType();
    }

    public List<String> getAllGroupAndName() {
        return commonDao.findAllGroupAndName();
    }

    public List<SimpleDTO> getAllName() {
        List<SimpleDTO> simpleDTOS = new ArrayList<>();
        for (String asset : commonDao.findAllName()) {
            SimpleDTO simpleDTO = new SimpleDTO(asset, asset);
            simpleDTOS.add(simpleDTO);
        }
        return simpleDTOS;
    }

    public List<AvailHistoryListDTO> getAvailHistory() {
        List<AvailHistoryListDTO> result = new ArrayList<>();

        List<String> nameList = assetInfoService.getViAssetNames();
        List<History> histories = historyRepository.findByActiveAndNameInOrderByNameAscTransactionDateAsc(true, nameList);
        List<HistoryDTO> historyDTOS = modelMapper.map(histories, new TypeToken<List<HistoryDTO>>() {
        }.getType());

        Map<String, List<HistoryDTO>> grouped = historyDTOS.stream().collect(Collectors.groupingBy(HistoryDTO::getName));
        TreeMap<String, List<HistoryDTO>> groupedByName = new TreeMap<>(grouped);
        for (Map.Entry<String, List<HistoryDTO>> entry : groupedByName.entrySet()) {
            List<HistoryDTO> list = entry.getValue();
            for (int i = 0; i < list.size(); i++) {
                HistoryDTO history = list.get(i);
                if (i == 0) {
                    history.setAvailUnit(history.getUnit());
                    history.setAvailNetAmount(history.getNetAmount());
                    history.setAvailDividend(BigDecimal.ZERO);
                } else {
                    HistoryDTO prevHistory = list.get(i - 1);
                    if (history.getSide().equals("B")) {    // buy
                        history.setAvailUnit(prevHistory.getAvailUnit().add(history.getUnit()));
                        history.setAvailNetAmount(prevHistory.getAvailNetAmount().add(history.getNetAmount()));
                        history.setAvailDividend(prevHistory.getAvailDividend());
                    } else {
                        history.setAvailUnit(prevHistory.getAvailUnit().subtract(history.getUnit()));
                        if (history.isDividend()) {
                            history.setAvailDividend(prevHistory.getAvailDividend().add(history.getNetAmount()));
                            history.setAvailNetAmount(prevHistory.getAvailNetAmount());
                        } else {        // sell
                            history.setAvailNetAmount(prevHistory.getAvailNetAmount().subtract(history.getNetAmount()));
                            history.setAvailDividend(prevHistory.getAvailDividend());
                        }
                    }
                }
            }
            result.add(new AvailHistoryListDTO(entry.getKey(), modelMapper.map(list, new TypeToken<List<AvailHistoryDTO>>() {}.getType())));
        }

        return result;
    }

    public List<TransactionDTO> getAllHistory(Map<String, AccountInfoDTO> accountInfoMap, List<String> accountTypes) throws IOException {
        AccountInfoDTO accountInfoDTO = null;
        List<History> histories = historyRepository.findByActiveOrderByTypeAscNameAscOrderMatchDescTransactionDateAsc(true);
        List<HistoryDTO> historyDTOS = modelMapper.map(histories, new TypeToken<List<HistoryDTO>>() {
        }.getType());
        HistoryDTO tmpDTO = new HistoryDTO();
        tmpDTO.setName("last");
        historyDTOS.add(tmpDTO);

        List<AssetInfoDTO> assetInfoDTOS = assetInfoService.getRefAssetInfo();
        Map<String, AssetInfoDTO> mapAssetInfo = assetInfoDTOS.stream()
                .collect(Collectors.toMap(AssetInfoDTO::getName, Function.identity()));

        BigDecimal currRate = priceService.getCurrencyRate("USD-THB");

        // calculate profit/loss fifo
        Set<String> assetActive = new HashSet<>();
        String name = null;
        String accountName = null;
        Map<String, CalculateDTO> map = new HashMap<>();
        List<HistoryDTO> sumHistoryList = new ArrayList<>();
        for (int i = 0; i < historyDTOS.size(); i++) {
            HistoryDTO historyDTO = historyDTOS.get(i);

            if (name != null) {
                if (!name.equals(historyDTO.getName())) { // change name
                    CalculateDTO calculateDTO = map.get(name);
                    if (calculateDTO != null) {
                        HistoryDTO sumHistoryDTO = new HistoryDTO();
                        sumHistoryDTO.setName(name);
                        sumHistoryDTO.setType(accountName);
                        sumHistoryDTO.setAmount(calculateDTO.getTotalAmount());
                        sumHistoryDTO.setNetAmount(calculateDTO.getTotalNetAmount());
                        sumHistoryDTO.setCost(calculateDTO.getTotalCost());
                        sumHistoryDTO.setUnit(calculateDTO.getTotalUnit());
                        sumHistoryDTO.setCommission(calculateDTO.getTotalCommission());
                        sumHistoryDTO.setFee(calculateDTO.getTotalFee());
                        sumHistoryDTO.setClearingFee(calculateDTO.getTotalClearingFee());
                        sumHistoryDTO.setVat(calculateDTO.getTotalVat());
                        sumHistoryDTO.setStatus("active");
                        sumHistoryDTO.setTotalDividend(BigDecimal.ZERO);
                        sumHistoryDTO.setGroupIndex(calculateDTO.getGroupIndex());
                        sumHistoryList.add(sumHistoryDTO);

                        map.remove(name);

                        assetActive.add(historyDTOS.get(i - 1).getType() + "|" + name);
                        if (mapAssetInfo.containsKey(name)) {
                            assetActive.add("ref|" + mapAssetInfo.get(name).getRefName());
                        }
                    }
                }
            }

            name = historyDTO.getName();
            accountName = historyDTO.getType();
            if (name.equals("last")) {
                break;
            }
            if (!map.containsKey(name)) {
                CalculateDTO calculateDTO = new CalculateDTO();
                calculateDTO.setGroupIndex(new ArrayList<>());
                calculateDTO.setTotalUnit(BigDecimal.ZERO);
                calculateDTO.setTotalAmount(BigDecimal.ZERO);
                calculateDTO.setTotalNetAmount(BigDecimal.ZERO);
                calculateDTO.setTotalNetAmountBuy(BigDecimal.ZERO);
                calculateDTO.setTotalNetAmountSell(BigDecimal.ZERO);
                calculateDTO.setTotalCost(BigDecimal.ZERO);
                calculateDTO.setTotalCommission(BigDecimal.ZERO);
                calculateDTO.setTotalFee(BigDecimal.ZERO);
                calculateDTO.setTotalClearingFee(BigDecimal.ZERO);
                calculateDTO.setTotalVat(BigDecimal.ZERO);
                map.put(name, calculateDTO);
            }

            CalculateDTO calculateDTO = map.get(name);
            // summary vat, commission, fee, clearing fee
            accountInfoDTO = accountInfoMap.get(historyDTO.getType());
            accountInfoDTO.setNetCommission(accountInfoDTO.getNetCommission().add(historyDTO.getCommission()));
            accountInfoDTO.setNetFee(accountInfoDTO.getNetFee().add(historyDTO.getFee()));
            accountInfoDTO.setNetVat(accountInfoDTO.getNetVat().add(historyDTO.getVat()));
            accountInfoDTO.setNetClearingFee(accountInfoDTO.getNetClearingFee().add(historyDTO.getClearingFee()));

            if (historyDTO.getSide().equals("B")) {
                calculateDTO.getGroupIndex().add(i);
                calculateDTO.setTotalUnit(calculateDTO.getTotalUnit().add(historyDTO.getUnit()));
                calculateDTO.setTotalAmount(calculateDTO.getTotalAmount().add(historyDTO.getAmount()));
                calculateDTO.setTotalNetAmount(calculateDTO.getTotalNetAmount().add(historyDTO.getNetAmount()));
                calculateDTO.setTotalNetAmountBuy(calculateDTO.getTotalNetAmountBuy().add(historyDTO.getNetAmount()));
                calculateDTO.setTotalCost(calculateDTO.getTotalCost().add(historyDTO.getNetAmount()));
                calculateDTO.setTotalCommission(calculateDTO.getTotalCommission().add(historyDTO.getCommission()));
                calculateDTO.setTotalFee(calculateDTO.getTotalFee().add(historyDTO.getFee()));
                calculateDTO.setTotalClearingFee(calculateDTO.getTotalClearingFee().add(historyDTO.getClearingFee()));
                calculateDTO.setTotalVat(calculateDTO.getTotalVat().add(historyDTO.getVat()));
                accountInfoDTO.setNetCashBalance(accountInfoDTO.getNetCashBalance().subtract(historyDTO.getNetAmount()));
            } else {
                calculateDTO.getGroupIndex().add(i);
                calculateDTO.setTotalUnit(calculateDTO.getTotalUnit().subtract(historyDTO.getUnit()));
                calculateDTO.setTotalAmount(calculateDTO.getTotalAmount().subtract(historyDTO.getAmount()));
                calculateDTO.setTotalNetAmount(calculateDTO.getTotalNetAmount().subtract(historyDTO.getNetAmount()));
                calculateDTO.setTotalNetAmountSell(calculateDTO.getTotalNetAmountSell().add(historyDTO.getNetAmount()));
                if (!historyDTO.isDividend()) {
                    calculateDTO.setTotalCost(calculateDTO.getTotalCost().subtract(historyDTO.getNetAmount()));
                    accountInfoDTO.setNetCashBalance(accountInfoDTO.getNetCashBalance().add(historyDTO.getNetAmount()));
                }
                calculateDTO.setTotalCommission(calculateDTO.getTotalCommission().add(historyDTO.getCommission()));
                calculateDTO.setTotalFee(calculateDTO.getTotalFee().add(historyDTO.getFee()));
                calculateDTO.setTotalClearingFee(calculateDTO.getTotalClearingFee().add(historyDTO.getClearingFee()));
                calculateDTO.setTotalVat(calculateDTO.getTotalVat().add(historyDTO.getVat()));
                if (calculateDTO.getTotalUnit().compareTo(BigDecimal.ZERO) == 0) {      // close position
                    HistoryDTO sumHistoryDTO = new HistoryDTO();
                    sumHistoryDTO.setName(name);
                    sumHistoryDTO.setType(accountName);
                    sumHistoryDTO.setCommission(calculateDTO.getTotalCommission());
                    sumHistoryDTO.setFee(calculateDTO.getTotalFee());
                    sumHistoryDTO.setClearingFee(calculateDTO.getTotalClearingFee());
                    sumHistoryDTO.setVat(calculateDTO.getTotalVat());
                    sumHistoryDTO.setRealizePL(calculateDTO.getTotalNetAmount().negate());
                    if (calculateDTO.getTotalNetAmountBuy().compareTo(BigDecimal.ZERO) > 0) {
                        sumHistoryDTO.setPercentPL(sumHistoryDTO.getRealizePL().multiply(ONE_HUNDRED));
                        sumHistoryDTO.setPercentPL(sumHistoryDTO.getPercentPL().divide(calculateDTO.getTotalNetAmountBuy(), 2, RoundingMode.HALF_UP));
                    }
                    if (sumHistoryDTO.getType().equalsIgnoreCase("crypto")) {
                        sumHistoryDTO.setRealizePL(sumHistoryDTO.getRealizePL().multiply(currRate).setScale(2, RoundingMode.HALF_UP));
                    }
                    sumHistoryDTO.setStatus("inactive");
                    sumHistoryDTO.setTotalDividend(BigDecimal.ZERO);
                    sumHistoryDTO.setGroupIndex(calculateDTO.getGroupIndex());
                    sumHistoryList.add(sumHistoryDTO);

                    map.remove(name);
                }
            }
        }

        // get last price
        assetActiveService.clearTable();
        assetActiveService.setActiveAsset(assetActive);
        Map<String, PriceDTO> lastPriceMap = priceService.getLastPrice(assetActive);

        TreeMap<String, List<HistoryDTO>> groupByDateAndName = new TreeMap<>();     // 1stDATE_TYPE_NAME

        for (int i = sumHistoryList.size() - 1; i >= 0; i--) {
            HistoryDTO sumHistory = sumHistoryList.get(i);
            int startIndex = sumHistory.getGroupIndex().get(0);

            Date dtFirstPosition = historyDTOS.get(startIndex).getTransactionDate();
            String strDate = dateFormat.format(dtFirstPosition);
            List<HistoryDTO> groupByDate = new ArrayList<>(Collections.singletonList(sumHistory));

            historyDTOS.add(startIndex, sumHistory);
            sumHistory.setType(historyDTOS.get(startIndex + 1).getType());
            sumHistory.setOrderMatch(historyDTOS.get(startIndex + 1).getOrderMatch());
            for (int j = startIndex + 1; j < historyDTOS.size(); j++) {
                if (historyDTOS.get(j).getSide() == null) {
                    break;
                }
                if (historyDTOS.get(j).isDividend()) {
                    sumHistory.setTotalDividend(sumHistory.getTotalDividend().add(historyDTOS.get(j).getNetAmount()));
                }
                historyDTOS.get(j).setStatus(sumHistory.getStatus());
                groupByDate.add(historyDTOS.get(j));
            }
            String keyMap = strDate + "_" + sumHistory.getType() + "_" + sumHistory.getName();
            while (groupByDateAndName.containsKey(keyMap)) {
                int idx = keyMap.lastIndexOf("_");
                StringBuilder sb = new StringBuilder(keyMap).insert(idx, "+");
                keyMap = sb.toString();
            }
            groupByDateAndName.put(keyMap, groupByDate);

            // set last price
            if (sumHistory.getStatus().equals("active")) {
                String key = sumHistory.getType() + "|" + sumHistory.getName();
                if (mapAssetInfo.containsKey(sumHistory.getName())) {
                    BigDecimal refPrice = lastPriceMap.containsKey("ref|" + mapAssetInfo.get(sumHistory.getName()).getRefName()) ? lastPriceMap.get("ref|" + mapAssetInfo.get(sumHistory.getName()).getRefName()).getPrice() : BigDecimal.ZERO;
                    sumHistory.setRefPrice(refPrice);
                }
                BigDecimal lastPrice = lastPriceMap.containsKey(key) ? lastPriceMap.get(key).getPrice() : BigDecimal.ZERO;
                if (sumHistory.getType().equals("¡Cash") || sumHistory.getType().equals("Bond")) {
                    lastPrice = BigDecimal.ONE;
                }
                BigDecimal currentAmount = lastPrice.multiply(sumHistory.getUnit());
                BigDecimal cost = sumHistory.getNetAmount();
                BigDecimal unPL = currentAmount.subtract(cost);
                unPL = unPL.setScale(2, RoundingMode.HALF_UP);
                sumHistory.setMktPrice(lastPrice);
                sumHistory.setMktPriceDt(lastPriceMap.containsKey(key) ? lastPriceMap.get(key).getSyncDate() : null);
                sumHistory.setUnPL(unPL);
                if (cost.compareTo(BigDecimal.ZERO) != 0) {
                    sumHistory.setPercentUnPL((unPL.divide(cost, 4, RoundingMode.HALF_UP)).multiply(ONE_HUNDRED));
                }
                if (sumHistory.getType().equalsIgnoreCase("crypto")) {
                    sumHistory.setUnPL(unPL.multiply(currRate).setScale(2, RoundingMode.HALF_UP));
                    sumHistory.setNetAmount(sumHistory.getNetAmount().multiply(currRate).setScale(2, RoundingMode.HALF_UP));
                }
            }
        }

        TreeMap<Integer, TreeMap<String, RealizePLByYearTypeDTO>> mapRealizePLByYear = new TreeMap<>();
        TreeMap<Integer, TreeMap<Integer, RealizePLByWeekDTO>> mapRealizePLByWeek = new TreeMap<>();      // key = year, weekOfYear
        TreeMap<String, RealizePLStockDTO> mapRealizePL = new TreeMap<>();      // key = stock name

        List<HistoryDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<HistoryDTO>> entry : groupByDateAndName.entrySet()) {
            List<HistoryDTO> value = entry.getValue();
            result.addAll(value);

            if (!value.isEmpty()) {
                String account = value.get(0).getType();
                BigDecimal profitLoss = value.get(0).getRealizePL() == null ? value.get(0).getUnPL() : value.get(0).getRealizePL();
                if (accountInfoMap.containsKey(account)) {
                    accountInfoDTO = accountInfoMap.get(account);
                    BigDecimal cost = accountInfoDTO.getCost();
                    BigDecimal netBalance = accountInfoDTO.getNetBalance();
                    accountInfoDTO.setNetBalance(netBalance.add(profitLoss));
                    BigDecimal pl = accountInfoDTO.getNetBalance().subtract(accountInfoDTO.getBalance());
                    BigDecimal percentPl = (pl.divide(cost, 4, RoundingMode.HALF_UP)).multiply(ONE_HUNDRED);
                    accountInfoDTO.setProfitLoss(pl);
                    accountInfoDTO.setPercentPL(percentPl);
                }

                // cal period hold
                Date startDate = value.get(1).getTransactionDate();
                Date endDate = value.get(value.size() - 1).getTransactionDate();
                String strS = displayDateFormat.format(startDate);
                String strE = displayDateFormat.format(endDate);
                endDate = DateTimeUtil.datePlusDay(endDate, 1);
                value.get(0).setStartDate(dateFormat2.format(startDate));
                value.get(0).setEndDate(dateFormat2.format(endDate));
                if (value.get(0).getStatus().equals("active")) {
                    endDate = DateTimeUtil.datePlusDay(DateTimeUtil.today(), 1);
                    strE = "now";
                    value.get(0).setEndDate(dateFormat2.format(endDate));
                } else {        // inactive
                    int year = DateTimeUtil.getYear(DateTimeUtil.datePlusDay(endDate, -1));
                    if (!mapRealizePLByYear.containsKey(year)) {
                        mapRealizePLByYear.put(year, new TreeMap<>(Collections.reverseOrder()));
                    }
                    if (!mapRealizePLByYear.get(year).containsKey(account)) {
                        mapRealizePLByYear.get(year).put(account, new RealizePLByYearTypeDTO(year));
                    }
                    mapRealizePLByYear.get(year).get(account).addRealizePL(value.get(0).getRealizePL());

                    // pl by week
                    int week = DateTimeUtil.getWeek(DateTimeUtil.datePlusDay(endDate, -1));
                    if(!mapRealizePLByWeek.containsKey(year)) {
                        mapRealizePLByWeek.put(year, new TreeMap<>());
                    }
                    if (!mapRealizePLByWeek.get(year).containsKey(week)) {
                        mapRealizePLByWeek.get(year).put(week, new RealizePLByWeekDTO(DateTimeUtil.datePlusDay(endDate, -1)));
                    }
                    value.get(0).setPeriodHold("(" + strS + " - " + strE + ")");
                    mapRealizePLByWeek.get(year).get(week).addRealizePL(value.get(0).getRealizePL());
                    mapRealizePLByWeek.get(year).get(week).addDetail(modelMapper.map(value.get(0), SimpleHistoryDTO.class));

                    // pl by stock
                    String keyName = value.get(0).getName();
                    if(!mapRealizePL.containsKey(keyName)) {
                        mapRealizePL.put(keyName, new RealizePLStockDTO(keyName));
                    }
                    mapRealizePL.get(keyName).addRealizePL(value.get(0).getRealizePL());
                    mapRealizePL.get(keyName).addDetail(modelMapper.map(value.get(0), SimpleHistoryDTO.class));
                }
                int[] diff = DateTimeUtil.getDayBetweenDMY(startDate, endDate);
                value.get(0).setPeriodHold("(" + strS + " - " + strE + ")");
                value.get(0).setDay(diff[0]);
                value.get(0).setMonth(diff[1]);
                value.get(0).setYear(diff[2]);
            }
        }

        Map<String, List<HistoryDTO>> groupedByType = result.stream().collect(Collectors.groupingBy(HistoryDTO::getType));
        List<TransactionDTO> transactionDTOList = new ArrayList<>();
        for (String type : accountTypes) {
            TransactionDTO transactionDTO = new TransactionDTO();
            transactionDTO.setType(type);
            transactionDTO.setAccountInfo(accountInfoMap.getOrDefault(type, null));
            transactionDTO.setList(groupedByType.get(type));
            transactionDTOList.add(transactionDTO);
        }
        if (!transactionDTOList.isEmpty()) {
            transactionDTOList.get(0).setMapRealizePLByYear(mapRealizePLByYear);
            transactionDTOList.get(0).setMapRealizePLByWeek(mapRealizePLByWeek);
            transactionDTOList.get(0).setMapRealizePL(mapRealizePL);
        }
        
        return transactionDTOList;
    }

    public History getHistory(Long historyId) throws DataNotFoundException {
        return historyRepository.findByIdAndActive(historyId, true).orElseThrow(()
                -> new DataNotFoundException("history id {" + historyId + "} doesn't exist."));
    }

    public List<RealizePLByYearTypeDTO> getRealizePLByYearType(TreeMap<Integer, TreeMap<String, RealizePLByYearTypeDTO>> mapRealizePLByYearType) {
        List<RealizePLByYearTypeDTO> realizePLByYearTypes = new ArrayList<>();
        List<RealizePLByYearTypeDTO> realizePLByYears;
        for (Map.Entry<Integer, TreeMap<String, RealizePLByYearTypeDTO>> entry : mapRealizePLByYearType.entrySet()) {
            Integer year = entry.getKey();
            realizePLByYears = new ArrayList<>();
            for (Map.Entry<String, RealizePLByYearTypeDTO> subEntry : entry.getValue().entrySet()) {
                RealizePLByYearTypeDTO pl = new RealizePLByYearTypeDTO(year);
                pl.setType(subEntry.getKey());
                pl.addRealizePL(subEntry.getValue().getRealizePL());
                realizePLByYears.add(pl);
                realizePLByYearTypes.add(pl);
            }
            BigDecimal realizePLYear = realizePLByYears.stream()
                    .map(RealizePLByYearTypeDTO::getRealizePL)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            for (RealizePLByYearTypeDTO pl : realizePLByYears) {
                pl.setRealizePLYear(realizePLYear);
            }
        }
        return realizePLByYearTypes;
    }

    public List<RealizePLByWeekDTO> getRealizePLByWeek(TreeMap<Integer, TreeMap<Integer, RealizePLByWeekDTO>> mapRealizePLByWeek) {
        List<RealizePLByWeekDTO> realizePLByWeeks = new ArrayList<>();
        for (Map.Entry<Integer, TreeMap<Integer, RealizePLByWeekDTO>> entry : mapRealizePLByWeek.entrySet()) {
            for (Map.Entry<Integer, RealizePLByWeekDTO> subEntry : entry.getValue().entrySet()) {
                realizePLByWeeks.add(subEntry.getValue());
            }
        }
        return realizePLByWeeks;
    }

    public List<RealizePLStockDTO> getRealizePL(TreeMap<String, RealizePLStockDTO> mapRealizePL) {
        List<RealizePLStockDTO> realizePLs = new ArrayList<>();
        for (Map.Entry<String, RealizePLStockDTO> entry : mapRealizePL.entrySet()) {
            realizePLs.add(entry.getValue());
        }
        return realizePLs;
    }

    public void createNewHistory(HistoryDTO historyDTO) {
        Date now = DateTimeUtil.now();
        History history = modelMapper.map(historyDTO, History.class);
        history.setCreatedAt(now);
        history.setUpdatedAt(now);

        if (historyDTO.isDividend()) {
            CashInOutDTO cashInOutDTO = new CashInOutDTO();
            if (historyDTO.isInterest()) {
                cashInOutDTO.setCashType(CashType.DEPOSIT);
            } else {
                cashInOutDTO.setCashType(CashType.WITHDRAW);
            }
            cashInOutDTO.setTransactionDate(historyDTO.getTransactionDate());
            cashInOutDTO.setAmount(historyDTO.getNetAmount());
            cashInOutDTO.setAccount(historyDTO.getType());
            cashInOutDTO.setRemark("ปันผล " + historyDTO.getName());
            cashInOutDTO.setDividend(true);
            cashInOutDTO = cashInOutService.create(cashInOutDTO);
            history.setCashInOutId(cashInOutDTO.getId());
        }
        historyRepository.save(history);
    }

    public void updateHistory(Long historyId, HistoryDTO historyDTO) throws DataNotFoundException {
        Date now = DateTimeUtil.now();
        History history = getHistory(historyId);
        modelMapper.map(historyDTO, history);
        history.setUpdatedAt(now);

        if (historyDTO.isDividend()) {
            long cashInOutId = historyDTO.getCashInOutId();
            CashInOutDTO cashInOutDTO = cashInOutService.getCashInOutDTO(cashInOutId);
            cashInOutDTO.setTransactionDate(historyDTO.getTransactionDate());
            cashInOutDTO.setAmount(historyDTO.getNetAmount());
            cashInOutService.update(cashInOutId, cashInOutDTO);
        }
        historyRepository.save(history);
    }

    public void deleteHistory(Long historyId) throws DataNotFoundException {
        Date now = DateTimeUtil.now();
        History history = getHistory(historyId);
        history.setActive(false);
        history.setUpdatedAt(now);

        if (history.isDividend()) {
            cashInOutService.delete(history.getCashInOutId());
        }

        historyRepository.save(history);
    }

    public void matchOrder(List<Long> historyIds) throws ValidationException {
        Date now = DateTimeUtil.now();
        Collections.sort(historyIds);
        List<History> histories = historyRepository.findByIdIsIn(historyIds);
        // validate unit
        BigDecimal unit = BigDecimal.ZERO;
        for(History history : histories) {
            unit = history.getSide().equals("B") ? unit.add(history.getUnit()) : unit.subtract(history.getUnit());
        }
        if (unit.compareTo(BigDecimal.ZERO) != 0) {
            throw new ValidationException("Match order unit incorrect");
        }

        // update
        for(History history : histories) {
            history.setOrderMatch(historyIds.get(0).toString());
            history.setUpdatedAt(now);
        }
        historyRepository.saveAll(histories);
    }

    public void unMatchOrder(Long matchOrderId) {
        Date now = DateTimeUtil.now();
        List<History> histories = historyRepository.findByOrderMatch(matchOrderId.toString());
        for(History history : histories) {
            history.setOrderMatch("-");
            history.setUpdatedAt(now);
        }
        historyRepository.saveAll(histories);
    }
}
