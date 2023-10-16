package com.mit.fabricsdk.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mit.fabricsdk.dto.BaseResponse;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.List;

/**
 * @author Haodong Li
 * @date 2023年05月30日 14:26
 */
@Data
public class BlockTxCountResponse {
    @ApiModelProperty("区块数量")
    List<Long> block;

    @ApiModelProperty("存证数量")
    List<Long> transaction;

    @JsonProperty("xaxis")
    @ApiModelProperty("channelName")
    List<String> chanelName;
}
