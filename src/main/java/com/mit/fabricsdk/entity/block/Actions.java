/**
  * Copyright 2024 bejson.com 
  */
package com.mit.fabricsdk.entity.block;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mit.fabricsdk.entity.block.data.actions.Header;
import com.mit.fabricsdk.entity.block.data.payload.Payload;
import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Actions {

    private Header header;
    private Payload payload;
    

}