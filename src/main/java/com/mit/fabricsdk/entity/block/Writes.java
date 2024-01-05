package com.mit.fabricsdk.entity.block;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@lombok.Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Writes {
    boolean is_delete;
    String key;
    String value;
}
