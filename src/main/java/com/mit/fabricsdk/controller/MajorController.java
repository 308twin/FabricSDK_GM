package com.mit.fabricsdk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mit.fabricsdk.component.ChannelInfo;
import com.mit.fabricsdk.component.SmartContract;
import com.mit.fabricsdk.dto.request.SaveMajorRequest;
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

    @SneakyThrows
    @PostMapping(value = "api/blockchain/major/search",produces = "application/json")
    @ApiOperation("QueryEvents查找major")
    public BaseResponse<Object> searchMajor(@RequestBody @Valid SearchMajorRequest request){
        List<Map<String,Object>> res = smartContractService.queryContract( channelInfo.getGatewayMap().get(request.getChannelName()).getNetwork(request.getChannelName()).getContract(request.getContractName()),request.toJSONString());
        return new BaseResponse<>(res,"查询成功");
    }

    @SneakyThrows
    @PostMapping(value = "api/blockchain/major/save",produces = "application/json")
    @ApiOperation("BatchAddEvents新增major")
    public BaseResponse<Object> saveMajors(@RequestBody @Valid SaveMajorRequest request){
        List<AddResponse> res = new ArrayList<>();
        for (Major major:request.getMajors() ) {
            Contract contract =  channelInfo.getGatewayMap().get(request.getChannelName()).getNetwork(request.getChannelName()).getContract(request.getContractName());
//            ObjectMapper objectMapper = new ObjectMapper();
//            String jsonString = objectMapper.writeValueAsString(major);
//            contract.submitTransaction("AddEvent",jsonString);
//
//            Date date = new Date();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String currentTime = sdf.format(date);
//            AddResponse addResponse = new AddResponse(currentTime);
//            res.add(addResponse);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(major);
            jsonString = BuildStrArgsUtil.jsonTrans(jsonString);
            RunableUtil runableUtil = new RunableUtil(contract,"AddEvent",jsonString);
            String currentTime = runableUtil.start();
            //contract.submitTransaction("AddInstruction",jsonString);


            AddResponse addResponse = new AddResponse(currentTime);
            res.add(addResponse);
        }
//        Contract contract =  channelInfo.getGatewayMap().get(request.getChannelName()).getNetwork(request.getChannelName()).getContract(request.getContractName());
//        contract.submitTransaction("BatchAddEvents",request.toJSONString());
        return new BaseResponse<>(res,"新增成功");
    }
}
