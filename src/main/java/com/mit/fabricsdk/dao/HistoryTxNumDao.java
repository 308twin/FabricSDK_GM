package com.mit.fabricsdk.dao;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import com.mit.fabricsdk.entity.HistoryTxNum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface HistoryTxNumDao extends PagingAndSortingRepository<HistoryTxNum, Long>, JpaSpecificationExecutor<HistoryTxNum> {
    @Query("SELECT h FROM HistoryTxNum h WHERE h.channel = :channelName ORDER BY h.id DESC")
    List<HistoryTxNum> getTopCommonlyHistoryTxNums(String channelName,Pageable pageable);

    @Query("SELECT DISTINCT channel FROM HistoryTxNum")
    List<String> findDistinctChannel();
}
