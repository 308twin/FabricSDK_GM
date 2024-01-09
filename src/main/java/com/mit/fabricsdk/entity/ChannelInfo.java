/*
 * @Author: LHD
 * @Date: 2024-01-09 14:09:21
 * @LastEditors: 308twin 790816436@qq.com
 * @LastEditTime: 2024-01-09 14:16:12
 * @Description: 
 * 
 * Copyright (c) 2024 by 308twin@790816436@qq.com, All Rights Reserved. 
 */
package com.mit.fabricsdk.entity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@lombok.Data
@Getter
@Setter
@Table(name = "channel_info")
public class ChannelInfo extends  BaseEntity{

    @ApiModelProperty("通道名称")
    @Column(name = "channel_name", nullable = false,columnDefinition = "VARCHAR(100) COMMENT '通道名称'")
    private String channelName;

    @ApiModelProperty("通道区块高度")
    @Column(name = "channel_height", nullable = false,columnDefinition = "bigint COMMENT '通道区块高度'")
    private Long channelHeight;

    @ApiModelProperty("通道交易数量")
    @Column(name = "channel_tx_count", nullable = false,columnDefinition = "bigint COMMENT '通道交易数量'")
    private Long channelTxCount;    

    @ApiModelProperty("最新区块哈希值")
    @Column(name = "newest_block_hash", nullable = false,columnDefinition = "VARCHAR(100) COMMENT '最新区块哈希值'")
    private String newestBlockHash;

    @ApiModelProperty("最新区块时间")
    @Column(name = "newest_block_time", nullable = false,columnDefinition = "VARCHAR(100) COMMENT '最新区块时间'")
    private String newestBlockTime;
}
