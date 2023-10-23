package com.mit.fabricsdk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mit.fabricsdk.component.ChannelInfo;
import com.mit.fabricsdk.component.SmartContract;
import com.mit.fabricsdk.dao.SecondaryComparisonDao;
import com.mit.fabricsdk.dto.request.SaveMajorRequest;
import com.mit.fabricsdk.dto.request.SaveSecondaryComparisonRequest;
import com.mit.fabricsdk.dto.request.SaveSecondaryRequest;
import com.mit.fabricsdk.dto.request.SearchMajorRequest;
import com.mit.fabricsdk.dto.request.SearchSecondaryComparison;
import com.mit.fabricsdk.dto.request.SearchSecondaryRequest;
import com.mit.fabricsdk.dto.response.AddResponse;
import com.mit.fabricsdk.entity.Major;
import com.mit.fabricsdk.entity.SecondaryCompareResult;
import com.mit.fabricsdk.entity.SecondaryData;
import com.mit.fabricsdk.dto.BaseResponse;
import com.mit.fabricsdk.service.SmartContractService;
import com.mit.fabricsdk.utils.BuildStrArgsUtil;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import com.mit.fabricsdk.utils.RunableUtil;
import lombok.SneakyThrows;
import org.hyperledger.fabric.gateway.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

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
import java.sql.Timestamp;

@RestController
public class SecondaryController {
    @Autowired
    SmartContractService smartContractService;

    @Autowired
    ChannelInfo channelInfo;

    @Autowired
    SecondaryComparisonDao secondaryComparisonDao;

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

    @SneakyThrows
    @PostMapping(value = "api/blockchain/secondaryData/search", produces = "application/json")
    @ApiOperation("查找二级数据")
    public BaseResponse<Object> searchScondary(@RequestBody @Valid SearchSecondaryRequest request) {
        try {
           Object res = smartContractService
                    .querySecondaryContract(
                            channelInfo.getGatewayMap().get(request.getChannelName())
                                    .getNetwork(request.getChannelName()).getContract(request.getContractName()),
                            request.toJSONString());
            return new BaseResponse<>(res, "查询成功");
        } catch (Exception e) {
            return new BaseResponse<>(e.toString(), "查询失败");
        }

    }

    @SneakyThrows
    @PostMapping(value = "api/blockchain/secondaryData/saveComparison", produces = "application/json")
    @ApiOperation("新增二级数据比对")
    public BaseResponse<Object> saveComparison(@RequestBody @Valid SaveSecondaryComparisonRequest request) {

        try {
            Timestamp generationTime = Timestamp.valueOf(request.getGenerationTime());
            SecondaryCompareResult secondaryData = new SecondaryCompareResult();
            secondaryData.setChannelName(request.getChannelName());
            secondaryData.setContractName(request.getContractName());
            secondaryData.setSequence(request.getSequence());
            secondaryData.setGenerationTime(generationTime);
            secondaryData.setType(request.getType());
            secondaryData.setTampering(request.getTampering());
            secondaryData.setRecieveMessage(request.getRecieveMessage());
            secondaryData.setSendMessage(request.getSendMessage());
            secondaryComparisonDao.save(secondaryData);
            return new BaseResponse<>(null, "新增成功");
        } catch (Exception e) {
            return new BaseResponse<>(e.toString(), "操作失败");
        }

    }

    @SneakyThrows
    @PostMapping(value = "api/blockchain/secondaryData/searchComparison", produces = "application/json")
    @ApiOperation("查找二级数据比对")
    public BaseResponse<Object> searchComparison(@RequestBody @Valid SearchSecondaryComparison request) {
        try {
            Sort sort;

            if (request.getSortBy() != null && !request.getSortBy().isEmpty()) {
                if ("desc".equalsIgnoreCase(request.getSortOrder())) {
                    sort = Sort.by(request.getSortBy()).descending();
                } else {
                    sort = Sort.by(request.getSortBy()).ascending();
                }
            } else {
                sort = Sort.unsorted();
            }

            Pageable pageable =  PageRequest.of(request.getPage(), request.getSize(), sort);

            Page<SecondaryCompareResult> res = secondaryComparisonDao.findByMultipleFieldsWithPagination(
                    request.getChannelName(), request.getContractName(), request.getSequence(),
                    request.getGenerationTimeFrom(), request.getGenerationTimeTo(), request.getType(),pageable);
            return new BaseResponse<>(res.getContent(), "查询成功");
        } catch (Exception e) {
            return new BaseResponse<>(e.toString(), "查询失败");
        }

    }

}

// class SecondaryDataTask implements Callable<String> {
// private final SecondaryData secondaryData;
// private final Contract contract;
// private final String eventName;

// public SecondaryDataTask(SecondaryData secondaryData, Contract contract,
// String eventName) {
// this.secondaryData = secondaryData;
// this.contract = contract;
// this.eventName = eventName;
// }

// @Override
// public String call() {
// String sequence = secondaryData.getSequence();
// try {

// ObjectMapper objectMapper = new ObjectMapper();
// String jsonString = objectMapper.writeValueAsString(secondaryData);
// contract.submitTransaction(eventName, jsonString);
// return sequence + " added at " + Instant.now().toString();
// } catch (Exception e) {
// e.printStackTrace();
// return sequence + "failed to added at " + Instant.now().toString();
// }
// }
// }