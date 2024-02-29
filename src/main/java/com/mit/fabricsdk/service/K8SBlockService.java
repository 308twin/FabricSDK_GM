/*
 * @Author: LHD
 * @Date: 2024-01-06 12:51:51
 * @LastEditors: 308twin 790816436@qq.com
 * @LastEditTime: 2024-01-22 14:48:51
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

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Base64;

import com.mit.fabricsdk.entity.block.BlockInfo;

import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.mit.fabricsdk.dao.ChannelDao;
import com.mit.fabricsdk.dao.ChannelInfoDao;
import com.mit.fabricsdk.dao.HistoryTxNumDao;
import com.mit.fabricsdk.dao.PlatformDao;
import com.mit.fabricsdk.dto.GetHistoryTxCountDto;
import com.mit.fabricsdk.dto.LatestBlockDto;
import com.mit.fabricsdk.dto.response.BlockTxCountResponse;
import com.mit.fabricsdk.entity.BlockChainChannel;
import com.mit.fabricsdk.entity.ChaincodeInvoke;
import com.mit.fabricsdk.entity.ChannelInfo;
import com.mit.fabricsdk.entity.HistoryTxNum;
import com.mit.fabricsdk.entity.Major;
import com.mit.fabricsdk.entity.Transaction;
import com.mit.fabricsdk.entity.block.ChainInfo;
import com.mit.fabricsdk.utils.BuildStrArgsUtil;
import com.mit.fabricsdk.utils.K8SUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;

@Service
public class K8SBlockService {
    private static final Logger logger = LoggerFactory.getLogger(SmartContractService.class);

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
        // 生成.block文件
        String bash1 = "export CORE_PEER_ADDRESS=" + channel.get(0).getTargetOrg() + peerTail
                + " && peer channel fetch "
                + blockNum + " " + blockFileName + " -c " + channelName + " -o " + order + " --tls --cafile "
                + cafile;
        String bash2 = "configtxlator proto_decode --input " + blockFileName
                + " --type common.Block | jq . > " + blockJsonFileName;
        String bash3 = "cat " + blockJsonFileName;
        String bash4 = "rm " + blockFileName + " " + blockJsonFileName;

        String[] command1 = new String[] { "/bin/bash", "-c",
                "export CORE_PEER_ADDRESS=" + channel.get(0).getTargetOrg() + peerTail + " && peer channel fetch "
                        + blockNum + " " + blockFileName + " -c " + channelName + " -o " + order + " --tls --cafile "
                        + cafile };
        // 生成.json文件
        String[] command2 = new String[] { "/bin/bash", "-c", "configtxlator proto_decode --input " + blockFileName
                + " --type common.Block | jq . > " + blockJsonFileName };
        // 读取.json文件
        String[] command3 = new String[] { "/bin/bash", "-c", "cat " + blockJsonFileName };
        // 删除.block和.json文件
        String[] command4 = new String[] { "/bin/bash", "-c", "rm " + blockFileName + " " + blockJsonFileName };
        String[] command = new String[] { "/bin/bash", "-c", bash1 + " && " + bash2 + " && " + bash3 + " && " + bash4 };

        String json = K8SUtil.excuteK8SCommand(namespace, channel.get(0).getTargetOrg() + adminTail, command);
        json = json.substring(json.indexOf('{'), json.lastIndexOf('}') + 1);
        // K8SUtil.excuteK8SCommand(namespace, channel.get(0).getTargetOrg() +
        // adminTail, command1);
        // K8SUtil.excuteK8SCommand(namespace, channel.get(0).getTargetOrg() +
        // adminTail, command2);
        // String json = K8SUtil.excuteK8SCommand(namespace,
        // channel.get(0).getTargetOrg() + adminTail, command3);
        // try {
        // K8SUtil.excuteK8SCommand(namespace, channel.get(0).getTargetOrg() +
        // adminTail, command4);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        BlockInfo blockInfo = null;
        try {
            blockInfo = mapper.readValue(json, BlockInfo.class);
            return blockInfo;

        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void GenerateChannelInfo(String designatedChannelName) throws Exception {
        List<BlockChainChannel> channels;
        if (designatedChannelName == null || designatedChannelName.equals("")) {
            channels = (List<BlockChainChannel>) channelDao.findAll();
        } else {
            channels = (List<BlockChainChannel>) channelDao.findByChannelName(designatedChannelName);
        }

        List<Object[]> resultList = channelInfoDao.findMaxChannelHeightForEachChannel();
        Map<String, Long> channelHeightMap = new HashMap<>();
        Map<String, Long> channelTxCountMap = new HashMap<>();
        for (Object[] result : resultList) {
            String channelName = (String) result[0];
            Long channelHeight = (Long) result[1];
            Long channelTxCount = (Long) result[2];
            channelHeightMap.put(channelName, channelHeight);
            channelTxCountMap.put(channelName, channelTxCount);
        }
        for (BlockChainChannel channel : channels) {
            try {
                Long recordHeight = channelHeightMap.getOrDefault(channel.getChannelName(), 0L);
                Long initHeight = channelHeightMap.getOrDefault(channel.getChannelName(), 4L); // 5 is the default value
                Long txCount = channelTxCountMap.getOrDefault(channel.getChannelName(), 0L);
                Long height = getBlockHeight(channel.getChannelName());
                // 数据库中没有记录的情况
                if (recordHeight == 0) {
                    // 高度小于5的部分(实际高度是4)，交易数量都是0
                    for (int i = 0; i < 5; i++) {
                        BlockInfo blockInfo = getBlockInfo(channel.getChannelName(), (long) i);
                        ChannelInfo channelInfo = new ChannelInfo();
                        String blockGenerationTime = getBlockGenerationTime(blockInfo);
                        channelInfo.setChannelName(channel.getChannelName());
                        channelInfo.setChannelHeight((long) i);
                        channelInfo.setChannelTxCount(0L);
                        channelInfo.setNewestBlockHash(blockInfo.getHeader().getData_hash());
                        channelInfo.setNewestBlockTime(blockGenerationTime);
                        channelInfoDao.save(channelInfo);
                    }
                    // 高度大于5的部分，实际高度是height-1
                    if (height > 5) {
                        for (long i = 5; i < height; i++) {
                            BlockInfo blockInfo = getBlockInfo(channel.getChannelName(), i);
                            if (blockInfo != null) {
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

                        }
                    }

                }
                // 数据库中有记录，则更新记录
                else {
                    if (height == 5) {
                        BlockInfo blockInfo = getBlockInfo(channel.getChannelName(), 4L);
                        if (blockInfo != null) {
                            String blockGenerationTime = getBlockGenerationTime(blockInfo);
                            ChannelInfo channelInfo = new ChannelInfo();
                            channelInfo.setChannelName(channel.getChannelName());
                            channelInfo.setChannelHeight(4L);
                            channelInfo.setChannelTxCount(0L);
                            channelInfo.setNewestBlockHash(blockInfo.getHeader().getData_hash());
                            channelInfo.setNewestBlockTime(blockGenerationTime);
                            channelInfoDao.save(channelInfo);
                        }
                    } else if (initHeight + 1 == height) { // 没有新增区块的情况，应该用之前区块的加上当前区块的交易数量
                        List<ChannelInfo> channelInfoList = channelInfoDao.findByChannelHeight(initHeight - 1);
                        if (channelInfoList.size() == 0)
                            continue;
                        ChannelInfo newestChannelInfo = channelInfoList.get(channelInfoList.size() - 1);
                        BlockInfo blockInfo = getBlockInfo(channel.getChannelName(), initHeight);
                        if (blockInfo != null) {
                            String blockGenerationTime = getBlockGenerationTime(blockInfo);
                            txCount = (long) getTransactionCount(blockInfo);
                            ChannelInfo channelInfo = new ChannelInfo();
                            channelInfo.setChannelName(channel.getChannelName());
                            channelInfo.setChannelHeight(initHeight);
                            channelInfo.setChannelTxCount(newestChannelInfo.getChannelTxCount() + txCount);
                            channelInfo.setNewestBlockHash(blockInfo.getHeader().getData_hash());
                            channelInfo.setNewestBlockTime(blockGenerationTime);
                            channelInfoDao.save(channelInfo);
                        }
                    } else {
                        for (Long i = initHeight + 1; i < height; i++) {
                            BlockInfo blockInfo = getBlockInfo(channel.getChannelName(), i);
                            if (blockInfo != null) {
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

                        }
                    }
                }

                // if (height == 5) { // 第一次初始化的情况
                // BlockInfo blockInfo = getBlockInfo(channel.getChannelName(), 4L);
                // if (blockInfo != null) {
                // String blockGenerationTime = getBlockGenerationTime(blockInfo);
                // ChannelInfo channelInfo = new ChannelInfo();
                // channelInfo.setChannelName(channel.getChannelName());
                // channelInfo.setChannelHeight(4L);
                // channelInfo.setChannelTxCount(0L);
                // channelInfo.setNewestBlockHash(blockInfo.getHeader().getData_hash());
                // channelInfo.setNewestBlockTime(blockGenerationTime);
                // channelInfoDao.save(channelInfo);
                // }

                // } else if (initHeight + 1 == height) { // 没有新增区块的情况，应该用之前区块的加上当前区块的交易数量
                // List<ChannelInfo> channelInfoList =
                // channelInfoDao.findByChannelHeight(initHeight - 1);
                // if (channelInfoList.size() == 0)
                // continue;
                // ChannelInfo newestChannelInfo = channelInfoList.get(channelInfoList.size() -
                // 1);
                // BlockInfo blockInfo = getBlockInfo(channel.getChannelName(), initHeight);
                // if (blockInfo != null) {
                // String blockGenerationTime = getBlockGenerationTime(blockInfo);
                // txCount = (long) getTransactionCount(blockInfo);
                // ChannelInfo channelInfo = new ChannelInfo();
                // channelInfo.setChannelName(channel.getChannelName());
                // channelInfo.setChannelHeight(initHeight);
                // channelInfo.setChannelTxCount(newestChannelInfo.getChannelTxCount() +
                // txCount);
                // channelInfo.setNewestBlockHash(blockInfo.getHeader().getData_hash());
                // channelInfo.setNewestBlockTime(blockGenerationTime);
                // channelInfoDao.save(channelInfo);
                // }
                // } else {
                // for (Long i = initHeight + 1; i < height; i++) {
                // BlockInfo blockInfo = getBlockInfo(channel.getChannelName(), i);
                // if (blockInfo != null) {
                // String blockGenerationTime = getBlockGenerationTime(blockInfo);
                // txCount += getTransactionCount(blockInfo);
                // ChannelInfo channelInfo = new ChannelInfo();
                // channelInfo.setChannelName(channel.getChannelName());
                // channelInfo.setChannelHeight(i);
                // channelInfo.setChannelTxCount(txCount);
                // channelInfo.setNewestBlockHash(blockInfo.getHeader().getData_hash());
                // channelInfo.setNewestBlockTime(blockGenerationTime);
                // channelInfoDao.save(channelInfo);
                // }

                // }
                // }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @Author: LHD
     * @Date: 2024-01-16 14:34:15
     * @description: 获取最新的交易数量：通过查询数据库获取之前的数量，外加计算未统计的数量
     * @param {String} channelName
     * @return {*}
     */
    public Long getTxNumNow(String channelName) {
        try {
            Long maxChannelHeight = channelInfoDao.findMaxChannelHeightForSingleChannel(channelName);
            Long initHeight = (maxChannelHeight != null) ? Math.max(maxChannelHeight, 4L) : 4L;
            Long maxTxCount = channelInfoDao.findMaxChannelTxForSingleChannel(channelName);
            Long txCount = (maxTxCount != null) ? Math.max(maxTxCount, 0L) : 0L;
            Long height = getBlockHeight(channelName);
            for (Long i = initHeight + 1; i < height; i++) {
                BlockInfo blockInfo = getBlockInfo(channelName, i);
                if (blockInfo != null) {
                    txCount += getTransactionCount(blockInfo);
                }

            }
            return txCount;

        } catch (Exception e) {
            e.printStackTrace();
            return -1L;
        }
    }

    /**
     * @Author: LHD
     * @Date: 2024-01-10 12:48:57
     * @description: 根据最新的五个区块获取最新的5个交易信息.
     * @return {*}
     */
    public List<Transaction> getLatestTx(String channelName, String type) {
        try {
            List<Transaction> transactions = new ArrayList<>();
            long height = getBlockHeight(channelName);
            for (long i = Math.max(5, height - 5); i < height; i++) {
                BlockInfo blockInfo = getBlockInfo(channelName, i);
                if (blockInfo != null) {
                    int txCount = getTransactionCount(blockInfo);
                    for (int j = 0; j < txCount; j++) {
                        Transaction transaction = new Transaction();
                        transaction.setTimestamp(
                                getFormatDate(blockInfo.getData().getData().get(j).getPayload().getHeader()
                                        .getChannel_header().getTimestamp()));
                        transaction.setTxHash(blockInfo.getData().getData().get(j).getPayload().getHeader()
                                .getChannel_header().getTx_id());
                        if (type.toUpperCase().equals("MAJOR"))
                            transaction.setDevice(getDevice(blockInfo, j));
                        transactions.add(transaction);
                    }
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
     * @Date: 2024-01-16 12:47:11
     * @description: 获取最新的n个区块信息
     * @return {*}
     */
    // public List<ChannelInfo> getLatestBlock(String channelName, int n) throws
    // Exception {
    // GenerateChannelInfo(channelName);
    // List<ChannelInfo> channelInfos =
    // channelInfoDao.findTopNByChannelName(channelName, n);
    // return channelInfos;
    // }
    public List<LatestBlockDto> getLatestBlock(String channelName, int n) throws Exception {
        GenerateChannelInfo(channelName);
        List<LatestBlockDto> dtos = new ArrayList<>();
        List<Object[]> results = channelInfoDao.findDistinctTopNByChannelName(channelName, n);
        for (Object[] result : results) {
            dtos.add(new LatestBlockDto(
                    (String) result[0],
                    ((BigInteger) result[1]).longValue(), 
                    ((BigInteger) result[2]).longValue(), // 同上
                    (String) result[3],
                    (String) result[4]));
        }
        return dtos;
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
            throw new Exception("channel not found");
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
        Object parsedJson = JSON.parse(json);
        List<Map<String, Object>> list = new LinkedList<>();

        if (parsedJson instanceof JSONArray) {
            // 如果json是数组
            JSONArray contentArray = (JSONArray) parsedJson;
            for (int i = 0; i < contentArray.size(); i++) {
                Map<String, Object> map = ((JSONObject) contentArray.get(i)).getInnerMap();
                list.add(map);
            }
        } else if (parsedJson instanceof JSONObject) {
            // 如果json是单个对象
            Map<String, Object> map = ((JSONObject) parsedJson).getInnerMap();
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
            String[] command = new String[] { "/bin/sh", "-c", "export CORE_PEER_ADDRESS="
                    + channel.get(0).getTargetOrg() + peerTail + " && peer chaincode invoke -C " + channelName + " -n "
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
        // argStr = argStr.replace("\"", "\\\"");
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

    /**
     * @Author: LHD
     * @Date: 2024-01-17 13:26:41
     * @description: 获取区块数量和交易数量
     * @return {*}
     */
    public BlockTxCountResponse getBlockTxCount() throws InvalidArgumentException, ProposalException {
        List<Object[]> channelInfos = channelInfoDao.findMaxChannelHeightChannels();
        BlockTxCountResponse blockTxCountResponse = new BlockTxCountResponse();
        List<Long> block = new ArrayList<>();
        List<Long> transaction = new ArrayList<>();
        List<String> chanelName = new ArrayList<>();
        for (Object[] channel : channelInfos) {
            try {
                block.add((Long) channel[2]);
                transaction.add((Long) channel[1]);
                chanelName.add((String) channel[0]);

            } catch (Exception e) {
                logger.info(e.toString());
            }

        }
        blockTxCountResponse.setBlock(block);
        blockTxCountResponse.setTransaction(transaction);
        blockTxCountResponse.setChanelName(chanelName);
        return blockTxCountResponse;
    }

}
