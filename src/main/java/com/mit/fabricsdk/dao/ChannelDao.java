package com.mit.fabricsdk.dao;

import com.mit.fabricsdk.entity.BlockChainChannel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ChannelDao extends PagingAndSortingRepository<BlockChainChannel, Long>, JpaSpecificationExecutor<BlockChainChannel> {
}

