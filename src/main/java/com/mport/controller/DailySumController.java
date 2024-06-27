package com.mport.controller;

import com.mport.domain.dto.DailySumDTORes;
import com.mport.domain.enums.ResponseCode;
import com.mport.domain.service.DailySumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/daily-sum")
public class DailySumController {

    private final DailySumService dailySumService;

    @Autowired
    public DailySumController(DailySumService dailySumService) {
        this.dailySumService = dailySumService;
    }

    @GetMapping(value = "")
    public ResponseEntity<DailySumDTORes> getDailySum() {
        log.info("getDailySum");
        DailySumDTORes responseDTO = new DailySumDTORes();
        try {
            responseDTO.setResult(dailySumService.getDailySumList());
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
