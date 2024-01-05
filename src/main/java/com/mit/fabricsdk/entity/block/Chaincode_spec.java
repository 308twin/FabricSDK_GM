/**
  * Copyright 2024 bejson.com 
  */
package com.mit.fabricsdk.entity.block;

import lombok.Data;
@Data
public class Chaincode_spec {

    private Chaincode_id chaincode_id;
    private Input input;
    private int timeout;
    private String type;    
}