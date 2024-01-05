/**
  * Copyright 2024 bejson.com 
  */
package com.mit.fabricsdk.entity.block;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rwset {

     private List<Object> metadata_writes;  //不确定
     private List<Object> range_queries_info; //不确定
    private List<Reads> reads;
    private List<Writes> writes;
   
}