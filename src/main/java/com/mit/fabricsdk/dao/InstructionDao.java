package com.mit.fabricsdk.dao;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import com.mit.fabricsdk.entity.InstructionDB;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface InstructionDao extends PagingAndSortingRepository<InstructionDB, Long>, JpaSpecificationExecutor<InstructionDB> {
  

}
