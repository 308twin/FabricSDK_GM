package com.mit.fabricsdk.entity.block.data.actions;

import com.mit.fabricsdk.entity.block.Creator;

@lombok.Data
public class Header {
    private Creator creator;
    private String nonce;
}
