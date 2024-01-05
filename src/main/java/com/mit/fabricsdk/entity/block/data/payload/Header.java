package com.mit.fabricsdk.entity.block.data.payload;

import org.apache.milagro.amcl.RSA2048.private_key;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mit.fabricsdk.entity.block.Channel_header;
import com.mit.fabricsdk.entity.block.Creator;
import com.mit.fabricsdk.entity.block.Signature_header;

@lombok.Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Header {
    private Channel_header channel_header;
    private Signature_header signature_header;
}
