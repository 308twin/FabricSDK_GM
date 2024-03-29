/*
 * @Description: IPFS Controller
 * @Version: 2.0
 * @Author: Haodong Li
 * @Date: 2023-07-11 08:53:12
 * @LastEditors: 308twin 790816436@qq.com
 * @LastEditTime: 2024-01-10 16:53:45
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
import com.mit.fabricsdk.service.K8SBlockService;
import com.mit.fabricsdk.service.SmartContractService;
import com.mit.fabricsdk.utils.BuildStrArgsUtil;
import com.mit.fabricsdk.utils.K8SUtil;

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
    K8SBlockService k8sBlockService;

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
           
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(ipfsFileInfo);
            String submitString = k8sBlockService.buildSubmitStr("AddFileInfo", jsonString);
            String res = k8sBlockService.submitK8S(request.getChannelName(), request.getContractName(), submitString);
           
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
            String res = k8sBlockService.searchK8S(request.getChannelName(), request.getContractName(), request.toChaincodeInvoke());            
            return new BaseResponse<>(k8sBlockService.toJsonObject(res), "查询成功");
        } catch (Exception e) {
            return new BaseResponse<>(e.toString(), "查询失败");
        }

    }
}
