package com.mit.fabricsdk.entity.block.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mit.fabricsdk.entity.block.Payload;

@lombok.Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {

     private Payload payload;
     private String signature;
}