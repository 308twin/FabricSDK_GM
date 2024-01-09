/**
  * Copyright 2024 bejson.com 
  */
package com.mit.fabricsdk.entity.block;
import java.util.Date;

import lombok.Data;
@Data
public class Channel_header {

    private String channel_id;
    private String epoch;
    private Extension extension;
    private String timestamp;
    private String tls_cert_hash;
    private String tx_id;
    private int type;
    private int version;   

}