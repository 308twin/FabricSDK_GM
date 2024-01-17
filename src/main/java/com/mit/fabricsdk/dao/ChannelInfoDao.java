/*
 * @Author: LHD
 * @Date: 2024-01-09 14:20:02
 * @LastEditors: 308twin 790816436@qq.com
 * @LastEditTime: 2024-01-17 15:19:48
 * @Description: 
 * 
 * Copyright (c) 2024 by 308twin@790816436@qq.com, All Rights Reserved. 
 */
package com.mit.fabricsdk.dao;

import java.util.List;
import com.mit.fabricsdk.entity.ChannelInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;

public interface ChannelInfoDao
        extends PagingAndSortingRepository<ChannelInfo, Long>, JpaSpecificationExecutor<ChannelInfo> {
    List<ChannelInfo> findByChannelName(String channelName, Sort sort);

    @Query("SELECT c.channelName, MAX(c.channelHeight),MAX(c.channelTxCount) FROM ChannelInfo c GROUP BY c.channelName")
    List<Object[]> findMaxChannelHeightForEachChannel();

    List<ChannelInfo> findByChannelHeight(Long channelHeight);

    @Query("SELECT  MAX(c.channelHeight) FROM ChannelInfo c WHERE c.channelName = :channelName GROUP BY c.channelName")
    Long findMaxChannelHeightForSingleChannel(@Param("channelName") String channelName);    

    @Query("SELECT  MAX(c.channelTxCount) FROM ChannelInfo c WHERE c.channelName = :channelName GROUP BY c.channelName")
    Long findMaxChannelTxForSingleChannel(@Param("channelName") String channelName);   

    @Query("SELECT c.id FROM ChannelInfo c WHERE c.channelHeight = (SELECT MAX(ci.channelHeight) FROM ChannelInfo ci WHERE ci.channelName = c.channelName)")
    List<Long> findIdsOfMaxHeightChannels();

    default List<ChannelInfo> findMaxHeightChannels() {
        List<Long> ids = findIdsOfMaxHeightChannels();
        return (List<ChannelInfo>) findAllById(ids);
    }

    @Query(value = "SELECT * FROM channel_info WHERE channel_name = ?1 ORDER BY id DESC LIMIT ?2", nativeQuery = true)
    List<ChannelInfo> findTopNByChannelName(String channelName, int n);

    @Query("SELECT c.channelName,MAX(c.channelTxCount), MAX(c.channelHeight) FROM ChannelInfo c GROUP BY c.channelName")
    List<Object[]> findMaxChannelHeightChannels();

}
