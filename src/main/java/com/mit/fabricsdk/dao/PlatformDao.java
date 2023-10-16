package com.mit.fabricsdk.dao;

import com.mit.fabricsdk.entity.HistoryTxNum;
import com.mit.fabricsdk.entity.Platform;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Haodong Li
 * @date 2023年05月30日 15:41
 */
public interface PlatformDao extends PagingAndSortingRepository<Platform, Long>, JpaSpecificationExecutor<Platform> {
}
