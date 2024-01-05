package com.mit.fabricsdk;

import java.io.IOException;

import org.springframework.boot.SpringApplication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mit.fabricsdk.entity.block.BlockInfo;
import com.mit.fabricsdk.utils.BashUtil;
import com.mit.fabricsdk.utils.K8SUtil;

public class Test {
    public static void main(String[] args) throws Exception{
      
         ObjectMapper mapper = new ObjectMapper();
         String[] command = new String[]{"/bin/bash", "-c", "cat latest_block.json"};

        try {
            String json =  K8SUtil.excuteK8SCommand("mx","org1-admin-cli", command);
            //System.out.println(json);
            // System.out.println();
             BlockInfo blockInfo = mapper.readValue(json, BlockInfo.class);
             System.out.println(blockInfo.getMetadata());
        } catch (IOException e) {
            e.printStackTrace();
        }
      
    }
}
