/*
 * @Author: LHD
 * @Date: 2023-12-19 13:54:39
 * @LastEditors: 308twin 790816436@qq.com
 * @LastEditTime: 2024-01-15 16:01:24
 * @Description: 
 * 
 * Copyright (c) 2024 by 308twin@790816436@qq.com, All Rights Reserved. 
 */
package com.mit.fabricsdk.dto.request;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mit.fabricsdk.dto.BaseRequest;
import com.mit.fabricsdk.entity.ChaincodeInvoke;

public class SearchSecondaryRequest extends BaseRequest{
    public String sequence;

    public  String[] toJSONString(){
        List<String> resList = new ArrayList<>();
        resList.add((sequence));
        return resList.toArray(new String[0]);
    }

    
    public String toChaincodeInvoke() {
        ChaincodeInvoke chaincodeInvoke = new ChaincodeInvoke();
        chaincodeInvoke.setFunction("QuerySecondaryDataBySequence");
        chaincodeInvoke.setArgs(toJSONString());
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
