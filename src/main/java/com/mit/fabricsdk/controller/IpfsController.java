/*
 * @Description: IPFS Controller
 * @Version: 2.0
 * @Author: Haodong Li
 * @Date: 2023-07-11 08:53:12
 * @LastEditors: Haodong Li
 * @LastEditTime: 2023-07-28 15:20:40
 */
package com.mit.fabricsdk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mit.fabricsdk.component.ChannelInfo;
import com.mit.fabricsdk.component.SmartContract;
import com.mit.fabricsdk.dto.request.SaveIpfsFileRequest;
import com.mit.fabricsdk.dto.request.SaveMajorRequest;
import com.mit.fabricsdk.dto.request.SearchIpfsFileRequest;
import com.mit.fabricsdk.dto.request.SearchMajorRequest;
import com.mit.fabricsdk.dto.response.AddResponse;
import com.mit.fabricsdk.entity.IpfsFileInfo;
import com.mit.fabricsdk.entity.Major;
import com.mit.fabricsdk.dto.BaseResponse;
import com.mit.fabricsdk.service.SmartContractService;
import com.mit.fabricsdk.utils.BuildStrArgsUtil;
import javax.validation.Valid;

import com.mit.fabricsdk.utils.RunableUtil;
import lombok.SneakyThrows;
import org.hyperledger.fabric.gateway.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
@RestController
public class IpfsController {
    @Autowired
    SmartContractService smartContractService;

    @Autowired
    ChannelInfo channelInfo;

    /**
     * @description: 存储IPFS文件
     * @return {*}
     * @author: Haodong Li
     */
    @PostMapping(value = "api/blockchain/ipfs/save", produces = "application/json")
    @ApiOperation("存储IPFS文件")
    public BaseResponse<Object> saveIpfsFileInfo(@RequestBody @Valid SaveIpfsFileRequest request) {
        try {
            LocalDateTime now = LocalDateTime.now();
            // 创建日期时间格式化器
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            // 格式化当前时间
            String formattedDateTime = now.format(formatter);
            request.getIpfsFileInfo().setGenerationTime(formattedDateTime);
            IpfsFileInfo ipfsFileInfo = request.getIpfsFileInfo();
            Contract contract = channelInfo.getGatewayMap().get(request.getChannelName())
                    .getNetwork(request.getChannelName()).getContract(request.getContractName());
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(ipfsFileInfo);
            
            RunableUtil runableUtil = new RunableUtil(contract, "AddFileInfo", jsonString);
            runableUtil.start();
            return new BaseResponse<>("", "新增成功");
        } catch (Exception e) {
            System.out.println(e);
            return new BaseResponse<>(e.toString(), "新增失败");
        }

    }

    @PostMapping(value = "api/blockchain/ipfs/search", produces = "application/json")
    @ApiOperation("QueryEvents查找ipfs")
    public BaseResponse<Object> searchIpfs(@RequestBody @Valid SearchIpfsFileRequest request) {
        try {
            if(channelInfo.getGatewayMap().get(request.getChannelName())==null)
                throw new Exception(request.getChannelName()+"通道Gateway未能正确创建");
            List<Map<String, Object>> res = smartContractService
                    .queryContract(
                            channelInfo.getGatewayMap().get(request.getChannelName())
                                    .getNetwork(request.getChannelName()).getContract(request.getContractName()),
                            request.toJSONString());
            return new BaseResponse<>(res, "查询成功");
        } catch (Exception e) {
            return new BaseResponse<>(e.toString(), "查询失败");
        }

    }
}
