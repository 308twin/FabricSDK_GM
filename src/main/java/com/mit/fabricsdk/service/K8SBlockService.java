/*
 * @Author: LHD
 * @Date: 2024-01-06 12:51:51
 * @LastEditors: 308twin 790816436@qq.com
 * @LastEditTime: 2024-01-09 17:28:59
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


import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.mit.fabricsdk.entity.block.BlockInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mit.fabricsdk.dao.ChannelDao;
import com.mit.fabricsdk.dao.ChannelInfoDao;
import com.mit.fabricsdk.dao.HistoryTxNumDao;
import com.mit.fabricsdk.dao.PlatformDao;
import com.mit.fabricsdk.dto.GetHistoryTxCountDto;
import com.mit.fabricsdk.dto.response.BlockTxCountResponse;
import com.mit.fabricsdk.entity.BlockChainChannel;
import com.mit.fabricsdk.entity.ChannelInfo;
import com.mit.fabricsdk.entity.HistoryTxNum;
import com.mit.fabricsdk.entity.Major;
import com.mit.fabricsdk.entity.SecondaryData;
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
    public Long getBlockHeight(String channelName) throws Exception{
        List<BlockChainChannel> channel = (List<BlockChainChannel>) channelDao.findByChannelName(channelName);
        if(channel.size()==0)
            return -1L;
        ObjectMapper mapper = new ObjectMapper();
        String[] command = new String[]{"/bin/bash", "-c", "export CORE_PEER_ADDRESS="+channel.get(0).getTargetOrg()+peerTail+" && peer channel getinfo -c "+channelName+" -o "+order};
        String json = K8SUtil.excuteK8SCommand(namespace, channel.get(0).getTargetOrg()+adminTail, command);
        System.out.println("This is raw json:"+json);
        int lastBracketIndex = json.lastIndexOf('{');
        String newJson = lastBracketIndex != -1 ? json.substring(lastBracketIndex) : json;
        System.out.println("This is new json:"+newJson);
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
    public BlockInfo getBlockInfo(String channelName,Long blockNum) throws Exception{
        List<BlockChainChannel> channel = (List<BlockChainChannel>) channelDao.findByChannelName(channelName);
        if(channel.size()==0)
            return null;
        ObjectMapper mapper = new ObjectMapper();
        String blockFileName = channelName+"_"+blockNum+".block";
        String blockJsonFileName = channelName+"_"+blockNum+".json";
        String[] command1 = new String[]{"/bin/bash", "-c", "export CORE_PEER_ADDRESS="+channel.get(0).getTargetOrg()+peerTail+" && peer channel fetch "+blockNum+" "+blockFileName+" -c "+channelName+" -o "+order+" --tls --cafile "+ cafile};
        String[] command2 = new String[]{"/bin/bash", "-c", "configtxlator proto_decode --input "+blockFileName+" --type common.Block | jq . > "+blockJsonFileName};
        String[] command3 = new String[]{"/bin/bash", "-c", "cat "+blockJsonFileName};
        String[] command4 = new String[]{"/bin/bash", "-c", "rm "+blockFileName+" "+blockJsonFileName};

        K8SUtil.excuteK8SCommand(namespace, channel.get(0).getTargetOrg()+adminTail, command1);
        K8SUtil.excuteK8SCommand(namespace, channel.get(0).getTargetOrg()+adminTail, command2);
        String json = K8SUtil.excuteK8SCommand(namespace, channel.get(0).getTargetOrg()+adminTail, command3);
        //K8SUtil.excuteK8SCommand(namespace, channel.get(0).getTargetOrg()+adminTail, command4);

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
    public int getTransactionCount(BlockInfo blockInfo){
        return blockInfo.getData().getData().size();
    }

    /**
     * @Author: LHD
     * @Date: 2024-01-09 17:17:05
     * @description: 获取区块生成时间
     * @param {BlockInfo} blockInfo
     * @return {*}
     */    
    public String getBlockGenerationTime(BlockInfo blockInfo){
        String isoDateTime = blockInfo.getData().getData().get(getTransactionCount(blockInfo)-1).getPayload().getHeader().getChannel_header().getTimestamp(); 
         // 将ISO 8601字符串解析为OffsetDateTime对象
        OffsetDateTime odt = OffsetDateTime.parse(isoDateTime);

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
    public void GenerateChannelInfo(){
        List<BlockChainChannel> channels = (List<BlockChainChannel>) channelDao.findAll();

        List<Object[]> resultList = channelInfoDao.findMaxChannelHeightForEachChannel();
        Map<String, Long> channelHeightMap = new HashMap<>();

        for (Object[] result : resultList) {
            String channelName = (String) result[0];
            Long channelHeight = (Long) result[1];

            channelHeightMap.put(channelName, channelHeight);
        }
        for(BlockChainChannel channel:channels){
            try {
                Long initHeight = channelHeightMap.getOrDefault(channel.getChannelName(), 5L); // 5 is the default value               
                Long height = getBlockHeight(channel.getChannelName());
                Long txCount = 0L;
                for (Long i = initHeight; i < height; i++) {
                    BlockInfo blockInfo = getBlockInfo(channel.getChannelName(),i);
                    String blockGenerationTime = getBlockGenerationTime(blockInfo);
                    txCount+=getTransactionCount(blockInfo);
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
}
