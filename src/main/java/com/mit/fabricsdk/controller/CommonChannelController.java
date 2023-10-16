package com.mit.fabricsdk.controller;

import com.mit.fabricsdk.component.SmartContract;
import com.mit.fabricsdk.dto.BaseResponse;
import com.mit.fabricsdk.dto.request.GetChainHeightRequest;
import com.mit.fabricsdk.dto.request.InitRequest;
import com.mit.fabricsdk.dto.request.SearchMajorRequest;
import com.mit.fabricsdk.service.SmartContractService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Description: 区块链的一些通用方法
 * date: 2023/5/26 22:11
 * @author: Haodong Li
 * @since: JDK 1.8

 */
@RestController
public class CommonChannelController {
    @Autowired
    SmartContractService smartContractService;

    @SneakyThrows
    @PostMapping(value = "api/blockchain/common/init",produces = "application/json")
    @ApiOperation("init")
    public BaseResponse<Object> searchMajor(@RequestBody @Valid InitRequest request){
       //smartContractService.initNewChannel(request.getOrgName(),request.getChannelName());
        smartContractService.initBlockChain();
        return new BaseResponse<>("","channel创建成功");
    }

    @SneakyThrows
    @GetMapping(value = "api/blockchain/common/height", produces = "application/json")
    @ApiOperation("成功获取高度")
    public BaseResponse<Object> getChainHeight(@RequestParam @ApiParam(value = "ChannelName", example = "pscada") String channelName) {
        Long height = smartContractService.getChainHeight(channelName);
        return new BaseResponse<>(height, "成功获取高度");
    }

    @SneakyThrows
    @GetMapping(value = "api/blockchain/common/latestBlock", produces = "application/json")
    @ApiOperation("获取最新区块")
    public BaseResponse<Object> getLatestBlock(@RequestParam @ApiParam(value = "ChannelName", example = "pscada") String channelName,
                                               @RequestParam @ApiParam(value = "Num", example = "10", defaultValue = "10") Integer num) {
        Object res = smartContractService.getLatestBlock(num,channelName);
        return new BaseResponse<>(res, "成功获取最新区块");
    }

    @SneakyThrows
    @GetMapping(value = "api/blockchain/common/latestTx", produces = "application/json")
    @ApiOperation("获取最新存证")
    public BaseResponse<Object> getLatestTx(@RequestParam @ApiParam(value = "ChannelName", example = "pscada") String channelName,
                                               @RequestParam @ApiParam(value = "Num", example = "10", defaultValue = "10") Integer num,
                                            @RequestParam @ApiParam(value = "Major", example = "10", defaultValue = "Major") String type) {
        Object res = smartContractService.getLatestTx(num,channelName,type);
        return new BaseResponse<>(res, "成功获取最新存证");
    }

    @SneakyThrows
    @GetMapping(value = "api/blockchain/common/latestTxNew", produces = "application/json")
    @ApiOperation("获取最新存证")
    public BaseResponse<Object> getLatestTxNew(@RequestParam @ApiParam(value = "ChannelName", example = "pscada") String channelName,
                                            @RequestParam @ApiParam(value = "Num", example = "10", defaultValue = "10") Integer num) {
        Object res = smartContractService.getLatestTxNew(num,channelName);
        return new BaseResponse<>(res, "成功获取最新存证");
    }

    @SneakyThrows
    @GetMapping(value = "api/blockchain/common/historyTxCount", produces = "application/json")
    @ApiOperation("获取历史交易数量(整点)")
    public BaseResponse<Object> historyTxCount(
            //@RequestParam @ApiParam(value = "ChannelName", example = "pscada") String channelName,
                                            @RequestParam @ApiParam(value = "Num", example = "10", defaultValue = "10") Integer num) {
        Object res = smartContractService.getHistoryTxCount(num);
        return new BaseResponse<>(res, "成功获取历史交易数量");
    }

    @SneakyThrows
    @GetMapping(value = "api/blockchain/common/blockTxCount", produces = "application/json")
    @ApiOperation("获取区块数量和交易数量")
    public BaseResponse<Object> blockTxCount(
            //@RequestParam @ApiParam(value = "ChannelName", example = "pscada") String channelName,
            //@RequestParam @ApiParam(value = "Num", example = "10", defaultValue = "10") Integer num)
    ){
        Object res = smartContractService.getBlockTxCount();
        return new BaseResponse<>(res, "获取区块数量和交易数量");
    }
//    @SneakyThrows
//    @GetMapping(value = "api/blockchain/common/getBlockTxCount", produces = "application/json")
//    @ApiOperation("获取区块数量和交易数量")
//    public BaseResponse<Object> getBlockTxCount(
//            @RequestParam @ApiParam(value = "Num", example = "10", defaultValue = "10") Integer num) {
//        Object res = smartContractService.getBlockTxCount();
//        return new BaseResponse<>(res, "成功获取历史交易数量");
//    }
}
