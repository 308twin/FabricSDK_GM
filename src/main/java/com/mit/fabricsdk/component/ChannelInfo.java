package com.mit.fabricsdk.component;

import lombok.Data;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.sdk.Channel;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Haodong Li
 * @date 2023年05月26日 16:38
 */
@Component
@Data
public class ChannelInfo {
    @PostConstruct
    private void initialize() {
        gatewayMap = new HashMap<>();
        channelMap = new HashMap<>();
        txNumMap = new HashMap<>();
        bkTemoryNumMap = new HashMap<>();
        dateTxNum = new HashMap<>();
        dateHeight = new HashMap<>();
        lockMap = new HashMap<>();
        schedulerMap = new HashMap<>();
        platformTypeStat = new HashMap<>();
        datePlatformTypeStat = new HashMap<>();
        newContractList = new HashMap<>();
        newTypeTypeList = new HashMap<>();
    }

    private long max_thoughput = 500;
    public long TransactionNum = 0;
    public long BlockTemporaryNum = -1;

    private HashMap<String, Gateway> gatewayMap = new HashMap<>();
    private HashMap<String, Channel> channelMap = new HashMap<>();
    private Map<String, Object> txNumMap = new HashMap<>();
    private Map<String, Object> bkTemoryNumMap = new HashMap<>();

    // 和txNum一起，加一个每日的存证数量，每日的块高记录
    private Map<String, Object> dateTxNum = new HashMap<>();
    private Map<String, Object> dateHeight = new HashMap<>();

    private Map<String, Object> lockMap = new HashMap<>();
    private Map<String, ScheduledExecutorService> schedulerMap = new HashMap<>();

    private Map<String, Object> platformTypeStat = new HashMap<>();
    private Map<String, Object> datePlatformTypeStat = new HashMap<>();

    private Map<String, Integer> newContractList = new HashMap<>();
    private Map<String, String> newTypeTypeList = new HashMap<>();
}
