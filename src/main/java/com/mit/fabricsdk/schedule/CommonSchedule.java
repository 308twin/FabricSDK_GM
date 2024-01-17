/*
 * @Author: LHD
 * @Date: 2023-12-19 13:54:39
 * @LastEditors: 308twin 790816436@qq.com
 * @LastEditTime: 2024-01-17 14:37:06
 * @Description: 
 * 
 * Copyright (c) 2024 by 308twin@790816436@qq.com, All Rights Reserved. 
 */
package com.mit.fabricsdk.schedule;

import com.mit.fabricsdk.component.ChannelInfo;
import com.mit.fabricsdk.dao.ChannelDao;
import com.mit.fabricsdk.dao.HistoryTxNumDao;
import com.mit.fabricsdk.dto.BaseResponse;
import com.mit.fabricsdk.entity.BlockChainChannel;
import com.mit.fabricsdk.entity.HistoryTxNum;
import com.mit.fabricsdk.service.K8SBlockService;
import com.mit.fabricsdk.service.SmartContractService;

import lombok.SneakyThrows;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.scheduling.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Haodong Li
 * @date 2023年05月28日 10:52
 */
@Component
// @EnableAsync
// @EnableScheduling
public class CommonSchedule {
    private static final Logger logger = LoggerFactory.getLogger(CommonSchedule.class);

    @Autowired
    ChannelInfo channelInfo;

    @Autowired
    HistoryTxNumDao historyTxNumDao;

    @Autowired
    SmartContractService smartContractService;

    @Autowired
    K8SBlockService k8sBlockService;

    @Autowired
    private ChannelDao channelDao;

    

    /**
     * @Author: LHD
     * @Date: 2024-01-17 14:37:45
     * @description: 每五分钟(300000ms)更新一次交易数量
     * @return {*}
     */   
    @Scheduled(cron = "50 59 * * * *")
    @Scheduled(fixedRate = 300000)
    public void CalculateTxNum() throws InvalidArgumentException, ProposalException {
        logger.info("CalculateTxNum:Start");
        List<BlockChainChannel> channels;
        channels = (List<BlockChainChannel>) channelDao.findAll();
        for (BlockChainChannel channel : channels) {
            long txNum = k8sBlockService.getTxNumNow(channel.getChannelName());
            HistoryTxNum entity = new HistoryTxNum();
            entity.setNum(txNum);
            entity.setChannel(channel.getChannelName());
            historyTxNumDao.save(entity);
        }
    }


    /**
     * @Author: LHD
     * @Date: 2024-01-17 14:37:22
     * @description: 每五分钟(300000ms)更新一次通道信息(区块高度、交易数量
     * @return {*}
     */    
    @Scheduled(fixedRate = 300000)
    public void UpdateChannelInfo() throws InvalidArgumentException, ProposalException {
        logger.info("UpdateChannelInfo:Start");
        k8sBlockService.GenerateChannelInfo(null);
    }
}
