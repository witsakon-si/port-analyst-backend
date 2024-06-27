package com.mport.controller;

import com.mport.domain.dto.NoteDTOListRes;
import com.mport.domain.dto.NoteDTORes;
import com.mport.domain.dto.ResponseDTO;
import com.mport.domain.enums.ResponseCode;
import com.mport.domain.exception.DataNotFoundException;
import com.mport.domain.service.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/note")
public class NoteController {

    private final NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping(value = "")
    public ResponseEntity<NoteDTOListRes> getNoteList() {
        log.info("getNoteList");
        NoteDTOListRes responseDTO = new NoteDTOListRes();
        try {
            responseDTO.setNotes(noteService.getNoteList());
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

    @GetMapping(value = "/{name}")
    public ResponseEntity<NoteDTORes> getNote(@PathVariable String name) {
        log.info("getNote: {}", name);
        NoteDTORes responseDTO = new NoteDTORes();
        try {
            responseDTO.setResult(noteService.getNote(name));
            responseDTO.setResponseCode(ResponseCode.COMPLETE);
            responseDTO.setMessage("search completed.");
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (DataNotFoundException e) {
            log.error("getNote: {}", e.getMessage());
            responseDTO.setResponseCode(ResponseCode.DATA_NOT_FOUND);
            responseDTO.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            responseDTO.setResponseCode(ResponseCode.EXCEPTION);
            responseDTO.setMessage("unexpected Exception. Please contact your system administrator.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }

    @PostMapping(value = "/{name}")
    public ResponseEntity<ResponseDTO> saveNote(@PathVariable String name, @RequestParam("note") String note) {
        log.info("saveNote: {}", name);
        ResponseDTO response = new ResponseDTO();
        try {
            noteService.updateNote(name, note);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setResponseCode(ResponseCode.EXCEPTION);
            response.setMessage("unexpected Exception. Please contact your system administrator.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
