package com.mport.controller;

import com.mport.domain.dto.AssetInfoDTO;
import com.mport.domain.dto.AssetInfoRes;
import com.mport.domain.dto.ResponseDTO;
import com.mport.domain.enums.ResponseCode;
import com.mport.domain.exception.DataDuplicateException;
import com.mport.domain.exception.DataNotFoundException;
import com.mport.domain.service.AssetInfoService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/")
public class AssetInfoController {

    private final AssetInfoService assetInfoService;

    @Autowired
    public AssetInfoController(AssetInfoService assetInfoService) {
        this.assetInfoService = assetInfoService;
    }

    @GetMapping(value = "/assetInfo")
    public ResponseEntity<AssetInfoRes> getAssetInfo() {
        log.info("assetInfo");
        AssetInfoRes response = new AssetInfoRes();
        response.setResult(assetInfoService.getAssetInfoList());
        response.setMessage("search completed.");
        response.setResponseCode(ResponseCode.COMPLETE);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/assetInfo")
    public ResponseEntity<ResponseDTO> updateAssetInfo(@RequestBody AssetInfoDTO assetInfoDTO) {
        log.info("updateAssetInfo: {}", assetInfoDTO);
        ResponseDTO response = new ResponseDTO();
        try {
            assetInfoService.saveAssetInfo(assetInfoDTO);
            response.setResponseCode(ResponseCode.COMPLETE);
            response.setMessage("save completed.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (DataDuplicateException ex) {
            response.setResponseCode(ResponseCode.VALIDATION_FAILED);
            response.setMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception ex) {
            response.setResponseCode(ResponseCode.EXCEPTION);
            response.setMessage("unexpected Exception. Please contact your system administrator.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping(value = "/assetInfo/{name}")
    public ResponseEntity<ResponseDTO> deleteAssetInfo(@PathVariable String name) {
        log.info("deleteAssetInfo: {}", name);
        ResponseDTO response = new ResponseDTO();
        try {
            assetInfoService.deleteAssetInfo(name);
            response.setResponseCode(ResponseCode.COMPLETE);
            response.setMessage("delete completed.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (DataNotFoundException ex) {
            response.setResponseCode(ResponseCode.DATA_NOT_FOUND);
            response.setMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception ex) {
            response.setResponseCode(ResponseCode.EXCEPTION);
            response.setMessage("unexpected Exception. Please contact your system administrator.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(value = "/getChartURL")
    public ResponseEntity<ResponseDTO> getChartURL(@RequestParam("asset") String asset) {
        log.info("getChartURL");
        ResponseDTO response = new ResponseDTO();
        response.setMessage(assetInfoService.getChartURL(asset));
        response.setResponseCode(ResponseCode.COMPLETE);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/getChartRefURL/{asset}")
    public ResponseEntity<ResponseDTO> getChartRefURL(@PathVariable String asset) {
        log.info("getChartRefURL");
        ResponseDTO response = new ResponseDTO();
        response.setMessage(assetInfoService.getChartRefURL(asset));
        response.setResponseCode(ResponseCode.COMPLETE);
        return ResponseEntity.ok(response);
    }

}
