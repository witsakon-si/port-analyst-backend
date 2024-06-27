package com.mport.controller;

import com.mport.domain.dto.MonthPLDTORes;
import com.mport.domain.enums.ResponseCode;
import com.mport.domain.service.DailyAssetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/daily-asset")
public class DailyAssetController {

    private final DailyAssetService dailyAssetService;

    @Autowired
    public DailyAssetController(DailyAssetService dailyAssetService) {
        this.dailyAssetService = dailyAssetService;
    }

    @GetMapping(value = "/monthly-pl")
    public ResponseEntity<MonthPLDTORes> getMonthlyPL(@RequestParam("type") String type,
                                                      @RequestParam("name") String name) {
        log.info("getMonthlyPL: {} {}", type, name);
        MonthPLDTORes responseDTO = new MonthPLDTORes();
        try {
            responseDTO.setResult(dailyAssetService.getMonthlyPL(type, name));
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

}
