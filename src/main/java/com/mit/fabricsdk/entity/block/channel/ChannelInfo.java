package com.mit.fabricsdk.entity.block.channel;

import lombok.Data;

@Data
public class ChannelInfo {
    private int height;
    private String currentBlockHash;
    private String previousBlockHash;
    
}
