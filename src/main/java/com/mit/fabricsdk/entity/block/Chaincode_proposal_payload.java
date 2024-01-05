/**
  * Copyright 2024 bejson.com 
  */
package com.mit.fabricsdk.entity.block;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class Chaincode_proposal_payload {

    private TransientMap TransientMap;
    @JsonProperty("input")
    private Input input;
   

}