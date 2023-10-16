package com.mit.fabricsdk.schedule;

import com.mit.fabricsdk.component.ChannelInfo;
import com.mit.fabricsdk.dao.HistoryTxNumDao;
import com.mit.fabricsdk.dto.BaseResponse;
import com.mit.fabricsdk.entity.HistoryTxNum;
import com.mit.fabricsdk.service.SmartContractService;

import lombok.SneakyThrows;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.scheduling.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author Haodong Li
 * @date 2023年05月28日 10:52
 */
@Component
//@EnableAsync
//@EnableScheduling
public class CommonSchedule  {
    private static final Logger logger = LoggerFactory.getLogger(CommonSchedule.class);

    @Autowired
    ChannelInfo channelInfo;

    @Autowired
    HistoryTxNumDao historyTxNumDao;

    @Autowired
    SmartContractService smartContractService;


    /**
     * Description: 定时执行查询操作,防止每次查询都要去查询区块链，数据库查更快点
     * date: 2023/5/28 10:58
     * @author: Haodong Li
     * @since: JDK 1.8

     */
    //@Scheduled(cron = "50 59 * * * *")
//    @Scheduled(fixedRate = 60000)
//    public void CalculateTxNum() throws InvalidArgumentException, ProposalException {
//        List<String> channelList = new ArrayList<>();
//        logger.info("CalculateTxNum:Start");
//        for (String channelname : channelInfo.getChannelMap().keySet()){
//            logger.info("Excute CalculateTxNum");
//           long num = smartContractService.calculateTxNum(channelname);
//            HistoryTxNum entity = new HistoryTxNum();
//            entity.setNum(num);
//            entity.setChannel(channelname);
//            historyTxNumDao.save(entity);
//            logger.info("Save Transaction Count:"+ entity);
//        }
//    }

    @Scheduled(fixedRate = 60000)
    public void CalculateAllCount() throws InvalidArgumentException, ProposalException {
        List<String> channelList = new ArrayList<>();
        logger.info("CalculateTxNum:Start");
        for (String channelname : channelInfo.getChannelMap().keySet()){
            logger.info("Excute CalculateTxNum");
            long num = smartContractService.calculateTxNum(channelname);
            HistoryTxNum entity = new HistoryTxNum();
            entity.setNum(num);
            entity.setChannel(channelname);
            historyTxNumDao.save(entity);
            logger.info("Save Transaction Count:"+ entity);
        }
    }

    @Scheduled(fixedRate = 60000)
    public void detectChannel(){
        smartContractService.initBlockChain();
    }
}
