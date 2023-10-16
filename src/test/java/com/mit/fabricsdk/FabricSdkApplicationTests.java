package com.mit.fabricsdk;

import com.mit.fabricsdk.dto.response.BlockTxCountResponse;
import com.mit.fabricsdk.service.SmartContractService;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FabricSdkApplicationTests {

    @Autowired
    SmartContractService smartContractService;

    @Test
    void contextLoads() throws InvalidArgumentException, ProposalException {
        smartContractService.getBlockTxCount();
    }

}
