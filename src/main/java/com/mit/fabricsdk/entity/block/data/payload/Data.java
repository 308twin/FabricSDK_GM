package com.mit.fabricsdk.entity.block.data.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mit.fabricsdk.entity.block.Actions;

@lombok.Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {
    List<Actions> actions;
    
}
