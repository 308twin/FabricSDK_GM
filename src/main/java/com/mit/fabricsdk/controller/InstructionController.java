/*
 * @Author: LHD
 * @Date: 2023-12-19 13:54:39
 * @LastEditors: 308twin 790816436@qq.com
 * @LastEditTime: 2024-01-14 15:56:11
 * @Description: 
 * 
 * Copyright (c) 2024 by 308twin@790816436@qq.com, All Rights Reserved. 
 */
package com.mit.fabricsdk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mit.fabricsdk.component.ChannelInfo;
import com.mit.fabricsdk.component.SmartContract;
import com.mit.fabricsdk.dto.BaseResponse;
import com.mit.fabricsdk.dto.request.SaveInsRequest;
import com.mit.fabricsdk.dto.request.SaveMajorRequest;
import com.mit.fabricsdk.dto.request.SearchInsRequest;
import com.mit.fabricsdk.dto.request.SearchMajorRequest;
import com.mit.fabricsdk.dto.response.AddResponse;
import com.mit.fabricsdk.entity.Instruction;
import com.mit.fabricsdk.entity.InstructionDB;
import com.mit.fabricsdk.entity.Major;
import com.mit.fabricsdk.entity.SecondaryData;
import com.mit.fabricsdk.service.K8SBlockService;
import com.mit.fabricsdk.service.SmartContractService;
import com.mit.fabricsdk.utils.BuildStrArgsUtil;
import com.mit.fabricsdk.utils.RunableUtil;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.hyperledger.fabric.gateway.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Haodong Li
 * @date 2023年05月25日 14:50
 */
@RestController
public class InstructionController {
    @Autowired
    SmartContractService smartContractService;

    @Autowired
    ChannelInfo channelInfo;

    @Autowired
    K8SBlockService k8sBlockService;

    @SneakyThrows
    @PostMapping(value = "api/blockchain/instruction/search", produces = "application/json")
    @ApiOperation("Instruction查找major")
    public BaseResponse<Object> searchIns(@RequestBody @Valid SearchInsRequest request) {
        try {
            String res = k8sBlockService.searchK8S(request.getChannelName(), request.getContractName(), request.toChaincodeInvoke());            
            return new BaseResponse<>(k8sBlockService.toJsonObject(res), "查询成功");
        } catch (Exception e) {
            return new BaseResponse<>(e.toString(), "查询失败");
        }        
    }

    /**
     * @Author: LHD
     * @Date: 2024-01-14 15:54:24
     * @description: 调用k8s新增instruction
     * @return {*}
     */    
    @SneakyThrows
    @PostMapping(value = "api/blockchain/instruction/save", produces = "application/json")
    @ApiOperation("BatchAddInstructions")
    public BaseResponse<Object> saveIns(@RequestBody @Valid SaveInsRequest request) {
        try {
            List<String> results = new ArrayList<>();
            ExecutorService es = Executors.newFixedThreadPool(request.getInstructions().size());
            CompletionService<String> cs = new ExecutorCompletionService<>(es);
            for (Instruction data : request.getInstructions()) {
                cs.submit(new Callable<String>() {
                    @Override
                    public String call() {
                        // 选取一个字段作为名称
                        String name = data.getPid();
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            String jsonString = objectMapper.writeValueAsString(data);
                            jsonString = BuildStrArgsUtil.jsonTrans(jsonString);
                            String submitString = k8sBlockService.buildSubmitStr("AddInstruction", jsonString);
                            String res = k8sBlockService.submitK8S(request.getChannelName(), request.getContractName(),
                                    submitString);
                            return name + " added at " + Instant.now().toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return name + " failed to added at " + Instant.now().toString() + " caused by "
                                    + e.toString();
                        }
                    }
                });
            }

            for (int i = 0; i < request.getInstructions().size(); i++) {
                try {
                    String result = cs.take().get();
                    results.add(result);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    // 处理异常，例如添加默认值或记录错误
                    results.add("Error processing data");
                }
            }

            // 关闭线程池
            es.shutdown();
            return new BaseResponse<>(results, "执行成功");
        } catch (Exception e) {
            return new BaseResponse<>(e.toString(), "执行失败");
        }
    }
}
