package com.mport.domain.service;

import com.mport.domain.dto.NoteDTO;
import com.mport.domain.exception.DataNotFoundException;
import com.mport.domain.model.Note;
import com.mport.domain.repository.NoteRepository;
import com.mport.domain.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class NoteService {

    private final NoteRepository noteRepository;
    private final ModelMapper modelMapper;

    public NoteService(NoteRepository noteRepository, ModelMapper modelMapper) {
        this.noteRepository = noteRepository;
        this.modelMapper = modelMapper;
    }

    public NoteDTO getNote(String name) throws DataNotFoundException {
        Note note = noteRepository.findByName(name).orElseThrow(()
                -> new DataNotFoundException("Note for {" + name + "} doesn't exist."));
        return modelMapper.map(note, NoteDTO.class);
    }

    public List<NoteDTO> getNoteList() {
        List<NoteDTO> noteDTOS = modelMapper.map(noteRepository.findAll(), new TypeToken<List<NoteDTO>>() {
        }.getType());
        return noteDTOS;
    }

    public void updateNote(String name, String noteStr) {
        Date now = DateTimeUtil.now();
        Optional<Note> note = noteRepository.findByName(name);
        Note data = null;
        if (!note.isPresent()) {
            if (noteStr.trim().isEmpty()) {
                return;
            }
            data = new Note();
            data.setName(name);
        } else {
            data = note.get();
            if (noteStr.trim().isEmpty()) {
                noteRepository.delete(note.get());
                return;
            }
        }
        data.setNote(noteStr);
        data.setUpdatedAt(now);
        noteRepository.save(data);
    }

}
