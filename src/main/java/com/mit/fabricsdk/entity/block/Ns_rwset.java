/**
  * Copyright 2024 bejson.com 
  */
package com.mit.fabricsdk.entity.block;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ns_rwset {

    private List<String> collection_hashed_rwset;
    private String namespace;
    private Rwset rwset;   

}