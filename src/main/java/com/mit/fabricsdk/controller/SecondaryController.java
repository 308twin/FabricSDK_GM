package com.mit.fabricsdk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mit.fabricsdk.component.ChannelInfo;
import com.mit.fabricsdk.component.SmartContract;
import com.mit.fabricsdk.dto.request.SaveMajorRequest;
import com.mit.fabricsdk.dto.request.SaveSecondaryRequest;
import com.mit.fabricsdk.dto.request.SearchMajorRequest;
import com.mit.fabricsdk.dto.response.AddResponse;
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
import java.util.*;

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
            List<String> res = new ArrayList<>();

            Contract contract = channelInfo.getGatewayMap().get(request.getChannelName())
                    .getNetwork(request.getChannelName()).getContract(request.getContractName());
            // smartContractService.asyncAddSecondaryData(request.getSecondaryData(),
            // contract, "AddSecondaryData")
            // .thenAccept(result -> {
            // result.forEach(item -> {
            // System.out.println(item);
            // res.add(item);
            // });
            // });

            List<String> result = smartContractService
                    .asyncAddSecondaryData(request.getSecondaryData(), contract, "AddSecondaryData")
                    .join(); // This will wait for the CompletableFuture to complete

            result.forEach(item -> {
                System.out.println(item);
                res.add(item);
            });
            return new BaseResponse<>(res, "新增成功");
        } catch (Exception e) {
            return new BaseResponse<>(e.toString(), "新增失败");
        }

    }
}
