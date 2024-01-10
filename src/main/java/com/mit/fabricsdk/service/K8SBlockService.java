/*
 * @Author: LHD
 * @Date: 2024-01-06 12:51:51
 * @LastEditors: 308twin 790816436@qq.com
 * @LastEditTime: 2024-01-10 16:52:08
 * @Description: 
 * 
 * Copyright (c) 2024 by 308twin@790816436@qq.com, All Rights Reserved. 
 */
/*
 * @Author: LHD
 * @Date: 2024-01-06 12:51:51
 * @LastEditors: 308twin 790816436@qq.com
 * @LastEditTime: 2024-01-09 17:02:44
 * @Description: 
 * 
 * Copyright (c) 2024 by 308twin@790816436@qq.com, All Rights Reserved. 
 */
/*
 * @Author: LHD
 * @Date: 2024-01-06 12:51:51
 * @LastEditors: 308twin 790816436@qq.com
 * @LastEditTime: 2024-01-09 15:08:18
 * @Description: 
 * 
 * Copyright (c) 2024 by 308twin@790816436@qq.com, All Rights Reserved. 
 */

package com.mit.fabricsdk.service;

import static com.mit.fabricsdk.utils.RemoveElementUtil.removeFirstElement;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Base64;

import com.mit.fabricsdk.entity.block.BlockInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.mit.fabricsdk.dao.ChannelDao;
import com.mit.fabricsdk.dao.ChannelInfoDao;
import com.mit.fabricsdk.dao.HistoryTxNumDao;
import com.mit.fabricsdk.dao.PlatformDao;
import com.mit.fabricsdk.dto.GetHistoryTxCountDto;
import com.mit.fabricsdk.entity.BlockChainChannel;
import com.mit.fabricsdk.entity.ChaincodeInvoke;
import com.mit.fabricsdk.entity.ChannelInfo;
import com.mit.fabricsdk.entity.HistoryTxNum;
import com.mit.fabricsdk.entity.Major;
import com.mit.fabricsdk.entity.SecondaryData;
import com.mit.fabricsdk.entity.Transaction;
import com.mit.fabricsdk.entity.block.ChainInfo;
import com.mit.fabricsdk.utils.BuildStrArgsUtil;
import com.mit.fabricsdk.utils.GatewayUtil;
import com.mit.fabricsdk.utils.JsonUtil;
import com.mit.fabricsdk.utils.K8SUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;

import lombok.SneakyThrows;

@Service
public class K8SBlockService {
    @Value("${k8s.namespace}")
    String namespace;

    @Value("${k8s.peerTail}")
    String peerTail;

    @Value("${k8s.order}")
    String order;

    @Value("${k8s.adminTail}")
    String adminTail;

    @Value("${k8s.cafile}")
    String cafile;

    @Autowired
    private HistoryTxNumDao historyTxNumDao;

    @Autowired
    private PlatformDao platformDao;

    @Autowired
    private ChannelDao channelDao;

    @Autowired
    private ChannelInfoDao channelInfoDao;

    /**
     * @Author: LHD
     * @Date: 2024-01-06 23:57:27
     * @description: 获取区块高度
     * @param {String} channelName
     * @return {*}
     */
    public long getBlockHeight(String channelName) throws Exception {
        List<BlockChainChannel> channel = (List<BlockChainChannel>) channelDao.findByChannelName(channelName);
        if (channel.size() == 0)
            return -1L;
        ObjectMapper mapper = new ObjectMapper();
        String[] command = new String[] { "/bin/bash", "-c", "export CORE_PEER_ADDRESS=" + channel.get(0).getTargetOrg()
                + peerTail + " && peer channel getinfo -c " + channelName + " -o " + order };
        String json = K8SUtil.excuteK8SCommand(namespace, channel.get(0).getTargetOrg() + adminTail, command);
        System.out.println("This is raw json:" + json);
        int lastBracketIndex = json.lastIndexOf('{');
        String newJson = lastBracketIndex != -1 ? json.substring(lastBracketIndex) : json;
        System.out.println("This is new json:" + newJson);
        ChainInfo chainInfo = mapper.readValue(newJson, ChainInfo.class);
        return chainInfo.getHeight();
    }

    /**
     * @Author: LHD
     * @Date: 2024-01-09 12:12:28
     * @description: 获取区块信息
     * @param {String} channelName
     * @return {*}
     */
    public BlockInfo getBlockInfo(String channelName, Long blockNum) throws Exception {
        List<BlockChainChannel> channel = (List<BlockChainChannel>) channelDao.findByChannelName(channelName);
        if (channel.size() == 0)
            return null;
        ObjectMapper mapper = new ObjectMapper();
        String blockFileName = channelName + "_" + blockNum + ".block";
        String blockJsonFileName = channelName + "_" + blockNum + ".json";
        String[] command1 = new String[] { "/bin/bash", "-c",
                "export CORE_PEER_ADDRESS=" + channel.get(0).getTargetOrg() + peerTail + " && peer channel fetch "
                        + blockNum + " " + blockFileName + " -c " + channelName + " -o " + order + " --tls --cafile "
                        + cafile };
        String[] command2 = new String[] { "/bin/bash", "-c", "configtxlator proto_decode --input " + blockFileName
                + " --type common.Block | jq . > " + blockJsonFileName };
        String[] command3 = new String[] { "/bin/bash", "-c", "cat " + blockJsonFileName };
        String[] command4 = new String[] { "/bin/bash", "-c", "rm " + blockFileName + " " + blockJsonFileName };

        K8SUtil.excuteK8SCommand(namespace, channel.get(0).getTargetOrg() + adminTail, command1);
        K8SUtil.excuteK8SCommand(namespace, channel.get(0).getTargetOrg() + adminTail, command2);
        String json = K8SUtil.excuteK8SCommand(namespace, channel.get(0).getTargetOrg() + adminTail, command3);
        // K8SUtil.excuteK8SCommand(namespace, channel.get(0).getTargetOrg()+adminTail,
        // command4);

        BlockInfo blockInfo = mapper.readValue(json, BlockInfo.class);
        return blockInfo;
    }

    /**
     * @Author: LHD
     * @Date: 2024-01-09 13:51:36
     * @description: 获取区块交易数
     * @param {Integer} num 分页数量
     * @return {*}
     */
    public Object getHistoryTxCount(Integer num) {
        List<String> channelList = historyTxNumDao.findDistinctChannel();
        List<GetHistoryTxCountDto> dtos = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(0, num);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        // for (String channelname : channelInfo.getChannelMap().keySet()){
        for (String channelname : channelList) {
            List<HistoryTxNum> entities = historyTxNumDao.getTopCommonlyHistoryTxNums(channelname, pageRequest);
            GetHistoryTxCountDto dto = new GetHistoryTxCountDto();
            dto.setChannelName(channelname);
            List<String> xaxis = new ArrayList<>();
            List<Long> yaxis = new ArrayList<>();
            for (HistoryTxNum entity : entities) {
                xaxis.add(formatter.format(entity.getCreateAt()));
                yaxis.add(entity.getNum());
            }
            dto.setYaxis(yaxis);
            dto.setXaxis(xaxis);
            dtos.add(dto);
        }
        return dtos;
    }

    /**
     * @Author: LHD
     * @Date: 2024-01-09 13:58:01
     * @description: 获取区块内的交易数量
     * @param {BlockInfo} blockInfo
     * @return {*}
     */
    public int getTransactionCount(BlockInfo blockInfo) {
        return blockInfo.getData().getData().size();
    }

    /**
     * @Author: LHD
     * @Date: 2024-01-09 17:17:05
     * @description: 获取区块生成时间
     * @param {BlockInfo} blockInfo
     * @return {*}
     */
    public String getBlockGenerationTime(BlockInfo blockInfo) {
        String isoDateTime = blockInfo.getData().getData().get(getTransactionCount(blockInfo) - 1).getPayload()
                .getHeader().getChannel_header().getTimestamp();
        return getFormatDate(isoDateTime);
    }

    /**
     * @Author: LHD
     * @Date: 2024-01-10 12:52:47
     * @description: 获取格式化的时间
     * @param {String} date
     * @return {*}
     */
    public String getFormatDate(String date) {
        OffsetDateTime odt = OffsetDateTime.parse(date);
        // 格式化日期时间为指定的格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = odt.format(formatter);
        return formattedDateTime;
    }

    /**
     * @Author: LHD
     * @Date: 2024-01-09 14:39:15
     * @description: 更新数据库中的channel_info
     * @return {*}
     */
    public void GenerateChannelInfo() {
        List<BlockChainChannel> channels = (List<BlockChainChannel>) channelDao.findAll();

        List<Object[]> resultList = channelInfoDao.findMaxChannelHeightForEachChannel();
        Map<String, Long> channelHeightMap = new HashMap<>();

        for (Object[] result : resultList) {
            String channelName = (String) result[0];
            Long channelHeight = (Long) result[1];

            channelHeightMap.put(channelName, channelHeight);
        }
        for (BlockChainChannel channel : channels) {
            try {
                Long initHeight = channelHeightMap.getOrDefault(channel.getChannelName(), 5L); // 5 is the default value
                Long height = getBlockHeight(channel.getChannelName());
                Long txCount = 0L;
                for (Long i = initHeight; i < height; i++) {
                    BlockInfo blockInfo = getBlockInfo(channel.getChannelName(), i);
                    String blockGenerationTime = getBlockGenerationTime(blockInfo);
                    txCount += getTransactionCount(blockInfo);
                    ChannelInfo channelInfo = new ChannelInfo();
                    channelInfo.setChannelName(channel.getChannelName());
                    channelInfo.setChannelHeight(i);
                    channelInfo.setChannelTxCount(txCount);
                    channelInfo.setNewestBlockHash(blockInfo.getHeader().getData_hash());
                    channelInfo.setNewestBlockTime(blockGenerationTime);
                    channelInfoDao.save(channelInfo);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @Author: LHD
     * @Date: 2024-01-10 12:48:57
     * @description: 根据最新的五个区块获取最新的5个交易信息.
     * @return {*}
     */
    public List<Transaction> getLatestTx(String channelName) {
        try {
            List<Transaction> transactions = new ArrayList<>();
            long height = getBlockHeight(channelName);
            for (long i = Math.max(5, height - 5); i < height; i++) {
                BlockInfo blockInfo = getBlockInfo(channelName, i);
                int txCount = getTransactionCount(blockInfo);
                for (int j = 0; j < txCount; j++) {
                    Transaction transaction = new Transaction();
                    transaction.setTimestamp(getFormatDate(blockInfo.getData().getData().get(j).getPayload().getHeader()
                            .getChannel_header().getTimestamp()));
                    transaction.setTxHash(blockInfo.getData().getData().get(j).getPayload().getHeader()
                            .getChannel_header().getTx_id());
                    transaction.setDevice(getDevice(blockInfo, j));
                    transactions.add(transaction);
                }
            }
            if (transactions.size() > 5)
                return transactions.subList(transactions.size() - 5, transactions.size());
            else
                return transactions;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Author: LHD
     * @Date: 2024-01-10 13:42:27
     * @description: 获取区块中的设备信息，如果是major的话
     * @param {BlockInfo} blockInfo
     * @param {int}       txIndex
     * @return {*}
     */
    public String getDevice(BlockInfo blockInfo, int txIndex) {
        String txValue = blockInfo.getData().getData().get(txIndex).getPayload().getData().getActions().get(0)
                .getPayload().getAction().getProposal_response_payload().getExtension().getResults().getNs_rwset()
                .get(1).getRwset().getWrites().get(0).getValue();
        byte[] decodedBytes = Base64.getDecoder().decode(txValue);
        txValue = new String(decodedBytes);
        if (txValue.contains("Device")) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String jsonStr = BuildStrArgsUtil.jsonToObjectTrans(txValue);
                Major major = mapper.readValue(jsonStr, Major.class);
                return major.getDevice();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * @Author: LHD
     * @Date: 2024-01-10 15:31:30
     * @description: 执行搜索相关命令
     * @param {String} channelName
     * @param {String} contractName
     * @param {String} searchString
     * @return {*}
     */
    public String searchK8S(String channelName, String contractName, String searchString) throws Exception {
        List<BlockChainChannel> channel = (List<BlockChainChannel>) channelDao.findByChannelName(channelName);
        if (channel.size() == 0)
            return null;
        String org = channel.get(0).getTargetOrg();
        String[] command = new String[] { "/bin/sh", "-c",
                "export CORE_PEER_ADDRESS=" + channel.get(0).getTargetOrg() + peerTail + " && peer chaincode query -C "
                        + channelName + " -n " + contractName + " -c '" + searchString + "'" };
        String res = K8SUtil.excuteK8SCommand(namespace, org, command);
        return res;
    }

    /**
     * @Author: LHD
     * @Date: 2024-01-10 15:31:46
     * @description: 转换json为匿名对象
     * @return {*}
     */
    public List<Map<String, Object>> toJsonObject(String json) {
        JSONArray contentArray1 = JSONArray.parseArray(json);
        if (contentArray1 == null)
            return null;

        List<Map<String, Object>> list = new LinkedList<>();
        for (int i = 0; i < contentArray1.size(); i++) {
            Map<String, Object> map = ((JSONObject) contentArray1.get(i)).getInnerMap();
            list.add(map);
        }

        return list;
    }

    /**
     * @Author: LHD
     * @Date: 2024-01-10 15:33:40
     * @description: 执行submit transaction 操作
     * @return {*} 执行结果
     * @throws Exception
     */
    public String submitK8S(String channelName, String contractName, String submitString) throws Exception {
        try {
            List<BlockChainChannel> channel = (List<BlockChainChannel>) channelDao.findByChannelName(channelName);
            if (channel.size() == 0)
                throw new Exception("channel not found");
            String org = channel.get(0).getTargetOrg();
            String[] command = new String[] { "/bin/sh", "-c", "export CORE_PEER_ADDRESS=" + channel.get(0).getTargetOrg() + peerTail + " && peer chaincode invoke -C " + channelName + " -n "
                    + contractName + " -c '" + submitString + "'" + " -o " + order + " --tls --cafile " + cafile };
            String res = K8SUtil.excuteK8SCommand(namespace, org, command);
            return res;
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * @Author: LHD
     * @Date: 2024-01-10 16:12:47
     * @description: 构造区块链链码新增相关功能的字符串
     *               例如：'{"function":"AddEvent","Args":["{\"GenerationTime\":\"2022-03-11T14:08:00Z\",\"Major\":\"EMCS\",\"IdentificationPoint\":\"event1\",\"Device\":\"device1\",\"Description\":\"description1\",\"EventLevel\":\"level1\",\"Remark\":\"remark1\"}"]}'
     * @param {String} function
     * @param {String} argStr
     * @return {*}
     */
    public String buildSubmitStr(String function, String argStr) {
        ChaincodeInvoke chaincodeInvoke = new ChaincodeInvoke();
        chaincodeInvoke.setFunction(function);
        //argStr = argStr.replace("\"", "\\\"");
        chaincodeInvoke.setArgs(new String[] { argStr });
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(chaincodeInvoke);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

}
