package com.mport.controller;

import com.mport.domain.dto.PriceAlertDTO;
import com.mport.domain.dto.PriceAlertDTORes;
import com.mport.domain.dto.PriceAlertListDTORes;
import com.mport.domain.dto.ResponseDTO;
import com.mport.domain.enums.PriceAlertCond;
import com.mport.domain.enums.PriceAlertFreq;
import com.mport.domain.enums.ResponseCode;
import com.mport.domain.exception.DataNotFoundException;
import com.mport.domain.service.AssetInfoService;
import com.mport.domain.service.PriceAlertService;
import com.mport.domain.service.PriceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.TreeMap;

@Slf4j
@RestController
@RequestMapping("/price-alert")
public class PriceAlertController {

    private final PriceAlertService priceAlertService;
    private final PriceService priceService;
    private final AssetInfoService assetInfoService;

    @Autowired
    public PriceAlertController(PriceAlertService priceAlertService, PriceService priceService, AssetInfoService assetInfoService) {
        this.priceAlertService = priceAlertService;
        this.priceService = priceService;
        this.assetInfoService = assetInfoService;
    }

    @GetMapping(value = "")
    public ResponseEntity<PriceAlertListDTORes> getPriceAlertList() {
        log.info("getPriceAlertList");
        PriceAlertListDTORes responseDTO = new PriceAlertListDTORes();
        try {
            List<PriceAlertDTO> list = priceAlertService.getPriceAlertList();
            responseDTO.setConditionType(PriceAlertCond.getOption());
            responseDTO.setNoticeFrequency(PriceAlertFreq.getOption());
            responseDTO.setSymbolList(priceAlertService.getAllSymbol());
            responseDTO.setRealtimePrice(priceService.getRealtimePrice());
            responseDTO.setAssetInfo(new TreeMap<>(assetInfoService.loadAssetRefTh()));
            responseDTO.setResult(list);
            responseDTO.setResponseCode(ResponseCode.COMPLETE);
            responseDTO.setMessage("search completed.");
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            responseDTO.setResponseCode(ResponseCode.EXCEPTION);
            responseDTO.setMessage("unexpected Exception. Please contact your system administrator.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PriceAlertDTORes> getPriceAlert(@PathVariable Long id) {
        log.info("getPriceAlert");
        PriceAlertDTORes responseDTO = new PriceAlertDTORes();
        try {
            responseDTO.setResult(priceAlertService.getPriceAlertDTO(id));
            responseDTO.setResponseCode(ResponseCode.COMPLETE);
            responseDTO.setMessage("search completed.");
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            responseDTO.setResponseCode(ResponseCode.EXCEPTION);
            responseDTO.setMessage("unexpected Exception. Please contact your system administrator.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }

    @PostMapping(value = "")
    public ResponseEntity<ResponseDTO> savePriceAlert(@RequestBody PriceAlertDTO priceAlertDTO) throws DataNotFoundException {
        log.info("savePriceAlert: {}", priceAlertDTO);
        ResponseDTO response = new ResponseDTO();
        Long id = priceAlertDTO.getId();
        if (id == null) {
            priceAlertService.create(priceAlertDTO);
        } else {
            priceAlertService.update(id, priceAlertDTO);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ResponseDTO> deletePriceAlert(@PathVariable Long id) throws DataNotFoundException {
        log.info("deletePriceAlert: {}", id);
        ResponseDTO response = new ResponseDTO();
        if (id == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        priceAlertService.delete(id);
        return ResponseEntity.ok(response);
    }

}
