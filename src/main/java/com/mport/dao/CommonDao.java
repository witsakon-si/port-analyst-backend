package com.mport.dao;

import com.mport.domain.dto.SimpleDTO;

import java.util.List;

public interface CommonDao {
    List<String> findAllType();
    List<String> findAllName();
    List<String> findAllGroupAndName();
    List<SimpleDTO> findAllSymbol();
    List<SimpleDTO> findAllAcctGroup();
}
