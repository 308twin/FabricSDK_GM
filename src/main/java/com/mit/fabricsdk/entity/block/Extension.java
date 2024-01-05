/**
  * Copyright 2024 bejson.com 
  */
package com.mit.fabricsdk.entity.block;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Extension {

    private Chaincode_id chaincode_id;
    private Results results;

}