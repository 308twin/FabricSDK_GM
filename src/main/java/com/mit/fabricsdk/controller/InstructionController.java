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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @SneakyThrows
    @PostMapping (value = "api/blockchain/instruction/search",produces = "application/json")
    @ApiOperation("Instruction查找major")
    public BaseResponse<Object> searchIns(@RequestBody @Valid SearchInsRequest request){
//        SmartContract contract = smartContractService.buildSmartContract(request.getChannelName(),request.getContractName());
//        List<Map<String,Object>> res = smartContractService.queryContract(contract.getContract(),request.toJSONString());
//        contract.getGateway().close();
//        return new BaseResponse<>(res,"查询成功");

        List<Map<String,Object>> res = smartContractService.queryContract( channelInfo.getGatewayMap().get(request.getChannelName()).getNetwork(request.getChannelName()).getContract(request.getContractName()),request.toJSONString());
        return new BaseResponse<>(res,"查询成功");
    }

    @SneakyThrows
    @PostMapping(value = "api/blockchain/instruction/save",produces = "application/json")
    @ApiOperation("BatchAddInstructions")
    public BaseResponse<Object> saveIns(@RequestBody @Valid SaveInsRequest request){
        List<AddResponse> res = new ArrayList<>();
        Contract contract =  channelInfo.getGatewayMap().get(request.getChannelName()).getNetwork(request.getChannelName()).getContract(request.getContractName());
        for (Instruction instruction:request.getInstructions() ) {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(instruction);
            jsonString = BuildStrArgsUtil.jsonTrans(jsonString);
            RunableUtil runableUtil = new RunableUtil(contract,"AddInstruction",jsonString);
            String currentTime = runableUtil.start();
            //contract.submitTransaction("AddInstruction",jsonString);


            AddResponse addResponse = new AddResponse(currentTime);
            res.add(addResponse);
        }
        return new BaseResponse<>(res,"新增成功");
    }

//    @PostMapping(value = "api/blockchain/instruction/savemysql",produces = "application/json")
//     @ApiOperation("BatchAddInstructions")
//     public BaseResponse<Object> saveInsDb(@RequestBody List<InstructionDB> instructionDBs){
//         List<String> finishTime = new ArrayList<>();
//         // for (InstructionDB instructionDB : instructionDBs) {
//         //     // instructionDao.save(instructionDB);
//         //     // Date date = new Date();
//         //     // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//         //     // String currentTime = sdf.format(date);
//         //     // finishTime.add(currentTime);
//         // }
//         return new BaseResponse<Object>(finishTime, "新增成功");
//     }
}
