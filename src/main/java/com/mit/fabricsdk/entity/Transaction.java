/*
 * @Author: LHD
 * @Date: 2024-01-10 12:34:52
 * @LastEditors: 308twin 790816436@qq.com
 * @LastEditTime: 2024-01-10 12:37:51
 * @Description: 
 * 
 * Copyright (c) 2024 by 308twin@790816436@qq.com, All Rights Reserved. 
 */
package com.mit.fabricsdk.entity;


@lombok.Data
public class Transaction {
    private String timestamp;
    private String txHash;
    private String device;
}
