package com.mport.controller;

import com.mport.domain.dto.AccountInfoDTO;
import com.mport.domain.dto.AvailHistoryRes;
import com.mport.domain.dto.HistoryDTO;
import com.mport.domain.dto.HistoryRes;
import com.mport.domain.dto.ResponseDTO;
import com.mport.domain.dto.SimpleDTO;
import com.mport.domain.dto.TransactionDTO;
import com.mport.domain.dto.TransactionRes;
import com.mport.domain.enums.ResponseCode;
import com.mport.domain.exception.DataNotFoundException;
import com.mport.domain.exception.ValidationException;
import com.mport.domain.model.History;
import com.mport.domain.service.CashInOutService;
import com.mport.domain.service.HistoryService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/history")
public class HistoryController {

    private final HistoryService historyService;
    private final CashInOutService cashInOutService;
    private final ModelMapper modelMapper;

    @Autowired
    public HistoryController(HistoryService historyService, CashInOutService cashInOutService, ModelMapper modelMapper) {
        this.historyService = historyService;
        this.cashInOutService = cashInOutService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(value = "")
    public ResponseEntity<TransactionRes> getHistoryList() {
        TransactionRes responseDTO = new TransactionRes();
        try {
            List<SimpleDTO> assetList  = historyService.getAllName();
            Map<String, AccountInfoDTO> accountInfoMap = cashInOutService.getAllAccountInfo();
            List<String> types = historyService.getAllType();
            List<String> typeAndNames = historyService.getAllGroupAndName();
            List<TransactionDTO> list = historyService.getAllHistory(accountInfoMap, types);
            responseDTO.setResult(list);
            responseDTO.setAssetList(assetList);
            types.addAll(typeAndNames);
            responseDTO.setGroupList(types);
            if (!list.isEmpty()) {
                responseDTO.setRealizePLByYearType(historyService.getRealizePLByYearType(list.get(0).getMapRealizePLByYear()));
                responseDTO.setRealizePLByWeek(historyService.getRealizePLByWeek(list.get(0).getMapRealizePLByWeek()));
                responseDTO.setRealizePL(historyService.getRealizePL(list.get(0).getMapRealizePL()));
                list.get(0).setMapRealizePLByYear(null);
            }
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

    @GetMapping(value = "avail")
    public ResponseEntity<AvailHistoryRes> getAvailHistory() {
        AvailHistoryRes responseDTO = new AvailHistoryRes();
        try {
            responseDTO.setResult(historyService.getAvailHistory());
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
    public ResponseEntity<HistoryRes> getHistory(@PathVariable Long id) {
        log.debug("getHistory: {}", id);
        HistoryRes responseDTO = new HistoryRes();
        try {
            log.debug("start call service");
            History History = historyService.getHistory(id);
            HistoryDTO HistoryDTO = modelMapper.map(History, HistoryDTO.class);
            log.debug("end call service");
            responseDTO.setResult(HistoryDTO);

            responseDTO.setResponseCode(ResponseCode.COMPLETE);
            responseDTO.setMessage("search completed.");
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (DataNotFoundException e) {
            log.error("getHistory: {}", e.getMessage());
            responseDTO.setResponseCode(ResponseCode.DATA_NOT_FOUND);
            responseDTO.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            responseDTO.setResponseCode(ResponseCode.EXCEPTION);
            responseDTO.setMessage("unexpected Exception. Please contact your system administrator.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> saveHistory(@RequestBody HistoryDTO historyDTO) {
        log.debug("saveHistory: {}", historyDTO);
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            Long id = historyDTO.getId();
            if (id == null) {
                historyService.createNewHistory(historyDTO);
            } else {
                historyService.updateHistory(id, historyDTO);
            }

            responseDTO.setResponseCode(ResponseCode.COMPLETE);
            responseDTO.setMessage("save complete.");
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            responseDTO.setResponseCode(ResponseCode.EXCEPTION);
            responseDTO.setMessage("unexpected Exception. Please contact your system administrator.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ResponseDTO> deleteHistory(@PathVariable Long id) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            // validate
            if (id == null) {
                responseDTO.setResponseCode(ResponseCode.VALIDATION_FAILED);
                responseDTO.setMessage("please specify History id");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
            }

            historyService.deleteHistory(id);

            responseDTO.setResponseCode(ResponseCode.COMPLETE);
            responseDTO.setMessage("delete complete.");
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (DataNotFoundException e) {
            log.error("deleteHistory: {}", e.getMessage());
            responseDTO.setResponseCode(ResponseCode.DATA_NOT_FOUND);
            responseDTO.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            responseDTO.setResponseCode(ResponseCode.EXCEPTION);
            responseDTO.setMessage("unexpected Exception. Please contact your system administrator.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }

    @PostMapping(value = "match-order")
    public ResponseEntity<ResponseDTO> matchOrder(@RequestBody List<Integer> historyId) {
        log.debug("matchOrder: {}", historyId);
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            List<Long> ids = historyId.stream().mapToLong(Integer::longValue).boxed().collect(Collectors.toList());
            historyService.matchOrder(ids);
            responseDTO.setResponseCode(ResponseCode.COMPLETE);
            responseDTO.setMessage("save completed.");
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (ValidationException e) {
            responseDTO.setResponseCode(ResponseCode.VALIDATION_FAILED);
            responseDTO.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            responseDTO.setResponseCode(ResponseCode.EXCEPTION);
            responseDTO.setMessage("unexpected Exception. Please contact your system administrator.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }

    @GetMapping(value = "unmatch-order/{matchOrderId}")
    public ResponseEntity<ResponseDTO> unMatchOrder(@PathVariable Long matchOrderId) {
        log.debug("unMatchOrder: {}", matchOrderId);
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            historyService.unMatchOrder(matchOrderId);
            responseDTO.setResponseCode(ResponseCode.COMPLETE);
            responseDTO.setMessage("save completed.");
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            responseDTO.setResponseCode(ResponseCode.EXCEPTION);
            responseDTO.setMessage("unexpected Exception. Please contact your system administrator.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }

}
