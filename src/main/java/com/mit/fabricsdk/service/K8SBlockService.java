/*
 * @Author: LHD
 * @Date: 2024-01-06 12:51:51
 * @LastEditors: 308twin 790816436@qq.com
 * @LastEditTime: 2024-01-06 23:59:12
 * @Description: 
 * 
 * Copyright (c) 2024 by 308twin@790816436@qq.com, All Rights Reserved. 
 */

package com.mit.fabricsdk.service;


import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Hex;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.protos.ledger.rwset.kvrwset.KvRwset;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.BlockchainInfo;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.TxReadWriteSetInfo;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mit.fabricsdk.component.ChannelInfo;
import com.mit.fabricsdk.constant.ChannelType;
import com.mit.fabricsdk.dao.ChannelDao;
import com.mit.fabricsdk.dao.HistoryTxNumDao;
import com.mit.fabricsdk.dao.PlatformDao;
import com.mit.fabricsdk.dto.GetHistoryTxCountDto;
import com.mit.fabricsdk.dto.response.BlockTxCountResponse;
import com.mit.fabricsdk.entity.BlockChainChannel;
import com.mit.fabricsdk.entity.HistoryTxNum;
import com.mit.fabricsdk.entity.Major;
import com.mit.fabricsdk.entity.SecondaryData;
import com.mit.fabricsdk.entity.block.ChainInfo;
import com.mit.fabricsdk.utils.BuildStrArgsUtil;
import com.mit.fabricsdk.utils.GatewayUtil;
import com.mit.fabricsdk.utils.JsonUtil;
import com.mit.fabricsdk.utils.K8SUtil;
import org.springframework.beans.factory.annotation.Value;

import lombok.SneakyThrows;
@Service
public class K8SBlockService {
    @Value("${k8s.namespace}")
    String namespace;

    @Value("${k8s.peerTail}")
    String peerTail;

    @Value("${k8s.order}")
    String order;

    @Value("${k8s.adminTail}")
    String adminTail;

    @Autowired
    ChannelInfo channelInfo;

    @Autowired
    private HistoryTxNumDao historyTxNumDao;
    
    @Autowired
    private PlatformDao platformDao;

    @Autowired
    private ChannelDao channelDao;

    /**
     * @Author: LHD
     * @Date: 2024-01-06 23:57:27
     * @description: 
     * @param {String} channelName
     * @return {*}
     */    
    public int getBlockHeight(String channelName) throws Exception{
        List<BlockChainChannel> channel = (List<BlockChainChannel>) channelDao.findByChannelName(channelName);
        if(channel.size()==0)
            return -1;
        ObjectMapper mapper = new ObjectMapper();
        String[] command = new String[]{"/bin/bash", "-c", "export CORE_PEER_ADDRESS="+channel.get(0).getTargetOrg()+peerTail+" && peer channel getinfo -c "+channelName+" -o "+order};
        String json = K8SUtil.excuteK8SCommand(namespace, channel.get(0).getTargetOrg()+adminTail, command);
        System.out.println("This is raw json:"+json);
        int lastBracketIndex = json.lastIndexOf('{');
        String newJson = lastBracketIndex != -1 ? json.substring(lastBracketIndex) : json;
        System.out.println("This is new json:"+newJson);
        ChainInfo chainInfo = mapper.readValue(newJson, ChainInfo.class);
        return chainInfo.getHeight();
    }
}
