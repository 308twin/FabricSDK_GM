/**
  * Copyright 2024 bejson.com 
  */
package com.mit.fabricsdk.entity.block;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mit.fabricsdk.entity.block.data.payload.Header;
@lombok.Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Payload {

    private com.mit.fabricsdk.entity.block.data.payload.Data data;
    private Header header;
   

}