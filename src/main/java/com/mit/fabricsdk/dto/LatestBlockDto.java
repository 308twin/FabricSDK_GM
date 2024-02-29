package com.mit.fabricsdk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LatestBlockDto {
    @ApiModelProperty("通道名称")
    private String channelName;

    @ApiModelProperty("通道区块高度")
    private Long channelHeight;

    @ApiModelProperty("通道交易数量")
    private Long channelTxCount;

    @ApiModelProperty("最新区块哈希值")
    private String newestBlockHash;

    @ApiModelProperty("最新区块时间")
    private String newestBlockTime;
}
