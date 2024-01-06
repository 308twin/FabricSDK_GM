package com.mit.fabricsdk.entity.block;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ChainInfo {
    @JsonProperty("height")
    private Integer height;

    @JsonProperty("currentBlockHash")
    private String currentBlockHash;

    @JsonProperty("previousBlockHash")
    private String previousBlockHash;
}
