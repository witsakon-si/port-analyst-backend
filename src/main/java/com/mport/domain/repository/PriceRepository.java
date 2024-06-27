package com.mport.domain.repository;

import com.mport.domain.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface PriceRepository extends JpaRepository<Price, Long>, JpaSpecificationExecutor<Price> {
    List<Price> findByNameIsIn(Set<String> name);
    List<Price> findByNameLike(String name);
}
