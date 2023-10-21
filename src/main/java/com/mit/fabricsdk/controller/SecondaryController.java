package com.mit.fabricsdk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mit.fabricsdk.component.ChannelInfo;
import com.mit.fabricsdk.component.SmartContract;
import com.mit.fabricsdk.dto.request.SaveMajorRequest;
import com.mit.fabricsdk.dto.request.SaveSecondaryRequest;
import com.mit.fabricsdk.dto.request.SearchMajorRequest;
import com.mit.fabricsdk.dto.response.AddResponse;
import com.mit.fabricsdk.entity.Major;
import com.mit.fabricsdk.entity.SecondaryData;
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

import io.netty.util.concurrent.Promise;
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
import java.util.stream.Collectors;

@RestController
public class SecondaryController {
    @Autowired
    SmartContractService smartContractService;

    @Autowired
    ChannelInfo channelInfo;

    @SneakyThrows
    @PostMapping(value = "api/blockchain/secondaryData/save", produces = "application/json")
    @ApiOperation("批量新增二级数据")
    public BaseResponse<Object> saveMajors(@RequestBody @Valid SaveSecondaryRequest request) {
        try {
            List<String> results = new ArrayList<>();

            Contract contract = channelInfo.getGatewayMap().get(request.getChannelName())
                    .getNetwork(request.getChannelName()).getContract(request.getContractName());
            ExecutorService es = Executors.newFixedThreadPool(request.getSecondaryData().size());
            CompletionService<String> cs = new ExecutorCompletionService<>(es);
            for (SecondaryData data : request.getSecondaryData()) {
                cs.submit(new Callable<String>() {
                    @Override
                    public String call() {
                        String sequence = data.getSequence();
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            String jsonString = objectMapper.writeValueAsString(data);
                            contract.submitTransaction("AddSecondaryData", jsonString);
                            return sequence + " added at " + Instant.now().toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return sequence + "failed to added at " + Instant.now().toString();
                        }
                    }
                });
            }

            for (int i = 0; i < request.getSecondaryData().size(); i++) {
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
            return new BaseResponse<>(results, "新增成功");
        } catch (Exception e) {
            return new BaseResponse<>(e.toString(), "新增失败");
        }
    }
}

// class SecondaryDataTask implements Callable<String> {
//     private final SecondaryData secondaryData;
//     private final Contract contract;
//     private final String eventName;

//     public SecondaryDataTask(SecondaryData secondaryData, Contract contract, String eventName) {
//         this.secondaryData = secondaryData;
//         this.contract = contract;
//         this.eventName = eventName;
//     }

//     @Override
//     public String call() {
//         String sequence = secondaryData.getSequence();
//         try {

//             ObjectMapper objectMapper = new ObjectMapper();
//             String jsonString = objectMapper.writeValueAsString(secondaryData);
//             contract.submitTransaction(eventName, jsonString);
//             return sequence + " added at " + Instant.now().toString();
//         } catch (Exception e) {
//             e.printStackTrace();
//             return sequence + "failed to added at " + Instant.now().toString();
//         }
//     }
// }