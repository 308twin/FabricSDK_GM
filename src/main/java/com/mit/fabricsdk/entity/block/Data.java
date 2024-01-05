/**
  * Copyright 2024 bejson.com 
  */
package com.mit.fabricsdk.entity.block;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@lombok.Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {
    private List<com.mit.fabricsdk.entity.block.data.Data> data;
}