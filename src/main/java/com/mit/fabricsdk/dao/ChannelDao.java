/*
 * @Author: LHD
 * @Date: 2023-12-19 13:54:39
 * @LastEditors: 308twin 790816436@qq.com
 * @LastEditTime: 2024-01-09 14:20:28
 * @Description: 
 * 
 * Copyright (c) 2024 by 308twin@790816436@qq.com, All Rights Reserved. 
 */
package com.mit.fabricsdk.dao;

import com.mit.fabricsdk.entity.BlockChainChannel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ChannelDao extends PagingAndSortingRepository<BlockChainChannel, String>, JpaSpecificationExecutor<BlockChainChannel> {
    List<BlockChainChannel> findByTargetOrg(String targetOrg);
    List<BlockChainChannel> findByChannelName(String channelName);
}

