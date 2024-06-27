package com.mport.controller;

import com.mport.domain.dto.CashInOutByYearDTO;
import com.mport.domain.dto.CashInOutDTO;
import com.mport.domain.dto.ResponseDTO;
import com.mport.domain.exception.DataNotFoundException;
import com.mport.domain.service.CashInOutService;
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

@Slf4j
@RestController
@RequestMapping("/account")
public class CashInOutController {

    private final CashInOutService cashInOutService;

    @Autowired
    public CashInOutController(CashInOutService cashInOutService) {
        this.cashInOutService = cashInOutService;
    }

    @GetMapping(value = "/cash-in-out/{account}")
    public ResponseEntity<List<CashInOutDTO>> getCashInOut(@PathVariable String account) {
        log.info("getCashInOut: {}", account);
        List<CashInOutDTO> response = cashInOutService.getCashInOutList(account);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/cash-in-out-by-year")
    public ResponseEntity<List<CashInOutByYearDTO>> getCashInOutByYear() {
        log.info("getCashInOutByYear");
        List<CashInOutByYearDTO> response = cashInOutService.getCashInOutByYear();
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/cash-in-out")
    public ResponseEntity<ResponseDTO> saveCashInOut(@RequestBody CashInOutDTO cashInOutDTO) throws DataNotFoundException {
        log.info("saveCashInOut: {}", cashInOutDTO);
        ResponseDTO response = new ResponseDTO();
        Long id = cashInOutDTO.getId();
        if (id == null) {
            cashInOutService.create(cashInOutDTO);
        } else {
            cashInOutService.update(id, cashInOutDTO);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/cash-in-out/{id}")
    public ResponseEntity<ResponseDTO> deleteCashInOut(@PathVariable Long id) throws DataNotFoundException {
        log.info("deleteCashInOut: {}", id);
        ResponseDTO response = new ResponseDTO();
        if (id == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        cashInOutService.delete(id);
        return ResponseEntity.ok(response);
    }

}
