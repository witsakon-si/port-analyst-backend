package com.mport.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.mport.domain.dto.ApplicationDTO;
import com.mport.domain.dto.AssetInfoDTO;
import com.mport.domain.dto.AssetRefThDTO;
import com.mport.domain.dto.FxPriceDTO;
import com.mport.domain.dto.PriceDTO;
import com.mport.domain.dto.TargetPriceDTO;
import com.mport.domain.model.AssetActive;
import com.mport.domain.model.Price;
import com.mport.domain.repository.PriceRepository;
import com.mport.domain.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class PriceService {

    private final PriceRepository priceRepository;
    private final AssetActiveService assetActiveService;
    private final AssetInfoService assetInfoService;
    private final ModelMapper modelMapper;
    private final ObjectMapper mapper;
    private final ApplicationDTO applicationDTO;
    private final HazelcastInstance hz;

    private final BigDecimal ONE_HUNDRED = new BigDecimal(100);
    private final BigDecimal TEN = new BigDecimal(10);
    private final BigDecimal TWO = new BigDecimal(2);
    private final DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    @Value("${api.key}")
    private String API_KEY;

    public PriceService(PriceRepository priceRepository, AssetActiveService assetActiveService, AssetInfoService assetInfoService, ModelMapper modelMapper, ObjectMapper mapper, ApplicationDTO applicationDTO, HazelcastInstance hz) {
        this.priceRepository = priceRepository;
        this.assetActiveService = assetActiveService;
        this.assetInfoService = assetInfoService;
        this.modelMapper = modelMapper;
        this.mapper = mapper;
        this.applicationDTO = applicationDTO;
        this.hz = hz;
    }

    public Map<String, PriceDTO> getLastPrice(Set<String> activeSymbol) {
        log.info("getLastPrice: {} ", activeSymbol);
        List<Price> priceList = priceRepository.findByNameIsIn(activeSymbol);
        List<PriceDTO> priceDTOS = modelMapper.map(priceList, new TypeToken<List<PriceDTO>>() {
        }.getType());
        Map<String, PriceDTO> groupedByName = priceDTOS.stream().collect(Collectors.toMap(PriceDTO::getName, Function.identity()));
        return groupedByName;
    }

    public List<String> syncPrice() {
        log.info("syncPrice");
        Date now = DateTimeUtil.now();
        List<String> errorMsg = new ArrayList<>();
        List<AssetActive> assetActives = assetActiveService.getAllAssetActive();
        List<AssetInfoDTO> assetInfoDTOS = assetInfoService.getFullNameAssetInfo();
        Map<String, AssetInfoDTO> mapAssetInfo = assetInfoDTOS.stream()
                .collect(Collectors.toMap(AssetInfoDTO::getName, Function.identity()));

        List<Price> priceList = new ArrayList<>();
        for (AssetActive assetActive : assetActives) {
            String type = assetActive.getName().split("\\|")[0];
            String name = assetActive.getName().split("\\|")[1];

            Price price = new Price();
            price.setName(assetActive.getName());
            price.setSyncDate(now);

            BigDecimal lastPrice = BigDecimal.ZERO;
            boolean successSync = false;
            if (type.toLowerCase().contains("stock")) {
                try {
                    if (name.contains("80X")) {
                        lastPrice = getDRxPrice(name);
                    } else {
                        name = mapAssetInfo.containsKey(name) ? mapAssetInfo.get(name).getFullName() : name;
                        lastPrice = getStockPrice(name);
                    }
                    successSync = true;
                } catch (Exception e) {
                    errorMsg.add("Error price of " + type.toLowerCase() + ":" + name);
                    log.error("", e);
                }
            } else if (type.toLowerCase().contains("fund")) {
                try {
                    lastPrice = getFundNav(name);
                    successSync = true;
                } catch (Exception e) {
                    errorMsg.add("Error price of " + type.toLowerCase() + ":" + name);
                    log.error("", e);
                }
            } else if (type.toLowerCase().contains("crypto")) {
                try {
                    lastPrice = getCryptoPrice(name);
                    successSync = true;
                } catch (Exception e) {
                    errorMsg.add("Error price of " + type.toLowerCase() + ":" + name);
                    log.error("", e);
                }
            } else if (type.toLowerCase().contains("gold")) {
                try {
                    lastPrice = getGoldPrice();
                    successSync = true;
                } catch (Exception e) {
                    errorMsg.add("Error price of " + type.toLowerCase() + ":" + name);
                    log.error("", e);
                }
            } else if (type.toLowerCase().contains("ref")) {
                try {
                    String symbol = name.split("\\(")[0].trim();
                    String divisor = name.split("\\(")[1].split("\\)")[0].split(":")[1];
                    String curr = name.split("\\[")[1].replaceAll("]", "").trim();
                    lastPrice = getRefPrice(symbol, new BigDecimal(divisor), curr);
                    successSync = true;
                } catch (Exception e) {
                    errorMsg.add("Error price of " + type.toLowerCase() + ":" + name);
                    log.error("", e);
                }
            }
            price.setPrice(lastPrice);
            price.setSuccessSync(successSync);
            priceList.add(price);
        }

        priceRepository.saveAll(priceList);

        return errorMsg;
    }

    public void clearTable() {
        priceRepository.deleteAll();
    }

    public PriceDTO setRefPrice(String refName) {
        try {
            String symbol = refName.split("\\(")[0].trim();
            String divisor = refName.split("\\(")[1].split("\\)")[0].split(":")[1];
            String curr = refName.split("\\[")[1].replaceAll("]", "").trim();
            BigDecimal lastPrice = getRefPrice(symbol, new BigDecimal(divisor), curr);

            List<Price> prices = priceRepository.findByNameLike("%|" + refName);
            for (Price price : prices) {
                price.setPrice(lastPrice);
                price.setSuccessSync(true);
                price.setSyncDate(DateTimeUtil.now());
            }
            priceRepository.saveAll(prices);

            PriceDTO priceDTO = new PriceDTO();
            priceDTO.setName(refName);
            priceDTO.setPrice(lastPrice);
            return priceDTO;
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public BigDecimal getStockPrice(String symbol) {
        log.info("getStockPrice: {} ", symbol);
        try {
            symbol = symbol + ":NASDAQ";
            Document doc = Jsoup.connect("https://www.google.com/finance/quote/" + symbol).get();
            Elements content = doc.getElementsByAttribute("data-last-price");
            String price = content.attr("data-last-price");
            return new BigDecimal(price);
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
    
    public FxPriceDTO getStockPriceFull(String symbol) {
        log.info("getStockPriceFull: {} ", symbol);
        FxPriceDTO priceDTO = new FxPriceDTO();
        try {
            Document doc = Jsoup.connect("https://www.google.com/finance/quote/" + symbol).get();
            Elements content = doc.getElementsByAttribute("data-last-price");
            String price = content.attr("data-last-price");
            priceDTO.setCurrent(new BigDecimal(price));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return priceDTO;
    }
    
    public BigDecimal getDRxPrice(String symbol) {
        log.info("getDRxPrice: {} ", symbol);
        // todo: implement
        return BigDecimal.ZERO;
    }
    public FxPriceDTO getDRxPriceFull(String symbol) {
        log.info("getDRxPriceFull: {} ", symbol);
        // todo: implement
        return new FxPriceDTO();
    }

    public BigDecimal getFundNav(String symbol) {
        log.info("getFundNav: {} ", symbol);
        // todo: implement
        return BigDecimal.ZERO;
    }

    public BigDecimal getGoldPrice() {
        log.info("getGoldPrice");
        // todo: implement
        return BigDecimal.ZERO;
    }

    public BigDecimal getCryptoPrice(String symbol) {
        log.info("getCryptoPrice: {} ", symbol);
        // todo: implement
        return BigDecimal.ZERO;
    }

    public BigDecimal getCurrencyRate(String currPair) {
        log.info("getCurrencyRate: {}", currPair);
        try {
            Document doc = Jsoup.connect("https://www.google.com/finance/quote/" + currPair).get();
            Elements content = doc.getElementsByAttribute("data-last-price");
            String price = content.attr("data-last-price");
            return new BigDecimal(price);
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getRefPrice(String symbol, BigDecimal divisor, String curr) throws IOException {
        log.info("getRefPrice: {} {} {}", symbol, divisor, curr);
        BigDecimal currRate = getCurrencyRate(curr + "-THB");
        BigDecimal priceStock = getStockPrice(symbol);
        BigDecimal result = priceStock.multiply(currRate).divide(divisor, 2, RoundingMode.HALF_UP);
        return result;
    }


    public BigDecimal getForexPrice(String symbol) {
        IMap<String, FxPriceDTO> priceDTOMap = hz.getMap("REALTIME_PRICE");
        FxPriceDTO fxPriceDTO = priceDTOMap.get(symbol);
        return fxPriceDTO == null ? BigDecimal.ZERO : fxPriceDTO.getCurrent();
    }

    public void setStockInfo(String symbol) {
        log.info("setStockInfo: {} ", symbol);
        try {
            String response = Jsoup.connect("https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=" + API_KEY).ignoreContentType(true).execute().body();
            JsonNode data = mapper.readTree(response);
            double change = data.get("d").asDouble();
            double percentChange = data.get("dp").asDouble();

            IMap<String, FxPriceDTO> priceDTOMap = hz.getMap("REALTIME_PRICE");
            FxPriceDTO fxPriceDTO = priceDTOMap.get(symbol);
            if (fxPriceDTO == null) {
                fxPriceDTO = new FxPriceDTO();
                fxPriceDTO.setSymbol(symbol);
            }

            fxPriceDTO.setLastClose(new BigDecimal(data.get("pc").toString()));
            fxPriceDTO.setOpen(new BigDecimal(data.get("o").toString()));
            fxPriceDTO.setCurrent(new BigDecimal(data.get("c").toString()));
            fxPriceDTO.setHigh(new BigDecimal(data.get("h").toString()));
            fxPriceDTO.setLow(new BigDecimal(data.get("l").toString()));
            fxPriceDTO.setChange(decimalFormat.format(change));
            fxPriceDTO.setPercentChange(decimalFormat.format(percentChange) + "%");
            fxPriceDTO.setLastUpdate(new Date());
            if (applicationDTO.getAssetRefTh().containsKey(fxPriceDTO.getSymbol())) {
                fxPriceDTO.setUnderlying(true);
            }

            priceDTOMap.put(symbol, fxPriceDTO);
            
            addAssetRefTh(fxPriceDTO);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<FxPriceDTO> getRealtimePrice() {
        IMap<String, FxPriceDTO> priceDTOMap = hz.getMap("REALTIME_PRICE");
        TreeMap<String, FxPriceDTO> treeMap = new TreeMap<>(priceDTOMap);
        return new ArrayList<>(treeMap.values());
    }

    private void addAssetRefTh(FxPriceDTO srcPriceDTO) {
        IMap<String, FxPriceDTO> priceDTOMap = hz.getMap("REALTIME_PRICE");
        if (applicationDTO.getAssetRefTh().containsKey(srcPriceDTO.getSymbol())) {
            AssetRefThDTO assetRefThDTO = applicationDTO.getAssetRefTh().get(srcPriceDTO.getSymbol());
            BigDecimal refClose = priceDTOMap.get(srcPriceDTO.getSymbol()).getLastClose();
            BigDecimal refPrice = priceDTOMap.get(srcPriceDTO.getSymbol()).getCurrent();
            if (refClose.compareTo(BigDecimal.ZERO) != 0) {
                if (priceDTOMap.containsKey(assetRefThDTO.getSymbol()) && priceDTOMap.containsKey(assetRefThDTO.getCurrency())) {
                    BigDecimal refCurr = priceDTOMap.get(assetRefThDTO.getCurrency()).getCurrent();
                    BigDecimal divisor = assetRefThDTO.getDivisor();
                    BigDecimal price = refClose.multiply(refCurr).divide(divisor, 20, RoundingMode.HALF_UP);

                    FxPriceDTO fxPriceDTO = new FxPriceDTO();
                    fxPriceDTO.setSymbol(assetRefThDTO.getSymbolTh());
                    fxPriceDTO.setCurrent(price.setScale(2, RoundingMode.HALF_UP));
                    fxPriceDTO.setLastUpdate(srcPriceDTO.getLastUpdate());
                    BigDecimal diff = new BigDecimal(0.01);
                    int roundDiff = 15;
                    if (fxPriceDTO.getCurrent().compareTo(TEN) >= 0) {
                        diff = new BigDecimal(0.05);
                        roundDiff = 50;
                    } else if (fxPriceDTO.getCurrent().compareTo(TWO) >= 0) {
                        diff = new BigDecimal(0.02);
                        roundDiff = 30;
                    }

                    List<TargetPriceDTO> targetPriceS = new ArrayList<>();
                    TreeMap<BigDecimal, TargetPriceDTO> sortMap = new TreeMap<>();
                    for (int i = -1*roundDiff; i <= roundDiff; i++) {
                        BigDecimal change = diff.multiply(new BigDecimal(i));
                        BigDecimal nextPrice = price.add(change);
                        BigDecimal nextRefPrice = nextPrice.multiply(divisor).divide(refCurr, 20, RoundingMode.HALF_UP);

                        TargetPriceDTO targetDTO = new TargetPriceDTO();
                        targetDTO.setPrice(nextPrice);
                        targetDTO.setRefPrice(nextRefPrice.setScale(2, RoundingMode.HALF_UP));
                        targetDTO.setRefCurr(refCurr);
                        targetDTO.setChange(decimalFormat.format(change));
                        targetDTO.setPercentChange(decimalFormat.format(nextRefPrice.subtract(refClose).multiply(ONE_HUNDRED).divide(refClose, 20, RoundingMode.HALF_UP)) + "%");
                        targetPriceS.add(targetDTO);

                        sortMap.put(nextRefPrice.subtract(refPrice).abs(), targetDTO);
                    }
                    fxPriceDTO.setTargetPrices(targetPriceS);

                    sortMap.firstEntry().getValue().setClosest(true);
                    priceDTOMap.put(fxPriceDTO.getSymbol(), fxPriceDTO);
                }
            }
        }
    }
}
