/**
  * Copyright 2024 bejson.com 
  */
package com.mit.fabricsdk.entity.block;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Results {

    private String data_model;
    private List<Ns_rwset> ns_rwset;
   

}