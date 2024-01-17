package com.mit.fabricsdk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mit.fabricsdk.component.ChannelInfo;
import com.mit.fabricsdk.component.SmartContract;
import com.mit.fabricsdk.dao.SecondaryComparisonDao;
import com.mit.fabricsdk.dto.request.SaveMajorRequest;
import com.mit.fabricsdk.dto.request.SaveSecondaryComparisonRequest;
import com.mit.fabricsdk.dto.request.SaveSecondaryRequest;
import com.mit.fabricsdk.dto.request.SearchHackRequest;
import com.mit.fabricsdk.dto.request.SearchMajorRequest;
import com.mit.fabricsdk.dto.request.SearchSecondaryComparison;
import com.mit.fabricsdk.dto.request.SearchSecondaryRequest;
import com.mit.fabricsdk.dto.response.AddResponse;
import com.mit.fabricsdk.entity.Instruction;
import com.mit.fabricsdk.entity.Major;
import com.mit.fabricsdk.entity.SecondaryCompareResult;
import com.mit.fabricsdk.entity.SecondaryData;
import com.mit.fabricsdk.dto.BaseRequest;
import com.mit.fabricsdk.dto.BaseResponse;
import com.mit.fabricsdk.service.K8SBlockService;
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

    @Autowired
    K8SBlockService k8sBlockService;


    /**
     * @Author: LHD
     * @Date: 2024-01-14 15:49:32
     * @description: 批量新增二级数据
     * @return {*}
     */    
    @SneakyThrows
    @PostMapping(value = "api/blockchain/secondaryData/save", produces = "application/json")
    @ApiOperation("批量新增二级数据")
    public BaseResponse<Object> saveMajors(@RequestBody @Valid SaveSecondaryRequest request) {
        try {
            List<String> results = new ArrayList<>();
            ExecutorService es = Executors.newFixedThreadPool(request.getSecondaryData().size());
            CompletionService<String> cs = new ExecutorCompletionService<>(es);
            for (SecondaryData data : request.getSecondaryData()) {
                cs.submit(new Callable<String>() {
                    @Override
                    public String call() {
                        // 选取一个字段作为名称
                        String name = data.getSequence();
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            String jsonString = objectMapper.writeValueAsString(data);
                            jsonString = BuildStrArgsUtil.jsonTrans(jsonString);
                            String submitString = k8sBlockService.buildSubmitStr("AddSecondaryData", jsonString);
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
            return new BaseResponse<>(results, "执行成功");
        } catch (Exception e) {
            return new BaseResponse<>(e.toString(), "执行失败");
        }
    }

    @SneakyThrows
    @PostMapping(value = "api/blockchain/secondaryData/search", produces = "application/json")
    @ApiOperation("查找二级数据")
    public BaseResponse<Object> searchScondary(@RequestBody @Valid SearchSecondaryRequest request) {
        try {
            String res = k8sBlockService.searchK8S(request.getChannelName(), request.getContractName(), request.toChaincodeInvoke());            
            return new BaseResponse<>(k8sBlockService.toJsonObject(res), "查询成功");
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
            secondaryData.setContent(request.getContent());
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
            Object res = secondaryComparisonDao.findByMultipleFields(
                    request.getChannelName(), request.getContractName(), request.getSequence(),
                    request.getGenerationTimeFrom(), request.getGenerationTimeTo(),request.getType());
            return new BaseResponse<>(res, "查询成功");
        } catch (Exception e) {
            return new BaseResponse<>(e.toString(), "查询失败");
        }

    }

    @SneakyThrows
    @PostMapping(value = "api/blockchain/secondaryData/searchHack", produces = "application/json")
    @ApiOperation("二级数据攻击后前端弹窗")
    public BaseResponse<Object> searchHack(@RequestBody @Valid SearchHackRequest request) {
        try {
          Timestamp now = new Timestamp(System.currentTimeMillis());
          Timestamp fiveSecondsAgo = new Timestamp(System.currentTimeMillis() - 5000);        
          List<SecondaryCompareResult>  res = secondaryComparisonDao.findByStationAndTimeNative( fiveSecondsAgo, now);
          List<SecondaryCompareResult> filteredResults = res.stream()
    .filter(scr -> scr.getChannelName().contains(request.stationName))
    .collect(Collectors.toList());
          
            return new BaseResponse<>(filteredResults, "查询成功");
        } catch (Exception e) {
            return new BaseResponse<>(e.toString(), "查询失败");
        }

    }


}
