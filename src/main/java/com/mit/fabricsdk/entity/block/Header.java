/**
  * Copyright 2024 bejson.com 
  */
package com.mit.fabricsdk.entity.block;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Header {

    private String data_hash;
    private String number;
    private String previous_hash;
   

}