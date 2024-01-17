/*
 * @Author: LHD
 * @Date: 2024-01-05 11:31:02
 * @LastEditors: 308twin 790816436@qq.com
 * @LastEditTime: 2024-01-16 13:14:35
 * @Description: 
 * 
 * Copyright (c) 2024 by 308twin@790816436@qq.com, All Rights Reserved. 
 */
/**
  * Copyright 2024 bejson.com 
  */
package com.mit.fabricsdk.entity.block;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ns_rwset {

    private List<Object> collection_hashed_rwset;
    private String namespace;
    private Rwset rwset;   

}