/*
 * @Author: LHD
 * @Date: 2023-12-20 13:34:31
 * @LastEditors: 308twin 790816436@qq.com
 * @LastEditTime: 2024-01-09 13:32:48
 * @Description: 
 * 
 * Copyright (c) 2024 by 308twin@790816436@qq.com, All Rights Reserved. 
 */
package com.mit.fabricsdk;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mit.fabricsdk.entity.block.BlockInfo;
import com.mit.fabricsdk.service.K8SBlockService;
import com.mit.fabricsdk.service.SmartContractService;
import com.mit.fabricsdk.utils.BashUtil;
import com.mit.fabricsdk.utils.K8SUtil;

public class Test {
     @Autowired
    static
    K8SBlockService k8sBlockService;
    public static void main(String[] args) throws Exception{
      
         ObjectMapper mapper = new ObjectMapper();
         String[] command = new String[]{"/bin/bash", "-c", "cat latest_major.json"};

        try {
            String json =  K8SUtil.excuteK8SCommand("mx","org1-admin-cli", command);
            //System.out.println(json);
            // System.out.println();
             BlockInfo blockInfo = mapper.readValue(json, BlockInfo.class);
             System.out.println(blockInfo.getMetadata());
            //k8sBlockService.getBlockHeight("pingliangroadmajorchannel");
        } catch (IOException e) {
            e.printStackTrace();
        }
      
    }
}
