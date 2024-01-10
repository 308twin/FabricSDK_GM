package com.mit.fabricsdk.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ChaincodeInvoke {
    private String function;

    @JsonProperty("Args")
    private String[] args;
}
