package com.mport.controller;

import com.mport.domain.dto.AssetInfoDTO;
import com.mport.domain.dto.FullPriceRes;
import com.mport.domain.dto.PriceDTO;
import com.mport.domain.dto.ResponseDTO;
import com.mport.domain.enums.ResponseCode;
import com.mport.domain.service.AssetInfoService;
import com.mport.domain.service.PriceService;
import com.mport.system.DailySumScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/")
public class PriceController {

    private final PriceService priceService;
    private final AssetInfoService assetInfoService;
    private final DailySumScheduler dailySumScheduler;

    @Autowired
    public PriceController(PriceService priceService, AssetInfoService assetInfoService, DailySumScheduler dailySumScheduler) {
        this.priceService = priceService;
        this.assetInfoService = assetInfoService;
        this.dailySumScheduler = dailySumScheduler;
    }

    @GetMapping(value = "/syncPrice")
    public ResponseEntity<ResponseDTO> syncPrice() {
        log.info("syncPrice");
        ResponseDTO response = new ResponseDTO();
        priceService.clearTable();
        priceService.syncPrice();
        response.setResponseCode(ResponseCode.COMPLETE);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/get-stock-price/{symbol}")
    public ResponseEntity<FullPriceRes> getStockPriceV2(@PathVariable String symbol) throws IOException {
        log.info("getStockPriceV2. [symbol: {}]", symbol);
        FullPriceRes response = new FullPriceRes();
        if (symbol.contains("80X")) {
            response.setFullPrice(priceService.getDRxPriceFull(symbol));
        } else {
            response.setFullPrice(priceService.getStockPriceFull(symbol));
        }
        AssetInfoDTO assetInfoDTO = assetInfoService.getRefAssetInfo(symbol);
        if (assetInfoDTO != null) {
            response.setRefPrice(priceService.setRefPrice(assetInfoDTO.getRefName()));
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/get-fund-price/{symbol}")
    public ResponseEntity<PriceDTO> getFundPrice(@PathVariable String symbol) {
        log.info("getFundPrice. [symbol: {}]", symbol);
        PriceDTO response = new PriceDTO();
        response.setPrice(priceService.getFundNav(symbol));
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/get-gold-price")
    public ResponseEntity<PriceDTO> getGoldPrice() {
        log.info("getGoldPrice.");
        PriceDTO response = new PriceDTO();
        response.setPrice(priceService.getGoldPrice());
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/get-crypto-price/{symbol}")
    public ResponseEntity<PriceDTO> getCryptoPrice(@PathVariable String symbol) {
        log.info("getCryptoPrice. [symbol: {}]", symbol);
        PriceDTO response = new PriceDTO();
        response.setPrice(priceService.getCryptoPrice(symbol));
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/get-currency-rate/{currPair}")
    public ResponseEntity<PriceDTO> getCurrencyRate(@PathVariable String currPair) {
        log.info("getCurrencyRate. [currPair: {}]", currPair);
        PriceDTO response = new PriceDTO();
        response.setPrice(priceService.getCurrencyRate(currPair));
        return ResponseEntity.ok(response);
    }
    
    @GetMapping(value = "/get-forex-price")
    public ResponseEntity<PriceDTO> getForexPrice(@RequestParam("symbol") String symbol) throws IOException {
        log.info("getForexPrice. {}", symbol);
        PriceDTO response = new PriceDTO();
        response.setPrice(priceService.getForexPrice(symbol));
        return ResponseEntity.ok(response);
    }

    // For testing
    @GetMapping(value = "/dailySumScheduler")
    public ResponseEntity<ResponseDTO> dailySumScheduler() {
        log.info("dailySumScheduler");
        ResponseDTO response = new ResponseDTO();
        dailySumScheduler.cronScheduleTask();
        response.setResponseCode(ResponseCode.COMPLETE);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/set-stock-info/{symbol}")
    public ResponseEntity<ResponseDTO> setStockInfo(@PathVariable String symbol) {
        log.info("setStockInfo. [symbol: {}]", symbol);
        ResponseDTO response = new ResponseDTO();
        priceService.setStockInfo(symbol);
        response.setResponseCode(ResponseCode.COMPLETE);
        return ResponseEntity.ok(response);
    }

}
