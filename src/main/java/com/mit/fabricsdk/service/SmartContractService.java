package com.mit.fabricsdk.service;

import static com.mit.fabricsdk.utils.RemoveElementUtil.removeFirstElement;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Hex;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.protos.ledger.rwset.kvrwset.KvRwset;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.BlockchainInfo;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.TxReadWriteSetInfo;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mit.fabricsdk.component.ChannelInfo;
import com.mit.fabricsdk.constant.ChannelType;
import com.mit.fabricsdk.dao.ChannelDao;
import com.mit.fabricsdk.dao.HistoryTxNumDao;
import com.mit.fabricsdk.dao.PlatformDao;
import com.mit.fabricsdk.dto.GetHistoryTxCountDto;
import com.mit.fabricsdk.dto.response.BlockTxCountResponse;
import com.mit.fabricsdk.entity.BlockChainChannel;
import com.mit.fabricsdk.entity.HistoryTxNum;
import com.mit.fabricsdk.entity.Major;
import com.mit.fabricsdk.entity.SecondaryData;
import com.mit.fabricsdk.utils.BuildStrArgsUtil;
import com.mit.fabricsdk.utils.GatewayUtil;
import com.mit.fabricsdk.utils.JsonUtil;

import lombok.SneakyThrows;

/**
 * @author Haodong Li
 * @date 2023年05月24日 12:29
 */
@Service
public class SmartContractService {
    private static final Logger logger = LoggerFactory.getLogger(SmartContractService.class);

    @Autowired
    ChannelInfo channelInfo;
    @Autowired
    private HistoryTxNumDao historyTxNumDao;

    @Autowired
    private PlatformDao platformDao;

    @Autowired
    private ChannelDao channelDao;

    @SneakyThrows
    public List<Map<String, Object>> queryContract(Contract contract, String... args) throws Exception {
        byte[] bt1 = contract.evaluateTransaction(args[0], removeFirstElement(args));

        JSONArray contentArray1 = JSONArray.parseArray(new String(bt1));
        if (contentArray1 == null)
            return null;

        List<Map<String, Object>> list = new LinkedList<>();
        for (int i = 0; i < contentArray1.size(); i++) {
            Map<String, Object> map = ((JSONObject) contentArray1.get(i)).getInnerMap();
            list.add(map);
        }

        return list;
    }

    public void initBlockChain() {
        List<BlockChainChannel> channels = (List<BlockChainChannel>) channelDao.findAll();
        logger.info(channels.toString());
        for (BlockChainChannel channel : channels) {
            try {
                String channelName = channel.getChannelName();
                String org = channel.getTargetOrg();
                if (!channelInfo.getSchedulerMap().containsKey(channelName)) {
                    Channel tempChannel = null;
                    Gateway gateway = null;
                    long tempTransactionNum = 0;
                    long tempBlockTemporaryNum = -1;
                    ReadWriteLock tempLock = new ReentrantReadWriteLock();
                    ScheduledExecutorService tempScheduledExecutorService = Executors
                            .newSingleThreadScheduledExecutor();

                    HashMap<String, Long> mapDateTxNum = new HashMap<String, Long>();
                    HashMap<String, Long> mapDateHeight = new HashMap<String, Long>();

                    HashMap<String, HashMap<String, HashMap<String, String>>> datePlatformTypeStatMap = new HashMap<String, HashMap<String, HashMap<String, String>>>();
                    datePlatformTypeStatMap.put("BLOCKNUM", new HashMap<String, HashMap<String, String>>());
                    datePlatformTypeStatMap.get("BLOCKNUM").put("BLOCKNUM", new HashMap<String, String>());
                    datePlatformTypeStatMap.get("BLOCKNUM").get("BLOCKNUM").put("BLOCKNUM", "0");

                    HashMap<String, HashMap<String, String>> platformTypeStatMap = new HashMap<String, HashMap<String, String>>();
                    platformTypeStatMap.put("BLOCKNUM", new HashMap<String, String>());
                    platformTypeStatMap.get("BLOCKNUM").put("BLOCKNUM", "0");

                    String ccpPath = new String("/home/lhd/Fabric/fabric-samples/test-network/organizations/peerOrganizations/" + org+".example.com/" +"connection-"+org+".json");
                    String certPath = new String("/home/lhd/Fabric/fabric-samples/test-network/organizations/peerOrganizations/" + org + ".example.com/"+"users/Admin@"+org+".example.com/msp/signcerts/Admin@"+org+".example.com-cert.pem");
                    String pkPath = new String("/home/lhd/Fabric/fabric-samples/test-network/organizations/peerOrganizations/" + org + ".example.com/"+"users/Admin@"+org+".example.com/msp/keystore/priv_sk");

                    // String ccpPath = new String("/home/mx-storage/fabric-config/" + org
                    // + "/fabric/organizations/peerOrganizations/" + org + ".example.com/" + org +
                    // "_ccp.json");
                    // String certPath = new String(
                    // "/home/mx-storage/fabric-config/" + org +
                    // "/fabric/organizations/peerOrganizations/" + org
                    // + ".example.com/" + "users/Admin@" + org +
                    // ".example.com/msp/signcerts/cert.pem");
                    // String pkPath = new String(
                    // "/home/mx-storage/fabric-config/" + org +
                    // "/fabric/organizations/peerOrganizations/" + org
                    // + ".example.com/" + "users/Admin@" + org +
                    // ".example.com/msp/keystore/server.key");

                    File certFile = new File(certPath);
                    File pkFile = new File(pkPath);
                    gateway = GatewayUtil.buildGateway(ccpPath, pkPath, certPath);
                    tempChannel = gateway.getNetwork(channelName).getChannel();
                    channelInfo.getGatewayMap().put(channelName, gateway);
                    channelInfo.getChannelMap().put(channelName, tempChannel);
                    channelInfo.getTxNumMap().put(channelName, tempTransactionNum);
                    channelInfo.getBkTemoryNumMap().put(channelName, tempBlockTemporaryNum);
                    channelInfo.getLockMap().put(channelName, tempLock);
                    channelInfo.getSchedulerMap().put(channelName, tempScheduledExecutorService);
                    channelInfo.getDateHeight().put(channelName, mapDateHeight);
                    channelInfo.getDateTxNum().put(channelName, mapDateTxNum);
                    channelInfo.getDatePlatformTypeStat().put(channelName, datePlatformTypeStatMap);
                    channelInfo.getPlatformTypeStat().put(channelName, platformTypeStatMap);

                    Map<String, Object> retMap = new HashMap<>();
                    retMap.put("new channel instance created: ", channelName);
                }

            } catch (Exception e) {
                logger.info(e.toString());
            }

        }
    }

    public void endExecutorScan(String channelname) throws Exception {
        if (channelInfo.getSchedulerMap().get(channelname) != null) {
            channelInfo.getSchedulerMap().get(channelname).shutdown();
            channelInfo.getSchedulerMap().get(channelname).awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    @SneakyThrows
    public long getChainHeight(String channelname) {
        Channel temp_channel = channelInfo.getChannelMap().get(channelname);
        if (temp_channel == null) {
            System.out.println("channel not found\n");
            throw new Exception("channel not found\n");
        }

        long height;
        BlockchainInfo blockchainInfo = temp_channel.queryBlockchainInfo();
        height = blockchainInfo.getHeight();
        System.out.printf("=====Request for Height of channel %s : The height is %s=====\n", temp_channel.getName(),
                height);
        return height;
    }

    public JSONObject getLatestBlock(int num, String channelName) throws Exception {

        long height = channelInfo.getChannelMap().get(channelName).queryBlockchainInfo().getHeight();
        long returnNum = num < height ? num : height;

        System.out.printf("=====Request for %s latest block(s), return %s block(s)=====\n", num, returnNum);
        List<JSONObject> transactions = new ArrayList<>();
        for (long i = height - 1; i > height - returnNum - 1; i--) {
            BlockInfo blockInfo = channelInfo.getChannelMap().get(channelName).queryBlockByNumber(i);
            Map<String, Object> info = new HashMap<>();
            info.put("BlockIndex", height - i - 1);
            info.put("BlockDataHash", Base64Utils.encode(blockInfo.getDataHash()));
            info.put("BlockTxNum", blockInfo.getTransactionCount());
            info.put("BlockNumber", blockInfo.getBlockNumber());
            info.put("BlockTxMetaData", Arrays.toString(blockInfo.getTransActionsMetaData()));

            int lastindex_tx = blockInfo.getTransactionCount() - 1 >= 0 ? blockInfo.getTransactionCount() - 1 : 0;
            info.put("TimeStamp", blockInfo.getEnvelopeInfo(lastindex_tx).getTimestamp().getTime());
            DateFormat dFormat = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
            info.put("Time", dFormat.format(blockInfo.getEnvelopeInfo(lastindex_tx).getTimestamp()));

            transactions.add(new JSONObject(info));
        }
        Map<String, Object> latestBlockMap = new HashMap<>();
        latestBlockMap.put("LatestBlocks", transactions);
        latestBlockMap.put("Num", returnNum);

        return new JSONObject(latestBlockMap);
    }

    public JSONObject getLatestTx(int number, String channelname, String type) throws Exception {

        JSONObject ret = null;
        System.out.printf(
                "=====Request for %s latest Tx(s), return %s Tx(s) (If number is more than all tx,error will return)=====\n",
                number, number);
        BlockchainInfo blockchainInfo = null;
        blockchainInfo = channelInfo.getChannelMap().get(channelname).queryBlockchainInfo();

        long i = blockchainInfo.getHeight() - 1;
        int txleftnum = number;
        BlockInfo blockInfo = channelInfo.getChannelMap().get(channelname).queryBlockByNumber(i);
        List<Map<String, Object>> txlist = new LinkedList<>();

        while (txleftnum > 0) {
            List<Map<String, Object>> blocktxlist = new LinkedList<>();
            for (BlockInfo.EnvelopeInfo envelopeInfo : blockInfo.getEnvelopeInfos()) {
                Map<String, Object> map = new HashMap<>();
                map.put("ChannelID", envelopeInfo.getChannelId());
                map.put("TxID", envelopeInfo.getTransactionID());
                map.put("ValidationCode", envelopeInfo.getValidationCode());
                map.put("TypeID", envelopeInfo.getType());
                map.put("Nonce", Hex.encodeHexString(envelopeInfo.getNonce()));
                map.put("CreatorMspID", envelopeInfo.getCreator().getMspid());
                map.put("isValid", envelopeInfo.isValid());
                map.put("TimeStamp", envelopeInfo.getTimestamp().getTime());
                DateFormat dFormat = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
                map.put("Time", dFormat.format(envelopeInfo.getTimestamp()));
                map.put("BlockNum", blockInfo.getBlockNumber());
                map.put("Creator", envelopeInfo.getCreator());
                map.put("Class", envelopeInfo.getClass());
                map.put("hashCode", envelopeInfo.hashCode());
                // 返回区块链的信息
                try {
                    if (type.toUpperCase().equals(ChannelType.MAJOR.name())) {
                        List<Major> majorList = new ArrayList<>();
                        System.out.println("MAJOR");
                        if (envelopeInfo.getType() == BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE) {
                            System.out.println("TRANSACTION");
                            BlockInfo.TransactionEnvelopeInfo transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) envelopeInfo;

                            for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo transactionActionInfo : transactionEnvelopeInfo
                                    .getTransactionActionInfos()) {
                                String chaincodename = transactionActionInfo.getChaincodeIDName();

                                int num_inputs = transactionActionInfo.getChaincodeInputArgsCount();
                                Map<String, Object> tx_input_map = new HashMap<>();

                                String jsonString = new String(transactionActionInfo.getChaincodeInputArgs(1));
                                jsonString = jsonString.replace("\\", "");
                                jsonString = BuildStrArgsUtil.jsonToObjectTrans(jsonString);
                                // String jsonString = new
                                // String(transactionActionInfo.getChaincodeInputArgs(1));
                                jsonString = jsonString.replace("\\", "");
                                Major major = JsonUtil.toObjectQuietly(jsonString, Major.class);
                                map.put("major", major);
                                map.put("chaincode_and_input_args", tx_input_map);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e);
                    logger.info(e.toString());
                }

                String Txtype = "系统类型存证";
                blocktxlist.add(map);
            }
            blocktxlist.sort((o1, o2) -> o2.get("TimeStamp").toString().compareTo(o1.get("TimeStamp").toString()));
            if (blocktxlist.size() > txleftnum) {
                txlist.addAll(blocktxlist.subList(0, txleftnum));
            } else {
                txlist.addAll(blocktxlist);
            }
            i--;
            if (i < 0) {
                break;
            }
            blockInfo = channelInfo.getChannelMap().get(channelname).queryBlockByNumber(i);
            txleftnum -= blocktxlist.size();
        }
        Map<String, Object> latestTxMap = new HashMap<>();
        latestTxMap.put("LatestTxs", txlist);
        latestTxMap.put("Num", txlist.size());
        ret = new JSONObject(latestTxMap);

        return ret;
    }

    public long calculateTxNum(String channelName) throws InvalidArgumentException, ProposalException {
        long txnum = 0;
        long i = 0;
        long height = this.getChainHeight(channelName);
        while (i < height) {
            txnum += channelInfo.getChannelMap().get(channelName).queryBlockByNumber(i).getTransactionCount();
            i++;
        }
        return txnum;
    }

    public JSONObject getLatestTxNew(int number, String channelname) throws Exception {

        JSONObject ret = null;
        System.out.printf(
                "=====Request for %s latest Tx(s), return %s Tx(s) (If number is more than all tx,error will return)=====\n",
                number, number);
        BlockchainInfo blockchainInfo = null;
        blockchainInfo = channelInfo.getChannelMap().get(channelname).queryBlockchainInfo();

        long i = blockchainInfo.getHeight() - 1;
        int txleftnum = number;
        BlockInfo blockInfo = channelInfo.getChannelMap().get(channelname).queryBlockByNumber(i);
        List<Map<String, Object>> txlist = new LinkedList<>();
        while (txleftnum > 0) {
            List<Map<String, Object>> blocktxlist = new LinkedList<>();
            for (BlockInfo.EnvelopeInfo envelopeInfo : blockInfo.getEnvelopeInfos()) {
                Map<String, Object> map = new HashMap<>();
                map.put("ChannelID", envelopeInfo.getChannelId());
                map.put("TxID", envelopeInfo.getTransactionID());
                map.put("ValidationCode", envelopeInfo.getValidationCode());
                map.put("TypeID", envelopeInfo.getType());
                map.put("Nonce", Hex.encodeHexString(envelopeInfo.getNonce()));
                map.put("CreatorMspID", envelopeInfo.getCreator().getMspid());
                map.put("isValid", envelopeInfo.isValid());
                map.put("TimeStamp", envelopeInfo.getTimestamp().getTime());
                DateFormat dFormat = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
                map.put("Time", dFormat.format(envelopeInfo.getTimestamp()));
                map.put("BlockNum", blockInfo.getBlockNumber());
                map.put("Creator", envelopeInfo.getCreator());
                map.put("Class", envelopeInfo.getClass());
                map.put("hashCode", envelopeInfo.hashCode());

                String Txtype = "系统类型存证";

                map.put("TxHash", channelInfo.getChannelMap().get(channelname)
                        .queryTransactionByID(envelopeInfo.getTransactionID()).getProcessedTransaction().hashCode());
                if (envelopeInfo.getType() == BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE) {
                    BlockInfo.TransactionEnvelopeInfo transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) envelopeInfo;

                    // 旧的for循环
                    for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo transactionActionInfo : transactionEnvelopeInfo
                            .getTransactionActionInfos()) {
                        TxReadWriteSetInfo readWriteSetInfo = transactionActionInfo.getTxReadWriteSet();
                        if (readWriteSetInfo != null) {
                            for (TxReadWriteSetInfo.NsRwsetInfo nsRwsetInfo : readWriteSetInfo.getNsRwsetInfos()) {
                                KvRwset.KVRWSet rws = nsRwsetInfo.getRwset();
                                List<String> writes = new LinkedList<>();
                                for (KvRwset.KVWrite writeline : rws.getWritesList()) {
                                    writes.add(writeline.getValue().toStringUtf8());
                                }
                                if (!writes.isEmpty() && writes.get(0).charAt(0) == '{') {
                                    JSONObject j = JSONObject.parseObject(writes.get(0));
                                    String typetype = j.getString("Typetype");
                                    if (typetype != null && channelInfo.getNewTypeTypeList().containsKey(typetype)) {
                                        Txtype = channelInfo.getNewTypeTypeList().get(typetype);
                                    }

                                }

                            }
                        }
                    }
                    ;

                }
                map.put("Txtype", Txtype);
                blocktxlist.add(map);
            }
            blocktxlist.sort((o1, o2) -> o2.get("TimeStamp").toString().compareTo(o1.get("TimeStamp").toString()));
            if (blocktxlist.size() > txleftnum) {
                txlist.addAll(blocktxlist.subList(0, txleftnum));
            } else {
                txlist.addAll(blocktxlist);
            }
            i--;
            if (i < 0) {
                break;
            }
            blockInfo = channelInfo.getChannelMap().get(channelname).queryBlockByNumber(i);
            txleftnum -= blocktxlist.size();
        }
        Map<String, Object> latestTxMap = new HashMap<>();
        latestTxMap.put("LatestTxs", txlist);
        latestTxMap.put("Num", txlist.size());
        ret = new JSONObject(latestTxMap);

        return ret;
    }

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

    public BlockTxCountResponse getBlockTxCount() throws InvalidArgumentException, ProposalException {
        BlockTxCountResponse blockTxCountResponse = new BlockTxCountResponse();
        List<BlockChainChannel> channels = (List<BlockChainChannel>) channelDao.findAll();
        List<Long> block = new ArrayList<>();
        List<Long> transaction = new ArrayList<>();
        List<String> chanelName = new ArrayList<>();
        for (BlockChainChannel channel : channels) {
            try {
                Long txNum = this.calculateTxNum(channel.getChannelName());
                Long blockNum = this.getChainHeight(channel.getChannelName());
                block.add(blockNum);
                transaction.add(txNum);
                chanelName.add(channel.getChannelName());

            } catch (Exception e) {
                logger.info(e.toString());
            }

        }
        blockTxCountResponse.setBlock(block);
        blockTxCountResponse.setTransaction(transaction);
        blockTxCountResponse.setChanelName(chanelName);
        return blockTxCountResponse;
    }

    public CompletableFuture<List<String>> asyncAddSecondaryData(List<SecondaryData> dataList, Contract contract,
            String eventName) {
        return CompletableFuture.supplyAsync(() -> {
            return dataList.stream()
                    .map(data -> {
                        String sequence = data.getSequence();
                        // Here, call the actual method to add the data to Fabric
                        // For the sake of this example, I'm just simulating the call
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            String jsonString = objectMapper.writeValueAsString(data);
                            contract.submitTransaction(eventName, jsonString);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return sequence + "failed to added at " + Instant.now().toString();
                        }
                        return sequence + " added at " + Instant.now().toString();
                    })
                    .collect(Collectors.toList());
        });
    }

}
