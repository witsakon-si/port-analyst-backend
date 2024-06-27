package com.mport.domain.dto;

import lombok.Data;

import java.util.List;


@Data
public class NoteDTOListRes extends ResponseDTO {
    List<NoteDTO> notes;
}

