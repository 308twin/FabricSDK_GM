/**
  * Copyright 2024 bejson.com 
  */
package com.mit.fabricsdk.entity.block;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Action {

    private List<Endorsements> endorsements;
    private Proposal_response_payload proposal_response_payload;   
}