package com.mit.fabricsdk.dao;

import com.mit.fabricsdk.entity.BlockChainChannel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ChannelDao extends PagingAndSortingRepository<BlockChainChannel, String>, JpaSpecificationExecutor<BlockChainChannel> {
    List<BlockChainChannel> findByTargetOrg(String targetOrg);
     List<BlockChainChannel> findByChannelName(String channelName);
}

