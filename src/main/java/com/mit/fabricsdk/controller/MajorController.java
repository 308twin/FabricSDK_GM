package com.mit.fabricsdk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mit.fabricsdk.component.ChannelInfo;
import com.mit.fabricsdk.component.SmartContract;
import com.mit.fabricsdk.dto.request.SaveInsRequest;
import com.mit.fabricsdk.dto.request.SaveMajorRequest;
import com.mit.fabricsdk.dto.request.SearchMajorRequest;
import com.mit.fabricsdk.dto.response.AddResponse;
import com.mit.fabricsdk.entity.Instruction;
import com.mit.fabricsdk.entity.Major;
import com.mit.fabricsdk.dto.BaseResponse;
import com.mit.fabricsdk.service.K8SBlockService;
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
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Haodong Li
 * @date 2023年05月24日 13:57
 */
@RestController
@Api(tags = "Major区块链接口调用")
public class MajorController {
    @Autowired
    SmartContractService smartContractService;

    @Autowired
    ChannelInfo channelInfo;

    @Autowired
    K8SBlockService k8sBlockService;

    @SneakyThrows
    @PostMapping(value = "api/blockchain/major/search", produces = "application/json")
    @ApiOperation("QueryEvents查找major")
    public BaseResponse<Object> searchMajor(@RequestBody @Valid SearchMajorRequest request) {
        try {
            String res = k8sBlockService.searchK8S(request.getChannelName(), request.getContractName(), request.toChaincodeInvoke());            
            return new BaseResponse<>(k8sBlockService.toJsonObject(res), "查询成功");
        } catch (Exception e) {
            return new BaseResponse<>(e.toString(), "查询失败");
        }        
    }

    @SneakyThrows
    @PostMapping(value = "api/blockchain/major/save", produces = "application/json")
    @ApiOperation("BatchAddEvents新增major")
    public BaseResponse<Object> saveMajors(@RequestBody @Valid SaveMajorRequest request) {
        try {
            List<String> results = new ArrayList<>();
            ExecutorService es = Executors.newFixedThreadPool(request.getMajors().size());
            CompletionService<String> cs = new ExecutorCompletionService<>(es);
            for (Major data : request.getMajors()) {
                cs.submit(new Callable<String>() {
                    @Override
                    public String call() {
                        // 选取一个字段作为名称
                        String name = data.getIdentificationPoint();
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            String jsonString = objectMapper.writeValueAsString(data);
                            jsonString = BuildStrArgsUtil.jsonTrans(jsonString);
                            String submitString = k8sBlockService.buildSubmitStr("AddEvent", jsonString);
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

            for (int i = 0; i < request.getMajors().size(); i++) {
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
