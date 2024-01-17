/*
 * @Author: LHD
 * @Date: 2023-12-19 13:54:39
 * @LastEditors: 308twin 790816436@qq.com
 * @LastEditTime: 2024-01-17 14:36:26
 * @FilePath: /FabricSDK_GM/src/main/java/com/mit/fabricsdk/controller/CommonChannelController.java
 * @Description: 
 * 
 * Copyright (c) 2024 by ${git_name_email}, All Rights Reserved. 
 */
package com.mit.fabricsdk.controller;

import com.mit.fabricsdk.component.SmartContract;
import com.mit.fabricsdk.dto.BaseResponse;
import com.mit.fabricsdk.dto.request.GetChainHeightRequest;
import com.mit.fabricsdk.dto.request.InitRequest;
import com.mit.fabricsdk.dto.request.SearchMajorRequest;
import com.mit.fabricsdk.service.K8SBlockService;
import com.mit.fabricsdk.service.SmartContractService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


/**
 * Description: 区块链的一些通用方法
 * date: 2023/5/26 22:11
 * @author: Haodong Li
 * @since: JDK 1.8

 */
@RestController
public class CommonChannelController {
    @Autowired
    SmartContractService smartContractService;

    @Autowired
    K8SBlockService k8sBlockService;

    @SneakyThrows
    @GetMapping(value = "api/blockchain/common/height", produces = "application/json")
    @ApiOperation("成功获取高度")
    public BaseResponse<Object> getChainHeight(@RequestParam @ApiParam(value = "ChannelName", example = "pscada") String channelName) {
        Long height = k8sBlockService.getBlockHeight(channelName);
        return new BaseResponse<>(height, "成功获取高度");
    }

    @SneakyThrows
    @GetMapping(value = "api/blockchain/common/latestBlock", produces = "application/json")
    @ApiOperation("获取最新区块")
    public BaseResponse<Object> getLatestBlock(@RequestParam @ApiParam(value = "ChannelName", example = "pscada") String channelName,
                                               @RequestParam @ApiParam(value = "Num", example = "10", defaultValue = "10") Integer num) {
        Object res = k8sBlockService.getLatestBlock(channelName,num);
        return new BaseResponse<>(res, "成功获取最新区块");
    }

    @SneakyThrows
    @GetMapping(value = "api/blockchain/common/latestTx", produces = "application/json")
    @ApiOperation("获取最新存证")
    public BaseResponse<Object> getLatestTx(@RequestParam @ApiParam(value = "ChannelName", example = "pscada") String channelName,
                                               @RequestParam @ApiParam(value = "Num", example = "10", defaultValue = "10") Integer num,
                                            @RequestParam @ApiParam(value = "Major", example = "10", defaultValue = "") String type) {
        Object res = k8sBlockService.getLatestTx(channelName,type);
        return new BaseResponse<>(res, "成功获取最新存证");
    }


    @SneakyThrows
    @GetMapping(value = "api/blockchain/common/historyTxCount", produces = "application/json")
    @ApiOperation("获取历史交易数量(整点)")
    public BaseResponse<Object> historyTxCount(
                                            @RequestParam @ApiParam(value = "Num", example = "10", defaultValue = "10") Integer num) {
        Object res = smartContractService.getHistoryTxCount(num);
        return new BaseResponse<>(res, "成功获取历史交易数量");
    }

    @SneakyThrows
    @GetMapping(value = "api/blockchain/common/blockTxCount", produces = "application/json")
    @ApiOperation("获取区块数量和交易数量")
    public BaseResponse<Object> blockTxCount(
    ){
        Object res = k8sBlockService.getBlockTxCount();
        return new BaseResponse<>(res, "获取区块数量和交易数量");
    }
    
}
