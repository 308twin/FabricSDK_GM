package com.mit.fabricsdk.entity.block.data.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mit.fabricsdk.entity.block.Chaincode_proposal_payload;

@lombok.Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Payload {
    private com.mit.fabricsdk.entity.block.Action action;
    //private Chaincode_proposal_payload chaincode_proposal_payload;
}
